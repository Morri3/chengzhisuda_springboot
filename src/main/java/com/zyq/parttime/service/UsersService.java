package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.resumemanage.GetResumeDto;
import com.zyq.parttime.form.resumemanage.ResumeInfoDto;
import com.zyq.parttime.form.resumemanage.ResumeUploadCallbackDto;
import com.zyq.parttime.form.userinfomanage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.ParseException;

@Service
public interface UsersService {
    //个人信息查看-学生
    StuInfoDto getStuInfo(GetInfoDto getInfoDto) throws ParttimeServiceException;

    //个人信息查看-兼职发布者/管理员
    EmpInfoDto getEmpInfo(GetInfoDto getInfoDto) throws ParttimeServiceException;

    //个人信息编辑-学生
    StuInfoDto editStuInfo(EditInfoDto editInfoDto) throws ParttimeServiceException, ParseException;

    //修改密码-学生
    StuInfoDto modifyStuPwd(ModifyPwdDto modifyPwdDto) throws ParttimeServiceException;

    //个人信息编辑-兼职发布者/管理员

    //修改密码-兼职发布者/管理员

    //简历查看-学生
    ResumeInfoDto getResume(GetResumeDto getResumeDto) throws ParttimeServiceException;

    //简历上传-学生
    ResumeUploadCallbackDto uploadResume(MultipartFile file,String telephone) throws ParttimeServiceException,Exception;

    //minio创建桶
    Boolean createBucket(String bucketName) throws ParttimeServiceException, Exception;

    //minio删除桶
    Boolean deleteBucket(String bucketName) throws ParttimeServiceException, Exception;

    //minio上传文件
    String upload(MultipartFile file) throws ParttimeServiceException, Exception;

    //minio下载文件
    String download(String fileName, HttpServletResponse res) throws ParttimeServiceException, Exception;

    //minio删除文件
    String deleteFile(String fileName) throws ParttimeServiceException, Exception;
}