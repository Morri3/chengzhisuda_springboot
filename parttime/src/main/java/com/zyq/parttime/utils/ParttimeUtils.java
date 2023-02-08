package com.zyq.parttime.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ParttimeUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String generateCacheKey(String... values) {
        //子系统code
        String ret = "exam:";
        for (String v : values) {
            ret = ret + v + ":";
        }
        return ret;
    }

    /**
     * bean 转换成 json
     *
     * @param bean
     * @return
     */
    public static String beanToJson(Object bean) {
        if (bean == null) {
            return "";
        }
        try {
            return mapper.writeValueAsString(bean);
        } catch (JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
