package com.zyq.parttime.form.comment;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OneCommentDto implements Serializable {
    private int p_id;
    private String content;
//    private Date create_time;
    private String memo;
}