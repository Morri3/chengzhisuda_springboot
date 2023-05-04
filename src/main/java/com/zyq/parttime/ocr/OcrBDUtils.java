package com.zyq.parttime.ocr;

import com.baidu.aip.ocr.AipOcr;
import com.zyq.parttime.utils.Constant;
import org.json.JSONObject;

import java.util.HashMap;

public class OcrBDUtils {
    //获取百度云OCR的客户端
    private static final AipOcr client = new AipOcr(Constant.APP_ID, Constant.API_KEY, Constant.SECRET_KEY);

    //连接超时时间
    static {
        client.setConnectionTimeoutInMillis(3000);
        client.setSocketTimeoutInMillis(50000);
    }

    //识别高精度
    public static JSONObject recognizeAccurate(byte[] imageBase64) {
        //请求参数
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG");//语言类型：中文+英语
        options.put("detect_direction", "true");//检测图片方向
        options.put("detect_language", "true");//检测语言
        options.put("probability", "true");

        //调API，通用文字识别（高精度）
        JSONObject result = client.basicAccurateGeneral(imageBase64, options);

        return result;
    }

    //识别高精度带位置
    public static JSONObject recognizeAccurateWithArea(byte[] imageBase64) {
        //请求参数
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG");//语言类型：中文+英语
        options.put("detect_direction", "true");//检测图片方向
        options.put("detect_language", "true");//检测语言
        options.put("probability", "true");

        //调API，通用文字识别（高精度带位置）
        JSONObject result = client.accurateGeneral(imageBase64, options);

        return result;
    }

    //识别基础
    public static JSONObject recognizeBasic(byte[] imageBase64) {
        //请求参数
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG");//语言类型：中文+英语
        options.put("detect_direction", "true");//检测图片方向
        options.put("detect_language", "true");//检测语言
        options.put("probability", "true");

        //调API，通用文字识别（基础）
        JSONObject result = client.basicGeneral(imageBase64, options);

        return result;
    }

    //识别网络图片
    public static JSONObject recognizeWeb(byte[] imageBase64) {
        //请求参数
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG");//语言类型：中文+英语
        options.put("detect_direction", "true");//检测图片方向
        options.put("detect_language", "true");//检测语言
        options.put("probability", "true");

        //调API，网络图片
        JSONObject result = client.webImage(imageBase64, options);

        return result;
    }

    //识别手写
    public static JSONObject recognizeWrite(byte[] imageBase64) {
        //请求参数
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG");//语言类型：中文+英语
        options.put("detect_direction", "true");//检测图片方向
        options.put("detect_language", "true");//检测语言
        options.put("probability", "true");

        //调API，网络图片
        JSONObject result = client.handwriting(imageBase64, options);

        return result;
    }

}
