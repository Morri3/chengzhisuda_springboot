package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;
@Data
public class CancelReturnDto implements Serializable {
    private String stu_id;
    private String memo;
}
