package com.zyq.parttime.form.logandreg;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//登录-返回给vue/android的dto
@Data
public class LogAndRegInfoDto implements Serializable {
    private String telephone;//账号
    private String token;//token信息
    private int u_id;
    private String jno;
    private int gender;//0男 1女
    private String emails;
    private String emp_name;
    private int age;
    private Date reg_date;
    private String head;
    private int emp_grade;
    private String memo;//备注
}
