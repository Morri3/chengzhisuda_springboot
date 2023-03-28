package com.zyq.parttime.controller;

import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.form.position.*;
import com.zyq.parttime.form.userinfomanage.StuInfoDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.PositionService;
import com.zyq.parttime.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/parttime")
public class PositionController {
    @Autowired
    private PositionService positionService;

    //TODO 获取所有兼职-学生
    @RequestMapping(value = "/stu/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getAllPosition() {
        List<PositionInfoDto> res = positionService.getAllPosition();
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取指定兼职-学生
    @RequestMapping(value = "/stu/get_one", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getPosition(@RequestBody int p_id) {
        PositionInfoDto res = positionService.getPosition(p_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 报名兼职-学生
    @RequestMapping(value = "/stu/signup", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData signup(@RequestBody SignupDto signupDto) throws ParseException {
        SignupReturnDto res = positionService.signup(signupDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 取消报名-学生
    @RequestMapping(value = "/stu/cancel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData cancel(@RequestBody CancelDto cancelDto) throws ParseException {
        CancelReturnDto res = positionService.cancel(cancelDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 查看所有报名-学生
    @RequestMapping(value = "/stu/history", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData history(@RequestBody HistoryDto historyDto) throws ParseException {
        List<SignupReturnDto> res = positionService.history(historyDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }
}
