package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PositionInfoToEmpDto implements Serializable {
    private int p_id;
    private String op_id;//操作员
    private String op_name;//操作员
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
    private int num_signup;//报名人数
    private int num_employment;//录用人数
    private int num_total;//名额数
    private String memo;
}
