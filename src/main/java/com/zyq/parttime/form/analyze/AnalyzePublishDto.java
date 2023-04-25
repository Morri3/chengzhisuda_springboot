package com.zyq.parttime.form.analyze;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AnalyzePublishDto implements Serializable {
    private String date;//日期
    private int num;//这天发布的兼职数
    private String memo;
}
