package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;

@Data
public class CanSignupDto implements Serializable {
    private boolean flag;//是否能够报名,默认值true，可以报名
    private String memo;

}
