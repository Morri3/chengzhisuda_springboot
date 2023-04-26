package com.zyq.parttime.service;

//import com.kennycason.kumo.WordFrequency;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.analyze.AnalyzeActivationDto;
import com.zyq.parttime.form.analyze.AnalyzeAvgScoreOfMarkDto;
import com.zyq.parttime.form.analyze.AnalyzePublishDto;
import com.zyq.parttime.form.analyze.AnalyzeThreeIndicatorsDto;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.comment.CommentToEmpDto;
import com.zyq.parttime.form.comment.OneCommentDto;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface AnalyzeService {
    //获取每日兼职发布数量（每日所有兼职发布数）
    List<AnalyzePublishDto> getNumOfDailyPublish() throws ParttimeServiceException, ParseException;

    //获取所有兼职的报名/录取/名额数记录
    List<AnalyzeThreeIndicatorsDto> getNumOfThreeIndicators() throws ParttimeServiceException, ParseException;

    //获取所有兼职的兼职名+平均综合评分
    List<AnalyzeAvgScoreOfMarkDto> getAvgScoreOfMark() throws ParttimeServiceException, ParseException;

    //获取学生活跃度
    List<AnalyzeActivationDto> getActivationOfStudents() throws ParttimeServiceException, ParseException;
}
