package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AddDetailDto implements Serializable {
    private String telephone;
    private int r_id;//简历id
    private String date;//start_time、end_time传进来是用-连接的字符串，后端处理转Date
    private String title;
    private String content;
    private String category;
}
