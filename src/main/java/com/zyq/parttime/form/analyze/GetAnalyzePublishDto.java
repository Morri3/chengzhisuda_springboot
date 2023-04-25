package com.zyq.parttime.form.analyze;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class GetAnalyzePublishDto implements Serializable {
    private Date date;//日期
    private int num;//这天发布的兼职数
}
