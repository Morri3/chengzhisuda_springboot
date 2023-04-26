package com.zyq.parttime.form.analyze;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnalyzeActivationDto implements Serializable {
    //柱状图数据
    private String stu_id;//学生id
    private String stu_name;//学生姓名
    private String num_name;//两个指标其中之一的名称
    private int num;//两个指标其中之一

    //备注
    private String memo;
}
