package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//编辑项目经历-返回给android的dto
@Data
public class GetProgramDto implements Serializable {
    private String telephone;
    private int rd_id;
    private String title;
    private String content;
    private Date start_time;
    private Date end_time;
    private String memo;
}