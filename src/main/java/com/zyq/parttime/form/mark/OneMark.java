package com.zyq.parttime.form.mark;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OneMark implements Serializable {
    //这里都是平均值
    private float total_score;
    private int pf;
    private int pl;
    private int we;
    private int lt;
    private int pt;
    private int ods;
    private int dsps;
}
