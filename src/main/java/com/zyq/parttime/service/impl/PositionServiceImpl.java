package com.zyq.parttime.service.impl;

import cn.hutool.crypto.asymmetric.Sign;
import com.zyq.parttime.entity.*;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.position.*;
import com.zyq.parttime.form.resumemanage.ResumeDetailDto;
import com.zyq.parttime.form.resumemanage.ResumeInfoDto;
import com.zyq.parttime.form.unit.UnitInfoDto;
import com.zyq.parttime.repository.logandreg.LogAndRegByEmpRepository;
import com.zyq.parttime.repository.resumemanage.ResumesInfoRepository;
import com.zyq.parttime.repository.unit.UnitRepository;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import com.zyq.parttime.repository.userinfomanage.StuInfoRepository;
import com.zyq.parttime.service.PositionService;
import io.minio.messages.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private LogAndRegByEmpRepository logAndRegByEmpRepository;
    @Autowired
    private ResumesInfoRepository resumesInfoRepository;
    @Autowired
    private UsersServiceImpl usersServiceImpl;

    @Override
    public List<PositionInfoDto> getAllPosition() throws ParttimeServiceException {
        List<PositionInfoDto> res = new ArrayList<>();
        //数据
        List<Parttimes> list = positionRepository.getAllPositions();
        if (list != null) {
            for (Parttimes i : list) {
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
                dto.setMemo("兼职获取成功");
                res.add(dto);
            }
            System.out.println(list.toString());
        } else {
            logger.warn("该账号不存在");
            PositionInfoDto dto = new PositionInfoDto();
            dto.setMemo("该账号不存在");
            res.add(dto);
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
                    List<Parttimes> curList = positionRepository.getPositionByIntention(i);
                    if (curList != null && curList.size() > 0) {
                        //有数据
                        for (Parttimes parttimes : curList) {
                            PositionInfoDto dto = new PositionInfoDto();
                            dto.setP_id(parttimes.getId());
                            dto.setCategory(parttimes.getCategory());
                            dto.setContent(parttimes.getContent());
                            dto.setExp(parttimes.getExp());
                            dto.setPosition_name(parttimes.getPositionName());
                            dto.setPosition_status(parttimes.getPositionStatus());
                            dto.setArea(parttimes.getArea());
                            dto.setCreate_time(parttimes.getCreateTime());
                            dto.setRequirement(parttimes.getRequirement());
                            dto.setSalary(parttimes.getSalary());
                            dto.setSettlement(parttimes.getSettlement());
                            dto.setSignup_ddl(parttimes.getSignupDdl());
                            dto.setSlogan(parttimes.getSlogan());
                            dto.setUpdate_time(parttimes.getUpdateTime());
                            dto.setOp_id(parttimes.getOp().getId());
                            dto.setWork_time(parttimes.getWorkTime());
                            dto.setMemo("兼职获取成功");
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
                    List<Parttimes> curList = positionRepository.getPositionByIntention(i);
                    if (curList != null && curList.size() > 0) {
                        //有数据
                        for (Parttimes parttimes : curList) {
                            PositionInfoDto dto = new PositionInfoDto();
                            dto.setP_id(parttimes.getId());
                            dto.setCategory(parttimes.getCategory());
                            dto.setContent(parttimes.getContent());
                            dto.setExp(parttimes.getExp());
                            dto.setPosition_name(parttimes.getPositionName());
                            dto.setPosition_status(parttimes.getPositionStatus());
                            dto.setArea(parttimes.getArea());
                            dto.setCreate_time(parttimes.getCreateTime());
                            dto.setRequirement(parttimes.getRequirement());
                            dto.setSalary(parttimes.getSalary());
                            dto.setSettlement(parttimes.getSettlement());
                            dto.setSignup_ddl(parttimes.getSignupDdl());
                            dto.setSlogan(parttimes.getSlogan());
                            dto.setUpdate_time(parttimes.getUpdateTime());
                            dto.setOp_id(parttimes.getOp().getId());
                            dto.setWork_time(parttimes.getWorkTime());
                            dto.setMemo("兼职获取成功");
                            res.add(dto);
                        }
                    }
                }
            } else {
                //兼职查找失败
                logger.warn("兼职信息获取失败");
                PositionInfoDto dto = new PositionInfoDto();
                dto.setMemo("兼职信息获取失败");
                res.add(dto);
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
        Parttimes parttimes = positionRepository.getPosition(p_id);
        if (parttimes != null) {
            res.setP_id(parttimes.getId());
            res.setCategory(parttimes.getCategory());
            res.setContent(parttimes.getContent());
            res.setExp(parttimes.getExp());
            res.setPosition_name(parttimes.getPositionName());
            res.setPosition_status(parttimes.getPositionStatus());
            res.setArea(parttimes.getArea());
            res.setCreate_time(parttimes.getCreateTime());
            res.setRequirement(parttimes.getRequirement());
            res.setSalary(parttimes.getSalary());
            res.setSettlement(parttimes.getSettlement());
            res.setSignup_ddl(parttimes.getSignupDdl());
            res.setSlogan(parttimes.getSlogan());
            res.setUpdate_time(parttimes.getUpdateTime());
            res.setOp_id(parttimes.getOp().getId());
            res.setWork_time(parttimes.getWorkTime());
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
                List<Signup> find = signupRepository.findExistsSignup(stu.getId(), p_id);

                //没报名过，继续后续操作
                if (find == null || find.size() == 0) {
                    System.out.println("兼职：" + dto.toString());
                    // 状态不是已招满和已结束，就可以报名
                    if (dto != null && (dto.getMemo()).equals("存在兼职")
                            && dto.getPosition_status().equals("已发布")) {
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
                        if (dto == null || !(dto.getMemo()).equals("存在兼职")) {
                            logger.warn("不存在该兼职");
                            res.setStu_id(telephone);
                            res.setMemo("不存在该兼职");
                        } else if (dto.getPosition_status().equals("已招满")) {
                            logger.warn("该兼职已招满");
                            res.setStu_id(telephone);
                            res.setMemo("该兼职已招满");
                        } else if (dto.getPosition_status().equals("已结束")) {
                            logger.warn("该兼职已结束");
                            res.setStu_id(telephone);
                            res.setMemo("该兼职已结束");
                        }
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
                        dto.setMemo("获取历史记录成功");
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
                        dto.setMemo("获取历史记录成功");
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

    @Override
    public List<PositionInfoDto> getPositionByCategory(String category) throws ParttimeServiceException {
        List<PositionInfoDto> res = new ArrayList<>();

        //输入的有数据
        if (category != null && !category.equals("")) {
            //找到该种类的所有兼职
            List<Parttimes> list = positionRepository.getAllPositionsByCategory(category);
            if (list != null && list.size() > 0) {
                for (Parttimes i : list) {
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
                    dto.setMemo("兼职获取成功");
                    res.add(dto);
                }
                System.out.println(list.toString());
            } else {
                logger.warn("暂无该类型兼职");
                PositionInfoDto dto = new PositionInfoDto();
                dto.setMemo("暂无该类型兼职");
                dto.setCategory(category);
                res.add(dto);
            }
        } else {
            logger.warn("请选择兼职种类后再筛选");
            PositionInfoDto dto = new PositionInfoDto();
            dto.setMemo("请选择兼职种类后再筛选");
            dto.setCategory(category);
            res.add(dto);
        }
        System.out.println(res.toString());

        return res;
    }

    @Override
    public List<PositionInfoToEmpDto> getAllPositionByEmp() throws ParttimeServiceException {
        List<PositionInfoToEmpDto> res = new ArrayList<>();
        //数据
        List<Parttimes> list = positionRepository.getAllPositions();
        if (list != null) {
            for (Parttimes i : list) {
                //获取该兼职的报名人数，从signup中找
                int num_signup = signupRepository.getNumOfSignup(i.getId());//根据p_id找
                int num_employment = signupRepository.getNumOfEmployment(i.getId());//根据p_id找

                PositionInfoToEmpDto dto = new PositionInfoToEmpDto();
                dto.setP_id(i.getId());
                dto.setCategory(i.getCategory());
                dto.setContent(i.getContent());
                dto.setNum_signup(num_signup);//已报名数
                dto.setNum_employment(num_employment);//已录取数
                dto.setNum_total(i.getNum());//名额数
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
                dto.setMemo("兼职获取成功");
                res.add(dto);
            }
            System.out.println(list.toString());
        } else {
            logger.warn("该账号不存在");
            PositionInfoToEmpDto dto = new PositionInfoToEmpDto();
            dto.setMemo("该账号不存在");
            res.add(dto);
        }
        System.out.println(res.toString());

        return res;
    }

    @Override
    public List<PositionInfoToEmpDto> getAllPositionByEmpId(String emp_id) throws ParttimeServiceException {
        List<PositionInfoToEmpDto> res = new ArrayList<>();

        //有输入
        if (emp_id != null && !emp_id.equals("")) {
            //数据
            List<Parttimes> list = positionRepository.getAllPositionsByEmpId(emp_id);
            if (list != null) {
                for (Parttimes i : list) {
                    //获取该兼职的报名人数，从signup中找
                    int num_signup = signupRepository.getNumOfSignup(i.getId());//根据p_id找
                    int num_employment = signupRepository.getNumOfEmployment(i.getId());//根据p_id找

                    PositionInfoToEmpDto dto = new PositionInfoToEmpDto();
                    dto.setP_id(i.getId());
                    dto.setCategory(i.getCategory());
                    dto.setContent(i.getContent());
                    dto.setNum_signup(num_signup);//已报名数
                    dto.setNum_employment(num_employment);//已录取数
                    dto.setNum_total(i.getNum());//名额数
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
                    dto.setMemo("兼职获取成功");
                    res.add(dto);
                }
                System.out.println(list.toString());
            } else {
                logger.warn("该账号不存在");
                PositionInfoToEmpDto dto = new PositionInfoToEmpDto();
                dto.setMemo("该账号不存在");
                res.add(dto);
            }
        } else {
            logger.warn("请检查输入");
            PositionInfoToEmpDto dto = new PositionInfoToEmpDto();
            dto.setMemo("请检查输入");
            res.add(dto);
        }

        System.out.println(res.toString());

        return res;
    }

    @Override
    public UnitInfoDto getUnitInfoByUnitName(String op_id) throws ParttimeServiceException {
        UnitInfoDto res = new UnitInfoDto();
        if (op_id != null && !op_id.equals("")) {
            //根据op_id找到u_id
            Employer emp = logAndRegByEmpRepository.findEmployerByTelephone(op_id);
            if (emp != null) {
                //从emp实体中找到unit实体
                Unit unit = emp.getU();
                if (unit != null) {
                    //找到了，构造res
                    res.setU_id(unit.getId());
                    res.setUnit_name(unit.getUnitName());
                    res.setDescriptions(unit.getDescriptions());
                    res.setJob_nums(unit.getJobNums());
                    res.setLoc(unit.getLoc());
                    res.setMemo("获取单位信息成功");
                } else {
                    logger.warn("不存在该单位");
                    res.setU_id(0);
                    res.setMemo("不存在该单位");
                }
            } else {
                logger.warn("不存在该兼职发布者");
                res.setU_id(0);
                res.setMemo("不存在该兼职发布者");
            }
        } else {
            logger.warn("请检查输入信息");
            res.setU_id(0);
            res.setMemo("请检查输入信息");
        }
        return res;
    }

    @Override
    public PositionInfoToEmpDto publishParttime(PublishInputDto input) throws ParttimeServiceException, ParseException {
        PositionInfoToEmpDto res = new PositionInfoToEmpDto();

        if (input != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String op_id = input.getOp_id();
            String position_name = input.getPosition_name();
            String category = input.getCategory();
            String salary = input.getSalary();
            String area = input.getArea();
            String exp = input.getExp();
            String content = input.getContent();
            String requirement = input.getRequirement();
            String signup_ddl_input = input.getSignup_ddl();
            String slogan = input.getSlogan();
            String work_time = input.getWork_time();
            String settlement = input.getSettlement();
            String create_time_input = input.getCreate_time();
            //string转Date
            Date signup_ddl = sdf.parse(signup_ddl_input);
            Date create_time = sdf.parse(create_time_input);
            int num = input.getNum_total();//名额数

            //找到这个操作者
            Employer emp = logAndRegByEmpRepository.findEmployerByTelephone(op_id);
            if (emp != null) {
                //存在操作者，更新数据库
                positionRepository.publishAParttime(op_id, position_name, num, category, salary,
                        area, exp, content, requirement, signup_ddl, slogan, work_time, settlement,
                        "已发布", create_time, create_time);//更新时间设置为创建时间

                //找到刚创建的兼职
                Parttimes createdParttimes = positionRepository.getLatestPosition();
                if (createdParttimes != null) {
                    //更新该操作员的unit中的job_nums字段值
                    Unit unit = unitRepository.findUnitByUnitId(emp.getU().getId());

                    if (unit != null) {
                        //存在
                        unitRepository.addJobNums(unit.getId());//更新job_nums

                        //构造返回的res
                        res.setP_id(createdParttimes.getId());
                        res.setOp_id(createdParttimes.getOp().getId());
                        res.setPosition_name(createdParttimes.getPositionName());
                        res.setCategory(createdParttimes.getCategory());
                        res.setSalary(createdParttimes.getSalary());
                        res.setArea(createdParttimes.getArea());
                        res.setExp(createdParttimes.getExp());
                        res.setContent(createdParttimes.getContent());
                        res.setRequirement(createdParttimes.getRequirement());
                        res.setSignup_ddl(createdParttimes.getSignupDdl());
                        res.setSlogan(createdParttimes.getSlogan());
                        res.setWork_time(createdParttimes.getWorkTime());
                        res.setSettlement(createdParttimes.getSettlement());
                        res.setPosition_status(createdParttimes.getPositionStatus());
                        res.setCreate_time(createdParttimes.getCreateTime());
                        res.setUpdate_time(createdParttimes.getUpdateTime());
                        res.setNum_total(createdParttimes.getNum());
                        res.setMemo("发布成功");
                    } else {
                        logger.warn("该兼职发布者不存在单位");
                        res.setP_id(0);
                        res.setMemo("该兼职发布者不存在单位");
                    }
                } else {
                    logger.warn("发布中发生异常");
                    res.setP_id(0);
                    res.setMemo("发布中发生异常");
                }
            } else {
                logger.warn("不存在该兼职发布者");
                res.setP_id(0);
                res.setMemo("不存在该兼职发布者");
            }
        } else {
            logger.warn("请检查输入的表单信息是否完整");
            res.setP_id(0);
            res.setMemo("请检查输入的表单信息是否完整");
        }
        return res;
    }

    @Override
    public PositionInfoToEmpDto undercarriageParttime(UndercarriageInputDto undercarriageInputDto) throws ParttimeServiceException, ParseException {
        PositionInfoToEmpDto res = new PositionInfoToEmpDto();

        if (undercarriageInputDto != null) {
            //获取输入的内容
            String telephone = undercarriageInputDto.getOp_id();
            int p_id = undercarriageInputDto.getP_id();

            //根据telephone找用户，判断是否存在
            Employer emp = logAndRegByEmpRepository.findEmployerByTelephone(telephone);

            if (emp != null) {
                //存在该操作者，去找该操作员是否是该兼职的管理者
                Parttimes hasAuthority = positionRepository.checkIsTheManager(telephone, p_id);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                //是负责人,且兼职不是已结束状态
                if (hasAuthority != null && !hasAuthority.getPositionStatus().equals("已结束")) {
                    //1.先判断是否有用户报名了改兼职，若有，将报名状态改为”已取消“，再下架
                    List<Signup> list = signupRepository.getAllSignupByPId(p_id);//根据p_id找到所有报名该兼职的记录
                    if (list.size() > 0) {
                        //有人报名了该兼职
                        for (Signup signup : list) {
                            if (signup.getSignupStatus().equals("已报名")) {
                                signupRepository.cancelSignup(sdf.parse(sdf.format(new Date())), signup.getId());//取消该兼职
                            }
                        }
                        //此时，都取消了，就下架
                        positionRepository.updatePositionStatus("已结束", sdf.parse(sdf.format(new Date())), hasAuthority.getId());
                    } else {
                        //2.若没有报名该兼职的直接下架
                        positionRepository.updatePositionStatus("已结束", sdf.parse(sdf.format(new Date())), hasAuthority.getId());
                    }

                    //更新该操作员的unit中的job_nums字段值
                    Unit unit = unitRepository.findUnitByUnitId(emp.getU().getId());

                    if (unit != null) {
                        //存在
                        unitRepository.minusJobNums(unit.getId());//更新job_nums

                        //构造res
                        res.setP_id(hasAuthority.getId());
                        res.setPosition_status("已结束");
                        res.setMemo("下架成功");
                    } else {
                        logger.warn("该兼职发布者不存在单位");
                        res.setP_id(0);
                        res.setMemo("该兼职发布者不存在单位");
                    }
                } else {
                    //不是负责人，不能操作
                    logger.warn("非兼职负责人不能操作");
                    res.setP_id(0);
                    res.setMemo("非兼职负责人不能操作");
                }
            } else {
                logger.warn("不存在该兼职发布者");
                res.setP_id(0);
                res.setMemo("不存在该兼职发布者");
            }
        } else {
            logger.warn("请检查输入的信息是否完整");
            res.setP_id(0);
            res.setMemo("请检查输入的信息是否完整");
        }
        return res;
    }

    @Override
    public List<SignupInfoToEmpDto> getSignupInfoByEmp(String emp_id) throws ParttimeServiceException, ParseException {
        List<SignupInfoToEmpDto> res = new ArrayList<>();

        if (emp_id != null && !emp_id.equals("")) {
            //有输入

            //1.找到该管理员所负责的所有兼职
            List<Parttimes> parttimes = positionRepository.getAllPositionManagedByEmp(emp_id);
            if (parttimes.size() > 0) {

                //有负责的兼职,遍历每个兼职
                for (Parttimes item : parttimes) {
                    //2.获取兼职信息
                    String p_name = item.getPositionName();
                    int num_employment = signupRepository.getNumOfEmployment(item.getId());//根据p_id找已录取数
                    int num_signup = signupRepository.getNumOfSignup(item.getId());
                    int num_total = item.getNum();

                    //3.遍历每个兼职，找到报名该兼职的signup
                    List<Signup> signups = signupRepository.getAllSignupByPId(item.getId());
                    if (signups.size() > 0) {

                        //存在报名数据
                        for (Signup item2 : signups) {

                            //4.遍历每个signup，获取报名的信息
                            String stu_id = item2.getStu().getId();//手机号
                            String signup_status = item2.getSignupStatus();//报名状态
                            Date signup_time = item2.getCreateTime();//报名时间

                            //5.找该报名的学生信息
                            Student student = stuInfoRepository.findStudentByTelephone(stu_id);
                            if (student != null) {
                                //存在该学生
                                String username = student.getStuName();
                                String gender = (student.getGender() == 1) ? "男" : "女";
                                String head = student.getHead();//头像
                                int age = student.getAge();
                                String grade = student.getGrade();

                                //6.获取该学生的简历信息
                                ResumeInfoDto resumeInfoDto = usersServiceImpl.getResume(student.getId());
                                if (resumeInfoDto != null && resumeInfoDto.getMemo().equals("存在简历")) {
                                    //存在简历，获取
                                    String current_area = resumeInfoDto.getCurrent_area();
                                    String exp = resumeInfoDto.getExp();
                                    Date upload_time = resumeInfoDto.getUpload_time();
                                    List<ResumeDetailDto> campusList = resumeInfoDto.getCampusExpList();
                                    List<ResumeDetailDto> educationBgList = resumeInfoDto.getEducationBgList();
                                    List<ResumeDetailDto> projectExpList = resumeInfoDto.getProjectExpList();
                                    List<ResumeDetailDto> professionalSkillList = resumeInfoDto.getProfessionalSkillList();

                                    //6-2.找到该简历的id
                                    Resumes resumes = resumesInfoRepository.findResumesByStuId(student.getId());
                                    //这里可以不用判断是否存在简历，因为resumesInfoDto此时存在
                                    String url = resumesInfoRepository.getUrlOfResumePhoto(resumes.getId());//根据r_id找到简历照片url
                                    if (url != null && !url.equals("")) {
                                        //存在简历照片，什么事都不做
                                    } else {
                                        //不存在简历照片，也就表示上面得到的4个list是空的
                                        System.out.println("学生" + student.getId() + "的简历已创建但尚未上传");
                                    }

                                    //7.构造返回的res
                                    SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                                    //兼职信息部分
                                    dto.setP_id(item.getId());
                                    dto.setP_name(p_name);
                                    dto.setNum_signup(num_signup);
                                    dto.setNum_employment(num_employment);
                                    dto.setNum_total(num_total);
                                    //用户信息部分
                                    dto.setStu_id(student.getId());
                                    dto.setUsername(username);
                                    dto.setGender(gender);
                                    dto.setHead(head);
                                    dto.setAge(age);
                                    dto.setGrade(grade);
                                    //简历部分
                                    dto.setExp(exp);
                                    dto.setCurrent_area(current_area);
                                    dto.setUrl(url);
                                    dto.setCampusExpList(campusList);
                                    dto.setEducationBgList(educationBgList);
                                    dto.setProjectExpList(projectExpList);
                                    dto.setProfessionalSkillList(professionalSkillList);
                                    //报名部分
                                    dto.setS_id(item2.getId());
                                    dto.setSignup_status(signup_status);
                                    dto.setSignup_time(signup_time);
                                    //备忘录
                                    dto.setMemo("获取报名信息成功");
                                    res.add(dto);//加到res列表中
                                } else {
                                    //不存在简历
                                    logger.warn("不存在简历");
                                    SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                                    dto.setP_id(item.getId());
                                    dto.setMemo("不存在简历");
                                    res.add(dto);
                                }
                            } else {
                                //不存在该学生
                                logger.warn("存在非法报名");
                                SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                                dto.setP_id(item.getId());
                                dto.setMemo("存在非法报名");
                                res.add(dto);
                            }
                        }
                    } else {
                        //无报名数据
                        logger.warn("暂时无人报名该兼职");
                        SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                        dto.setP_id(item.getId());
                        dto.setMemo("暂时无人报名该兼职");
                        res.add(dto);
                    }
                }
            } else {
                //无负责的兼职
                logger.warn("暂时无负责的兼职");
                SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                dto.setP_id(0);
                dto.setMemo("暂时无负责的兼职");
                res.add(dto);
            }
        } else {
            logger.warn("请检查输入的信息是否完整");
            SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
            dto.setP_id(0);
            dto.setMemo("请检查输入的信息是否完整");
            res.add(dto);
        }
        System.out.println("操作员" + emp_id + "所负责的所有兼职的报名数据："
                + res.toString());

        return res;
    }

    @Override
    public SignupInfoToEmpDto getASpecialSignupInfoByEmp(String emp_id, int s_id) throws ParttimeServiceException, ParseException {
        SignupInfoToEmpDto res = new SignupInfoToEmpDto();

        if (emp_id != null && !emp_id.equals("") && s_id > 0) {
            //有输入

            //1.找到指定报名对应的兼职
            Signup signup = signupRepository.findSignup(s_id);
            if (signup != null) {
                //存在该报名
                Parttimes parttimes = positionRepository.checkIsTheManager(emp_id, signup.getP().getId());
                if (parttimes != null) {
                    //是该兼职的负责人

                    //2.获取兼职信息
                    String p_name = parttimes.getPositionName();
                    int num_employment = signupRepository.getNumOfEmployment(signup.getP().getId());//根据p_id找已录取数
                    int num_signup = signupRepository.getNumOfSignup(signup.getP().getId());
                    int num_total = parttimes.getNum();

                    //3.获取报名的信息
                    String stu_id = signup.getStu().getId();//手机号
                    String signup_status = signup.getSignupStatus();//报名状态
                    Date signup_time = signup.getCreateTime();//报名时间

                    //4.找该报名的学生信息
                    Student student = stuInfoRepository.findStudentByTelephone(stu_id);
                    if (student != null) {
                        //存在该学生
                        String username = student.getStuName();
                        String gender = (student.getGender() == 1) ? "男" : "女";
                        String head = student.getHead();//头像
                        int age = student.getAge();
                        String grade = student.getGrade();

                        //5.获取该学生的简历信息
                        ResumeInfoDto resumeInfoDto = usersServiceImpl.getResume(student.getId());
                        if (resumeInfoDto != null && resumeInfoDto.getMemo().equals("存在简历")) {
                            //存在简历，获取
                            String current_area = resumeInfoDto.getCurrent_area();
                            String exp = resumeInfoDto.getExp();
                            Date upload_time = resumeInfoDto.getUpload_time();
                            List<ResumeDetailDto> campusList = resumeInfoDto.getCampusExpList();
                            List<ResumeDetailDto> educationBgList = resumeInfoDto.getEducationBgList();
                            List<ResumeDetailDto> projectExpList = resumeInfoDto.getProjectExpList();
                            List<ResumeDetailDto> professionalSkillList = resumeInfoDto.getProfessionalSkillList();

                            //5-2.找到该简历的id
                            Resumes resumes = resumesInfoRepository.findResumesByStuId(student.getId());
                            //这里可以不用判断是否存在简历，因为resumesInfoDto此时存在
                            String url = resumesInfoRepository.getUrlOfResumePhoto(resumes.getId());//根据r_id找到简历照片url
                            if (url != null && !url.equals("")) {
                                //存在简历照片，什么事都不做
                            } else {
                                //不存在简历照片，也就表示上面得到的4个list是空的
                                System.out.println("学生" + student.getId() + "的简历已创建但尚未上传");
                            }

                            //6.构造返回的res
                            SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                            //兼职信息部分
                            dto.setP_id(parttimes.getId());
                            dto.setP_name(p_name);
                            dto.setNum_signup(num_signup);
                            dto.setNum_employment(num_employment);
                            dto.setNum_total(num_total);
                            //用户信息部分
                            dto.setStu_id(student.getId());
                            dto.setUsername(username);
                            dto.setGender(gender);
                            dto.setHead(head);
                            dto.setAge(age);
                            dto.setGrade(grade);
                            //简历部分
                            dto.setExp(exp);
                            dto.setCurrent_area(current_area);
                            dto.setUrl(url);
                            dto.setCampusExpList(campusList);
                            dto.setEducationBgList(educationBgList);
                            dto.setProjectExpList(projectExpList);
                            dto.setProfessionalSkillList(professionalSkillList);
                            //报名部分
                            dto.setS_id(s_id);
                            dto.setSignup_status(signup_status);
                            dto.setSignup_time(signup_time);
                            //备忘录
                            res.setMemo("获取报名信息成功");
                        } else {
                            //不存在简历
                            logger.warn("不存在简历");
                            res.setP_id(0);
                            res.setMemo("不存在简历");
                        }
                    } else {
                        //不存在该学生
                        logger.warn("存在非法报名");
                        res.setP_id(parttimes.getId());
                        res.setMemo("存在非法报名");
                    }
                } else {
                    logger.warn("不存在该兼职");
                    res.setP_id(parttimes.getId());
                    res.setMemo("不存在该兼职");
                }
            } else {
                //无报名数据
                logger.warn("暂时无人报名该兼职");
                res.setP_id(0);
                res.setMemo("暂时无人报名该兼职");
            }
        } else {
            logger.warn("请检查输入的信息是否完整");
            res.setP_id(0);
            res.setMemo("请检查输入的信息是否完整");
        }

        return res;
    }

    @Override
    public List<SignupInfoToEmpDto> confirmSignup(ConfirmInputDto confirmInputDto) throws ParttimeServiceException, ParseException {
        List<SignupInfoToEmpDto> res = new ArrayList<>();

        if (confirmInputDto != null) {
            //有输入
            String emp_id = confirmInputDto.getEmp_id();
            int s_id = confirmInputDto.getS_id();

            //根据据s_id找到对应的p_id
            Signup signup = signupRepository.findSignup(s_id);
            if (signup != null) {
                //存在signup
                int p_id = signup.getP().getId();

                //由p_id找parttimes
                Parttimes parttimes = positionRepository.checkIsTheManager(emp_id, p_id);
                if (parttimes != null) {
                    //是该兼职的操作员，就录用该学生的报名，更新报名状态为“已录取”
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now = sdf.parse(sdf.format(new Date()));
                    signupRepository.confirmSignup(now, s_id);

                    //获取所有报名信息，实现页面刷新
                    List<SignupInfoToEmpDto> list = this.getSignupInfoByEmp(emp_id);
                    if (list.size() > 0 && list.get(0).getMemo().equals("获取报名信息成功")) {
                        res = list;
                        System.out.println(res.toString());
                    }
//                    //获取该报名的所有信息
//                    SignupInfoToEmpDto signupInfoToEmpDto=this.getASpecialSignupInfoByEmp(emp_id,s_id);
//                    if(signupInfoToEmpDto!=null && signupInfoToEmpDto.getMemo().equals("获取报名信息成功")){
//                        //
//                    }
                } else {
                    logger.warn("不能操作非负责的兼职");
                    SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                    dto.setP_id(0);
                    dto.setMemo("不能操作非负责的兼职");
                    res.add(dto);
                }
            } else {
                //不存在signup
                logger.warn("不存在该报名");
                SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                dto.setP_id(0);
                dto.setMemo("不存在该报名");
                res.add(dto);
            }

        } else {
            logger.warn("请检查输入的信息是否完整");
            SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
            dto.setP_id(0);
            dto.setMemo("请检查输入的信息是否完整");
            res.add(dto);
        }

        return res;
    }

    @Override
    public List<SignupInfoToEmpDto> rejectSignup(RejectInputDto rejectInputDto) throws ParttimeServiceException, ParseException {
        List<SignupInfoToEmpDto> res = new ArrayList<>();

        if (rejectInputDto != null) {
            //有输入
            String emp_id = rejectInputDto.getEmp_id();
            int s_id = rejectInputDto.getS_id();

            //根据据s_id找到对应的p_id
            Signup signup = signupRepository.findSignup(s_id);
            if (signup != null) {
                //存在signup
                int p_id = signup.getP().getId();

                //由p_id找parttimes
                Parttimes parttimes = positionRepository.checkIsTheManager(emp_id, p_id);
                if (parttimes != null) {
                    //是该兼职的操作员，就录用该学生的报名，更新报名状态为“已录取”
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now = sdf.parse(sdf.format(new Date()));
                    signupRepository.rejectSignup(now, s_id);

                    //获取所有报名信息，实现页面刷新
                    List<SignupInfoToEmpDto> list = this.getSignupInfoByEmp(emp_id);
                    if (list.size() > 0 && list.get(0).getMemo().equals("获取报名信息成功")) {
                        res = list;
                        System.out.println(res.toString());
                    }
//                    //获取该报名的所有信息
//                    SignupInfoToEmpDto signupInfoToEmpDto=this.getASpecialSignupInfoByEmp(emp_id,s_id);
//                    if(signupInfoToEmpDto!=null && signupInfoToEmpDto.getMemo().equals("获取报名信息成功")){
//                        //
//                    }
                } else {
                    logger.warn("不能操作非负责的兼职");
                    SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                    dto.setP_id(0);
                    dto.setMemo("不能操作非负责的兼职");
                    res.add(dto);
                }
            } else {
                //不存在signup
                logger.warn("不存在该报名");
                SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
                dto.setP_id(0);
                dto.setMemo("不存在该报名");
                res.add(dto);
            }
        } else {
            logger.warn("请检查输入的信息是否完整");
            SignupInfoToEmpDto dto = new SignupInfoToEmpDto();
            dto.setP_id(0);
            dto.setMemo("请检查输入的信息是否完整");
            res.add(dto);
        }

        return res;
    }
}