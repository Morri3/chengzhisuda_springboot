package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//学生编辑简历（专业技能）-android传来的dto
@Data
public class EditSkillsDto implements Serializable {
    private String telephone;
    private int rd_id;
    private String content;
}