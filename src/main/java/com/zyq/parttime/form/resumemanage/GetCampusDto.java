package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//编辑校园经历-返回给android的dto
@Data
public class GetCampusDto implements Serializable {
    private String telephone;
    private int rd_id;
    private String title;
    private String content;
    private Date start_time;
    private Date end_time;
    private String memo;
    private String rd_status;
//    private String category;
//    private Date create_time;
}