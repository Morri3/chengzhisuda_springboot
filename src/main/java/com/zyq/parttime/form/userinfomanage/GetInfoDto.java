package com.zyq.parttime.form.userinfomanage;

import lombok.Data;

import java.io.Serializable;

//学生个人信息查看-android传来的dto
@Data
public class GetInfoDto implements Serializable {
    private String telephone;
}
