package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//学生编辑简历（个人信息）-android传来的dto
@Data
public class EditPersonalDto implements Serializable {
    private String telephone;
    private String exp;
    private String current_area;
}