package com.zyq.parttime.controller;

import com.zyq.parttime.entity.Comment;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.comment.OneCommentDto;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.mark.MarkPostDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.CommentService;
import com.zyq.parttime.service.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    //TODO 获取指定报名的评论-学生
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getComment(@RequestParam int s_id) {
        CommentDto res = commentService.getComment(s_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 评论-学生
    @RequestMapping(value = "/stu/post", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData post(@RequestBody CommentPostDto commentPostDto) {
        CommentDto res = commentService.post(commentPostDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取某一兼职的所有学生的评论（前3条）-学生
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getCommentThree(@RequestParam int p_id) {
        OneCommentDto res = commentService.getCommentThree(p_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }
}