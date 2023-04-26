package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AddDetailCallbackDto implements Serializable {
    private String telephone;
    private int rd_id;
    private int r_id;//简历id
    private Date start_time;
    private Date end_time;
    private String title;
    private String content;
    private Date create_time;
    private String category;
    private String rd_status;
    private String memo;
}
