package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;

@Data
public class EditInputDto implements Serializable {
    private String op_id;//操作员
    private int p_id;//根据兼职id找兼职
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
    private String update_time;//更新时间，发布那里是创建时间
    private int num_total;//名额数
}
