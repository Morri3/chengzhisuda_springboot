package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//简历上传返回给android的信息
@Data
public class ResumeUploadCallbackDto implements Serializable {
    private String telephone;
    private String current_area;
    private String exp;
    private Date upload_time;
    private String intended;//求职意向
    private List<ResumeDetailDto> campusExpList;
    private List<ResumeDetailDto> educationBgList;
    private List<ResumeDetailDto> projectExpList;
    private List<ResumeDetailDto> professionalSkillList;
    private String memo;//若不存在简历，该字段为”请填写简历“
    private String pic_url;//简历图片的url
}