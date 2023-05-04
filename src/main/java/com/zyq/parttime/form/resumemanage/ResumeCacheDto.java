package com.zyq.parttime.form.resumemanage;

import com.zyq.parttime.utils.Utils;

import java.io.Serializable;
import java.util.Date;

public class ResumeCacheDto implements Serializable {
    public static final String KEY_PREFIX = "resumes";//缓存前缀

    private String telephone;
    private Date upload_time;

    //生成缓存的key
    public static String cacheKey(String id) {
        return Utils.generateCacheKey(KEY_PREFIX, id);
    }
}
