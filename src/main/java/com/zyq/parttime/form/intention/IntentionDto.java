package com.zyq.parttime.form.intention;

import lombok.Data;

import java.io.Serializable;

@Data
public class IntentionDto implements Serializable {
    private int i_id;
    private String stu_id;
    private String content;//意向兼职的名称
    private String memo;
}
