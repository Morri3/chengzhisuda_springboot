package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//学生简历详细信息
@Data
public class ResumeDetailDto implements Serializable {
    private String telephone;
    private int rd_id;
    private int r_id;
    private String time;//起止时间
    private String title;
    private String content;
    private String category;
    private int hasContent;//0表示该DTO有内容，1表示该DTO无内容
    private String memo;//若不存在简历，该字段为”请填写简历“
}