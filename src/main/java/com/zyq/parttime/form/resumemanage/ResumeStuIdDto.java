package com.zyq.parttime.form.resumemanage;

import com.zyq.parttime.utils.Utils;

import java.io.Serializable;
import java.util.Date;

public class ResumeStuIdDto implements Serializable {
    public static final String KEY_PREFIX = "Screenshot";//缓存前缀

    private long id;
    private String telephone;

    //生成缓存的key
    public static String cacheKey(long id) {
        return Utils.generateCacheKey(KEY_PREFIX, String.valueOf(id));
    }
}
