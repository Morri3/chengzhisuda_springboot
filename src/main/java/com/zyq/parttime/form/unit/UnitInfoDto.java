package com.zyq.parttime.form.unit;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnitInfoDto implements Serializable {
    private int u_id;
    private String unit_name;
    private String descriptions;
    private String loc;
    private int job_nums;
    private String memo;
}
