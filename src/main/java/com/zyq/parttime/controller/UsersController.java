package com.zyq.parttime.controller;

import com.alibaba.fastjson.JSON;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.intention.EditIntentionDto;
import com.zyq.parttime.form.intention.IntentionDto;
import com.zyq.parttime.form.resumemanage.*;
import com.zyq.parttime.form.userinfomanage.*;
import com.zyq.parttime.result.ExceptionMsg;
import com.zyq.parttime.result.ResponseData;
import com.zyq.parttime.service.LogAndRegService;
import com.zyq.parttime.service.UsersService;
import com.zyq.parttime.service.impl.UsersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    //TODO 个人信息查看-学生
    @RequestMapping(value = "/info/get_stu", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getStuInfo(@RequestParam String telephone) {
        StuInfoDto res = usersService.getStuInfo(telephone);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 个人信息查看-兼职发布者/管理员
    @RequestMapping(value = "/info/get_emp", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getEmpInfo(@RequestParam String telephone) {
        EmpInfoDto res = usersService.getEmpInfo(telephone);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 个人信息编辑-学生
    @RequestMapping(value = "/info/edit_stu", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editStuInfo(@RequestBody EditInfoDto editInfoDto) throws ParseException {
        StuInfoDto res = usersService.editStuInfo(editInfoDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 修改密码-学生
    @RequestMapping(value = "/info/modify_stu_pwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData modifyStuPwd(@RequestBody ModifyPwdDto modifyPwdDto) throws ParseException {
        StuInfoDto res = usersService.modifyStuPwd(modifyPwdDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 个人信息编辑-兼职发布者/管理员
    @RequestMapping(value = "/info/edit_emp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editEmpInfo(@RequestBody EditEmpInfoDto editEmpInfoDto) throws ParseException {
        EmpInfoDto res = usersService.editEmpInfo(editEmpInfoDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 修改密码-兼职发布者/管理员
    @RequestMapping(value = "/info/modify_emp_pwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData modifyEmpPwd(@RequestBody ModifyPwdDto modifyPwdDto) throws ParseException {
        EmpInfoDto res = usersService.modifyEmpPwd(modifyPwdDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 创建简历，简历上传stu_id-学生
    @RequestMapping(value = "/resumes/create", method = RequestMethod.POST)
    @ResponseBody
//    public ResponseData createResume(@RequestParam String telephone, @RequestParam String upload_time)
    public ResponseData createResume(@RequestBody CreateResumeDto createResumeDto)throws ParseException, Exception {
//        ResumeInfoDto res = usersService.createResume(telephone, upload_time);
        ResumeInfoDto res = usersService.createResume(createResumeDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历查看-学生
    @RequestMapping(value = "/resumes/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getResume(@RequestParam String telephone) throws ParseException {
        ResumeInfoDto res = usersService.getResume(telephone);
        System.out.println(JSON.toJSONString(res));
        System.out.println(res);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历上传 step1上传账号+上传时间-学生
    @RequestMapping(value = "/resumes/upload/stu_info", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData uploadResumeWithStuInfo(@RequestParam String telephone,@RequestParam String upload_time)
            throws ParseException, Exception {
        String res = usersService.uploadResumeWithStuInfo(telephone, upload_time);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历上传 step2上传图片-学生
    @RequestMapping(value = "/resumes/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData uploadResume(MultipartFile file) throws ParseException, Exception {
        ResumeUploadCallbackDto res = usersService.uploadResume(file);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历编辑（个人信息）-学生
    @RequestMapping(value = "/resumes/edit_personal", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editPersonal(@RequestBody EditPersonalDto editPersonalDto) throws ParseException, Exception {
        ResumeEditCallbackDto res = usersService.editPersonal(editPersonalDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历编辑（校园经历）-学生
    @RequestMapping(value = "/resumes/edit_campus", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editCampus(@RequestBody EditCampusDto editCampusDto) throws ParseException, Exception {
        GetCampusDto res = usersService.editCampus(editCampusDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历编辑（教育背景）-学生
    @RequestMapping(value = "/resumes/edit_education", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editEducation(@RequestBody EditEducationDto editEducationDto) throws ParseException, Exception {
        GetEducationDto res = usersService.editEducation(editEducationDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历编辑（项目经历）-学生
    @RequestMapping(value = "/resumes/edit_program", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editProgram(@RequestBody EditProgramDto editProgramDto) throws ParseException, Exception {
        GetProgramDto res = usersService.editProgram(editProgramDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历编辑（专业技能）-学生
    @RequestMapping(value = "/resumes/edit_skills", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editSkills(@RequestBody EditSkillsDto editSkillsDto) throws ParseException, Exception {
        GetSkillsDto res = usersService.editSkills(editSkillsDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历详情删除-学生
    @RequestMapping(value = "/resumes/delete_detail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteDetail(@RequestBody DeleteDetailDto deleteDetailDto) throws ParseException, Exception {
        DeleteDetailCallbackDto res = usersService.deleteDetail(deleteDetailDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历详情增加-学生
    @RequestMapping(value = "/resumes/add_detail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addDetail(@RequestBody AddDetailDto addDetailDto) throws ParseException, Exception {
        AddDetailCallbackDto res = usersService.addDetail(addDetailDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历删除-学生
    @RequestMapping(value = "/resumes/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteResume(@RequestBody DeleteResumeDto deleteResumeDto) throws ParseException, Exception {
        DeleteResumeCallbackDto res = usersService.deleteResume(deleteResumeDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 获取意向兼职-学生
    @RequestMapping(value = "/intention/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getIntention(@RequestParam String telephone) throws ParseException, Exception {
        List<IntentionDto> res = usersService.getIntention(telephone);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 编辑意向兼职-学生
    @RequestMapping(value = "/intention/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editIntention(@RequestBody EditIntentionDto editIntentionDto) throws ParseException, Exception {
        List<IntentionDto> res = usersService.editIntention(editIntentionDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //下面是minio的相关操作

    //TODO minio创建桶
    @RequestMapping(value = "/minio/create_bucket", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData createBucket(@RequestBody String bucketName) throws ParseException, Exception {
        Boolean res = usersService.createBucket(bucketName);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO minio删除桶
    @RequestMapping(value = "/minio/delete_bucket", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteBucket(@RequestBody String bucketName) throws ParseException, Exception {
        Boolean res = usersService.deleteBucket(bucketName);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO minio上传文件
    @RequestMapping(value = "/minio/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData upload(@RequestBody MultipartFile file) throws ParseException, Exception {
        String res = usersService.upload(file);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO minio下载文件
    @RequestMapping(value = "/minio/download", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData download(@RequestBody String fileName, HttpServletResponse res) throws ParseException, Exception {
        String result = usersService.download(fileName, res);
        return new ResponseData(ExceptionMsg.SUCCESS, result);
    }

    //TODO minio删除文件
    @RequestMapping(value = "/minio/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteFile(@RequestBody String fileName) throws ParseException, Exception {
        String res = usersService.deleteFile(fileName);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

//    public ResponseData uploadResume(MultipartFile file, String telephone,
//                                     String upload_time) throws ParseException, Exception {
//        ResumeUploadCallbackDto res = usersService.uploadResume(file, telephone, upload_time);
////    public ResponseData uploadResume(@RequestBody File file, @RequestParam String telephone,
////                                     @RequestParam String upload_time) throws ParseException, Exception {
//
////        ResumeUploadCallbackDto res = usersService.uploadResume(uploadInputDto);
//        return new ResponseData(ExceptionMsg.SUCCESS, res);
//    }
}
