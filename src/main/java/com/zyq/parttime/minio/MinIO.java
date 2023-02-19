package com.zyq.parttime.minio;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyq.parttime.config.MinIOClientConfig;
import com.zyq.parttime.exception.ParttimeServiceException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class MinIO {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.bucket}")
    public String bucketName;
//    @Value("${minio.urlprefix}")
//    public String urlprefix;

    private static final int DEFAULT_EXPIRY_TIME = 4 * 24 * 3600;//有效期为4天

    //判断bucket是否存在
    public Boolean existBucket(String name) throws Exception {
        Boolean flag = true;//是否存在，true存在，false不存在
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
            flag = false;
        }
        return flag;
    }

    //创建bucket
    public Boolean createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //删除bucket
    public Boolean deleteBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //上传文件
    public String uploadFile(MultipartFile file, String bucketName) throws Exception {
        existBucket(bucketName);
        try {
            //得到文件流
            InputStream inputStream = file.getInputStream();

            //重名会覆盖
            String fileName = file.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).
                    stream(inputStream, inputStream.available(), -1).
                    contentType(file.getContentType()).build());

            //生成url
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .method(Method.GET)
                    .expiry(DEFAULT_EXPIRY_TIME)//有效期
                    .build());
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    //下载文件
    public InputStream downloadFile(String bucketName, String fileName, HttpServletResponse res) throws Exception {
        try {
            //把fileName转json对象，再获取其中的文件名
            JSONObject obj = JSONObject.parseObject(fileName);

            //根据桶名+文件名获得文件输入流
            InputStream file = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object((String) (obj.get("fileName"))).build());

            //获取utf-8编译后的文件名，解决中文乱码
            String filename = new String(((String) (obj.get("fileName"))).getBytes("ISO8859-1"), StandardCharsets.UTF_8);
//            if (StrUtil.isNotBlank(originalName)) {
//                filename = originalName;
//            }

            //设置消息头
            res.setHeader("Content-Disposition", "attachment;filename=" + filename);

            //输出流
            ServletOutputStream servletOutputStream = res.getOutputStream();
            int len;//实际读取的字节数
            byte[] buffer = new byte[1024];//缓冲区数组
            while ((len = file.read(buffer)) > 0) {//从输入流中读取一定数量的字节并将其存储到缓冲区数组buffer中
                servletOutputStream.write(buffer, 0, len);
            }
            servletOutputStream.flush();//刷新此输出流并强制写出所有缓冲的输出字节
            file.close();//关闭文件
            servletOutputStream.close();//关闭输出流
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //删除文件
    public String deleteFile(String bucketName, String fileName) throws Exception {
        try {
            Iterable<Result<Item>> list = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());//获得bucket中所有文件
            list.forEach(item -> {
                try {
                    JSONObject obj = JSONObject.parseObject(fileName);//把fileName转json对象，再获取其中的文件名
                    if (item.get().objectName().equals(obj.get("fileName"))) {//找到目标文件，删除
                        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.get().objectName()).build());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return "删除成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "删除失败";
    }

//    //查看文件对象
//    //@return 存储bucket内文件对象信息
//    public List<ObjectItem> listObjects(String bucketName) {
//        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
//        List<ObjectItem> objectItems = new ArrayList<>();
//        try {
//            for (Result<Item> result : results) {
//                Item item = result.get();
//
//                ObjectItem objectItem = new ObjectItem();
//                objectItem.setObjectName(item.objectName());
//                objectItem.setSize(item.size());
//
//                objectItems.add(objectItem);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return objectItems;
//    }

    /**
     * 批量删除文件对象
     *
     * @param bucketName 存储bucket名称
     * @param objects    对象名称集合
     */
    public Map<String, String> removeObjects(String bucketName, List<String> objects) {
        Map<String, String> resultMap = new HashMap<>();
        List<DeleteObject> dos = objects.stream().map(e -> new DeleteObject(e)).collect(Collectors.toList());
        try {
            minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
            resultMap.put("mes", "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("mes", "网络异常，删除失败");
        }
        return resultMap;
    }

}