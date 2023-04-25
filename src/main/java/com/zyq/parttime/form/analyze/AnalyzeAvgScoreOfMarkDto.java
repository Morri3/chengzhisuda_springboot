package com.zyq.parttime.form.analyze;

import lombok.Data;

import java.io.Serializable;
@Data
public class AnalyzeAvgScoreOfMarkDto implements Serializable {
    private int p_id;
    private String p_name;
    private float avg_total;
    private String memo;
}
