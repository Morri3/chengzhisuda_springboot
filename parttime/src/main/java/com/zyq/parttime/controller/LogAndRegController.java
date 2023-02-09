package com.zyq.parttime.controller;

import com.zyq.parttime.form.LoginDto;
import com.zyq.parttime.form.StuInfoDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.LogAndRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class LogAndRegController {
    @Autowired
    private LogAndRegService logAndRegService;

    //TODO 登录-学生
    @RequestMapping(value = "/login/stu", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData loginStu(@RequestBody LoginDto loginDto) {
        StuInfoDto res = logAndRegService.loginStu(loginDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

}
