package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateResumeDto implements Serializable {
    private String telephone;
    private String upload_time;//实际上是创建时间
}
