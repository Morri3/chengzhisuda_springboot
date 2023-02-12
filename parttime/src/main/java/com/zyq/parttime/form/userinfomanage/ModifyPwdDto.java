package com.zyq.parttime.form.userinfomanage;

import lombok.Data;

import java.io.Serializable;

//修改密码-android传来的dto
@Data
public class ModifyPwdDto implements Serializable {
    private String telephone;
    private String old_pwd;
    private String new_pwd;
    private String new_pwd2;
}
