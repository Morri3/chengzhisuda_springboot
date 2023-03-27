package com.zyq.parttime.controller;

import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.CommentService;
import com.zyq.parttime.service.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    //TODO 获取评论-学生
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getComment(@RequestParam int s_id) {
        CommentDto res = commentService.getComment(s_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

}