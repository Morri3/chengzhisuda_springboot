package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//学生删除简历详情-android传来的dto
@Data
public class DeleteDetailDto implements Serializable {
    private String telephone;
    private int rd_id;
}