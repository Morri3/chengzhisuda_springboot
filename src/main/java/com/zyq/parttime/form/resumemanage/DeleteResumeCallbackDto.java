package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//学生删除简历-返回给android的dto
@Data
public class DeleteResumeCallbackDto implements Serializable {
    private String telephone;
    private String memo;
}