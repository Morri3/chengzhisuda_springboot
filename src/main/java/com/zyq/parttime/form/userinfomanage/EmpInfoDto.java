package com.zyq.parttime.form.userinfomanage;

import lombok.Data;

import java.io.Serializable;

//兼职发布者/管理员个人信息-返回给vue的dto
@Data
public class EmpInfoDto implements Serializable {
    private String emp_name;
    private int gender;
    private String emails;
    private int age;
    private String telephone;
    private String jno;
    private String unit_name;
    private String unit_descriptions;
    private String unit_loc;
    private int job_nums;
    private int emp_grade;//是否是管理员
    private String head;//头像
    private String memo;//备注
}
