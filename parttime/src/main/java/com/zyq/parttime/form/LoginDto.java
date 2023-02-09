package com.zyq.parttime.form;

import lombok.Data;

import java.io.Serializable;

//登录-vue传来的dto
@Data
public class LoginDto implements Serializable {
    private String telephone;//账号
    private String pwd;//密码
}
