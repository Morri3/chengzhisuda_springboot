package com.zyq.parttime.service.impl;

import cn.hutool.crypto.asymmetric.Sign;
import com.zyq.parttime.entity.Comment;
import com.zyq.parttime.entity.Mark;
import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.comment.OneCommentDto;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.mark.MarkPostDto;
import com.zyq.parttime.repository.comment.CommentRepository;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.service.CommentService;
import com.zyq.parttime.service.MarkService;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public OneCommentDto getCommentThree(int p_id) throws ParttimeServiceException {
        OneCommentDto res = new OneCommentDto();
        List<CommentDto> list = new ArrayList<>();//存放该兼职的所有评论

        //1.signup中查找该p_id的所有signup
        List<Signup> signups = signupRepository.getAllSignupByPId(p_id);
        if (signups != null && signups.size() > 0) {
            boolean flag = false;//是否有评论

            //遍历signups，找到s_id
            for (Signup signup : signups) {
                System.out.println("当前遍历的s_id：" + signup.getId());

                //2.由s_id找到对应的comment，若有就加入，没有就跳过
                Comment comment = commentRepository.getComment(signup.getId());
                if (comment != null) {
                    //有该评论
                    flag = true;

                    CommentDto dto = new CommentDto();
                    dto.setS_id(signup.getId());
                    dto.setC_id(comment.getId());
                    dto.setContent(comment.getContent());
                    dto.setCreate_time(comment.getCreateTime());
                    dto.setMemo("获取成功");
                    //加入到res
                    list.add(dto);
                } else {
                    //没有该报名的评论，跳过
                }
            }

            if (flag == true) {
                //有评论，取前3个

                if (list.size() > 3) {
                    String str = "";
                    for (int i = 0; i < 2; i++) {
                        str += "No" + (i + 1) + ": " + list.get(i).getContent() + "\n";
                    }
                    str += "No3: " + list.get(2).getContent();
                    res.setContent(str);//set到res中
//                    //取前3个
//                    for (int i = 3; i < res.size(); i++) {
//                        res.remove(i);//移除第i个
//                    }
                } else if (list.size() > 0 && list.size() <= 3) {
                    //不做操作，保留这3个
                    String str = "";
                    for (int i = 0; i < list.size() - 1; i++) {
                        str += "No" + (i + 1) + ": " + list.get(i).getContent() + "\n";
                    }
                    str += "No" + list.size() + ": " + list.get(list.size() - 1).getContent();
                    res.setContent(str);//set到res中
                }
            } else {
                //无评论
                logger.warn("暂无评论");
                res.setP_id(p_id);
                res.setMemo("暂无评论");
            }
        } else {
            //没人报名过该兼职，返回空的
//            CommentDto dto = new CommentDto();
//            dto.setS_id(0);
//            dto.setC_id(0);
//            dto.setMemo("暂无评论");
//            res.add(dto);
            logger.warn("暂无评论");
            res.setP_id(p_id);
            res.setMemo("暂无评论");
        }
//        List<Comment> list = commentRepository.getCommentByPId(p_id);
//        if (list != null && list.size() > 0) {
//            for (Comment comment : list) {
//                CommentDto dto = new CommentDto();
//                dto.setC_id(comment.getId());
//                dto.setContent(comment.getContent());
//                dto.setCreate_time(comment.getCreateTime());
//                dto.setS_id(comment.getS().getId());
//                dto.setMemo("获取成功");
//                res.add(dto);
//            }
//        } else {
//            CommentDto dto = new CommentDto();
//            dto.setC_id(0);
//            dto.setS_id(0);
//            dto.setMemo("获取失败");
//            res.add(dto);
//        }
        System.out.println("兼职" + p_id + "的评论数据为：" + res.getContent());

        return res;
    }
}
