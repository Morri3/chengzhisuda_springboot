package com.zyq.parttime.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Student;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.userinfomanage.*;
import com.zyq.parttime.repository.userinfomanage.EmpInfoRepository;
import com.zyq.parttime.repository.userinfomanage.StuInfoRepository;
import com.zyq.parttime.service.LogAndRegService;
import com.zyq.parttime.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class UsersServiceImpl implements UsersService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private StuInfoRepository stuInfoRepository;
    @Autowired
    private EmpInfoRepository empInfoRepository;

    @Override
    public StuInfoDto getStuInfo(GetInfoDto getInfoDto) throws ParttimeServiceException {
        StuInfoDto res = new StuInfoDto();

        if (getInfoDto != null) {
            //获取传入的手机号
            String telephone = getInfoDto.getTelephone();

            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //获取该用户的个人信息，填充到res
                String stu_name = stu.getStuName();
                int gender = stu.getGender();
                String emails = stu.getEmails();
                int age = stu.getAge();
                String school_name = stu.getSchoolName();
                String sno = stu.getSno();
                String grade = stu.getGrade();

                res.setStu_name(stu_name);
                res.setGender(gender);
                res.setTelephone(telephone);
                res.setEmails(emails);
                res.setAge(age);
                res.setSchool_name(school_name);
                res.setSno(sno);
                res.setGrade(grade);
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }
        return res;
    }

    @Override
    public EmpInfoDto getEmpInfo(GetInfoDto getInfoDto) throws ParttimeServiceException {
        EmpInfoDto res = new EmpInfoDto();

        if (getInfoDto != null) {
            //获取传入的手机号
            String telephone = getInfoDto.getTelephone();

            //查找该用户是否存在
            Employer emp = empInfoRepository.findEmployerByTelephone(telephone);
            if (emp != null) {//存在
                //获取该用户的个人信息，填充到res
                String emp_name = emp.getEmpName();
                int gender = emp.getGender();
                String emails = emp.getEmails();
                int age = emp.getAge();
                String jno = emp.getJno();
                String unit_name = emp.getU().getUnitName();
                String unit_descriptions = emp.getU().getDescriptions();
                String unit_loc = emp.getU().getLoc();
                int job_nums = emp.getU().getJobNums();

                res.setEmp_name(emp_name);
                res.setGender(gender);
                res.setEmails(emails);
                res.setAge(age);
                res.setTelephone(telephone);
                res.setJno(jno);
                res.setUnit_name(unit_name);
                res.setUnit_descriptions(unit_descriptions);
                res.setUnit_loc(unit_loc);
                res.setJob_nums(job_nums);
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }
        return res;
    }

    @Override
    public StuInfoDto editStuInfo(EditInfoDto editInfoDto) throws ParttimeServiceException, ParseException {
        StuInfoDto res = new StuInfoDto();

        if (editInfoDto != null) {
            //获取传入的dto的字段
            String telephone = editInfoDto.getTelephone();
            int gender = editInfoDto.getGender();
            int age = editInfoDto.getAge();
            String emails = editInfoDto.getEmails();
            String entrance_date = editInfoDto.getEntrance_date();
            String graduation_date = editInfoDto.getGraduation_date();

            //入学年月、毕业年月
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月");
            Date entrance = sdf2.parse(entrance_date);
            Date graduation = sdf2.parse(graduation_date);

            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //修改用户信息
                stuInfoRepository.editStuInfo(gender, age, emails, entrance, graduation, telephone);

                //填充返回的dto
                res.setTelephone(telephone);
                res.setMemo("修改个人信息成功");
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }
        return res;
    }

    @Override
    public StuInfoDto modifyStuPwd(ModifyPwdDto modifyPwdDto) throws ParttimeServiceException {
        StuInfoDto res = new StuInfoDto();

        if (modifyPwdDto != null) {
            //获取传入的dto的信息
            String telephone = modifyPwdDto.getTelephone();
            String old_pwd = modifyPwdDto.getOld_pwd();
            String new_pwd = modifyPwdDto.getNew_pwd();
            String new_pwd2 = modifyPwdDto.getNew_pwd2();

            //判断两次新密码是否输入正确
            if (!new_pwd.equals(new_pwd2)) {
                logger.warn("两次新密码请输入正确");
                res.setTelephone(telephone);
                res.setMemo("两次新密码请输入正确");
                return res;
            }

            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //判断输入的旧密码和真正的旧密码是否相同
                old_pwd = SaSecureUtil.md5BySalt(old_pwd, "stu");//md5加盐加密后的密码
                if (stu.getPwd().equals(old_pwd)) {//相同
                    //更新DB
                    String md5pwd = SaSecureUtil.md5BySalt(new_pwd, "stu");//md5加盐加密后的密码
                    stuInfoRepository.modifyStuPwd(md5pwd, telephone);

                    //填充返回的dto
                    res.setTelephone(telephone);
                    res.setMemo("修改密码成功");
                } else {//旧密码输入错误
                    logger.warn("请输入正确的旧密码");
                    res.setTelephone(telephone);
                    res.setMemo("请输入正确的旧密码");
                }
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }
        return res;
    }

}
