package com.zyq.parttime.form.userinfomanage;

import lombok.Data;

import java.io.Serializable;

@Data
public class EditEmpInfoDto implements Serializable {
    private String telephone;
    private int gender;
    private int age;
    private String emails;
    private String unit_descriptions;
    private String unit_loc;
}