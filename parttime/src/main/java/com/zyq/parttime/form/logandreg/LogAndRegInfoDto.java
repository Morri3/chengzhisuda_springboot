package com.zyq.parttime.form.logandreg;

import lombok.Data;

import java.io.Serializable;

//登录-返回给vue/android的dto
@Data
public class LogAndRegInfoDto implements Serializable {
    private String telephone;//账号
    private String token;//token信息
    private String memo;//备注
}
