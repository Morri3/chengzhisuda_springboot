package com.zyq.parttime.form;

import lombok.Data;

import java.io.Serializable;

//登录-返回给vue的dto
@Data
public class StuInfoDto implements Serializable {
    private String telephone;//账号

}
