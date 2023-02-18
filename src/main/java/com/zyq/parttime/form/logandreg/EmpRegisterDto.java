package com.zyq.parttime.form.logandreg;

import lombok.Data;

import java.io.Serializable;

//兼职发布者注册-vue传来的dto
@Data
public class EmpRegisterDto implements Serializable {
    private String telephone;
    private String unit_name;
    private String pwd;
    private String pwd2;
    private String jno;
    private int gender;
    private String emails;
    private String emp_name;
    private int age;
    private String reg_date;
    private boolean emp_grade;//是兼职发布者还是管理员
}
