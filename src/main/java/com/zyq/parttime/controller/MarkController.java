package com.zyq.parttime.controller;

import com.zyq.parttime.form.mark.MarkDto;
import com.zyq.parttime.form.position.PositionInfoDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.MarkService;
import com.zyq.parttime.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mark")
public class MarkController {
    @Autowired
    private MarkService markService;

    //TODO 获取评分-学生
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getMark(@RequestParam int s_id) {
        MarkDto res = markService.getMark(s_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

}