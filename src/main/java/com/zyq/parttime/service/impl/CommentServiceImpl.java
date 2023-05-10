package com.zyq.parttime.service.impl;

import cn.hutool.crypto.asymmetric.Sign;
import com.zyq.parttime.entity.*;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.comment.CommentToEmpDto;
import com.zyq.parttime.form.comment.OneCommentDto;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.mark.MarkPostDto;
import com.zyq.parttime.repository.comment.CommentRepository;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.repository.userinfomanage.EmpInfoRepository;
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
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private EmpInfoRepository empInfoRepository;

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

    //TODO 评论-学生
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

    //TODO 获取某一兼职的所有评论，并取出前3条-学生
    @Override
    public OneCommentDto getCommentThree(int p_id) throws ParttimeServiceException {
        //1.结果是前3个评论组成字符串
        OneCommentDto res = new OneCommentDto();

        //存放该兼职的所有评论
        List<CommentDto> list = new ArrayList<>();

        //2.signup中查找该p_id的所有报名
        List<Signup> signups = signupRepository.getAllSignupByPId(p_id);
        if (signups != null && signups.size() > 0) {
            boolean flag = false;//用于判断是否有评论

            //3.遍历所有报名，找到s_id
            for (Signup signup : signups) {
                System.out.println("当前遍历的s_id：" + signup.getId());

                //4.由s_id找到对应的comment，若有就加入，没有就跳过
                Comment comment = commentRepository.getComment(signup.getId());
                if (comment != null) {
                    //4-1.有该评论
                    flag = true;

                    //5.构造dto，加入到res
                    CommentDto dto = new CommentDto();
                    dto.setS_id(signup.getId());
                    dto.setC_id(comment.getId());
                    dto.setContent(comment.getContent());
                    dto.setCreate_time(comment.getCreateTime());
                    dto.setMemo("获取成功");
                    list.add(dto);
                } else {
                    //4-2.没有该报名的评论，跳过
                }
            }

            if (flag == true) {
                //6.有评论，取前3条评论，拼接成字符串
                if (list.size() > 3) {
                    //6-1.评论条数＞3，要取前3条
                    String str = "";
                    for (int i = 0; i < 2; i++) {
                        str += "No" + (i + 1) + ": " + list.get(i).getContent() + "\n";
                    }
                    str += "No3: " + list.get(2).getContent();

                    //7.set到res中
                    res.setContent(str);
                } else if (list.size() > 0 && list.size() <= 3) {
                    //6-2.评论条数≤3，保留这3条
                    String str = "";
                    for (int i = 0; i < list.size() - 1; i++) {
                        str += "No" + (i + 1) + ": " + list.get(i).getContent() + "\n";
                    }
                    str += "No" + list.size() + ": " + list.get(list.size() - 1).getContent();

                    //7.set到res中
                    res.setContent(str);
                }
            } else {
                //无评论
                logger.warn("暂无评论");
                res.setP_id(p_id);
                res.setMemo("暂无评论");
            }
        } else {
            //没人报名过该兼职，返回空的
            logger.warn("暂无评论");
            res.setP_id(p_id);
            res.setMemo("暂无评论");
        }
        System.out.println("兼职" + p_id + "的评论数据为：" + res.getContent());

        return res;
    }

    //TODO 获取自己负责的所有兼职的所有评论记录-兼职发布者
    @Override
    public List<CommentToEmpDto> getAllSpecialComment(String emp_id) throws ParttimeServiceException {
        List<CommentToEmpDto> res = new ArrayList<>();

        if (emp_id != null && !emp_id.equals("")) {
            //1.有输入，根据emp_id找到管理的所有兼职
            List<Parttimes> parttimes = positionRepository.getAllPositionManagedByEmp(emp_id);
            if (parttimes.size() > 0) {

                //2.有负责的兼职,遍历每个兼职
                for (Parttimes item : parttimes) {

                    //3.遍历每个兼职，找到报名该兼职的signup
                    List<Signup> signups = signupRepository.getAllSignupByPId(item.getId());
                    if (signups.size() > 0) {

                        //4.存在报名，遍历报名
                        for (Signup item2 : signups) {
                            //5.找到该signup的comment记录
                            Comment comment = commentRepository.getComment(item2.getId());
                            if (comment != null) {

                                //6.存在该评论记录，构造dto，加入res
                                CommentToEmpDto commentDto = new CommentToEmpDto();
                                commentDto.setC_id(comment.getId());
                                commentDto.setS_id(item2.getId());
                                commentDto.setContent(comment.getContent());
                                commentDto.setCreate_time(comment.getCreateTime());
                                commentDto.setMemo("获取成功");
                                commentDto.setP_name(item.getPositionName());
                                commentDto.setCategory(item.getCategory());
                                commentDto.setUser_id(item2.getStu().getId());
                                commentDto.setUsername(item2.getStu().getStuName());
                                res.add(commentDto);
                            } else {
                                logger.warn("该报名尚未评论");
                                CommentToEmpDto dto = new CommentToEmpDto();
                                dto.setC_id(0);
                                dto.setS_id(item2.getId());
                                dto.setMemo("该报名尚未评论");
                                res.add(dto);
                            }
                        }
                    } else {
                        logger.warn("该兼职尚未报名");
                        CommentToEmpDto dto = new CommentToEmpDto();
                        dto.setC_id(0);
                        dto.setS_id(0);
                        dto.setMemo("该兼职尚未报名");
                        res.add(dto);
                    }
                }
            } else {
                logger.warn("暂无负责的兼职");
                CommentToEmpDto dto = new CommentToEmpDto();
                dto.setC_id(0);
                dto.setS_id(0);
                dto.setMemo("暂无负责的兼职");
                res.add(dto);
            }
        } else {
            logger.warn("请检出输入");
            CommentToEmpDto dto = new CommentToEmpDto();
            dto.setC_id(0);
            dto.setS_id(0);
            dto.setMemo("请检出输入");
            res.add(dto);
        }

        return res;
    }

    //TODO 获取所有兼职的所有评论记录-管理员
    @Override
    public List<CommentToEmpDto> getAllSpecialCommentByAdmin(String emp_id) throws ParttimeServiceException {
        List<CommentToEmpDto> res = new ArrayList<>();

        if (emp_id != null && !emp_id.equals("")) {
            //有输入

            //1.找到该emp，判断是否是管理员
            Employer emp = empInfoRepository.findEmployerByTelephone(emp_id);

            if (emp != null && emp.getEmpGrade() == 1) {
                //2.存在该用户且是管理员，根据emp_id找到管理的所有兼职
                List<Parttimes> parttimes = positionRepository.getAllPositions();
                if (parttimes.size() > 0) {

                    //3.有负责的兼职,遍历每个兼职
                    for (Parttimes item : parttimes) {

                        //4.遍历每个兼职，找到报名该兼职的signup
                        List<Signup> signups = signupRepository.getAllSignupByPId(item.getId());
                        if (signups.size() > 0) {
                            //5.存在报名，遍历报名

                            for (Signup item2 : signups) {
                                //6.找到该signup的comment记录
                                Comment comment = commentRepository.getComment(item2.getId());
                                if (comment != null) {
                                    //7.存在该评论记录，构造dto，加入res
                                    CommentToEmpDto commentDto = new CommentToEmpDto();
                                    commentDto.setC_id(comment.getId());
                                    commentDto.setS_id(item2.getId());
                                    commentDto.setContent(comment.getContent());
                                    commentDto.setCreate_time(comment.getCreateTime());
                                    commentDto.setMemo("获取成功");
                                    commentDto.setP_name(item.getPositionName());
                                    commentDto.setCategory(item.getCategory());
                                    commentDto.setUser_id(item2.getStu().getId());
                                    commentDto.setUsername(item2.getStu().getStuName());
                                    res.add(commentDto);
                                } else {
                                    logger.warn("该报名尚未评论");
                                    CommentToEmpDto dto = new CommentToEmpDto();
                                    dto.setC_id(0);
                                    dto.setS_id(item2.getId());
                                    dto.setMemo("该报名尚未评论");
                                    res.add(dto);
                                }
                            }
                        } else {
                            logger.warn("该兼职尚未报名");
                            CommentToEmpDto dto = new CommentToEmpDto();
                            dto.setC_id(0);
                            dto.setS_id(0);
                            dto.setMemo("该兼职尚未报名");
                            res.add(dto);
                        }
                    }
                } else {
                    logger.warn("暂无负责的兼职");
                    CommentToEmpDto dto = new CommentToEmpDto();
                    dto.setC_id(0);
                    dto.setS_id(0);
                    dto.setMemo("暂无负责的兼职");
                    res.add(dto);
                }
            } else {
                logger.warn("不存在该管理员");
                CommentToEmpDto dto = new CommentToEmpDto();
                dto.setC_id(0);
                dto.setS_id(0);
                dto.setMemo("不存在该管理员");
                res.add(dto);
            }
        } else {
            logger.warn("请检出输入");
            CommentToEmpDto dto = new CommentToEmpDto();
            dto.setC_id(0);
            dto.setS_id(0);
            dto.setMemo("请检出输入");
            res.add(dto);
        }

        return res;
    }

    //TODO 获取某一兼职的所有评论-兼职发布者/管理员
    @Override
    public List<CommentToEmpDto> getCommentThreeByEmp(int p_id, String emp_id) throws ParttimeServiceException {
        List<CommentToEmpDto> res = new ArrayList<>();

        //1.判断是否有输入
        if (emp_id != null && !emp_id.equals("")) {
            if (p_id > 0) {
                //有输入

                //2.管理员、兼职发布者分类讨论
                Employer emp = empInfoRepository.findEmployerByTelephone(emp_id);
                if (emp != null && emp.getEmpGrade() == 1) {
                    //2-1.是管理员，可以查看某一兼职的全部评论数据
                    Parttimes parttimes = positionRepository.getPosition(p_id);

                    //3.有负责的兼职
                    if (parttimes != null) {

                        //4.找到报名该兼职的所有signup
                        List<Signup> signups = signupRepository.getAllSignupByPId(parttimes.getId());
                        if (signups.size() > 0) {
                            //5.存在报名

                            for (Signup item : signups) {
                                //6.找到该signup的comment记录
                                Comment comment = commentRepository.getComment(item.getId());
                                if (comment != null) {
                                    //7.存在该评论记录，构造dto，加入res
                                    CommentToEmpDto commentDto = new CommentToEmpDto();
                                    commentDto.setC_id(comment.getId());
                                    commentDto.setS_id(item.getId());
                                    commentDto.setContent(comment.getContent());
                                    commentDto.setCreate_time(comment.getCreateTime());
                                    commentDto.setP_name(parttimes.getPositionName());
                                    commentDto.setUser_id(item.getStu().getId());
                                    commentDto.setUsername(item.getStu().getStuName());
                                    commentDto.setMemo("获取成功");
                                    res.add(commentDto);
                                } else {
                                    logger.warn("该报名尚未评论");
                                    CommentToEmpDto dto = new CommentToEmpDto();
                                    dto.setC_id(0);
                                    dto.setS_id(item.getId());
                                    dto.setMemo("该报名尚未评论");
                                    res.add(dto);
                                }
                            }
                        } else {
                            logger.warn("该兼职尚未报名");
                            CommentToEmpDto dto = new CommentToEmpDto();
                            dto.setC_id(0);
                            dto.setS_id(0);
                            dto.setMemo("该兼职尚未报名");
                            res.add(dto);
                        }
                    } else {
                        logger.warn("暂无负责的兼职");
                        CommentToEmpDto dto = new CommentToEmpDto();
                        dto.setC_id(0);
                        dto.setS_id(0);
                        dto.setMemo("暂无负责的兼职");
                        res.add(dto);
                    }
                } else if (emp != null && emp.getEmpGrade() == 0) {
                    //2-2.是兼职发布者，只有是该兼职的负责人才能查看评论数据
                    Parttimes parttimes = positionRepository.checkIsTheManager(emp_id, p_id);

                    //3.该兼职发布者是该兼职的负责人
                    if (parttimes != null) {

                        //4.找到报名该兼职的所有signup
                        List<Signup> signups = signupRepository.getAllSignupByPId(parttimes.getId());
                        if (signups.size() > 0) {
                            //5.存在报名

                            for (Signup item : signups) {
                                //6.找到该signup的comment记录
                                Comment comment = commentRepository.getComment(item.getId());
                                if (comment != null) {
                                    //7.存在该评论记录，构造dto，加入res
                                    CommentToEmpDto commentDto = new CommentToEmpDto();
                                    commentDto.setC_id(comment.getId());
                                    commentDto.setS_id(item.getId());
                                    commentDto.setContent(comment.getContent());
                                    commentDto.setCreate_time(comment.getCreateTime());
                                    commentDto.setP_name(parttimes.getPositionName());
                                    commentDto.setUser_id(item.getStu().getId());
                                    commentDto.setUsername(item.getStu().getStuName());
                                    commentDto.setMemo("获取成功");
                                    res.add(commentDto);
                                } else {
                                    logger.warn("该报名尚未评论");
                                    CommentToEmpDto dto = new CommentToEmpDto();
                                    dto.setC_id(0);
                                    dto.setS_id(item.getId());
                                    dto.setMemo("该报名尚未评论");
                                    res.add(dto);
                                }
                            }
                        } else {
                            logger.warn("该兼职尚未报名");
                            CommentToEmpDto dto = new CommentToEmpDto();
                            dto.setC_id(0);
                            dto.setS_id(0);
                            dto.setMemo("该兼职尚未报名");
                            res.add(dto);
                        }
                    } else {
                        logger.warn("暂无负责的兼职");
                        CommentToEmpDto dto = new CommentToEmpDto();
                        dto.setC_id(0);
                        dto.setS_id(0);
                        dto.setMemo("暂无负责的兼职");
                        res.add(dto);
                    }
                }
            } else {
                logger.warn("请确保输入完整");
                CommentToEmpDto dto = new CommentToEmpDto();
                dto.setC_id(0);
                dto.setS_id(0);
                dto.setMemo("请确保输入完整");
                res.add(dto);
            }
        } else {
            logger.warn("请确保输入完整");
            CommentToEmpDto dto = new CommentToEmpDto();
            dto.setC_id(0);
            dto.setS_id(0);
            dto.setMemo("请确保输入完整");
            res.add(dto);
        }

        return res;
    }
}
