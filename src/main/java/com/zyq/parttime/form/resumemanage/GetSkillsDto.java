package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//编辑专业技能-返回给android的dto
@Data
public class GetSkillsDto implements Serializable {
    private String telephone;
    private int rd_id;
    private String content;
    private String memo;
}