package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.logandreg.EmpRegisterDto;
import com.zyq.parttime.form.logandreg.LoginDto;
import com.zyq.parttime.form.logandreg.StuRegisterDto;
import com.zyq.parttime.form.logandreg.LogAndRegInfoDto;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface LogAndRegService {
    //登录-学生
    LogAndRegInfoDto loginByStu(LoginDto loginDto) throws ParttimeServiceException;

    //注册-学生
    LogAndRegInfoDto registerByStu(StuRegisterDto stuRegisterDto) throws ParttimeServiceException, ParseException;

    //登录-兼职发布者/管理员
    LogAndRegInfoDto loginByEmp(LoginDto loginDto) throws ParttimeServiceException;

    //注册-兼职发布者/管理员
    LogAndRegInfoDto registerByEmp(EmpRegisterDto empRegisterDto) throws ParttimeServiceException, ParseException;

    //退出登录-学生
    String logoutByStu(String token) throws ParttimeServiceException;

    //退出登录-兼职发布者/管理员
    String logoutByEmp(String token) throws ParttimeServiceException;
}
