package com.zyq.parttime.form.comment;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommentToEmpDto implements Serializable {
    private int c_id;
    private int s_id;
    private String content;
    private Date create_time;
    private String memo;
    private String p_name;
    private String username;
    private String user_id;
}
