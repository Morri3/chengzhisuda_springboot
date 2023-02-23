package com.zyq.parttime.controller;

import com.zyq.parttime.exception.ParttimeServiceException;
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
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    //TODO 个人信息查看-学生
    @RequestMapping(value = "/info/get_stu", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getStuInfo(@RequestBody GetInfoDto getInfoDto) {
        StuInfoDto res = usersService.getStuInfo(getInfoDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 个人信息查看-兼职发布者/管理员
    @RequestMapping(value = "/info/get_emp", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getEmpInfo(@RequestBody GetInfoDto getInfoDto) {
        EmpInfoDto res = usersService.getEmpInfo(getInfoDto);
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

    //TODO 修改密码-兼职发布者/管理员

    //TODO 简历查看-学生
    @RequestMapping(value = "/resumes/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData getResume(@RequestBody GetResumeDto getResumeDto) throws ParseException {
        ResumeInfoDto res = usersService.getResume(getResumeDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历上传-学生
    @RequestMapping(value = "/resumes/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData uploadResume(@RequestBody MultipartFile file, @RequestParam String telephone,
                                     @RequestBody Date upload_time) throws ParseException, Exception {
        ResumeUploadCallbackDto res = usersService.uploadResume(file, telephone, upload_time);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

    //TODO 简历编辑（个人信息）-学生
    @RequestMapping(value = "/resumes/edit_personal", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editPersonal(@RequestBody EditPersonalDto editPersonalDto) throws ParseException, Exception {
        ResumeEditCallbackDto res = usersService.editPersonal(editPersonalDto);
        return new ResponseData(ExceptionMsg.SUCCESS, res);
    }

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
}
