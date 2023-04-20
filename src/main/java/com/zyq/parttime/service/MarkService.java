package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.mark.MarkPostDto;
import com.zyq.parttime.form.mark.MarkToEmpDto;
import com.zyq.parttime.form.mark.OneMarkDto;
import com.zyq.parttime.form.position.PositionInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MarkService {
    //获取评分
    MarkDto getMark(int s_id) throws ParttimeServiceException;

    //获取某兼职的全部评分
    OneMarkDto getMarkAll(int p_id) throws ParttimeServiceException;

    //评分
    MarkDto post(MarkPostDto markPostDto) throws ParttimeServiceException;

    //获取自己负责的所有兼职的所有评分记录
    List<MarkToEmpDto> getAllSpecialMark(String emp_id) throws ParttimeServiceException;
}
