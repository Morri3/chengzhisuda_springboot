package com.zyq.parttime.service.impl;

import cn.hutool.crypto.asymmetric.Sign;
import com.zyq.parttime.controller.UsersController;
import com.zyq.parttime.entity.Position;
import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.entity.Student;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.position.*;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.repository.userinfomanage.StuInfoRepository;
import com.zyq.parttime.service.PositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class PositionServiceImpl implements PositionService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private StuInfoRepository stuInfoRepository;
    @Autowired
    private SignupRepository signupRepository;

    @Override
    public List<PositionInfoDto> getAllPosition() throws ParttimeServiceException {
        List<PositionInfoDto> res = new ArrayList<>();
        //数据
        List<Position> list = positionRepository.getAllPositions();
        if (list != null) {
            for (Position i : list) {
                PositionInfoDto dto = new PositionInfoDto();
                dto.setP_id(i.getId());
                dto.setCategory(i.getCategory());
                dto.setContent(i.getContent());
                dto.setExp(i.getExp());
                dto.setPosition_name(i.getPositionName());
                dto.setPosition_status(i.getPositionStatus());
                dto.setArea(i.getArea());
                dto.setCreate_time(i.getCreateTime());
                dto.setRequirement(i.getRequirement());
                dto.setSalary(i.getSalary());
                dto.setSettlement(i.getSettlement());
                dto.setSignup_ddl(i.getSignupDdl());
                dto.setSlogan(i.getSlogan());
                dto.setUpdate_time(i.getUpdateTime());
                dto.setOp_id(i.getOp().getId());
                dto.setWork_time(i.getWorkTime());
                res.add(dto);
            }
            System.out.println(list.toString());
        } else {
            logger.warn("该账号不存在");
        }
        System.out.println(res.toString());

        return res;
    }

    @Override
    public List<PositionInfoDto> getAllPositionByIntentions(GetPositionDto getPositionDto) throws ParttimeServiceException {
        List<PositionInfoDto> res = new ArrayList<>();

        if (getPositionDto != null) {
            //获取意向兼职数组
            List<String> intentions = getPositionDto.getIntentions();

            //遍历意向兼职数组，先后找到这些类型的兼职，按照兼职种类在数组中的顺序先后加入到res中
            if (intentions != null && intentions.size() > 0) {//有意向兼职
                for (String i : intentions) {
                    List<Position> curList = positionRepository.getPositionByIntention(i);
                    if (curList != null && curList.size() > 0) {
                        //有数据
                        for (Position position : curList) {
                            PositionInfoDto dto = new PositionInfoDto();
                            dto.setP_id(position.getId());
                            dto.setCategory(position.getCategory());
                            dto.setContent(position.getContent());
                            dto.setExp(position.getExp());
                            dto.setPosition_name(position.getPositionName());
                            dto.setPosition_status(position.getPositionStatus());
                            dto.setArea(position.getArea());
                            dto.setCreate_time(position.getCreateTime());
                            dto.setRequirement(position.getRequirement());
                            dto.setSalary(position.getSalary());
                            dto.setSettlement(position.getSettlement());
                            dto.setSignup_ddl(position.getSignupDdl());
                            dto.setSlogan(position.getSlogan());
                            dto.setUpdate_time(position.getUpdateTime());
                            dto.setOp_id(position.getOp().getId());
                            dto.setWork_time(position.getWorkTime());
                            res.add(dto);
                        }
                    }
                }
            }

            //TODO  不管是否有意向兼职，最后都是取所有兼职数据
            //获取两个List的差集，求出意向兼职外的兼职种类
            List<String> allCategory = Arrays.asList("课程助教", "学生助理", "军训助理", "体测助理", "讲解员",
                    "公寓宣传员", "班助", "服务员");
            List<String> others = allCategory.stream().filter(item ->
                    !intentions.contains(item)).collect(toList());
            System.out.println("其他兼职种类：" + others.toString());//输出测试

            //再获得意向兼职外的兼职，加入res
            if (others != null && others.size() > 0) {
                //有兼职可以查找
                for (String i : others) {
                    List<Position> curList = positionRepository.getPositionByIntention(i);
                    if (curList != null && curList.size() > 0) {
                        //有数据
                        for (Position position : curList) {
                            PositionInfoDto dto = new PositionInfoDto();
                            dto.setP_id(position.getId());
                            dto.setCategory(position.getCategory());
                            dto.setContent(position.getContent());
                            dto.setExp(position.getExp());
                            dto.setPosition_name(position.getPositionName());
                            dto.setPosition_status(position.getPositionStatus());
                            dto.setArea(position.getArea());
                            dto.setCreate_time(position.getCreateTime());
                            dto.setRequirement(position.getRequirement());
                            dto.setSalary(position.getSalary());
                            dto.setSettlement(position.getSettlement());
                            dto.setSignup_ddl(position.getSignupDdl());
                            dto.setSlogan(position.getSlogan());
                            dto.setUpdate_time(position.getUpdateTime());
                            dto.setOp_id(position.getOp().getId());
                            dto.setWork_time(position.getWorkTime());
                            res.add(dto);
                        }
                    }
                }
            } else {
                //兼职查找失败
                logger.warn("兼职信息获取失败");
            }

//            //数据
//            List<Position> list = positionRepository.getAllPositionsByIntentions(intentions);
//            if (list != null) {
//                for (Position i : list) {
//                    PositionInfoDto dto = new PositionInfoDto();
//                    dto.setP_id(i.getId());
//                    dto.setCategory(i.getCategory());
//                    dto.setContent(i.getContent());
//                    dto.setExp(i.getExp());
//                    dto.setPosition_name(i.getPositionName());
//                    dto.setPosition_status(i.getPositionStatus());
//                    dto.setArea(i.getArea());
//                    dto.setCreate_time(i.getCreateTime());
//                    dto.setRequirement(i.getRequirement());
//                    dto.setSalary(i.getSalary());
//                    dto.setSettlement(i.getSettlement());
//                    dto.setSignup_ddl(i.getSignupDdl());
//                    dto.setSlogan(i.getSlogan());
//                    dto.setUpdate_time(i.getUpdateTime());
//                    dto.setOp_id(i.getOp().getId());
//                    dto.setWork_time(i.getWorkTime());
//                    res.add(dto);
//                }
//                System.out.println(list.toString());
//            } else {
//                logger.warn("该账号不存在");
//            }
            System.out.println(res.toString());
        }

        return res;
    }

    @Override
    public PositionInfoDto getPosition(int p_id) throws ParttimeServiceException {
        PositionInfoDto res = new PositionInfoDto();
        //数据
        Position position = positionRepository.getPosition(p_id);
        if (position != null) {
            res.setP_id(position.getId());
            res.setCategory(position.getCategory());
            res.setContent(position.getContent());
            res.setExp(position.getExp());
            res.setPosition_name(position.getPositionName());
            res.setPosition_status(position.getPositionStatus());
            res.setArea(position.getArea());
            res.setCreate_time(position.getCreateTime());
            res.setRequirement(position.getRequirement());
            res.setSalary(position.getSalary());
            res.setSettlement(position.getSettlement());
            res.setSignup_ddl(position.getSignupDdl());
            res.setSlogan(position.getSlogan());
            res.setUpdate_time(position.getUpdateTime());
            res.setOp_id(position.getOp().getId());
            res.setWork_time(position.getWorkTime());
            res.setMemo("存在兼职");
        } else {
            logger.warn("该兼职不存在");
            res.setMemo("该兼职不存在");
        }
        System.out.println(res.toString());

        return res;
    }

    @Override
    public SignupReturnDto signup(SignupDto signupDto) throws ParttimeServiceException, ParseException {
        SignupReturnDto res = new SignupReturnDto();

        if (signupDto != null) {
            String telephone = signupDto.getTelephone();
            int p_id = signupDto.getP_id();

            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //判断兼职是否存在
                PositionInfoDto dto = getPosition(p_id);
                //判断该用户是否已经报名过该兼职
//                Signup find = signupRepository.findExistsSignup(stu.getId(), p_id);
                List<Signup> find = signupRepository.findExistsSignup(stu.getId(), p_id);

                //没报名过，就可以直接报名
                if (find == null || find.size() == 0) {
                    System.out.println("兼职：" + dto.toString());
                    if (dto != null && (dto.getMemo()).equals("存在兼职")) {
                        //添加signup记录
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date now = sdf.parse(sdf.format(new Date()));
                        signupRepository.createASignupRecord(telephone, p_id, "已报名", now, now);

                        //查询最新的记录
                        Signup signup = signupRepository.getLatestSignup(now);
                        if (signup != null) {
                            res.setS_id(signup.getId());
                            res.setStu_id(signup.getStu().getId());
                            res.setP_id(signup.getP().getId());
                            res.setSignup_status(signup.getSignupStatus());
                            res.setCreate_time(signup.getCreateTime());
                            res.setUpdate_time(signup.getUpdateTime());
                            res.setMemo("报名成功");
                        } else {
                            logger.warn("报名失败");
                            res.setStu_id(telephone);
                            res.setMemo("报名失败");
                        }
                    } else {
                        logger.warn("不存在该兼职");
                        res.setStu_id(telephone);
                        res.setMemo("不存在该兼职");
                    }
                } else if (find != null && find.size() > 0) {
                    //有报名过
                    boolean flag = false;//true表示有该兼职对应的正在进行的报名
                    for (Signup signup : find) {
                        //遍历find列表，查看是否有“已报名”“已录取”两种状态
                        if (signup.getSignupStatus().equals("已报名")
                                || signup.getSignupStatus().equals("已录取")) {
                            flag = true;
                        }
                    }

                    if (flag == true) {//不能报名

                        logger.warn("请先完成正在进行的兼职");
                        res.setStu_id(telephone);
                        res.setMemo("请先完成正在进行的兼职");
                    } else {//可以报名

                        //添加signup记录
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date now = sdf.parse(sdf.format(new Date()));
                        signupRepository.createASignupRecord(telephone, p_id, "已报名", now, now);

                        //查询最新的记录
                        Signup signup = signupRepository.getLatestSignup(now);
                        if (signup != null) {
                            res.setS_id(signup.getId());
                            res.setStu_id(signup.getStu().getId());
                            res.setP_id(signup.getP().getId());
                            res.setSignup_status(signup.getSignupStatus());
                            res.setCreate_time(signup.getCreateTime());
                            res.setUpdate_time(signup.getUpdateTime());
                            res.setMemo("报名成功");
                        } else {
                            logger.warn("报名失败");
                            res.setStu_id(telephone);
                            res.setMemo("报名失败");
                        }
                    }
                }
//                //没报名过
//                if (find == null || find.size() == 0) {
//                    System.out.println("兼职："+dto.toString());
//                    if (dto != null && (dto.getMemo()).equals("存在兼职")) {
//                        //添加signup记录
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date now = sdf.parse(sdf.format(new Date()));
//                        signupRepository.createASignupRecord(telephone, p_id, "已报名", now, now);
//
//                        //查询最新的记录
//                        Signup signup = signupRepository.getLatestSignup(now);
//                        if (signup != null) {
//                            res.setS_id(signup.getId());
//                            res.setStu_id(signup.getStu().getId());
//                            res.setP_id(signup.getP().getId());
//                            res.setSignup_status(signup.getSignupStatus());
//                            res.setCreate_time(signup.getCreateTime());
//                            res.setUpdate_time(signup.getUpdateTime());
//                            res.setMemo("报名成功");
//                        } else {
//                            logger.warn("报名失败");
//                            res.setStu_id(telephone);
//                            res.setMemo("报名失败");
//                        }
//                    } else {
//                        logger.warn("不存在该兼职");
//                        res.setStu_id(telephone);
//                        res.setMemo("不存在该兼职");
//                    }
//                } else {
//                    if (find.getSignupStatus().equals("已结束") || find.getSignupStatus().equals("已取消")) {
//                        //已结束或已取消，可以再报名
//                        System.out.println(dto.toString());
//                        if (dto != null && (dto.getMemo()).equals("存在兼职")) {
//                            //添加signup记录
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date now = sdf.parse(sdf.format(new Date()));
//                            signupRepository.createASignupRecord(telephone, p_id, "已报名", now, now);
//
//                            //查询最新的记录
//                            Signup signup = signupRepository.getLatestSignup(now);
//                            if (signup != null) {
//                                res.setS_id(signup.getId());
//                                res.setStu_id(signup.getStu().getId());
//                                res.setP_id(signup.getP().getId());
//                                res.setSignup_status(signup.getSignupStatus());
//                                res.setCreate_time(signup.getCreateTime());
//                                res.setUpdate_time(signup.getUpdateTime());
//                                res.setMemo("报名成功");
//                            } else {
//                                logger.warn("报名失败");
//                                res.setStu_id(telephone);
//                                res.setMemo("报名失败");
//                            }
//                        } else {
//                            logger.warn("不存在该兼职");
//                            res.setStu_id(telephone);
//                            res.setMemo("不存在该兼职");
//                        }
//                    } else {
//                        logger.warn("该用户已报名");
//                        res.setStu_id(telephone);
//                        res.setMemo("该用户已报名");
//                    }
//                }
            } else {//不存在
                logger.warn("该账号不存在");
                res.setStu_id(telephone);
                res.setMemo("该账号不存在");
            }
        }
        return res;
    }

    @Override
    public CancelReturnDto cancel(CancelDto cancelDto) throws ParttimeServiceException, ParseException {
        CancelReturnDto res = new CancelReturnDto();

        if (cancelDto != null) {
            String telephone = cancelDto.getTelephone();
            int s_id = cancelDto.getS_id();

            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //判断报名是否存在
                Signup find = signupRepository.findSignup(s_id);
                if (find != null && find.getSignupStatus().equals("已报名")) {
                    //存在报名，且已报名状态可取消

                    //当前时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now = sdf.parse(sdf.format(new Date()));
                    signupRepository.cancelSignup(now, s_id);//修改DB

                    //构造返回
                    res.setStu_id(telephone);
                    res.setMemo("取消成功");
                } else {
                    logger.warn("不存在报名或报名不能取消");
                    res.setStu_id(telephone);
                    res.setMemo("不存在报名或报名不能取消");
                }
            } else {//不存在
                logger.warn("该账号不存在");
                res.setStu_id(telephone);
                res.setMemo("该账号不存在");
            }
        }
        return res;
    }

    @Override
    public List<SignupReturnDto> history(HistoryDto historyDto) throws ParttimeServiceException, ParseException {
        List<SignupReturnDto> res = new ArrayList<>();

        if (historyDto != null) {
            String telephone = historyDto.getTelephone();

            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //查询该用户所有报名
                List<Signup> list = signupRepository.getAllSignup(telephone);
                if (list != null && list.size() > 0) {
                    for (Signup item : list) {
                        SignupReturnDto dto = new SignupReturnDto();
                        dto.setS_id(item.getId());
                        dto.setStu_id(item.getStu().getId());
                        dto.setP_id(item.getP().getId());
                        dto.setSignup_status(item.getSignupStatus());
                        dto.setCreate_time(item.getCreateTime());
                        dto.setUpdate_time(item.getUpdateTime());
                        res.add(dto);
                    }
                } else {
                    logger.warn("获取历史记录失败");
                    SignupReturnDto dto = new SignupReturnDto();
                    dto.setStu_id(telephone);
                    dto.setMemo("获取历史记录失败");
                    res.add(dto);
                }
            } else {
                logger.warn("该账号不存在");
                SignupReturnDto dto = new SignupReturnDto();
                dto.setStu_id(telephone);
                dto.setMemo("该账号不存在");
                res.add(dto);
            }
        } else {
            logger.warn("输入有误");
            SignupReturnDto dto = new SignupReturnDto();
            dto.setMemo("输入有误");
            res.add(dto);
        }
        return res;
    }

    @Override
    public List<SignupReturnDto> getOneStatus(String telephone, String signup_status) throws ParttimeServiceException, ParseException {
        List<SignupReturnDto> res = new ArrayList<>();

        if (telephone != null) {
            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //查询该用户所有报名
                List<Signup> list = signupRepository.getSignupByStatus(telephone, signup_status);
                if (list != null && list.size() > 0) {
                    for (Signup item : list) {
                        SignupReturnDto dto = new SignupReturnDto();
                        dto.setS_id(item.getId());
                        dto.setStu_id(item.getStu().getId());
                        dto.setP_id(item.getP().getId());
                        dto.setSignup_status(item.getSignupStatus());
                        dto.setCreate_time(item.getCreateTime());
                        dto.setUpdate_time(item.getUpdateTime());
                        res.add(dto);
                    }
                } else {
                    logger.warn("获取历史记录失败");
                    SignupReturnDto dto = new SignupReturnDto();
                    dto.setStu_id(telephone);
                    dto.setMemo("获取历史记录失败");
                    res.add(dto);
                }
            } else {
                logger.warn("该账号不存在");
                SignupReturnDto dto = new SignupReturnDto();
                dto.setStu_id(telephone);
                dto.setMemo("该账号不存在");
                res.add(dto);
            }
        } else {
            logger.warn("输入有误");
            SignupReturnDto dto = new SignupReturnDto();
            dto.setMemo("输入有误");
            res.add(dto);
        }
        return res;
    }

    @Override
    public CanSignupDto getSpecialSignup(String telephone, int p_id) throws ParttimeServiceException, ParseException {
        CanSignupDto res = new CanSignupDto();
        res.setFlag(true);//默认值true

        if (telephone != null) {
            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);

            if (stu != null) {//存在学生
                //查询该用户所有报名
                List<Signup> list = signupRepository.getAllSignup(telephone);
                if (list != null && list.size() > 0) {//有数据
                    for (Signup item : list) {
                        if (item.getP().getId() == p_id) {
                            //当前遍历的是用户想要报名的兼职
                            if (item.getSignupStatus().equals("已报名") || item.getSignupStatus().equals("已录取")) {
                                //只要有该兼职对应的报名的状态是：已报名、已录取，就不能报名
                                res.setFlag(false);
                            }
                        }
                    }
                    //遍历完后，如果flag是true，表示可以报名
                    if (res.isFlag() == true) {
                        logger.warn("可以报名");
                        res.setMemo("可以报名");
                        res.setFlag(true);
                    } else {
                        logger.warn("不能报名");
                        res.setMemo("不能报名");
                        res.setFlag(false);
                    }
                } else {
                    //没有报名记录，可以直接报名
                    logger.warn("可以报名");
                    res.setMemo("可以报名");
                    res.setFlag(true);
                }
            } else {
                logger.warn("该账号不存在");
                res.setMemo("该账号不存在");
                res.setFlag(false);
            }
        } else {
            logger.warn("输入有误");
            res.setMemo("输入有误");
            res.setFlag(false);
        }
        return res;
    }
}