package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//编辑教育背景-返回给android的dto
@Data
public class GetEducationDto implements Serializable {
    private String telephone;
    private int rd_id;
    private String title;
    private String content;
    private Date start_time;
    private Date end_time;
    private String memo;
}