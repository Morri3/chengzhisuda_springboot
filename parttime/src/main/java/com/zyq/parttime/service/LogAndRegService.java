package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.LoginDto;
import com.zyq.parttime.form.StuInfoDto;
import org.springframework.stereotype.Service;

@Service
public interface LogAndRegService {
    //登录-学生
    StuInfoDto loginStu(LoginDto loginDto) throws ParttimeServiceException;


}
