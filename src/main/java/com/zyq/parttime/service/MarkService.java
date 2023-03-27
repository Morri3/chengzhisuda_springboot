package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.position.PositionInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MarkService {
    //获取评分
    MarkDto getMark(int s_id) throws ParttimeServiceException;
}
