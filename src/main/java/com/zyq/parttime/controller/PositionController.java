package com.zyq.parttime.controller;

import com.zyq.parttime.form.position.*;
import com.zyq.parttime.form.unit.UnitInfoDto;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin
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

    //TODO 获取所有兼职，按意向兼职排序-学生
    @RequestMapping(value = "/stu/get_intention", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData getAllPositionByIntentions(@RequestBody GetPositionDto getPositionDto) {
        List<PositionInfoDto> res = positionService.getAllPositionByIntentions(getPositionDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取指定兼职-学生
    @RequestMapping(value = "/stu/get_one", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getPosition(@RequestParam int p_id) {
        PositionInfoDto res = positionService.getPosition(p_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 报名兼职-学生
    @RequestMapping(value = "/stu/signup", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData signup(@RequestBody SignupDto signupDto) throws ParseException {
        SignupReturnDto res = positionService.signup(signupDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 取消报名-学生
    @RequestMapping(value = "/stu/cancel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData cancel(@RequestBody CancelDto cancelDto) throws ParseException {
        CancelReturnDto res = positionService.cancel(cancelDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 查看所有报名-学生
    @RequestMapping(value = "/stu/history", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData history(@RequestBody HistoryDto historyDto) throws ParseException {
        List<SignupReturnDto> res = positionService.history(historyDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 查看指定状态的报名-学生
    @RequestMapping(value = "/stu/signup_one", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getOneStatus(@RequestParam String telephone, @RequestParam String signup_status) throws ParseException {
        List<SignupReturnDto> res = positionService.getOneStatus(telephone, signup_status);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 查看学生是否有报名某个兼职-学生
    @RequestMapping(value = "/stu/signup_special", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getSpecialSignup(@RequestParam String telephone, @RequestParam int p_id) throws ParseException {
        CanSignupDto res = positionService.getSpecialSignup(telephone, p_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 按种类筛选兼职-学生
    @RequestMapping(value = "/stu/get_category", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getPositionByCategory(@RequestParam String category) {
        List<PositionInfoDto> res = positionService.getPositionByCategory(category);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取所有兼职-管理员
    @RequestMapping(value = "/emp/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getAllPositionByEmp() {
        List<PositionInfoToEmpDto> res = positionService.getAllPositionByEmp();
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取自己管理的所有兼职-兼职发布者
    @RequestMapping(value = "/emp/get_own", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getAllPositionByEmpId(@RequestParam String emp_id) {
        List<PositionInfoToEmpDto> res = positionService.getAllPositionByEmpId(emp_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 根据op_id获取单位信息-管理员
    @RequestMapping(value = "/unit/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getUnitInfoByUnitName(@RequestParam String op_id) {
        UnitInfoDto res = positionService.getUnitInfoByUnitName(op_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 发布兼职-管理员
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData publishParttime(@RequestBody PublishInputDto input) throws ParseException {
        PositionInfoToEmpDto res = positionService.publishParttime(input);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 下架兼职-管理员
    @RequestMapping(value = "/undercarriage", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData undercarriageParttime(@RequestBody UndercarriageInputDto undercarriageInputDto) throws ParseException {
        PositionInfoToEmpDto res = positionService.undercarriageParttime(undercarriageInputDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取自己管理的所有兼职的所有报名信息-兼职发布者/管理员
    @RequestMapping(value = "/signup/get_info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getSignupInfoByEmp(@RequestParam String emp_id) throws ParseException {
        List<SignupInfoToEmpDto> res = positionService.getSignupInfoByEmp(emp_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取某个兼职的某个报名信息-兼职发布者/管理员
    @RequestMapping(value = "/signup/get_one_info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getASpecialSignupInfoByEmp(@RequestParam String emp_id, @RequestParam int p_id) throws ParseException {
        SignupInfoToEmpDto res = positionService.getASpecialSignupInfoByEmp(emp_id, p_id);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 录取-兼职发布者/管理员
    @RequestMapping(value = "/signup/confirm", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData confrimSignup(@RequestBody ConfirmInputDto confirmInputDto) throws ParseException {
        List<SignupInfoToEmpDto> res = positionService.confirmSignup(confirmInputDto);//录取后返回报名的信息
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 婉拒-兼职发布者/管理员
    @RequestMapping(value = "/signup/reject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData rejectSignup(@RequestBody RejectInputDto rejectInputDto) throws ParseException {
        List<SignupInfoToEmpDto> res = positionService.rejectSignup(rejectInputDto);//录取后返回报名的信息
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }
}
