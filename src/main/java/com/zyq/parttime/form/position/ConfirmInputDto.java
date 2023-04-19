package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConfirmInputDto implements Serializable {
    private String emp_id;
    private int s_id;
}
