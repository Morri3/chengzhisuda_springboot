package com.zyq.parttime.form.logandreg;

import lombok.Data;

import java.io.Serializable;

//学生注册-android传来的dto
@Data
public class StuRegisterDto implements Serializable {
    private String stu_name;
    private int gender;
    private String telephone;
    private String emails;
    private String pwd;
    private String pwd2;
    private int age;
    private String school_name;
    private String sno;
    private String entrance_date;
    private String graduation_date;
    private String reg_date;
}
