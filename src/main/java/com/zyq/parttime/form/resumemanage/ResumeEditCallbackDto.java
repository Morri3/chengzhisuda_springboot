package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//编辑简历（个人信息）-返回给android的dto
@Data
public class ResumeEditCallbackDto implements Serializable {
    private String telephone;
    private ResumeInfoDto info;
    private String memo;
}