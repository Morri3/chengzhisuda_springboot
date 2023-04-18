package com.zyq.parttime.controller;

import com.zyq.parttime.form.logandreg.*;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.LogAndRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@CrossOrigin
@RequestMapping("/")
public class LogAndRegController {
    @Autowired
    private LogAndRegService logAndRegService;

    //TODO 登录-学生
    @RequestMapping(value = "/login/stu", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData loginByStu(@RequestBody LoginDto loginDto) throws ParseException {
        LogAndRegInfoDto res = logAndRegService.loginByStu(loginDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 注册-学生
    @RequestMapping(value = "/register/stu", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData registerByStu(@RequestBody StuRegisterDto stuRegisterDto) throws ParseException {
        LogAndRegInfoDto res = logAndRegService.registerByStu(stuRegisterDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 登录-兼职发布者/管理员
    @RequestMapping(value = "/login/emp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData loginByEmp(@RequestBody LoginDto loginDto) throws ParseException {
        LogAndRegInfoDto res = logAndRegService.loginByEmp(loginDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 注册-兼职发布者/管理员
    @RequestMapping(value = "/register/emp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData registerByEmp(@RequestBody EmpRegisterDto empRegisterDto) throws ParseException {
        LogAndRegInfoDto res = logAndRegService.registerByEmp(empRegisterDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 退出登录-学生
    @RequestMapping(value = "/logout/stu", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData logoutByStu(@RequestBody LogoutDto logoutDto) {
        String res = logAndRegService.logoutByStu(logoutDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 退出登录-兼职发布者/管理员
    @RequestMapping(value = "/logout/emp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData logoutByEmp(@RequestBody LogoutDto logoutDto) {
        String res = logAndRegService.logoutByEmp(logoutDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

}
