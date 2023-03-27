package com.zyq.parttime.controller;

import com.zyq.parttime.form.position.PositionInfoDto;
import com.zyq.parttime.form.userinfomanage.StuInfoDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.PositionService;
import com.zyq.parttime.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

//    //TODO 获取所有兼职-学生
//    @RequestMapping(value = "/stu/get", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseData getAllPosition() {
//        List<PositionInfoDto> res = positionService.getAllPosition();
//        return new ResponseData(ExceptionMsg.SUCCESS, res);
//    }
}
