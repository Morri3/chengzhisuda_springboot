package com.zyq.parttime.controller;

import com.zyq.parttime.form.userinfomanage.*;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.LogAndRegService;
import com.zyq.parttime.service.UsersService;
import com.zyq.parttime.service.impl.UsersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    //TODO 个人信息查看-学生
    @RequestMapping(value = "/info/get_stu", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getStuInfo(@RequestBody GetInfoDto getInfoDto) {
        StuInfoDto res = usersService.getStuInfo(getInfoDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 个人信息查看-兼职发布者/管理员
    @RequestMapping(value = "/info/get_emp", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getEmpInfo(@RequestBody GetInfoDto getInfoDto) {
        EmpInfoDto res = usersService.getEmpInfo(getInfoDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 个人信息编辑-学生
    @RequestMapping(value = "/info/edit_stu", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editStuInfo(@RequestBody EditInfoDto editInfoDto) throws ParseException {
        StuInfoDto res = usersService.editStuInfo(editInfoDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 修改密码-学生
    @RequestMapping(value = "/info/modify_stu_pwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData modifyStuPwd(@RequestBody ModifyPwdDto modifyPwdDto) throws ParseException {
        StuInfoDto res = usersService.modifyStuPwd(modifyPwdDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 个人信息编辑-兼职发布者/管理员

    //TODO 修改密码-兼职发布者/管理员

}
