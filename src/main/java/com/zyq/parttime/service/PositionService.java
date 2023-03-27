package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.position.PositionInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PositionService {
    //获取所有兼职
    List<PositionInfoDto> getAllPosition() throws ParttimeServiceException;
}
