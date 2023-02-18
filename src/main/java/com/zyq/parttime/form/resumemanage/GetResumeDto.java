package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

//学生简历查看-android传来的dto
@Data
public class GetResumeDto implements Serializable {
    private String telephone;
}