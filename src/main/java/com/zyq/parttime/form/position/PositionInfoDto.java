package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PositionInfoDto implements Serializable {
    private int p_id;
    private String op_id;//操作员
    private String position_name;
    private String category;
    private String salary;
    private String area;
    private String exp;
    private String content;
    private String requirement;
    private Date signup_ddl;
    private String slogan;
    private String work_time;
    private String settlement;
    private String position_status;
    private Date create_time;
    private Date update_time;
    private String memo;
}
