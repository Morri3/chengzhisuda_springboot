package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//学生编辑简历（校园经历）-android传来的dto
@Data
public class EditCampusDto implements Serializable {
    private String telephone;
    private int rd_id;
    private String title;
    private String content;
    private String start_time;
    private String end_time;
}