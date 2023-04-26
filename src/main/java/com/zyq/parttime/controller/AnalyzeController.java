package com.zyq.parttime.controller;

//import com.kennycason.kumo.WordFrequency;
import com.zyq.parttime.form.analyze.AnalyzeAvgScoreOfMarkDto;
import com.zyq.parttime.form.analyze.AnalyzePublishDto;
import com.zyq.parttime.form.analyze.AnalyzeThreeIndicatorsDto;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.comment.CommentToEmpDto;
import com.zyq.parttime.form.comment.OneCommentDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.AnalyzeService;
import com.zyq.parttime.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/analyze")
public class AnalyzeController {
    //TODO  管理员专区

    @Autowired
    private AnalyzeService analyzeService;

    //TODO 获取每日兼职发布数量（每日所有兼职发布数）
    @RequestMapping(value = "/publish/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getNumOfDailyPublish() throws ParseException {
        List<AnalyzePublishDto> res = analyzeService.getNumOfDailyPublish();
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取所有兼职的报名/录取/名额数记录
    @RequestMapping(value = "/parttimes/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getNumOfThreeIndicators() throws ParseException {
        List<AnalyzeThreeIndicatorsDto> res = analyzeService.getNumOfThreeIndicators();
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取所有兼职的兼职名+平均综合评分
    @RequestMapping(value = "/mark/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getAvgScoreOfMark() throws ParseException {
        List<AnalyzeAvgScoreOfMarkDto> res = analyzeService.getAvgScoreOfMark();
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

//    //TODO 获取评论词云
//    @RequestMapping(value = "/comment/get", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseData getWordCloudOfComment() throws ParseException {
//        List<WordFrequency> res = analyzeService.getWordCloudOfComment();
//        return new ResponseData(ExceptionMsg.SUCCESS, res);
//    }
}