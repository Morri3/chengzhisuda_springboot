package com.zyq.parttime.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    @Value("${minio.urlprefix}")
    public String urlprefix;

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
        try {
            //是否存在这个桶
            this.existBucket(bucketName);
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //新的文件名 = 存储桶文件名_时间戳.后缀名
            assert originalFilename != null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = bucketName + "_" + System.currentTimeMillis() + "_" +
                    format.format(new Date()) + "_" + new Random().nextInt(1000) +
                    originalFilename.substring(originalFilename.lastIndexOf("."));
            //开始上传
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType()).build());
            return file.getName() + "上传成功！url：" + endpoint + "/" + bucketName + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return file.getName() + "上传失败!";
        }
    }

    //下载文件
    public void downloadFile(@RequestParam(name = "fileName") String fileName,
                             @RequestParam(defaultValue = "false") Boolean delete,
                             HttpServletResponse response) {

//        InputStream inputStream = null;
//        OutputStream outputStream = null;
//        try {
//            if (StringUtils.isBlank(fileName)) {
//                response.setHeader("Content-type", "text/html;charset=UTF-8");
//                String data = "文件下载失败";
//                OutputStream ps = response.getOutputStream();
//                ps.write(data.getBytes("UTF-8"));
//                return;
//            }
//
//            outputStream = response.getOutputStream();
//            // 获取文件对象
//            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(this.bucketName).object(fileName).build());
//            byte buf[] = new byte[1024];
//            int length = 0;
//            response.reset();
//            response.setHeader("Content-Disposition", "attachment;filename=" +
//                    URLEncoder.encode(fileName.substring(fileName.lastIndexOf("/") + 1), "UTF-8"));
//            response.setContentType("application/octet-stream");
//            response.setCharacterEncoding("UTF-8");
//            // 输出文件
//            while ((length = inputStream.read(buf)) > 0) {
//                outputStream.write(buf, 0, length);
//            }
//            inputStream.close();
//            // 判断：下载后是否同时删除minio上的存储文件
//            if (BooleanUtils.isTrue(delete)) {
//                minioClient.removeObject(RemoveObjectArgs.builder().bucket(this.bucketName).object(fileName).build());
//            }
//        } catch (Throwable ex) {
//            response.setHeader("Content-type", "text/html;charset=UTF-8");
//            String data = "文件下载失败";
//            try {
//                OutputStream ps = response.getOutputStream();
//                ps.write(data.getBytes("UTF-8"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } finally {
//            try {
//                outputStream.close();
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

//
//    /**
//     * 查看文件对象
//     * @param bucketName 存储bucket名称
//     * @return 存储bucket内文件对象信息
//     */
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