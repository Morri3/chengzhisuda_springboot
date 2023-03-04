package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//学生删除简历-android传来的dto
@Data
public class DeleteResumeDto implements Serializable {
    private String telephone;
}