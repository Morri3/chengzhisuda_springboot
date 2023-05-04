package com.zyq.parttime.form.userinfomanage;

import lombok.Data;

import java.io.Serializable;

//学生个人信息编辑-android传来的dto
@Data
public class EditInfoDto implements Serializable {
    private String telephone;
    private int gender;
    private int age;
    private String birth_year;//出生年份
    private String birth_month;//出生月份
    private String emails;
    private String entrance_date;
    private String graduation_date;
}
