package com.zyq.parttime.controller;

import com.zyq.parttime.form.logandreg.EmpRegisterDto;
import com.zyq.parttime.form.logandreg.LoginDto;
import com.zyq.parttime.form.logandreg.StuRegisterDto;
import com.zyq.parttime.form.logandreg.LogAndRegInfoDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.LogAndRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/")
public class LogAndRegController {
    @Autowired
    private LogAndRegService logAndRegService;

    //TODO 登录-学生
    @RequestMapping(value = "/login/stu", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData loginByStu(@RequestBody LoginDto loginDto) {
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
    public ResponseData loginByEmp(@RequestBody LoginDto loginDto) {
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
    @RequestMapping(value = "/logout/stu/{token}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData logoutByStu(@PathVariable("token") String token) {
        String res = logAndRegService.logoutByStu(token);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 退出登录-兼职发布者/管理员
    @RequestMapping(value = "/logout/emp/{token}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData logoutByEmp(@PathVariable("token") String token) {
        String res = logAndRegService.logoutByEmp(token);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }
}
