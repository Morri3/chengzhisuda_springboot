package com.zyq.parttime.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Student;
import com.zyq.parttime.entity.Unit;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.logandreg.EmpRegisterDto;
import com.zyq.parttime.form.logandreg.LoginDto;
import com.zyq.parttime.form.logandreg.StuRegisterDto;
import com.zyq.parttime.form.logandreg.LogAndRegInfoDto;
import com.zyq.parttime.repository.logandreg.LogAndRegByEmpRepository;
import com.zyq.parttime.repository.logandreg.LogAndRegByStuRepository;
import com.zyq.parttime.repository.UnitRepository;
import com.zyq.parttime.service.LogAndRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class LogAndRegServiceImpl implements LogAndRegService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private LogAndRegByStuRepository logAndRegByStuRepository;
    @Autowired
    private LogAndRegByEmpRepository logAndRegByEmpRepository;
    @Autowired
    private UnitRepository unitRepository;

    @Override
    public LogAndRegInfoDto loginByStu(LoginDto loginDto) throws ParttimeServiceException {
        LogAndRegInfoDto res = new LogAndRegInfoDto();//存结果

        if (loginDto != null) {
            String telephone = loginDto.getTelephone();
            String pwd = loginDto.getPwd();

            //查看是否存在该用户
            Student stu = logAndRegByStuRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在学生
                String real_pwd = stu.getPwd();//获取该用户真正的密码
                if (real_pwd.equals(SaSecureUtil.md5BySalt(pwd, "stu"))) {//密码正确，可以登录
                    //sa-token标记当前会话用户登录
                    StpUtil.login(stu.getId());//id作为登录的标准，登录

                    //获取当前登录用户的token
                    SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
                    String token = saTokenInfo.getTokenValue();

                    res.setTelephone(stu.getId());//学生手机号
                    res.setToken(token);//填充token信息
                } else {//密码错误
                    logger.warn("密码或账号错误，请检查后重新输入");
                    res.setTelephone(stu.getId());//学生手机号
                    res.setToken("密码或账号错误，请检查后重新输入");//填充错误提示
                }
            } else {//不存在该学生
                logger.warn("不存在该学生");
                res.setTelephone(telephone);//学生手机号
                res.setToken("不存在该学生");//填充错误提示
            }
        }
        return res;
    }

    @Override
    public LogAndRegInfoDto registerByStu(StuRegisterDto stuRegisterDto) throws ParttimeServiceException, ParseException {
        LogAndRegInfoDto res = new LogAndRegInfoDto();

        if (stuRegisterDto != null) {
            //获取android传来的dto的信息
            String stu_name = stuRegisterDto.getStu_name();
            int gender = stuRegisterDto.getGender();
            String telephone = stuRegisterDto.getTelephone();
            String emails = stuRegisterDto.getEmails();
            String pwd = stuRegisterDto.getPwd();
            String pwd2 = stuRegisterDto.getPwd2();
            int age = stuRegisterDto.getAge();
            String school_name = stuRegisterDto.getSchool_name();
            String sno = stuRegisterDto.getSno();
            String reg_date = stuRegisterDto.getReg_date();
            String entrance_date = stuRegisterDto.getEntrance_date();
            String graduation_date = stuRegisterDto.getGraduation_date();

            //判断两次新密码是否输入正确
            if (!pwd.equals(pwd2)) {
                logger.warn("两次密码请输入正确");
                res.setTelephone(telephone);
                res.setMemo("两次密码请输入正确");
                return res;
            }

            //查看是否存在该手机号（手机号=账号）
            Student stu = logAndRegByStuRepository.findStudentByTelephone(telephone);
            if (stu != null) {//该手机号已注册过，用户在登录界面直接登录
                logger.warn("该手机号已注册，请直接登录");
                res.setTelephone(telephone);
                res.setToken("该手机号已注册，请直接登录");
            } else {//注册
                String md5pwd = SaSecureUtil.md5BySalt(pwd, "stu");//md5加盐加密后的密码

                //注册时间
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                Date reg = sdf1.parse(reg_date);

                //入学年月、毕业年月
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月");
                Date entrance = sdf2.parse(entrance_date);
                Date graduation = sdf2.parse(graduation_date);

                //计算年级
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(entrance);
                cal2.setTime(graduation);
                Integer y1 = cal1.get(Calendar.YEAR);//获取年
                Integer y2 = cal2.get(Calendar.YEAR);//获取年
                int diff = y2 - y1;//年数
                String grade = "";//把int转为字符串类型
                if (diff == 1) grade = "大一";
                else if (diff == 2) grade = "大二";
                else if (diff == 3) grade = "大三";
                else if (diff == 4) grade = "大四";
                else if (diff == 5) grade = "大五（五年制）";
                else if (diff > 5) grade = null;

                //注册
                logAndRegByStuRepository.registerByStu(telephone, md5pwd, sno, school_name, gender,
                        emails, stu_name, age, reg, entrance, graduation, 0, grade);

                //填充返回给android的dto
                res.setTelephone(telephone);
                res.setToken("注册成功");
            }
        }
        return res;
    }

    @Override
    public LogAndRegInfoDto loginByEmp(LoginDto loginDto) throws ParttimeServiceException {
        LogAndRegInfoDto res = new LogAndRegInfoDto();//存结果

        if (loginDto != null) {
            String telephone = loginDto.getTelephone();
            String pwd = loginDto.getPwd();

            //查看是否存在该用户
            Employer emp = logAndRegByEmpRepository.findEmployerByTelephone(telephone);
            if (emp != null) {//存在兼职发布者
                String real_pwd = emp.getPwd();//获取该用户真正的密码
                if (real_pwd.equals(SaSecureUtil.md5BySalt(pwd, "emp"))) {//密码正确，可以登录
                    //sa-token标记当前会话用户登录
                    StpUtil.login(emp.getId());//id作为登录的标准，登录

                    //获取当前登录用户的token
                    SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
                    String token = saTokenInfo.getTokenValue();

                    res.setTelephone(emp.getId());//兼职发布者/管理员手机号
                    res.setToken(token);//填充token信息
                } else {//密码错误
                    logger.warn("密码或账号错误，请检查后重新输入");
                    res.setTelephone(emp.getId());//兼职发布者/管理员手机号
                    res.setToken("密码或账号错误，请检查后重新输入");//填充错误提示
                }
            } else {//不存在该兼职发布者
                logger.warn("不存在该兼职发布者");
                res.setTelephone(telephone);//兼职发布者/管理员手机号
                res.setToken("不存在该兼职发布者");//填充错误提示
            }
        }
        return res;
    }

    @Override
    public LogAndRegInfoDto registerByEmp(EmpRegisterDto empRegisterDto) throws ParttimeServiceException, ParseException {
        LogAndRegInfoDto res = new LogAndRegInfoDto();

        if (empRegisterDto != null) {
            //获取vue传来的dto的信息
            String telephone = empRegisterDto.getTelephone();
            String pwd = empRegisterDto.getPwd();
            String pwd2 = empRegisterDto.getPwd2();
            String emp_name = empRegisterDto.getEmp_name();
            int gender = empRegisterDto.getGender();
            String emails = empRegisterDto.getEmails();
            int age = empRegisterDto.getAge();
            String unit_name = empRegisterDto.getUnit_name();
            String jno = empRegisterDto.getJno();
            String reg_date = empRegisterDto.getReg_date();
            boolean emp_grade = empRegisterDto.isEmp_grade();

            //判断两次新密码是否输入正确
            if (!pwd.equals(pwd2)) {
                logger.warn("两次密码请输入正确");
                res.setTelephone(telephone);
                res.setMemo("两次密码请输入正确");
                return res;
            }

            //查看是否存在该手机号（手机号=账号）
            Employer emp = logAndRegByEmpRepository.findEmployerByTelephone(telephone);
            if (emp != null) {//该手机号已注册过，用户在登录界面直接登录
                logger.warn("该手机号已注册，请直接登录");
                res.setTelephone(telephone);
                res.setToken("该手机号已注册，请直接登录");
            } else {//注册
                String md5pwd = SaSecureUtil.md5BySalt(pwd, "emp");//md5加盐加密后的密码

                //注册时间
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                Date reg = sdf1.parse(reg_date);

                //若单位名称存在DB中，找到该单位的id；若不存在，则在unit表新增一条记录，并返回该记录id
                int u_id = 0;
                Unit unit = unitRepository.findUnitByUnitName(unit_name);
                if (unit != null) {//存在单位
                    u_id = unit.getId();
                } else {//不存在单位
                    unitRepository.createUnitByUnitName(unit_name, 0);
                    u_id = unitRepository.findUnitByUnitNameWithUId(unit_name);
                }

                //注册
                logAndRegByEmpRepository.registerByEmp(telephone, u_id, md5pwd, jno, gender,
                        emails, emp_name, age, reg, 0, emp_grade);

                //填充返回给android的dto
                res.setTelephone(telephone);
                res.setToken("注册成功");
            }
        }
        return res;
    }

    @Override
    public String logoutByStu(String token) throws ParttimeServiceException {
        String res = "";

        if (token != "" || token != null) {
            //获取token
            String usertoken = token;

            //根据token找用户id
            if (StpUtil.getLoginIdByToken(usertoken) != null) {
                String telephone = (String) StpUtil.getLoginIdByToken(usertoken);

                //查找该用户
                Student student = logAndRegByStuRepository.findStudentByTelephone(telephone);
                if (student != null) {
                    StpUtil.logout();//用户登出
                    res = "用户登出成功";
                }
            }
        }
        return res;
    }

    @Override
    public String logoutByEmp(String token) throws ParttimeServiceException {
        String res = "";

        if (token != "" || token != null) {
            //获取token
            String usertoken = token;

            //根据token找用户id
            if (StpUtil.getLoginIdByToken(usertoken) != null) {
                String telephone = (String) StpUtil.getLoginIdByToken(usertoken);

                //查找该用户
                Employer employer = logAndRegByEmpRepository.findEmployerByTelephone(telephone);
                if (employer != null) {
                    StpUtil.logout();//用户登出
                    res = "用户登出成功";
                }
            }
        }
        return res;
    }
}
