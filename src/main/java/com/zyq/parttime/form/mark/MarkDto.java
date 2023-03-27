package com.zyq.parttime.form.mark;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MarkDto implements Serializable {
    private int m_id;
    private int s_id;
    private int total_score;
    private int pf;
    private int pl;
    private int we;
    private int lt;
    private int pt;
    private int ods;
    private int dsps;
    private Date create_time;
    private String memo;
}
