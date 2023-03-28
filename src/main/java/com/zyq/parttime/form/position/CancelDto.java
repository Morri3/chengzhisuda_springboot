package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;
@Data
public class CancelDto implements Serializable {
    private String telephone;
    private int s_id;
}
