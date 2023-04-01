package com.zyq.parttime.service.impl;

import com.alibaba.fastjson.JSON;
import com.zyq.parttime.entity.Mark;
import com.zyq.parttime.entity.Position;
import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.mark.MarkPostDto;
import com.zyq.parttime.form.mark.OneMark;
import com.zyq.parttime.form.mark.OneMarkDto;
import com.zyq.parttime.form.position.PositionInfoDto;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.service.MarkService;
import com.zyq.parttime.service.PositionService;
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
}
