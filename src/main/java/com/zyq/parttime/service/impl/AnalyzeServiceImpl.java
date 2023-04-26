package com.zyq.parttime.service.impl;

import com.alibaba.fastjson.JSON;
//import com.kennycason.kumo.WordFrequency;
//import com.kennycason.kumo.nlp.FrequencyAnalyzer;
//import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.zyq.parttime.entity.Comment;
import com.zyq.parttime.entity.Parttimes;
import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.analyze.AnalyzeAvgScoreOfMarkDto;
import com.zyq.parttime.form.analyze.AnalyzePublishDto;
import com.zyq.parttime.form.analyze.AnalyzeThreeIndicatorsDto;
import com.zyq.parttime.form.analyze.GetAnalyzePublishDto;
import com.zyq.parttime.form.comment.CommentDto;
import com.zyq.parttime.form.comment.CommentPostDto;
import com.zyq.parttime.form.comment.CommentToEmpDto;
import com.zyq.parttime.form.comment.OneCommentDto;
import com.zyq.parttime.form.mark.OneMark;
import com.zyq.parttime.repository.comment.CommentRepository;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.service.AnalyzeService;
import com.zyq.parttime.service.CommentService;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AnalyzeServiceImpl implements AnalyzeService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SignupRepository signupRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private MarkRepository markRepository;

    @Override
    public List<AnalyzePublishDto> getNumOfDailyPublish() throws ParttimeServiceException, ParseException {
        List<AnalyzePublishDto> res = new ArrayList<>();
        List<AnalyzePublishDto> tmp = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //1.获取所有兼职
        List<Map<String, Object>> list = positionRepository.getNumOfDailyPublish();

        if (list.size() > 0) {
            //2.有数据，遍历每个Map
            for (Map<String, Object> item : list) {
                System.out.println("1: " + item.get("create_time"));
                System.out.println("2: " + item.get("num"));
                //3.对于每个map，获取value，构造dto
                AnalyzePublishDto dto = new AnalyzePublishDto();
                dto.setDate(sdf.format(sdf.parse(item.get("create_time").toString())));//Date转String
                dto.setNum(Integer.parseInt(item.get("num").toString()));
                dto.setMemo("获取成功");
                tmp.add(dto);//加入列表
            }
            //4.创建map，存放日期、数量
            HashMap<String, Integer> map = new HashMap<>();
            //5.遍历dto列表
            for (AnalyzePublishDto item : tmp) {
                if (map.containsKey(item.getDate())) {// 若map中的key包含该日期，put前加上原来的数量
                    map.put(item.getDate(), item.getNum() + map.get(item.getDate()));// 将合并数量的该对象重新存入map集合，因key值相同，所以会覆盖掉之前的对象
                } else {//不包含，直接put
                    map.put(item.getDate(), item.getNum());
                }
            }
            //测试
//            List<Integer> numList = new ArrayList<>();
//            for (String date : map.keySet()) {// 将map中的对象重新存放新的List集合
//                Integer num = map.get(date);
//                numList.add(num);
//            }
//            System.out.println(numList.toString());
            //6.构造dto
            for (String date : map.keySet()) {
                AnalyzePublishDto dto = new AnalyzePublishDto();
                dto.setDate(date);//日期是string类型
                dto.setNum(Integer.parseInt(map.get(date).toString()));
                dto.setMemo("获取成功");
                res.add(dto);//加入列表
            }
            System.out.println("结果：" + res.toString());
        } else {
            logger.warn("暂无兼职");
            AnalyzePublishDto dto = new AnalyzePublishDto();
            dto.setMemo("暂无兼职");
            res.add(dto);
        }

        return res;
    }

    @Override
    public List<AnalyzeThreeIndicatorsDto> getNumOfThreeIndicators() throws ParttimeServiceException, ParseException {
        List<AnalyzeThreeIndicatorsDto> res = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //1.从parttimes找到所有兼职
        List<Parttimes> allParttimes = positionRepository.getAllPositions();
        if (allParttimes.size() > 0) {
            //2.有兼职，遍历每个兼职
            for (Parttimes item : allParttimes) {
                //3-1.名额数
                AnalyzeThreeIndicatorsDto dto = new AnalyzeThreeIndicatorsDto();
                //4.找到名额数
                dto.setNum_name("名额数");
                dto.setNum(item.getNum());
                //5.其他参数
                dto.setCreate_time(sdf.format(item.getCreateTime()));//字符串型，年月日
                dto.setP_name(item.getPositionName());
                dto.setEmp_id(item.getOp().getId());
                dto.setEmp_name(item.getOp().getEmpName());
                dto.setP_id(item.getId());
                dto.setMemo("获取成功");
                res.add(dto);


                //3-2.找到录取数
                AnalyzeThreeIndicatorsDto dto2 = new AnalyzeThreeIndicatorsDto();
                //4.找到录取数
                dto2.setNum_name("录取数");
                dto2.setNum(signupRepository.getNumOfEmployment(item.getId()));
                //5.其他参数
                dto2.setCreate_time(sdf.format(item.getCreateTime()));//字符串型，年月日
                dto2.setP_name(item.getPositionName());
                dto2.setEmp_id(item.getOp().getId());
                dto2.setEmp_name(item.getOp().getEmpName());
                dto2.setP_id(item.getId());
                dto2.setMemo("获取成功");
                res.add(dto2);


                //3-3.找到报名数
                AnalyzeThreeIndicatorsDto dto3 = new AnalyzeThreeIndicatorsDto();
                //4.找到报名数
                dto3.setNum_name("报名数");
                dto3.setNum(signupRepository.getNumOfSignup(item.getId()));
                //5.其他参数
                dto3.setCreate_time(sdf.format(item.getCreateTime()));//字符串型，年月日
                dto3.setP_name(item.getPositionName());
                dto3.setEmp_id(item.getOp().getId());
                dto3.setEmp_name(item.getOp().getEmpName());
                dto3.setP_id(item.getId());
                dto3.setMemo("获取成功");
                res.add(dto3);
            }
        } else {
            logger.warn("暂无兼职");
            AnalyzeThreeIndicatorsDto dto = new AnalyzeThreeIndicatorsDto();
            dto.setMemo("暂无兼职");
            res.add(dto);
        }

        return res;
    }

    @Override
    public List<AnalyzeAvgScoreOfMarkDto> getAvgScoreOfMark() throws ParttimeServiceException, ParseException {
        List<AnalyzeAvgScoreOfMarkDto> res = new ArrayList<>();

        //1.从parttimes找到所有兼职
        List<Parttimes> allParttimes = positionRepository.getAllPositions();
        if (allParttimes.size() > 0) {
            //2.有兼职，遍历每个兼职
            for (Parttimes item : allParttimes) {
                //3.获取每个item的平均综合评分
                Map<String, Object> map = markRepository.getMarkByPId(item.getId());
                //4.判断该兼职是否有评分
                if (map.get("total_score") == null) {
                    //该兼职无评分
                    logger.warn("该兼职暂无评分");
                    AnalyzeAvgScoreOfMarkDto dto = new AnalyzeAvgScoreOfMarkDto();
                    dto.setMemo("该兼职暂无评分");
                    res.add(dto);
                } else {
                    //有评分
                    OneMark mark = JSON.parseObject(JSON.toJSONString(map), OneMark.class);//map转dto
                    if (mark != null) {
                        float avg_total = mark.getTotal_score();
                        System.out.println("avg: " + avg_total);
                        //4.构造dto
                        AnalyzeAvgScoreOfMarkDto dto = new AnalyzeAvgScoreOfMarkDto();
                        dto.setAvg_total(avg_total);
                        dto.setP_id(item.getId());
                        dto.setP_name(item.getPositionName());
                        dto.setMemo("获取成功");
                        res.add(dto);
                    } else {
                        logger.warn("获取失败");
                        AnalyzeAvgScoreOfMarkDto dto = new AnalyzeAvgScoreOfMarkDto();
                        dto.setMemo("获取失败");
                        res.add(dto);
                    }
                }
            }
        } else {
            logger.warn("暂无兼职");
            AnalyzeAvgScoreOfMarkDto dto = new AnalyzeAvgScoreOfMarkDto();
            dto.setMemo("暂无兼职");
            res.add(dto);
        }

        return res;
    }

//    @Override
//    public List<WordFrequency> getWordCloudOfComment() throws ParttimeServiceException, ParseException {
//        //1.数据库获取所有评论
//        List<Comment> list = commentRepository.getAllComment();
//        //2.根据list构造词云所需List<String>
//        List<String> source = new ArrayList<>();
//        for (Comment comment : list) {
//            source.add(comment.getContent());
//        }
//        System.out.println("词云源数据： " + source.toString());
////        if (list.size() > 0) {
//        //2.有数据
//        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
//        //3.设置分词返回数量(频率最高的600个词)
//        frequencyAnalyzer.setWordFrequenciesToReturn(600);
//        //4.最小分词长度
//        frequencyAnalyzer.setMinWordLength(2);
//        //5.引入中文解析器
//        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
//        //6.直接从文件中读取，返回前端
//        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(source);
//        System.out.println("词云结果："+wordFrequencies.toString());
//        return wordFrequencies;
////        } else {
////            //2.没数据
////            logger.warn("暂无兼职");
////            List<WordFrequency> nocontent = new ArrayList<>();
////            return nocontent;
////        }
//
//    }
}
