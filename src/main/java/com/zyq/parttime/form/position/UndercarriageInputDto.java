package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;

@Data
public class UndercarriageInputDto implements Serializable {
    private String op_id;//操作员手机号
    private int p_id;//兼职id
}
