package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//专业技能
@Data
public class ProfessionalSkillDto implements Serializable {
    private int r_id;
    private String time;//起止时间
    private String title;
    private String content;
    private String category;
    private int hasContent;//0表示该DTO有内容，1表示该DTO无内容
}