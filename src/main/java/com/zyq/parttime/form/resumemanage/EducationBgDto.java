package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//教育背景
@Data
public class EducationBgDto implements Serializable {
    private int rd_id;
    private int r_id;
    private String time;//起止时间
    private String title;
    private String content;
    private String category;
    private int hasContent;//0表示该DTO有内容，1表示该DTO无内容
}