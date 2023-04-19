package com.zyq.parttime.service.impl;

import com.alibaba.fastjson.JSON;
import com.zyq.parttime.entity.Mark;
import com.zyq.parttime.entity.Parttimes;
import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.mark.MarkPostDto;
import com.zyq.parttime.form.mark.OneMark;
import com.zyq.parttime.form.mark.OneMarkDto;
import com.zyq.parttime.form.position.SignupInfoToEmpDto;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.service.MarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MarkServiceImpl implements MarkService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private MarkRepository markRepository;
    @Autowired
    private SignupRepository signupRepository;
    @Autowired
    private PositionRepository positionRepository;

    @Override
    public MarkDto getMark(int s_id) throws ParttimeServiceException {
        MarkDto res = new MarkDto();
        //数据
        Mark mark = markRepository.getMark(s_id);
        if (mark != null) {
            res.setM_id(mark.getId());
            res.setDsps(mark.getDsps());
            res.setCreate_time(mark.getCreateTime());
            res.setLt(mark.getLt());
            res.setOds(mark.getOds());
            res.setPf(mark.getPf());
            res.setPl(mark.getPl());
            res.setPt(mark.getPt());
            res.setS_id(mark.getS().getId());
            res.setTotal_score(mark.getTotalScore());
            res.setWe(mark.getWe());
            res.setMemo("获取成功");
        } else {
            logger.warn("获取失败");
            res.setMemo("获取失败");
        }
        System.out.println(res.toString());

        return res;
    }

    @Override
    public OneMarkDto getMarkAll(int p_id) throws ParttimeServiceException {
        OneMarkDto res = new OneMarkDto();
        //数据
        Map<String, Object> map = markRepository.getMarkByPId(p_id);
        OneMark mark = JSON.parseObject(JSON.toJSONString(map), OneMark.class);//map转dto
        //该岗位的第一个mark的时间作为该岗位所有mark的时间
        Date create_time = markRepository.getMarkDateByPId(p_id);
        if (mark != null) {
            res.setP_id(p_id);
            res.setDsps(mark.getDsps());
            res.setCreate_time(create_time);
            res.setLt(mark.getLt());
            res.setOds(mark.getOds());
            res.setPf(mark.getPf());
            res.setPl(mark.getPl());
            res.setPt(mark.getPt());
            res.setTotal_score(mark.getTotal_score());
            res.setWe(mark.getWe());
            res.setMemo("获取成功");
        } else {
            logger.warn("获取失败");
            res.setMemo("获取失败");
        }
        System.out.println(res.toString());

        return res;
    }

    @Override
    public MarkDto post(MarkPostDto markPostDto) throws ParttimeServiceException {
        MarkDto res = new MarkDto();
        if (markPostDto != null) {
            //获取传入参数
            int s_id = markPostDto.getS_id();
            int pf = markPostDto.getPf();
            int pl = markPostDto.getPl();
            int we = markPostDto.getWe();
            int lt = markPostDto.getLt();
            int pt = markPostDto.getPt();
            int ods = markPostDto.getOds();
            int dsps = markPostDto.getDsps();
            Date create_time = markPostDto.getCreate_time();
            float total_score = (pf + pl + we + lt + pt + ods + dsps) / 7;

            //判断报名是否存在
            Signup signup = signupRepository.findSignup(s_id);
            //判断是否有该s_id在mark中
            Mark mark = markRepository.getMark(s_id);

            if (signup != null) {
                if (mark == null) {
                    if (signup.getSignupStatus().equals("已结束")) {
                        //存在报名（未删除），且未评分过，则可以评分
                        markRepository.post(s_id, total_score, pf, pl, we, lt, pt, ods, dsps, create_time);

                        //获取该mark记录
                        Mark created = markRepository.getLatestMark();
                        if (created != null) {
                            res.setM_id(created.getId());
                            res.setS_id(created.getS().getId());
                            res.setTotal_score(created.getTotalScore());
                            res.setPt(created.getPt());
                            res.setWe(created.getWe());
                            res.setPl(created.getPl());
                            res.setPf(created.getPf());
                            res.setOds(created.getOds());
                            res.setLt(created.getLt());
                            res.setCreate_time(created.getCreateTime());
                            res.setDsps(created.getDsps());
                            res.setMemo("评分成功");
                        } else {
                            logger.warn("评分失败");
                            res.setMemo("评分失败");
                        }
                    } else {
                        logger.warn("只能操作已结束状态的报名");
                        res.setMemo("只能操作已结束状态的报名");
                    }
                } else {
                    logger.warn("已评分，不能重复评分");
                    res.setMemo("已评分，不能重复评分");
                }
            } else {
                logger.warn("不存在该报名");
                res.setMemo("不存在该报名");
            }
        }
        return res;
    }

    @Override
    public List<MarkDto> getAllSpecialMark(String emp_id) throws ParttimeServiceException {
        List<MarkDto> res = new ArrayList<>();

        if (emp_id != null && !emp_id.equals("")) {
            //有输入,根据emp_id找到管理的所有兼职
            List<Parttimes> parttimes = positionRepository.getAllPositionManagedByEmp(emp_id);
            if (parttimes.size() > 0) {

                //有负责的兼职,遍历每个兼职
                for (Parttimes item : parttimes) {

                    //遍历每个兼职，找到报名该兼职的signup
                    List<Signup> signups = signupRepository.getAllSignupByPId(item.getId());
                    if (signups.size() > 0) {
                        //存在报名

                        for (Signup item2 : signups) {
                            //找到该signup的mark记录
                            Mark mark = markRepository.getMark(item2.getId());
                            if (mark != null) {
                                //存在该评分记录，构造dto，加入res
                                MarkDto markDto = new MarkDto();
                                markDto.setM_id(mark.getId());
                                markDto.setS_id(item2.getId());
                                markDto.setDsps(mark.getDsps());
                                markDto.setCreate_time(mark.getCreateTime());
                                markDto.setLt(mark.getLt());
                                markDto.setOds(mark.getOds());
                                markDto.setPf(mark.getPf());
                                markDto.setPl(mark.getPl());
                                markDto.setPt(mark.getPt());
                                markDto.setTotal_score(mark.getTotalScore());
                                markDto.setWe(mark.getWe());
                                markDto.setMemo("获取成功");
                                res.add(markDto);
                            } else {
                                logger.warn("该报名尚未评分");
                                MarkDto dto = new MarkDto();
                                dto.setM_id(0);
                                dto.setS_id(item2.getId());
                                dto.setMemo("该报名尚未评分");
                                res.add(dto);
                            }
                        }
                    } else {
                        logger.warn("该兼职尚未报名");
                        MarkDto dto = new MarkDto();
                        dto.setM_id(0);
                        dto.setS_id(0);
                        dto.setMemo("该兼职尚未报名");
                        res.add(dto);
                    }
                }
            } else {
                logger.warn("暂无负责的兼职");
                MarkDto dto = new MarkDto();
                dto.setM_id(0);
                dto.setS_id(0);
                dto.setMemo("暂无负责的兼职");
                res.add(dto);
            }
        } else {
            logger.warn("请检出输入");
            MarkDto dto = new MarkDto();
            dto.setM_id(0);
            dto.setS_id(0);
            dto.setMemo("请检出输入");
            res.add(dto);
        }

        return res;
    }

}
