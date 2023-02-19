package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//学生简历详细信息-返回给android的dto
@Data
public class ResumeDetailDto implements Serializable {
    private String telephone;
    private int r_id;
    private String title;
    private String content;
    private Date start_time;
    private Date end_time;
    private String category;
    private String memo;//若不存在简历，该字段为”请填写简历“
}