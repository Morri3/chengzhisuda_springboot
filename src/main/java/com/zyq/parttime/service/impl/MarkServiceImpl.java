package com.zyq.parttime.service.impl;

import com.zyq.parttime.entity.Mark;
import com.zyq.parttime.entity.Position;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.position.PositionInfoDto;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.service.MarkService;
import com.zyq.parttime.service.PositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarkServiceImpl implements MarkService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private MarkRepository markRepository;

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
        } else {
            res.setMemo("获取失败");
        }
        System.out.println(res.toString());

        return res;
    }
}
