package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.userinfomanage.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface UsersService {
    //个人信息查看-学生
    StuInfoDto getStuInfo(GetInfoDto getInfoDto) throws ParttimeServiceException;

    //个人信息查看-兼职发布者/管理员
    EmpInfoDto getEmpInfo(GetInfoDto getInfoDto) throws ParttimeServiceException;

    //个人信息编辑-学生
    StuInfoDto editStuInfo(EditInfoDto editInfoDto) throws ParttimeServiceException, ParseException;

    //修改密码-学生
    StuInfoDto modifyStuPwd(ModifyPwdDto modifyPwdDto) throws ParttimeServiceException;

    //个人信息编辑-兼职发布者/管理员

    //修改密码-兼职发布者/管理员

}
