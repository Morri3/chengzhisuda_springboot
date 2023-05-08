package com.zyq.parttime.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Student;
import com.zyq.parttime.entity.Unit;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.logandreg.*;
import com.zyq.parttime.form.resumemanage.ResumeStuIdDto;
import com.zyq.parttime.repository.logandreg.LogAndRegByEmpRepository;
import com.zyq.parttime.repository.logandreg.LogAndRegByStuRepository;
import com.zyq.parttime.repository.unit.UnitRepository;
import com.zyq.parttime.service.LogAndRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogAndRegServiceImpl implements LogAndRegService {
    private static int idx = 0;//用户用户登录登出
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;//redis缓存
    @Autowired
    private LogAndRegByStuRepository logAndRegByStuRepository;
    @Autowired
    private LogAndRegByEmpRepository logAndRegByEmpRepository;
    @Autowired
    private UnitRepository unitRepository;

    //判断邮箱格式是否正确
    public static boolean isEmail(String emails) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";//正则表达式
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(emails);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    //TODO 登录-学生
    @Override
    public LogAndRegInfoDto loginByStu(LoginDto loginDto) throws ParttimeServiceException, ParseException {
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
                    res.setMemo("登录成功");

                    //token存入缓存
                    UsersTokenDto dto = new UsersTokenDto();
                    dto.setTelephone(stu.getId());
                    dto.setToken(token);
                    dto.setLogin(true);
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    Date now = sdf.parse(sdf.format(new Date()));
                    dto.setTime(now);
                    redisTemplate.opsForValue().set(UsersTokenDto.cacheKey(idx), JSON.toJSONString(dto));
                    logger.warn("存储该学生的token等信息[{}]", UsersTokenDto.cacheKey(idx));
                    idx++;

                } else {//密码错误
                    logger.warn("密码或账号错误，请检查后重新输入");
                    res.setTelephone(stu.getId());//学生手机号
                    res.setMemo("密码或账号错误，请检查后重新输入");//填充错误提示
                }
            } else {//不存在该学生
                logger.warn("不存在该学生");
                res.setTelephone(telephone);//学生手机号
                res.setMemo("不存在该学生");//填充错误提示
            }
        }
        return res;
    }

    //TODO 注册-学生
    @Override
    public LogAndRegInfoDto registerByStu(StuRegisterDto stuRegisterDto) throws ParttimeServiceException, ParseException {
        LogAndRegInfoDto res = new LogAndRegInfoDto();

        if (stuRegisterDto != null) {
            //1.获取android传来的dto的信息
            String stu_name = stuRegisterDto.getStu_name();
            int gender = stuRegisterDto.getGender();
            String telephone = stuRegisterDto.getTelephone();
            String emails = stuRegisterDto.getEmails();
            String pwd = stuRegisterDto.getPwd();
            String pwd2 = stuRegisterDto.getPwd2();
            int age = stuRegisterDto.getAge();
            String birth_year = stuRegisterDto.getBirth_year();//出生年份
            String birth_month = stuRegisterDto.getBirth_month();//出生月份
            String school_name = stuRegisterDto.getSchool_name();
            String sno = stuRegisterDto.getSno();
            String reg_date = stuRegisterDto.getReg_date();
            String entrance_date = stuRegisterDto.getEntrance_date();
            String graduation_date = stuRegisterDto.getGraduation_date();

            //2.判断两次新密码是否输入正确
            if (!pwd.equals(pwd2)) {
                logger.warn("两次密码请输入正确");
                res.setTelephone(telephone);
                res.setMemo("两次密码请输入正确");
                return res;
            }

            //3.查看是否存在该手机号（手机号=账号）
            Student stu = logAndRegByStuRepository.findStudentByTelephone(telephone);
            Employer emp = logAndRegByEmpRepository.findEmployerByTelephone(telephone);
            if (stu != null || emp != null) {
                //4.该手机号已注册过，用户在登录界面直接登录
                logger.warn("该手机号已注册，请直接登录");
                res.setTelephone(telephone);
                res.setMemo("该手机号已注册，请直接登录");
            } else if (stu == null && emp == null) {//注册
                //5.md5加盐加密后的密码
                String md5pwd = SaSecureUtil.md5BySalt(pwd, "stu");

                //6.当前时间作为注册时间
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date reg = sdf1.parse(reg_date);

                //7.入学年月、毕业年月
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
                Date entrance = sdf2.parse(entrance_date);
                Date graduation = sdf2.parse(graduation_date);
                Date now = sdf2.parse(sdf2.format(new Date()));

                //8.计算年级
                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                Calendar c3 = Calendar.getInstance();
                c1.setTime(entrance);//入学时间
                c2.setTime(graduation);//毕业时间
                c3.setTime(now);//现在

                int diff = 0;
                if ((c1.getTime()).before(c2.getTime()) && (c2.getTime()).before(c3.getTime())) {
                    //8-1.入学时间在毕业时间前，且毕业时间在现在时间前，已经毕业
                    diff = 6;
                } else if ((c1.getTime()).before(c2.getTime()) && (c2.getTime()).after(c3.getTime())) {
                    //8-2.入学时间在毕业时间前，且毕业时间在现在时间后，未毕业
                    int tmp1 = c3.get(Calendar.DATE) - c1.get(Calendar.DATE);
                    int tmp2 = c3.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
                    int tmp3 = c3.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
                    if (tmp2 > 0) {//月份更大
                        tmp2 = 1;//最后要+1
                    } else if (tmp2 == 0) {//月份相同
                        tmp2 = tmp1 <= 0 ? 0 : 1;//判断日期，前面日期更小，最后月份要+1
                    } else {//月份更小，不用+
                        tmp2 = 0;
                    }
                    diff = tmp3 + tmp2;
                }

                //9.把int型年级转为字符串类型
                String grade = "";
                if (diff == 1) grade = "大一";
                else if (diff == 2) grade = "大二";
                else if (diff == 3) grade = "大三";
                else if (diff == 4) grade = "大四";
                else if (diff == 5) grade = "大五（五年制）";
                else if (diff == 6) grade = "已毕业";

                //10.注册（暂不支持头像上传）
                logAndRegByStuRepository.registerByStu(telephone, md5pwd, sno, school_name, gender,
                        emails, stu_name, age, reg, entrance, graduation, 0, grade);

                //11.将出生年份、月份存入缓存
                UserBirthDto userBirthDto = new UserBirthDto();
                userBirthDto.setTelephone(telephone);
                userBirthDto.setBirth_year(birth_year);
                userBirthDto.setBirth_month(birth_month);
                redisTemplate.opsForValue().set(UserBirthDto.cacheKey(telephone), JSON.toJSONString(userBirthDto));
                logger.warn("存储该学生的出生年月[{}]", UserBirthDto.cacheKey(telephone));

                //12.填充返回给android的dto
                res.setTelephone(telephone);
                res.setMemo("注册成功");
            }
        } else {
            logger.warn("请输入表单信息");
            res.setTelephone("0");
            res.setMemo("请输入表单信息");
        }
        return res;
    }

    //TODO 登录-兼职发布者/管理员
    @Override
    public LogAndRegInfoDto loginByEmp(LoginDto loginDto) throws ParttimeServiceException, ParseException {
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
                    //同时把username等信息传回去
                    res.setAge(emp.getAge());
                    res.setEmails(emp.getEmails());
                    res.setEmp_grade(emp.getEmpGrade());
                    res.setGender(emp.getGender());
                    res.setHead(emp.getHead());
                    res.setJno(emp.getJno());
                    res.setReg_date(emp.getRegDate());
                    res.setEmp_name(emp.getEmpName());
                    res.setU_id(emp.getU().getId());
                    res.setMemo("登录成功");

                    //token存入缓存
                    UsersTokenDto dto = new UsersTokenDto();
                    dto.setTelephone(emp.getId());
                    dto.setToken(token);
                    dto.setLogin(true);
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    Date now = sdf.parse(sdf.format(new Date()));
                    dto.setTime(now);
                    redisTemplate.opsForValue().set(UsersTokenDto.cacheKey(idx), JSON.toJSONString(dto));
                    logger.warn("存储该兼职发布者的token等信息[{}]", UsersTokenDto.cacheKey(idx));
                    idx++;
                } else {//密码错误
                    logger.warn("密码或账号错误，请检查后重新输入");
                    res.setTelephone(emp.getId());//兼职发布者/管理员手机号
                    res.setMemo("密码或账号错误，请检查后重新输入");//填充错误提示
                }
            } else {//不存在该兼职发布者
                logger.warn("不存在该兼职发布者");
                res.setTelephone(telephone);//兼职发布者/管理员手机号
                res.setMemo("不存在该兼职发布者");//填充错误提示
            }
        }
        return res;
    }

    //TODO 注册-兼职发布者
    @Override
    public LogAndRegInfoDto registerByEmp(EmpRegisterDto empRegisterDto) throws ParttimeServiceException, ParseException {
        LogAndRegInfoDto res = new LogAndRegInfoDto();

        if (empRegisterDto != null) {
            //1.获取vue传来的dto的信息
            String telephone = empRegisterDto.getTelephone();
            String pwd = empRegisterDto.getPwd();
            String pwd2 = empRegisterDto.getPwd2();
            String emp_name = empRegisterDto.getEmp_name();
            int gender = empRegisterDto.getGender();
            String emails = empRegisterDto.getEmails();
            int age = empRegisterDto.getAge();
            String birth_year = empRegisterDto.getBirth_year();//出生年份
            String birth_month = empRegisterDto.getBirth_month();//出生月份
            String unit_name = empRegisterDto.getUnit_name();
            String jno = empRegisterDto.getJno();
            String reg_date = empRegisterDto.getReg_date();
            boolean emp_grade = empRegisterDto.isEmp_grade();

            //2.判断两次新密码是否输入正确
            if (!pwd.equals(pwd2)) {
                logger.warn("两次密码请输入正确");
                res.setTelephone(telephone);
                res.setMemo("两次密码请输入正确");
                return res;
            }

            //3.判断邮箱格式是否正确
            if (!isEmail(emails)) {
                //不是邮箱格式
                logger.warn("请输入正确的邮箱格式");
                res.setTelephone(telephone);
                res.setMemo("请输入正确的邮箱格式");
                return res;
            }

            //4.查看是否存在该手机号（手机号=账号）
            Student stu = logAndRegByStuRepository.findStudentByTelephone(telephone);
            Employer emp = logAndRegByEmpRepository.findEmployerByTelephone(telephone);
            if (stu != null || emp != null) {
                //4-1.该手机号已注册过，用户在登录界面直接登录
                logger.warn("该手机号已注册，请直接登录");
                res.setTelephone(telephone);
                res.setMemo("该手机号已注册，请直接登录");
            } else if (stu == null && emp == null) {//注册
                //4.md5加盐加密后的密码
                String md5pwd = SaSecureUtil.md5BySalt(pwd, "emp");

                //5.注册时间
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date reg = sdf1.parse(reg_date);

                //6.若unit表有该单位名称，就找到该单位id；若不存在，在unit表加一条记录，返回该记录id
                int u_id = 0;
                Unit unit = unitRepository.findUnitByUnitName(unit_name);
                if (unit != null) {//存在单位
                    u_id = unit.getId();
                } else {//不存在单位
                    unitRepository.createUnitByUnitName(unit_name, 0);//按名创建
                    u_id = unitRepository.findUnitByUnitNameWithUId(unit_name);
                }

                //7.注册（暂不支持头像上传）
                logAndRegByEmpRepository.registerByEmp(telephone, u_id, md5pwd, jno, gender, emails, emp_name,
                        age, reg, 0, emp_grade);

                //8.将出生年份、月份存入缓存
                UserBirthDto userBirthDto = new UserBirthDto();
                userBirthDto.setTelephone(telephone);
                userBirthDto.setBirth_year(birth_year);
                userBirthDto.setBirth_month(birth_month);
                redisTemplate.opsForValue().set(UserBirthDto.cacheKey(telephone), JSON.toJSONString(userBirthDto));
                logger.warn("存储该兼职发布者的出生年月[{}]", UserBirthDto.cacheKey(telephone));

                //9.填充返回给android的dto
                res.setTelephone(telephone);
                res.setMemo("注册成功");
            }
        } else {
            logger.warn("请输入表单信息");
            res.setMemo("请输入表单信息");
        }
        return res;
    }

    //TODO 退出登录-学生
    @Override
    public String logoutByStu(LogoutDto logoutDto) throws ParttimeServiceException {
        String res = "";

        if (logoutDto != null) {
            String input_telephone = logoutDto.getInput_telephone();//手机号

            //1.缓存中取token
            String cur_token = "";
            for (int i = 0; i < idx; i++) {
                String str = (String) redisTemplate.opsForValue().get(UsersTokenDto.cacheKey(idx));
                UsersTokenDto dto = com.alibaba.fastjson.JSONObject.parseObject(str, UsersTokenDto.class);//json转dto
                logger.warn("存储该学生的token[{}]", UsersTokenDto.cacheKey(idx));
                if (dto != null && dto.getTelephone().equals(input_telephone)) {
                    //账号符合、token符合
                    cur_token = dto.getToken();
                    break;
                }
            }

            if (cur_token != "" || cur_token != null) {
                //2.查找该用户
                Student student = logAndRegByStuRepository.findStudentByTelephone(input_telephone);
                if (student != null) {
                    //3.用户登出
                    StpUtil.logout();
                    res = "用户登出成功";
                } else {
                    res = "用户登出失败";
                }
            }
        } else {
            res = "用户登出失败";
        }
        return res;
    }

    //TODO 退出登录-兼职发布者/管理员
    @Override
    public String logoutByEmp(LogoutDto logoutDto) throws ParttimeServiceException {
        String res = "";

        if (logoutDto != null) {
            String input_telephone = logoutDto.getInput_telephone();//手机号

            //1.缓存取token
            String cur_token = "";
            for (int i = 0; i < idx; i++) {
                String str = (String) redisTemplate.opsForValue().get(UsersTokenDto.cacheKey(idx));
                UsersTokenDto dto = com.alibaba.fastjson.JSONObject.parseObject(str, UsersTokenDto.class);//json转dto
                logger.warn("存储该兼职发布者/管理员的token[{}]", UsersTokenDto.cacheKey(idx));
                if (dto != null && dto.getTelephone().equals(input_telephone)) {
                    //账号符合、token符合
                    cur_token = dto.getToken();
                    break;
                }
            }

            if (cur_token != "" || cur_token != null) {
                //2.查找该用户
                Employer employer = logAndRegByEmpRepository.findEmployerByTelephone(input_telephone);
                if (employer != null) {
                    //3.用户登出
                    StpUtil.logout();
                    res = "用户登出成功";
                } else {
                    res = "用户登出失败";
                }
            }
        } else {
            res = "用户登出失败";
        }
        return res;
    }
}
