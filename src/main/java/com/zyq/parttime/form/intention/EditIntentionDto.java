package com.zyq.parttime.form.intention;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EditIntentionDto implements Serializable {
    private String telephone;
    private String[] intentions;
}
