package com.zyq.parttime.service.impl;

import com.zyq.parttime.entity.Comment;
import com.zyq.parttime.entity.Mark;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.repository.comment.CommentRepository;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.service.CommentService;
import com.zyq.parttime.service.MarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public CommentDto getComment(int s_id) throws ParttimeServiceException {
        CommentDto res = new CommentDto();
        //数据
        Comment comment = commentRepository.getComment(s_id);
        if (comment != null) {
            res.setC_id(comment.getId());
            res.setCreate_time(comment.getCreateTime());
            res.setS_id(comment.getS().getId());
            res.setContent(comment.getContent());
        } else {
            res.setMemo("获取失败");
        }
        System.out.println(res.toString());

        return res;
    }
}
