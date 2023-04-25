package com.zyq.parttime.form.analyze;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AnalyzeThreeIndicatorsDto implements Serializable {
    private int p_id;//兼职id
    private String p_name;//兼职名称
    private String emp_id;//兼职的发布者id
    private String emp_name;//兼职的发布者name
    private String create_time;//兼职发布时间,字符串型
//    private int num_employment;//报名数
//    private int num_signup;//录取数
//    private int num_total;//名额
    private String num_name;//三个数其中之一
    private int num;//三个数其中之一
    private String memo;
}
