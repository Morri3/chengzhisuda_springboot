package com.zyq.parttime.form.comment;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommentPostDto implements Serializable {
    private int s_id;
    private String content;
    private Date create_time;
}
