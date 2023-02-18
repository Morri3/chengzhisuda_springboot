package com.zyq.parttime.form.userinfomanage;

import lombok.Data;

import java.io.Serializable;

//学生个人信息-返回给android的dto
@Data
public class StuInfoDto implements Serializable {
    private String stu_name;
    private int gender;
    private String emails;
    private int age;
    private String telephone;
    private String school_name;
    private String sno;
    private String grade;
    private String memo;//备注
}
