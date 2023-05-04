package com.zyq.parttime.form.logandreg;

import com.zyq.parttime.utils.Utils;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserBirthDto implements Serializable {
    public static final String KEY_PREFIX = "Birth";//缓存前缀

    private String telephone;//账号
    private String birth_year;//年份
    private String birth_month;//月份

    //生成缓存的key
    public static String cacheKey(String telephone) {
        return Utils.generateCacheKey(KEY_PREFIX, telephone);//账号作为键，年份+月份是值
    }
}
