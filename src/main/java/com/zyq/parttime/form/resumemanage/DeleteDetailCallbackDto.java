package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//学生删除简历详情-返回给android的dto
@Data
public class DeleteDetailCallbackDto implements Serializable {
    private String telephone;
    private int rd_id;
    private String memo;
}