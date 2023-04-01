package com.zyq.parttime.service.impl;

import com.zyq.parttime.entity.Comment;
import com.zyq.parttime.entity.Mark;
import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.mark.MarkPostDto;
import com.zyq.parttime.repository.comment.CommentRepository;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.service.CommentService;
import com.zyq.parttime.service.MarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentServiceImpl implements CommentService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SignupRepository signupRepository;

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
            res.setMemo("获取成功");
        } else {
            res.setMemo("获取失败");
        }
        System.out.println(res.toString());

        return res;
    }

    @Override
    public CommentDto post(CommentPostDto commentPostDto) throws ParttimeServiceException {
        CommentDto res = new CommentDto();
        if (commentPostDto != null) {
            //获取传入参数
            int s_id = commentPostDto.getS_id();
            String content = commentPostDto.getContent();
            Date create_time = commentPostDto.getCreate_time();

            //判断报名是否存在
            Signup signup = signupRepository.findSignup(s_id);
            //判断是否有该s_id在comment中
            Comment comment = commentRepository.getComment(s_id);

            if (signup != null) {
                if (comment == null) {
                    if (signup.getSignupStatus().equals("已结束")) {
                        //存在报名（未删除），且未评论过，则可以评分
                        commentRepository.post(s_id, content, create_time);

                        //获取该comment记录
                        Comment created = commentRepository.getLatestComment();
                        if (created != null) {
                            res.setC_id(created.getId());
                            res.setS_id(created.getS().getId());
                            res.setContent(created.getContent());
                            res.setCreate_time(created.getCreateTime());
                            res.setMemo("评论成功");
                        } else {
                            logger.warn("评论失败");
                            res.setMemo("评论失败");
                        }
                    } else {
                        logger.warn("只能操作已结束状态的报名");
                        res.setMemo("只能操作已结束状态的报名");
                    }
                } else {
                    logger.warn("已评论，不能重复评分");
                    res.setMemo("已评论，不能重复评分");
                }
            } else {
                logger.warn("不存在该报名");
                res.setMemo("不存在该报名");
            }
        }
        return res;
    }
}
