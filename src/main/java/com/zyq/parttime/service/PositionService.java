package com.zyq.parttime.service;

import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.position.*;
import com.zyq.parttime.form.unit.UnitInfoDto;
//import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.List;

@Service
public interface PositionService {
    //获取所有兼职
    List<PositionInfoDto> getAllPosition() throws ParttimeServiceException;

    //获取所有兼职——按照意向兼职排序
    List<PositionInfoDto> getAllPositionByIntentions(GetPositionDto getPositionDto) throws ParttimeServiceException;

    //获取指定兼职
    PositionInfoDto getPosition(int p_id) throws ParttimeServiceException;

    //报名兼职
    SignupReturnDto signup(SignupDto signupDto) throws ParttimeServiceException, ParseException;

    //取消报名兼职
    CancelReturnDto cancel(CancelDto cancelDto) throws ParttimeServiceException, ParseException;

    //获取所有报名
    List<SignupReturnDto> history(HistoryDto historyDto) throws ParttimeServiceException, ParseException;

    //获取指定状态的报名
    List<SignupReturnDto> getOneStatus(String telephone, String signup_status) throws ParttimeServiceException, ParseException;

    //查看学生是否有报名某个兼职
    CanSignupDto getSpecialSignup(String telephone, int p_id) throws ParttimeServiceException, ParseException;

    //按种类筛选兼职
    List<PositionInfoDto> getPositionByCategory(String category) throws ParttimeServiceException;

    //获取所有兼职
    List<PositionInfoToEmpDto> getAllPositionByAdmin(String emp_id) throws ParttimeServiceException;

    //获取所有兼职String emp_id, int p_id
    List<PositionInfoToEmpDto> getAllPositionByEmpId(String emp_id) throws ParttimeServiceException;

    //根据unit_name获取单位信息
    UnitInfoDto getUnitInfoByUnitName(String op_id) throws ParttimeServiceException;

    //发布兼职
    PositionInfoToEmpDto publishParttime(PublishInputDto input) throws ParttimeServiceException, ParseException;

    //编辑兼职
    PositionInfoToEmpDto editParttime(EditInputDto input) throws ParttimeServiceException, ParseException;

    //下架兼职
    PositionInfoToEmpDto undercarriageParttime(UndercarriageInputDto undercarriageInputDto) throws ParttimeServiceException, ParseException;

    //获取报名信息
    List<SignupInfoToEmpDto> getSignupInfoByEmp(String emp_id) throws ParttimeServiceException, ParseException;

    //获取报名信息
    List<SignupInfoToEmpDto> getSignupInfoByAdmin(String emp_id) throws ParttimeServiceException, ParseException;

    //获取指定的一个报名信息
    SignupInfoToEmpDto getASpecialSignupInfoByEmp(String emp_id, int p_id) throws ParttimeServiceException, ParseException;

    //录取
    SignupInfoToEmpDto confirmSignup(ConfirmInputDto confirmInputDto) throws ParttimeServiceException, ParseException;

    //婉拒
    SignupInfoToEmpDto rejectSignup(RejectInputDto rejectInputDto) throws ParttimeServiceException, ParseException;

//    //推荐兼职
//    List<PositionInfoDto> recommendParttimes(String stu_id) throws ParttimeServiceException, ParseException;

}
