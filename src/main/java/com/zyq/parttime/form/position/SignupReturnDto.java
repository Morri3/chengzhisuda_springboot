package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SignupReturnDto implements Serializable {
    private int s_id;
    private String stu_id;
    private int p_id;
    private String signup_status;
    private Date create_time;
    private Date update_time;
    private String memo;
}
