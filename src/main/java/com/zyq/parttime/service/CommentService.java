package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.mark.MarkDto;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    //获取评分
    CommentDto getComment(int s_id) throws ParttimeServiceException;

    //评分
    CommentDto post(CommentPostDto commentPostDto) throws ParttimeServiceException;
}
