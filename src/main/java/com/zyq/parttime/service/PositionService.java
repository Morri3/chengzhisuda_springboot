package com.zyq.parttime.service;

import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.position.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface PositionService {
    //获取所有兼职
    List<PositionInfoDto> getAllPosition() throws ParttimeServiceException;

    //获取指定兼职
    PositionInfoDto getPosition(int p_id) throws ParttimeServiceException;

    //报名兼职
    SignupReturnDto signup(SignupDto signupDto) throws ParttimeServiceException, ParseException;

    //取消报名兼职
    CancelReturnDto cancel(CancelDto cancelDto) throws ParttimeServiceException, ParseException;

    //获取所有报名
    List<SignupReturnDto> history(HistoryDto historyDto) throws ParttimeServiceException, ParseException;

    //获取指定状态的报名
    List<SignupReturnDto> getOneStatus(String telephone,String signup_status) throws ParttimeServiceException, ParseException;

}
