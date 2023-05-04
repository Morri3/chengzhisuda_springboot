//package com.zyq.parttime.ocr;
//
//import com.baidu.aip.ocr.AipOcr;
//import com.tencentcloudapi.common.Credential;
//import com.tencentcloudapi.common.exception.TencentCloudSDKException;
//import com.tencentcloudapi.common.profile.ClientProfile;
//import com.tencentcloudapi.common.profile.HttpProfile;
//import com.tencentcloudapi.ocr.v20181119.OcrClient;
//import com.tencentcloudapi.ocr.v20181119.models.*;
//import com.zyq.parttime.utils.Constant;
//
//import java.util.Base64;
//
//public class OcrTxUtils {
//    private static final Credential cred = new Credential(Constant.SID, Constant.SK);
//
//    public static GeneralFastOCRResponse ocr(String absoluteUrl, byte[] buf) {
//        try {
//            // 实例化一个http选项，可选的，没有特殊需求可以跳过
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("ocr.tencentcloudapi.com");
//
//            // 实例化一个client选项，可选的，没有特殊需求可以跳过
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//
//            // 实例化要请求产品的client对象,clientProfile是可选的
//            OcrClient client = new OcrClient(cred, "ap-shanghai", clientProfile);
//
//            // 实例化一个请求对象,每个接口都会对应一个request对象
//            GeneralFastOCRRequest req = new GeneralFastOCRRequest();
////            String imagePath = absoluteUrl;//本地图片的绝对地址
////            req.setImageUrl(imagePath);
//            String imageBase64 = Base64.getEncoder().encodeToString(buf);
//            req.setImageBase64(imageBase64);//将base64编码传进去
//
//            // 返回检测到的实例
//            GeneralFastOCRResponse resp = client.GeneralFastOCR(req);
//            return resp;
//        } catch (TencentCloudSDKException e) {
//            System.out.println(e.toString());
//        }
//        return null;
//    }
//
//}
