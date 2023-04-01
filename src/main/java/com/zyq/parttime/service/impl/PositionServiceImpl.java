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
import java.util.Date;
import java.util.List;

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
                Signup find = signupRepository.findExistsSignup(stu.getId(), p_id);
                if (find == null) {
                    System.out.println(dto.toString());
                    System.out.println(dto.getMemo());
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
                } else {
                    logger.warn("该用户已报名");
                    res.setStu_id(telephone);
                    res.setMemo("该用户已报名");
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
                    signupRepository.cancelSignup(s_id);

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
    public List<SignupReturnDto> getOneStatus(String telephone,String signup_status) throws ParttimeServiceException, ParseException{
        List<SignupReturnDto> res = new ArrayList<>();

        if (telephone != null) {
            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //查询该用户所有报名
                List<Signup> list = signupRepository.getSignupByStatus(telephone,signup_status);
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
}