package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PublishInputDto implements Serializable {
    private String op_id;//操作员
    private String position_name;
    private String category;
    private String salary;
    private String area;
    private String exp;
    private String content;
    private String requirement;
    private String signup_ddl;//传进来String，后端转Date
    private String slogan;
    private String work_time;
    private String settlement;
    private String create_time;
    private int num_total;//名额数
}
