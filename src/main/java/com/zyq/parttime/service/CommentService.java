package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.comment.OneCommentDto;
import com.zyq.parttime.form.mark.MarkDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    //获取评分
    CommentDto getComment(int s_id) throws ParttimeServiceException;

    //评分
    CommentDto post(CommentPostDto commentPostDto) throws ParttimeServiceException;

    //获取某一兼职的所有学生的评论（前3条）
    OneCommentDto getCommentThree(int p_id) throws ParttimeServiceException;

    //获取自己负责的所有兼职的所有评论记录
    List<CommentDto> getAllSpecialComment(String emp_id) throws ParttimeServiceException;
}
