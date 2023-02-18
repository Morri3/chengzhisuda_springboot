package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//学生简历信息-返回给android的dto
@Data
public class ResumeInfoDto implements Serializable {
    private String telephone;
    private String current_area;
    private String exp;
    private Date upload_time;
    private List<CampusExpDto> campusExpList;
    private List<EducationBgDto> educationBgList;
    private List<ProjectExpDto> projectExpList;
    private List<ProfessionalSkillDto> professionalSkillList;
    private String memo;//若不存在简历，该字段为”请填写简历“
}