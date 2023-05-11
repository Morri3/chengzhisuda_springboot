package com.zyq.parttime.service.impl;

import com.alibaba.fastjson.JSON;
//import com.kennycason.kumo.WordFrequency;
//import com.kennycason.kumo.nlp.FrequencyAnalyzer;
//import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.zyq.parttime.entity.Comment;
import com.zyq.parttime.entity.Parttimes;
import com.zyq.parttime.entity.Student;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.analyze.*;
import com.zyq.parttime.form.mark.OneMark;
import com.zyq.parttime.repository.comment.CommentRepository;
import com.zyq.parttime.repository.mark.MarkRepository;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.repository.userinfomanage.StuInfoRepository;
import com.zyq.parttime.service.AnalyzeService;
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
    @Autowired
    private StuInfoRepository stuInfoRepository;

    //TODO 获取每日兼职发布数（每日所有兼职发布数）
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

                //3.对于每个map，获取value，构造dto
                AnalyzePublishDto dto = new AnalyzePublishDto();
                dto.setDate(sdf.format(sdf.parse(item.get("create_time").toString())));//Date转String
                dto.setNum(Integer.parseInt(item.get("num").toString()));
                dto.setMemo("获取成功");
                tmp.add(dto);//加入列表
            }

            //4.创建map，存放日期、数量
            HashMap<String, Integer> map = new HashMap<>();

            //5.遍历dto列表，实现相同日期的兼职发布数进行叠加
            for (AnalyzePublishDto item : tmp) {
                if (map.containsKey(item.getDate())) {
                    // 若map中的key包含该日期，put前加上原来的数量
                    map.put(item.getDate(), item.getNum() + map.get(item.getDate()));// 将合并数量的该对象重新存入map集合，因key值相同，所以会覆盖掉之前的对象
                } else {
                    //不包含，直接put到map中
                    map.put(item.getDate(), item.getNum());
                }
            }

            //6.使用treemap+自定义比较器接口实现按日期升序排序
            Map<String, Object> sortMap = new TreeMap<>(new MapKeyComparator());
            sortMap.putAll(map);
            System.out.println("升序排序后的map：" + sortMap);

            //7.构造dto
            for (String date : sortMap.keySet()) {
                AnalyzePublishDto dto = new AnalyzePublishDto();
                dto.setDate(date);//日期是string类型
                dto.setNum(Integer.parseInt(map.get(date).toString()));
                dto.setMemo("获取成功");
                res.add(dto);//加入列表
            }
            System.out.println("接口1的结果：" + res.toString());
        } else {
            logger.warn("暂无兼职");
            AnalyzePublishDto dto = new AnalyzePublishDto();
            dto.setMemo("暂无兼职");
            res.add(dto);
        }

        return res;
    }

    //TODO 获取所有兼职的报名/录取/名额数记录
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


                int n1 = signupRepository.getNumOfSpecialStatus(item.getId(), "已报名");
                int n2 = signupRepository.getNumOfSpecialStatus(item.getId(), "已录取");
                int n3 = signupRepository.getNumOfSpecialStatus(item.getId(), "已结束");
                int n4 = signupRepository.getNumOfSpecialStatus(item.getId(), "已取消");
                int num_signup = n1 + n2 + n3 + n4;
                int num_employment = n2 + n3;
                //3-2.找到录取数
                AnalyzeThreeIndicatorsDto dto2 = new AnalyzeThreeIndicatorsDto();
                //4.找到录取数
                dto2.setNum_name("录取数");
                dto2.setNum(num_employment);
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
                dto3.setNum(num_signup);
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

    //TODO 获取所有兼职的兼职名+平均综合评分
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
                    //4-1.该兼职无评分
                    logger.warn("该兼职暂无评分");
                    AnalyzeAvgScoreOfMarkDto dto = new AnalyzeAvgScoreOfMarkDto();
                    dto.setMemo("该兼职暂无评分");
                    res.add(dto);
                } else {
                    //4-2.有评分

                    //map转dto
                    OneMark mark = JSON.parseObject(JSON.toJSONString(map), OneMark.class);
                    if (mark != null) {
                        float avg_total = mark.getTotal_score();//综合评分的均分
                        System.out.println("avg: " + avg_total);

                        //5.构造dto
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

    //TODO 获取学生活跃度
    @Override
    public List<AnalyzeActivationDto> getActivationOfStudents() throws ParttimeServiceException, ParseException {
        List<AnalyzeActivationDto> res = new ArrayList<>();

        //1.从student找到所有学生
        List<Student> allStudents = stuInfoRepository.getAllStudents();
        if (allStudents.size() > 0) {
            //2.存在学生，遍历每个学生
            for (Student item : allStudents) {
                //3.获取四个报名状态的记录数
                int n1 = signupRepository.getNumOfSpecialCategoryByStuId(item.getId(), "已报名");
                int n2 = signupRepository.getNumOfSpecialCategoryByStuId(item.getId(), "已录取");
                int n3 = signupRepository.getNumOfSpecialCategoryByStuId(item.getId(), "已结束");
                int n4 = signupRepository.getNumOfSpecialCategoryByStuId(item.getId(), "已取消");

                //4.根据学生id在signup获取其报名数
                AnalyzeActivationDto dto = new AnalyzeActivationDto();
                //4-1.获取学生信息
                dto.setStu_id(item.getId());
                dto.setStu_name(item.getStuName());
                //4-2.获取报名数
                dto.setNum_name("报名数");
                dto.setNum(n1 + n2 + n3 + n4);
                dto.setMemo("获取成功");
                res.add(dto);

                //5.根据学生id在signup获取其录用数（录用数=“已录取”报名数+“已结束”报名数）
                AnalyzeActivationDto dto2 = new AnalyzeActivationDto();
                //5-1.获取学生信息
                dto2.setStu_id(item.getId());
                dto2.setStu_name(item.getStuName());
                //5-2.获取录用数
                dto2.setNum_name("录用数");
                dto2.setNum(n2 + n3);
                dto2.setMemo("获取成功");
                res.add(dto2);

                //6.根据学生id在signup获取其取消数
                AnalyzeActivationDto dto3 = new AnalyzeActivationDto();
                //6-1.获取学生信息
                dto3.setStu_id(item.getId());
                dto3.setStu_name(item.getStuName());
                //6-2.获取取消数
                dto3.setNum_name("取消数");
                dto3.setNum(n4);
                dto3.setMemo("获取成功");
                res.add(dto3);
            }
        } else {
            logger.warn("暂无学生");
            AnalyzeActivationDto dto = new AnalyzeActivationDto();
            dto.setMemo("暂无学生");
            res.add(dto);
        }

        return res;
    }

    //自定义map键比较器类，实现map的键按字典顺序升序排序，从而实现日期的升序排序
    //e.g. 2023-05-05 < 2023-05-08，前9位字符相同，第10位字符的5小于8，故前者小于后者
    public class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }
}
