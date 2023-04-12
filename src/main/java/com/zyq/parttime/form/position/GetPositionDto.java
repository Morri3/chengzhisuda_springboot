package com.zyq.parttime.form.position;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetPositionDto implements Serializable {
    private List<String> intentions;//意向兼职列表
}
