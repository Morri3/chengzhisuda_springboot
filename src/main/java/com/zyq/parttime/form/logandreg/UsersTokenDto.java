package com.zyq.parttime.form.logandreg;

import com.zyq.parttime.utils.Utils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UsersTokenDto implements Serializable {
    public static final String KEY_PREFIX = "Users";//缓存前缀

    private String telephone;
    private String token;
    private Date time;
    private boolean isLogin;//true表示登录状态，false表示退出状态

    //生成缓存的key
    public static String cacheKey(int id) {
        return Utils.generateCacheKey(KEY_PREFIX, String.valueOf(id));
    }

}
