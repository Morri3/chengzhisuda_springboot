package com.zyq.parttime.service;

import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.resumemanage.*;
import com.zyq.parttime.form.userinfomanage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

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
    ResumeUploadCallbackDto uploadResume(MultipartFile file, String telephone, String upload_time)
            throws ParttimeServiceException, Exception;

    //编辑简历（个人信息）——学生
    ResumeEditCallbackDto editPersonal(EditPersonalDto editPersonalDto) throws ParseException, Exception;

    //编辑简历（校园经历）——学生
    GetCampusDto editCampus(EditCampusDto editCampusDto) throws ParseException, Exception;

    //编辑简历（教育背景）——学生
    GetEducationDto editEducation(EditEducationDto editEducationDto) throws ParseException, Exception;

    //编辑简历（项目经历）——学生
    GetProgramDto editProgram(EditProgramDto editProgramDto) throws ParseException, Exception;

    //编辑简历（专业技能）——学生
    GetSkillsDto editSkills(EditSkillsDto editSkillsDto) throws ParseException, Exception;

    //删除简历详情——学生
    DeleteDetailCallbackDto deleteDetail(DeleteDetailDto deleteDetailDto) throws ParseException, Exception;

    //删除简历——学生
    DeleteResumeCallbackDto deleteResume(DeleteResumeDto deleteResumeDto) throws ParseException, Exception;

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