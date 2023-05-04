package com.zyq.parttime.form.position;

import com.zyq.parttime.form.resumemanage.ResumeDetailDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class SignupInfoToEmpDto implements Serializable {
    //兼职信息部分
    private int p_id;
    private String p_name;
    private int num_signup;//报名人数
    private int num_employment;//录用人数
    private int num_total;//名额数
    private String op_id;//负责人id
    private String op_name;//负责人姓名
    private String category;//兼职种类
    //用户信息部分
    private String stu_id;
    private String username;
    private String gender;
    private String head;//头像
    private int age;
    private String grade;
    //简历部分
    private String exp;
    private String current_area;
    private String url;//照片地址
    private List<ResumeDetailDto> campusExpList;
    private List<ResumeDetailDto> educationBgList;
    private List<ResumeDetailDto> projectExpList;
    private List<ResumeDetailDto> professionalSkillList;
    //报名部分
    private int s_id;
    private String signup_status;
    private Date signup_time;//报名时间
    //备忘录
    private String memo;
}
