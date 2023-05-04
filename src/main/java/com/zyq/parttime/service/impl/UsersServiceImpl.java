package com.zyq.parttime.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
//导入可选配置类
import com.alibaba.fastjson.JSON;
import com.zyq.parttime.entity.*;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.intention.EditIntentionDto;
import com.zyq.parttime.form.intention.IntentionDto;
import com.zyq.parttime.form.logandreg.UserBirthDto;
import com.zyq.parttime.form.resumemanage.*;
import com.zyq.parttime.form.userinfomanage.*;
import com.zyq.parttime.minio.MinIO;
import com.zyq.parttime.ocr.OcrBDUtils;
import com.zyq.parttime.repository.intention.IntentionRepository;
import com.zyq.parttime.repository.resumemanage.ResumesDetailRepository;
import com.zyq.parttime.repository.resumemanage.ResumesInfoRepository;
import com.zyq.parttime.repository.unit.UnitRepository;
import com.zyq.parttime.repository.userinfomanage.EmpInfoRepository;
import com.zyq.parttime.repository.userinfomanage.StuInfoRepository;
import com.zyq.parttime.service.UsersService;
import com.zyq.parttime.utils.MockUtils;
import net.coobird.thumbnailator.Thumbnails;
//import org.apache.commons.lang.time.DateUtils;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UsersServiceImpl implements UsersService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;//redis缓存

    @Autowired
    private StuInfoRepository stuInfoRepository;
    @Autowired
    private EmpInfoRepository empInfoRepository;
    @Autowired
    private ResumesInfoRepository resumesInfoRepository;
    @Autowired
    private ResumesDetailRepository resumesDetailRepository;
    @Autowired
    private IntentionRepository intentionRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private MinIO minIO;

    // minio配置
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.bucket}")
    private String bucket;

    //TODO 获取个人信息——学生
    @Override
    public StuInfoDto getStuInfo(String telephone) throws ParttimeServiceException {
        StuInfoDto res = new StuInfoDto();

        if (telephone != null) {
            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);

            if (stu != null) {
                //存在学生，获取该用户的个人信息，填充到res
                String stu_name = stu.getStuName();
                int gender = stu.getGender();
                String emails = stu.getEmails();
                int age = stu.getAge();
                String school_name = stu.getSchoolName();
                String sno = stu.getSno();
                String grade = stu.getGrade();
                Date entrance_date = stu.getEntranceDate();
                Date graduation_date = stu.getGraduationDate();
                String head = stu.getHead();

                //缓存中获取出生年月
                String dtoStr = (String) redisTemplate.opsForValue().get(UserBirthDto.cacheKey(telephone));
                UserBirthDto tmp = com.alibaba.fastjson.JSONObject.parseObject(dtoStr, UserBirthDto.class);//json转dto
                String birth_year = "", birth_month = "";//出生年月
                if (tmp != null) {
                    //缓存中存在该用户的出生年月数据
                    birth_year = tmp.getBirth_year();
                    birth_month = tmp.getBirth_month();
                }

                res.setStu_name(stu_name);
                res.setGender(gender);
                res.setTelephone(telephone);
                res.setEmails(emails);
                res.setAge(age);
                res.setBirth_year(birth_year);//出生年份
                res.setBirth_month(birth_month);//出生月份
                res.setSchool_name(school_name);
                res.setSno(sno);
                res.setEntrance_date(entrance_date);
                res.setGraduation_date(graduation_date);
                res.setGrade(grade);
                res.setHead(head);
                res.setMemo("获取成功");
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        } else {
            logger.warn("请检查输入");
            res.setTelephone(telephone);
            res.setMemo("请检查输入");
        }

        return res;
    }

    //TODO 个人信息查看-兼职发布者/管理员
    @Override
    public EmpInfoDto getEmpInfo(String telephone) throws ParttimeServiceException {
        EmpInfoDto res = new EmpInfoDto();

        if (telephone != null) {
            //1.查找该用户是否存在
            Employer emp = empInfoRepository.findEmployerByTelephone(telephone);

            if (emp != null) {
                //2.存在，获取该用户的个人信息，填充到res
                String emp_name = emp.getEmpName();
                int gender = emp.getGender();
                String emails = emp.getEmails();
                int age = emp.getAge();
                String jno = emp.getJno();
                String unit_name = emp.getU().getUnitName();
                String unit_descriptions = emp.getU().getDescriptions();
                String unit_loc = emp.getU().getLoc();
                int job_nums = emp.getU().getJobNums();
                String head = emp.getHead();
                int emp_grade = emp.getEmpGrade();

                //3.缓存中获取出生年月
                String dtoStr = (String) redisTemplate.opsForValue().get(UserBirthDto.cacheKey(telephone));
                UserBirthDto tmp = com.alibaba.fastjson.JSONObject.parseObject(dtoStr, UserBirthDto.class);//json转dto
                String birth_year = "", birth_month = "";//出生年月
                if (tmp != null) {
                    //缓存中存在该用户的出生年月数据
                    birth_year = tmp.getBirth_year();
                    birth_month = tmp.getBirth_month();
                }

                //4.构造res
                res.setEmp_name(emp_name);
                res.setGender(gender);
                res.setEmails(emails);
                res.setAge(age);
                res.setBirth_year(birth_year);//出生年份
                res.setBirth_month(birth_month);//出生月份
                res.setTelephone(telephone);
                res.setJno(jno);
                res.setUnit_name(unit_name);
                res.setUnit_descriptions(unit_descriptions);
                res.setUnit_loc(unit_loc);
                res.setJob_nums(job_nums);
                res.setHead(head);
                res.setEmp_grade(emp_grade);
                res.setMemo("获取成功");
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        } else {
            logger.warn("请检查输入");
            res.setTelephone(telephone);
            res.setMemo("请检查输入");
        }
        return res;
    }

    //TODO 编辑信息——学生
    @Override
    public StuInfoDto editStuInfo(EditInfoDto editInfoDto) throws ParttimeServiceException, ParseException {
        StuInfoDto res = new StuInfoDto();

        if (editInfoDto != null) {
            //获取传入的dto的字段
            String telephone = editInfoDto.getTelephone();
            int gender = editInfoDto.getGender();
            int age = editInfoDto.getAge();
            String birth_year = editInfoDto.getBirth_year();
            String birth_month = editInfoDto.getBirth_month();
            String emails = editInfoDto.getEmails();
            String entrance_date = editInfoDto.getEntrance_date();
            String graduation_date = editInfoDto.getGraduation_date();


            //入学年月、毕业年月
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
            Date entrance = sdf2.parse(entrance_date);
            Date graduation = sdf2.parse(graduation_date);
            Date now = sdf2.parse(sdf2.format(new Date()));

            //计算年级
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            Calendar c3 = Calendar.getInstance();
            c1.setTime(entrance);//入学时间
            c2.setTime(graduation);//毕业时间
            c3.setTime(now);//现在

            int diff = 0;
            if ((c1.getTime()).before(c2.getTime()) && (c2.getTime()).before(c3.getTime())) {
                //入学时间在毕业时间前，且毕业时间在现在时间前，已经毕业
                diff = 6;
            } else if ((c1.getTime()).before(c2.getTime()) && (c2.getTime()).after(c3.getTime())) {
                //入学时间在毕业时间前，且毕业时间在现在时间后，未毕业
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

            //把int型年级转为字符串类型
            String grade = "";
            if (diff == 1) grade = "大一";
            else if (diff == 2) grade = "大二";
            else if (diff == 3) grade = "大三";
            else if (diff == 4) grade = "大四";
            else if (diff == 5) grade = "大五（五年制）";
            else if (diff == 6) grade = "已毕业";
//            else if (diff > 5) grade = null;
            System.out.println("年级:" + grade);


            //将出生年月存入缓存
            UserBirthDto userBirthDto = new UserBirthDto();
            userBirthDto.setBirth_year(birth_year);
            userBirthDto.setBirth_month(birth_month);
            redisTemplate.opsForValue().set(UserBirthDto.cacheKey(telephone), JSON.toJSONString(userBirthDto));
            logger.warn("存储该学生的出生年月[{}]", UserBirthDto.cacheKey(telephone));


            //查找该用户是否存在
            Student stu = stuInfoRepository.findStudentByTelephone(telephone);
            if (stu != null) {//存在
                //修改用户信息
                stuInfoRepository.editStuInfo(gender, age, emails, entrance,
                        graduation, grade, telephone);

                //填充返回的dto
                res.setTelephone(telephone);
                res.setAge(age);
                res.setGender(gender);
                res.setEmails(emails);
                res.setEntrance_date(entrance);
                res.setGraduation_date(graduation);
                res.setGrade(grade);
                res.setMemo("修改个人信息成功");
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        } else {
            logger.warn("请检查输入的信息是否完整");
            res.setMemo("请检查输入的信息是否完整");
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
        } else {//不存在
            logger.warn("请检查输入的信息是否完整");
            res.setTelephone("0");
            res.setMemo("请检查输入的信息是否完整");
        }
        return res;
    }

    //TODO 简历查看-学生
    @Override
    public ResumeInfoDto getResume(String telephone) throws ParttimeServiceException {
        ResumeInfoDto res = new ResumeInfoDto();

        if (telephone != null) {
            //根据学生账号查找该学生的resumes
            Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
            if (resumes != null) {//存在简历
                //填充简历的基本信息
                res.setTelephone(telephone);
                res.setCurrent_area(resumes.getCurrentArea());
                res.setExp(resumes.getExp());
                res.setUpload_time(resumes.getUploadTime());
                res.setR_id(resumes.getId());

                //根据r_id查找四个子类的内容
                List<Resumedetail> campusExpList =
                        resumesDetailRepository.findResumeDetailListByRIdAndCategory(resumes.getId(), "校园经历");
                List<Resumedetail> educationBgList =
                        resumesDetailRepository.findResumeDetailListByRIdAndCategory(resumes.getId(), "教育背景");
                List<Resumedetail> projectExpList =
                        resumesDetailRepository.findResumeDetailListByRIdAndCategory(resumes.getId(), "项目经历");
                List<Resumedetail> professionalSkillList =
                        resumesDetailRepository.findResumeDetailListByRIdAndCategory(resumes.getId(), "专业技能");

                // 遍历四个子类
                //1.遍历校园经历
                List<ResumeDetailDto> list1 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : campusExpList) {
                        ResumeDetailDto dto = new ResumeDetailDto();
                        dto.setTelephone(telephone);
                        dto.setRd_id(item.getId());
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("校园经历");
                        dto.setHasContent(1);//有内容
                        //获取start_time、end_time拼成String
                        Date start_time = item.getStartTime();
                        Date end_time = item.getEndTime();
                        DateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        String start = sdf.format(start_time);
                        String end = sdf.format(end_time);
                        dto.setTime(start + '-' + end);
                        dto.setStatus(item.getRdStatus());
                        list1.add(dto);
                    }
                } else {
                    //无内容
                    ResumeDetailDto dto = new ResumeDetailDto();
                    dto.setTelephone(telephone);
                    dto.setR_id(resumes.getId());
                    dto.setCategory("校园经历");
                    dto.setHasContent(0);//无内容
                    list1.add(dto);
                }

                //2.遍历教育背景
                List<ResumeDetailDto> list2 = new ArrayList<>();
                if (educationBgList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : educationBgList) {
                        ResumeDetailDto dto = new ResumeDetailDto();
                        dto.setTelephone(telephone);
                        dto.setRd_id(item.getId());
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("教育背景");
                        dto.setHasContent(1);//有内容
                        //获取start_time、end_time拼成String
                        Date start_time = item.getStartTime();
                        Date end_time = item.getEndTime();
                        DateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        String start = sdf.format(start_time);
                        String end = sdf.format(end_time);
                        dto.setTime(start + '-' + end);
                        dto.setStatus(item.getRdStatus());
                        list2.add(dto);
                    }
                } else {//无内容
                    ResumeDetailDto dto = new ResumeDetailDto();
                    dto.setTelephone(telephone);
                    dto.setR_id(resumes.getId());
                    dto.setCategory("教育背景");
                    dto.setHasContent(0);//无内容
                    list2.add(dto);
                }

                //3.遍历项目经历
                List<ResumeDetailDto> list3 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : projectExpList) {
                        ResumeDetailDto dto = new ResumeDetailDto();
                        dto.setTelephone(telephone);
                        dto.setRd_id(item.getId());
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("项目经历");
                        dto.setHasContent(1);//有内容
                        //获取start_time、end_time拼成String
                        Date start_time = item.getStartTime();
                        Date end_time = item.getEndTime();
                        DateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        String start = sdf.format(start_time);
                        String end = sdf.format(end_time);
                        dto.setTime(start + '-' + end);
                        dto.setStatus(item.getRdStatus());
                        list3.add(dto);
                    }
                } else {//无内容
                    ResumeDetailDto dto = new ResumeDetailDto();
                    dto.setTelephone(telephone);
                    dto.setR_id(resumes.getId());
                    dto.setCategory("项目经历");
                    dto.setHasContent(0);//无内容
                    list3.add(dto);
                }

                //4.遍历专业技能
                List<ResumeDetailDto> list4 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : professionalSkillList) {
                        ResumeDetailDto dto = new ResumeDetailDto();
                        dto.setTelephone(telephone);
                        dto.setRd_id(item.getId());
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("专业技能");
                        dto.setHasContent(1);//有内容
                        dto.setStatus(item.getRdStatus());
                        list4.add(dto);
                    }
                } else {//无内容
                    ResumeDetailDto dto = new ResumeDetailDto();
                    dto.setTelephone(telephone);
                    dto.setR_id(resumes.getId());
                    dto.setCategory("专业技能");
                    dto.setHasContent(0);//无内容
                    list4.add(dto);
                }

                //把四个子类填充到res中
                res.setCampusExpList(list1);
                res.setEducationBgList(list2);
                res.setProjectExpList(list3);
                res.setProfessionalSkillList(list4);

                //状态
                res.setStatus(resumes.getrStatus());
                res.setMemo("存在简历");
            } else {
                logger.warn("不存在简历");
                res.setTelephone(telephone);
                res.setMemo("不存在简历");
            }
        } else {
            logger.warn("请检查输入");
            res.setTelephone(telephone);
            res.setMemo("请检查输入");
        }

        return res;
    }

    //TODO 创建简历，简历上传stu_id-学生
    @Override
//    public ResumeInfoDto createResume(String telephone, String upload_time) throws ParttimeServiceException, Exception {
    public ResumeInfoDto createResume(CreateResumeDto createResumeDto) throws ParttimeServiceException, Exception {
        ResumeInfoDto res = new ResumeInfoDto();

        //有输入
        if (createResumeDto != null) {
            String telephone = createResumeDto.getTelephone();
            String create_time = createResumeDto.getUpload_time();
            //这里upload_time实际上是创建时间create_time

            //根据手机号查找学生用户
            Student student = stuInfoRepository.findStudentByTelephone(telephone);

            //存在学生
            if (student != null) {
                System.out.println("学生学号：" + student.getId());

                //判断是否存在简历，不存在就创建
                Resumes resumes = resumesInfoRepository.findResumesByStuId(student.getId());

                if (resumes == null) {
                    //不存在，创建

                    //String转Date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date time = new Date();
                    try {
                        time = sdf.parse(create_time);
                        resumesInfoRepository.createAResumeRecord(student.getId(), null, null, time, "已创建");
                        //找到该用户的简历
                        resumes = resumesInfoRepository.findResumesByStuId(student.getId());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("创建的简历信息：" + resumes.toString());//test

                //填充简历的基本信息
                res.setTelephone(resumes.getStu().getId());
                res.setCurrent_area(resumes.getCurrentArea());
                res.setExp(resumes.getExp());
                res.setUpload_time(resumes.getUploadTime());
                res.setR_id(resumes.getId());

                //根据r_id查找四个子类的内容
                List<Resumedetail> campusExpList =
                        resumesDetailRepository.findResumeDetailListByRIdAndCategory(resumes.getId(), "校园经历");
                List<Resumedetail> educationBgList =
                        resumesDetailRepository.findResumeDetailListByRIdAndCategory(resumes.getId(), "教育背景");
                List<Resumedetail> projectExpList =
                        resumesDetailRepository.findResumeDetailListByRIdAndCategory(resumes.getId(), "项目经历");
                List<Resumedetail> professionalSkillList =
                        resumesDetailRepository.findResumeDetailListByRIdAndCategory(resumes.getId(), "专业技能");

                // 遍历四个子类
                //1.遍历校园经历
                List<ResumeDetailDto> list1 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : campusExpList) {
                        ResumeDetailDto dto = new ResumeDetailDto();
                        dto.setTelephone(telephone);
                        dto.setRd_id(item.getId());
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("校园经历");
                        dto.setHasContent(1);//有内容
                        //获取start_time、end_time拼成String
                        Date start_time = item.getStartTime();
                        Date end_time = item.getEndTime();
                        DateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        String start = sdf.format(start_time);
                        String end = sdf.format(end_time);
                        dto.setTime(start + '-' + end);
                        dto.setStatus(item.getRdStatus());
                        list1.add(dto);
                    }
                } else {//无内容
                    ResumeDetailDto dto = new ResumeDetailDto();
                    dto.setTelephone(telephone);
                    dto.setR_id(resumes.getId());
                    dto.setCategory("校园经历");
                    dto.setHasContent(0);//无内容
                    list1.add(dto);
                }

                //2.遍历教育背景
                List<ResumeDetailDto> list2 = new ArrayList<>();
                if (educationBgList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : educationBgList) {
                        ResumeDetailDto dto = new ResumeDetailDto();
                        dto.setTelephone(telephone);
                        dto.setRd_id(item.getId());
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("教育背景");
                        dto.setHasContent(1);//有内容
                        //获取start_time、end_time拼成String
                        Date start_time = item.getStartTime();
                        Date end_time = item.getEndTime();
                        DateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        String start = sdf.format(start_time);
                        String end = sdf.format(end_time);
                        dto.setTime(start + '-' + end);
                        dto.setStatus(item.getRdStatus());
                        list2.add(dto);
                    }
                } else {//无内容
                    ResumeDetailDto dto = new ResumeDetailDto();
                    dto.setTelephone(telephone);
                    dto.setR_id(resumes.getId());
                    dto.setCategory("教育背景");
                    dto.setHasContent(0);//无内容
                    list2.add(dto);
                }

                //3.遍历项目经历
                List<ResumeDetailDto> list3 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : projectExpList) {
                        ResumeDetailDto dto = new ResumeDetailDto();
                        dto.setTelephone(telephone);
                        dto.setRd_id(item.getId());
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("项目经历");
                        dto.setHasContent(1);//有内容
                        //获取start_time、end_time拼成String
                        Date start_time = item.getStartTime();
                        Date end_time = item.getEndTime();
                        DateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        String start = sdf.format(start_time);
                        String end = sdf.format(end_time);
                        dto.setTime(start + '-' + end);
                        dto.setStatus(item.getRdStatus());
                        list3.add(dto);
                    }
                } else {//无内容
                    ResumeDetailDto dto = new ResumeDetailDto();
                    dto.setTelephone(telephone);
                    dto.setR_id(resumes.getId());
                    dto.setCategory("项目经历");
                    dto.setHasContent(0);//无内容
                    list3.add(dto);
                }

                //4.遍历专业技能
                List<ResumeDetailDto> list4 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : professionalSkillList) {
                        ResumeDetailDto dto = new ResumeDetailDto();
                        dto.setTelephone(telephone);
                        dto.setRd_id(item.getId());
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("专业技能");
                        dto.setHasContent(1);//有内容
                        dto.setStatus(item.getRdStatus());
                        list4.add(dto);
                    }
                } else {//无内容
                    ResumeDetailDto dto = new ResumeDetailDto();
                    dto.setTelephone(telephone);
                    dto.setR_id(resumes.getId());
                    dto.setCategory("专业技能");
                    dto.setHasContent(0);//无内容
                    list4.add(dto);
                }

                //把四个子类填充到res中
                res.setCampusExpList(list1);
                res.setEducationBgList(list2);
                res.setProjectExpList(list3);
                res.setProfessionalSkillList(list4);

                //设置状态
                res.setStatus(resumes.getrStatus());
                res.setMemo("存在学生，简历创建成功");
            } else {
                logger.warn("不存在该学生");
                res.setMemo("不存在该学生");
            }
        } else {
            logger.warn("请检查输入");
            res.setMemo("请检查输入");
        }

        return res;
    }

    //TODO 简历上传step1上传账号+上传时间-学生
    @Override
    public String uploadResumeWithStuInfo(String telephone, String upload_time) throws ParttimeServiceException, Exception {
        String res = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (telephone != null) {
            //根据手机号查找学生用户
            Student student = stuInfoRepository.findStudentByTelephone(telephone);

            if (student != null) {//存在学生
                //判断是否存在简历，不存在就创建
                Resumes resumes = resumesInfoRepository.findResumesByStuId(student.getId());
                if (resumes == null) {//不存在，创建
                    //String转Date
                    Date time = new Date();
                    try {
                        time = sdf.parse(upload_time);
                        resumesInfoRepository.createAResumeRecord(student.getId(), null,
                                null, time, "已创建");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                //将学号存入redis缓存，这个缓存永远只有一条记录
                redisTemplate.opsForValue().set(ResumeStuIdDto.cacheKey(1), telephone);
                logger.warn("存储该学生的学号[{}]", ResumeStuIdDto.cacheKey(1));

                //将学号+上传日期存入redis缓存，会有多条记录
                redisTemplate.opsForValue().set(ResumeCacheDto.cacheKey(telephone), upload_time);
                logger.warn("存储该学生简历的信息[{}]", ResumeCacheDto.cacheKey(telephone));

                //结果
                res = "学号时间上传成功";
            } else {
                logger.warn("学号时间上传失败");
                res = ("学号时间上传失败");
            }
        } else {
            logger.warn("不存在该学生");
            res = ("不存在该学生");
        }

        return res;
    }

    //TODO 简历上传step2上传图片-学生
    @Override
    public ResumeUploadCallbackDto uploadResume(MultipartFile file) throws ParttimeServiceException, Exception {
        ResumeUploadCallbackDto res = new ResumeUploadCallbackDto();

//        System.out.println(file.getOriginalFilename());//test
        //原来的文件名：2023-05-03_5835703641455551212.jpg

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("时间3" + sdf.parse(sdf.format(new Date())));

        //0.图片压缩
        MultipartFile uploadFile = null;
        if (!file.isEmpty()) {
            //0-1.获取后缀名
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);//获取.后面的子串

            //0-2.判断是否是图片，是否大小大于100K
            if (isPicture(suffix) && (1024 * 1024 * 0.1) <= file.getSize()) {
                String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();//本地文件夹中的路径
                File upload = new File(path, "static/images/parttimes/");
                //不存在文件夹就创建
                if (!upload.exists()) {
                    upload.mkdirs();
                }
                System.out.println("本地临时文件的上传路径:" + upload.getAbsolutePath());

                //在项目根目录下的parttimes目录生成临时文件
                File newFile = new File(upload.getAbsolutePath() + "/" + file.getOriginalFilename());

                //outputQuality输出质量，接近1质量越高
                //scale用于等比例缩放
                if ((1024 * 1024 * 0.1) <= file.getSize() && file.getSize() <= (1024 * 1024)) {
                    //0.1M≤大小≤1M（file是传进来的图片）

//                    Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.3f).toFile(newFile);
                    Thumbnails.of(file.getInputStream()).scale(0.85f).outputQuality(0.35f).toFile(newFile);
                } else if ((1024 * 1024) < file.getSize() && file.getSize() <= (1024 * 1024 * 2)) {
                    //1M<大小≤2M（file是传进来的图片）

//                    Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.2f).toFile(newFile);
                    Thumbnails.of(file.getInputStream()).scale(0.8f).outputQuality(0.3f).toFile(newFile);
                } else if (file.getSize() > (1024 * 1024 * 2)) {
                    //大小>2M（file是传进来的图片）

//                    Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.1f).toFile(newFile);
                    Thumbnails.of(file.getInputStream()).scale(0.7f).outputQuality(0.25f).toFile(newFile);
                }

                //输入流
                FileInputStream input = new FileInputStream(newFile);

                //用自定义MockMultipartFile类的方法将文件转为MultipartFile
                MultipartFile multipartFile = new MockUtils("file", newFile.getName(), "text/plain", input);

                //赋值给文件uploadFile，用于在识别结果处理后进行图片上传到minio
                uploadFile = multipartFile;

                //删除生成的临时文件
                newFile.delete();
                System.out.println("时间4" + sdf.parse(sdf.format(new Date())));
            }
        }

        //1.获取当前的用户的学号，键永远是1，每次都覆盖这个
        String telephone = (String) redisTemplate.opsForValue().get(ResumeStuIdDto.cacheKey(1));

        //2.再通过学号来找他的upload_time
        String dateStr = (String) redisTemplate.opsForValue().get(ResumeCacheDto.cacheKey(telephone));
        Date upload_time = new Date();
        try {
            upload_time = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //3.文件为空，直接返回
//        if (ObjectUtils.isEmpty(file) || file.getSize() <= 0) {
        if (ObjectUtils.isEmpty(uploadFile) || uploadFile.getSize() <= 0) {
            res.setTelephone(telephone);
            res.setMemo("上传文件大小为空");
            return res;
        }

        //4.判断手机号是否为空
        if (telephone != null && !telephone.equals("")) {
            //4-1.手机号不为空，根据手机号查找学生用户
            Student student = stuInfoRepository.findStudentByTelephone(telephone);

            if (student != null) {
                System.out.println("时间5" + sdf.parse(sdf.format(new Date())));

                //5.获取刚刚创建的resume的id（根据stu_id找到resumes实体，再找到r_id）
                Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
                int r_id;
                if (resumes != null) {
                    r_id = resumes.getId();
                } else {
                    res.setTelephone(telephone);
                    res.setMemo("无法获取用户简历信息");
                    return res;
                }

                //⭐6.调用api解析图片中的文字
                System.out.println("时间6-1" + sdf.parse(sdf.format(new Date())));
                byte[] buf = new byte[0];//二进制数组
                try {
                    //获取文件的二进制数组
//                    buf = file.getBytes();//20230503-2323
                    buf = uploadFile.getBytes();//识别压缩图
                } catch (IOException e) {
                    e.printStackTrace();
                    res.setMemo("获取文件字节数据异常" + e.getMessage());
                }
                System.out.println("时间6-2" + sdf.parse(sdf.format(new Date())));

                //7.调用百度云ocr客户端，result就是获取的结果
                System.out.println("时间7-1" + sdf.parse(sdf.format(new Date())));
                JSONObject result = OcrBDUtils.recognizeBasic(buf);
                System.out.println("时间7-2" + sdf.parse(sdf.format(new Date())));

                //8.解析文字的处理
                System.out.println("处理前的识别结果：" + result.toString());
                ArrayList<JSONObject> words = new ArrayList<>();//存放识别出的文字的列表
                JSONArray arr = (JSONArray) result.get("words_result");//获取识别结果JSON数组

                //9.遍历JSON数组，把元素添加到words中，words用于后面遍历获取相应的数据
                for (int i = 0; i < arr.length(); i++) {
                    words.add((JSONObject) arr.get(i));
                }
                System.out.println("识别结果：" + words.toString());//输出识别结果

                //10.个人信息部分
                String birth, phone, current_area, exp;
                int age;
                try {
                    if (((words.get(0).get("words")).toString()).length() > 3) {
                        //第一个字段识别的是“姓名：XXX”，正常识别
                        birth = ((words.get(1).get("words")).toString().split("："))[1];//出生年月
                        phone = ((words.get(3).get("words")).toString().split("："))[1];//联系方式
                        current_area = ((words.get(4).get("words")).toString().split("："))[1];//现居地
                        exp = ((words.get(5).get("words")).toString().split("："))[1];//工作经验
                    } else {
                        //第一个字段识别的是“姓名：”，非正常识别
                        birth = ((words.get(2).get("words")).toString().split("："))[1];//出生年月
                        phone = ((words.get(4).get("words")).toString().split("："))[1];//联系方式
                        current_area = ((words.get(5).get("words")).toString().split("："))[1];//现居地
                        exp = ((words.get(6).get("words")).toString().split("："))[1];//工作经验
                    }
                    System.out.println("识别到的手机号：" + phone + "；现居地：" + current_area + "；工作经验：" + exp);

                    //计算年龄


                    //11.把现居地、工作经验填充到res
                    res.setTelephone(phone);
                    res.setAge(0);
                    res.setCurrent_area(current_area);
                    res.setExp(exp);
                    System.out.println("时间8" + sdf.parse(sdf.format(new Date())));

                } catch (ArrayIndexOutOfBoundsException e) {
                    res.setTelephone(telephone);
                    res.setMemo("文字识别异常");
                    return res;
                }

                //12.求职意向
                String intended = "";//求职意向岗位
                if (words.get(6).get("words").equals("求职意向")) {
                    //第一个字段识别的是“姓名：XXX”，正常识别
                    intended = ((words.get(7).get("words")).toString().split("："))[1];
                } else if (words.get(7).get("words").equals("求职意向")) {
                    //第一个字段识别的是“姓名：”，非正常识别
                    intended = ((words.get(8).get("words")).toString().split("："))[1];
                }
                //12-1.拆分意向兼职
                String[] intentions = intended.split("；");
                //12-2.求职意向更新到DB中
                EditIntentionDto editIntentionDto = new EditIntentionDto();//构造传入的dto
                editIntentionDto.setTelephone(phone);
                editIntentionDto.setIntentions(intentions);
                List<IntentionDto> updateIntentions = this.editIntention(editIntentionDto);//调用函数更新意向兼职
                if (((updateIntentions.get(0)).getMemo()).equals("参数传入错误")) {
                    logger.warn("参数传入错误");
                } else if (((updateIntentions.get(0)).getMemo()).equals("不存在该用户")) {
                    logger.warn("不存在该用户");
                } else {
                    //12-3.把求职意向填充到res
                    res.setIntended(intended);
                }
                System.out.println("时间9" + sdf.parse(sdf.format(new Date())));

                //其余部分
                int startIdx;//开始遍历的位置下标，根据前面识别到哪了来确定
                if (((words.get(0).get("words")).toString()).length() > 3) {
                    //第一个字段识别的是“姓名：XXX”，正常识别
                    startIdx = 8;
                } else {
                    //第一个字段识别的是“姓名：”，非正常识别
                    startIdx = 9;
                }
                for (int i = startIdx; i < words.size(); i++) {//遍历列表
                    //13.教育背景【以本科生为例，设定为只有本科一段教育经历】
                    ResumeDetailDto dto1 = new ResumeDetailDto();
                    if (words.get(i).get("words").equals("教育背景") || words.get(i).get("words").equals("敦育背景")) {
                        //可能会识别为“敦育背景”
                        String time = (words.get(i + 1).get("words")).toString();
                        String start = (time.split("-"))[0];
                        String end = (time.split("-"))[1];

                        //String转Date
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM");
                        Date start_time = sdf2.parse(start);//用于存到DB
                        Date end_time = sdf2.parse(end);//用于存到DB

                        //标题
                        String title = (words.get(i + 2).get("words")).toString();

                        //内容
                        int j = i + 3;
                        String content = "";
                        while (!(words.get(j)).get("words").equals("项目经历")) {
                            content += (words.get(j).get("words")).toString() + "。";
                            j++;
                        }

                        //添加一条resumesdetail记录
                        Date now2 = new Date(System.currentTimeMillis());
                        resumesDetailRepository.addAResumesDetailRecord(r_id, start_time, end_time, title,
                                content, "教育背景", now2, "已上传");
                        int rd_id = resumesDetailRepository.findLatestResumesDetail();

                        //把信息填充到dto中
                        dto1.setTelephone(phone);
                        dto1.setR_id(r_id);
                        dto1.setRd_id(rd_id);
                        dto1.setTitle(title);
                        dto1.setContent(content);
                        dto1.setTime(time);
                        dto1.setCategory("教育背景");
                        dto1.setHasContent(1);
                        dto1.setStatus("已上传");

                        List<ResumeDetailDto> list = new ArrayList<>();
                        list.add(dto1);
                        res.setEducationBgList(list);//set到res中
                    }
                    System.out.println("时间10" + sdf.parse(sdf.format(new Date())));

                    //14.项目经历【1~N个】
                    if (words.get(i).get("words").equals("项目经历")) {
                        List<ResumeDetailDto> list = new ArrayList();

                        int k = i + 1;
                        //将日期分割，分别进行判断是否是日期格式
                        String time = (words.get(k).get("words")).toString();
                        String start = (time.split("-"))[0];
                        String end = (time.split("-"))[1];
                        while (isDate(start) == true && isDate(end) == true) {//当前的是时间格式，就继续循环，表示当前遇到的是一个项目的时间
                            //String转Date
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM");
                            Date start_time = sdf2.parse(start);//用于存到DB
                            Date end_time = sdf2.parse(end);//用于存到DB

                            //标题
                            String title = (words.get(k + 1).get("words")).toString();

                            //项目内容【1~N行】
                            int j = k + 2;
                            String content = "";
                            //只要不是日期or专业技能字样就循环把内容加到content
                            while (!(words.get(j)).get("words").equals("专业技能")) {
                                if ((isDate(((words.get(j).get("words").toString()).split("-"))[0]) == true &&
                                        isDate(((words.get(j).get("words").toString()).split("-"))[1]) == true)) {//是日期
                                    break;
                                } else {//不是日期，就加到内容
                                    content += (words.get(j).get("words")).toString() + "。";
                                    j++;
                                }
                            }

                            //创建一个detail记录
                            Date now2 = new Date(System.currentTimeMillis());
                            resumesDetailRepository.addAResumesDetailRecord(r_id, start_time, end_time, title,
                                    content, "项目经历", now2, "已上传");
                            int rd_id = resumesDetailRepository.findLatestResumesDetail();//最新的记录的rd_id

                            //构造dto
                            ResumeDetailDto dto = new ResumeDetailDto();
                            dto.setTelephone(phone);
                            dto.setR_id(r_id);
                            dto.setRd_id(rd_id);
                            dto.setTitle(title);
                            dto.setContent(content);
                            dto.setTime(time);
                            dto.setCategory("项目经历");
                            dto.setHasContent(1);
                            dto.setStatus("已上传");
                            list.add(dto);//加到列表中

                            k = j;//更新k的值
                            if ((isDate(((words.get(k).get("words").toString()).split("-"))[0]) == true &&
                                    isDate(((words.get(k).get("words").toString()).split("-"))[1]) == true)) {//是日期
                                time = (words.get(k).get("words")).toString();
                                start = (time.split("-"))[0];
                                end = (time.split("-"))[1];
                            } else {//不是日期，就退出循环
                                break;
                            }
                        }
                        res.setProjectExpList(list);//set到res中
                    }
                    System.out.println("时间11" + sdf.parse(sdf.format(new Date())));

                    //15.专业技能【1~N个】
                    if (words.get(i).get("words").equals("专业技能")) {
                        List<ResumeDetailDto> list = new ArrayList();

                        int k = i + 1;

                        //专业技能【1~N行】
                        String content = "";
                        //只要不是校园经历字样content，就添加一条记录
                        while (!(words.get(k)).get("words").equals("校园经历")) {
                            content = (words.get(k).get("words")).toString() + "。";
                            k++;

                            //创建一个detail记录
                            Date now2 = new Date(System.currentTimeMillis());
                            resumesDetailRepository.addAProfessionalResumesDetailRecord(r_id,
                                    content, "专业技能", now2, "已上传");
                            int rd_id = resumesDetailRepository.findLatestResumesDetail();//最新的记录的rd_id

                            //构造dto
                            ResumeDetailDto dto = new ResumeDetailDto();
                            dto.setTelephone(phone);
                            dto.setR_id(r_id);
                            dto.setRd_id(rd_id);
                            dto.setContent(content);
                            dto.setCategory("专业技能");
                            dto.setHasContent(1);
                            dto.setStatus("已上传");
                            list.add(dto);//加到列表中
                        }
                        res.setProfessionalSkillList(list);//set到res中
                    }
                    System.out.println("时间12" + sdf.parse(sdf.format(new Date())));

                    //16.校园经历【1~N个，多为1个】
                    if (words.get(i).get("words").equals("校园经历")) {
                        List<ResumeDetailDto> list = new ArrayList();

                        int k = i + 1;
                        //将日期分割，分别进行判断是否是日期格式
                        String time = (words.get(k).get("words")).toString();
                        String start = (time.split("-"))[0];
                        String end = (time.split("-"))[1];
                        while (isDate(start) == true && isDate(end) == true) {//当前的是时间格式，就继续循环，表示当前遇到的是一个项目的时间
                            //String转Date
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM");
                            Date start_time = sdf2.parse(start);//用于存到DB
                            Date end_time = sdf2.parse(end);//用于存到DB

                            //标题
                            String title = (words.get(k + 1).get("words")).toString();

                            //校园经历【1~N行】
                            int j = k + 2;
                            String content = "";
                            //只要不为空就循环把内容加到content
                            String word = (String) words.get(j).get("words");
                            while (word != null && !word.equals("")) {
                                if (!(isDate(((words.get(j).get("words").toString()).split("-"))[0]) == true &&
                                        isDate(((words.get(j).get("words").toString()).split("-"))[1]) == true)) {//不是日期，就加到内容
                                    content += (words.get(j).get("words")).toString() + "。";
                                    j++;
                                } else {//是日期
                                    break;
                                }
                                if (words.size() != j) {//下一个内容不是空对象，就获取内容
                                    word = (String) words.get(j).get("words");
                                } else {//否则退出while
                                    break;
                                }
                            }

                            //创建一个detail记录
                            Date now2 = new Date(System.currentTimeMillis());
                            resumesDetailRepository.addAResumesDetailRecord(r_id, start_time, end_time, title,
                                    content, "校园经历", now2, "已上传");
                            int rd_id = resumesDetailRepository.findLatestResumesDetail();//最新的记录的rd_id

                            //构造dto
                            ResumeDetailDto dto = new ResumeDetailDto();
                            dto.setTelephone(phone);
                            dto.setR_id(r_id);
                            dto.setRd_id(rd_id);
                            dto.setTitle(title);
                            dto.setContent(content);
                            dto.setTime(time);
                            dto.setCategory("校园经历");
                            dto.setHasContent(1);
                            dto.setStatus("已上传");
                            list.add(dto);//加到列表中

                            if (words.size() != j) {//下一个内容不是空对象，就更新k的值
                                k = j;//更新k的值
                                if ((isDate(((words.get(k).get("words").toString()).split("-"))[0]) == true &&
                                        isDate(((words.get(k).get("words").toString()).split("-"))[1]) == true)) {//是日期
                                    time = (words.get(k).get("words")).toString();
                                    start = (time.split("-"))[0];
                                    end = (time.split("-"))[1];
                                } else {//不是日期，就退出循环
                                    break;
                                }
                            } else {//否则退出if
                                break;
                            }
                        }
                        res.setCampusExpList(list);//set到res中
                        res.setR_status("已上传");
                        res.setMemo("上传成功");
                    }
                }
                System.out.println("时间13" + sdf.parse(sdf.format(new Date())));

                //⭐17.存在学生，把图片存入minio，并返回图片的url
                String pic_url = this.upload(uploadFile);//上传压缩图
                System.out.println("时间14" + sdf.parse(sdf.format(new Date())));

                //18.填充照片url+上传时间
                res.setPic_url(pic_url);
                res.setUpload_time(upload_time);

                //19.图片url、现居地等信息存入DB，即更新resume记录
                resumesInfoRepository.modifyResumeRecord(pic_url, upload_time, current_area, exp, "已上传", telephone);
                System.out.println("时间15" + sdf.parse(sdf.format(new Date())));
            } else {//不存在学生
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }

        return res;
    }

    //TODO 简历编辑（个人信息）-学生
    @Override
    public ResumeEditCallbackDto editPersonal(EditPersonalDto editPersonalDto) throws ParseException, Exception {
        ResumeEditCallbackDto res = new ResumeEditCallbackDto();

        if (editPersonalDto != null) {
            String telephone = editPersonalDto.getTelephone();//手机号
            String exp = editPersonalDto.getExp();
            String current_area = editPersonalDto.getCurrent_area();

            if (telephone != null && !telephone.equals("")) {//手机号不为空
                //根据手机号查找学生用户
                Student student = stuInfoRepository.findStudentByTelephone(telephone);

                if (student != null) {//存在学生
                    //根据手机号找到该学生的简历
                    Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);

                    if (resumes != null) {//存在简历
                        //更新DB
                        resumesInfoRepository.updateResumesInfo(exp, current_area, "已修改", telephone);

                        //找到简历的详细信息
//                        GetResumeDto request = new GetResumeDto();
//                        request.setTelephone(telephone);
//                        ResumeInfoDto dto = this.getResume(request);//返回的详细信息dto
                        ResumeInfoDto dto = this.getResume(telephone);
                        dto.setExp(exp);
                        dto.setCurrent_area(current_area);
                        dto.setStatus("已修改");

                        //填充到res
                        res.setTelephone(telephone);
                        res.setInfo(dto);
                    } else {//不存在简历
                        logger.warn("该账号不存在简历信息");
                        res.setTelephone(telephone);
                        res.setMemo("该账号不存在简历信息");
                    }
                } else {//不存在账号
                    logger.warn("该账号不存在");
                    res.setTelephone(telephone);
                    res.setMemo("该账号不存在");
                }
            } else {//手机号为空
                logger.warn("请输入手机号");
                res.setTelephone(telephone);
                res.setMemo("请输入手机号");
            }
        }
        return res;
    }

    //TODO 简历编辑（校园经历）-学生
    @Override
    public GetCampusDto editCampus(EditCampusDto editCampusDto) throws ParseException, Exception {
        GetCampusDto res = new GetCampusDto();

        if (editCampusDto != null) {
            String telephone = editCampusDto.getTelephone();//手机号
            int rd_id = editCampusDto.getRd_id();
            String title = editCampusDto.getTitle();
            String content = editCampusDto.getContent();
            String start_time = editCampusDto.getStart_time();
            String end_time = editCampusDto.getEnd_time();

            if (telephone != null && !telephone.equals("")) {//手机号不为空
                //根据手机号查找学生用户
                Student student = stuInfoRepository.findStudentByTelephone(telephone);

                if (student != null) {//存在学生
                    //根据手机号找到该学生的简历、校园经历
                    Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
                    //根据r_id+类型找到校园经历
                    List<Resumedetail> list = resumesDetailRepository.findResumeDetailByRIdAndCategory2(resumes.getId(), "校园经历");

                    //遍历列表，找到那个detail
                    Resumedetail resumedetail = new Resumedetail();
                    for (Resumedetail detail : list) {
                        if (detail.getId() == rd_id) {//rd_id相同
                            resumedetail = detail;
                        }
                    }

                    if (resumes != null && resumedetail != null) {//存在简历和校园经历
                        int f1 = 0, f2 = 0;//判断是否为空
                        //处理时间
                        Date start = new Date(), end = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        if (start_time != null && !start_time.equals("")) {//开始日期非空
                            start = sdf.parse(start_time);
                        } else {//空
                            f1 = 1;
                        }
                        if (end_time != null && !end_time.equals("")) {//结束日期非空
                            end = sdf.parse(end_time);
                        } else {//空
                            f2 = 1;
                        }

                        //更新DB
                        if (f1 == 1 && f2 == 1) {
                            resumesDetailRepository.updateResumedetailInfo1(title, content, "已修改", rd_id);
                        } else if (f1 == 1 && f2 == 0) {
                            resumesDetailRepository.updateResumedetailInfo2(title, content, end, "已修改", rd_id);
                        } else if (f1 == 0 && f2 == 1) {
                            resumesDetailRepository.updateResumedetailInfo3(title, content, start, "已修改", rd_id);
                        } else {
                            resumesDetailRepository.updateResumedetailInfo4(title, content, start, end, "已修改", rd_id);
                        }
                        //更新简历表的状态
                        resumesInfoRepository.updateResumesStatus("已修改", telephone);

                        Resumedetail resumedetail2 = resumesDetailRepository.findResumeDetailByRdId(rd_id, resumes.getId());
                        //填充到res
                        res.setTelephone(telephone);
                        res.setRd_id(rd_id);
                        res.setTitle(resumedetail2.getTitle());
                        res.setContent(resumedetail2.getContent());
                        res.setStart_time(resumedetail2.getStartTime());
                        res.setEnd_time(resumedetail2.getEndTime());
                        res.setRd_status(resumedetail2.getRdStatus());
                    } else {//不存在简历/校园经历
                        if (resumes != null && resumedetail == null) {
                            logger.warn("该账号不存在校园经历信息");

                            //创建一条detail
                            Date now2 = new Date(System.currentTimeMillis());
                            //处理时间
                            Date start = new Date(), end = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                            if (start_time != null && !start_time.equals("")) {//开始日期非空
                                start = sdf.parse(start_time);
                            }
                            if (end_time != null && !end_time.equals("")) {//结束日期非空
                                end = sdf.parse(end_time);
                            }

                            //更新DB
                            resumesDetailRepository.addAResumesDetailRecord(resumes.getId(), start, end, title,
                                    content, "校园经历", now2, "已上传");
                            int latestId = resumesDetailRepository.findLatestResumesDetail();//最新的记录的rd_id

                            res.setTelephone(telephone);
                            res.setRd_id(latestId);
                            res.setTitle(title);
                            res.setContent(content);
                            res.setStart_time(start);
                            res.setEnd_time(end);
                            res.setRd_status("已上传");
                            res.setMemo("成功编辑第一条校园经历");
                        } else if (resumes == null) {
                            logger.warn("该账号不存在简历信息");
                            res.setMemo("该账号不存在简历信息");
                        }
                    }
                } else {//不存在账号
                    logger.warn("该账号不存在");
                    res.setTelephone(telephone);
                    res.setMemo("该账号不存在");
                }
            } else {//手机号为空
                logger.warn("请输入手机号");
                res.setTelephone(telephone);
                res.setMemo("请输入手机号");
            }
        }
        return res;
    }

    //TODO 简历编辑（教育背景）-学生
    @Override
    public GetEducationDto editEducation(EditEducationDto editEducationDto) throws ParseException, Exception {
        GetEducationDto res = new GetEducationDto();

        if (editEducationDto != null) {
            String telephone = editEducationDto.getTelephone();//手机号
            int rd_id = editEducationDto.getRd_id();
            String title = editEducationDto.getTitle();
            String content = editEducationDto.getContent();
            String start_time = editEducationDto.getStart_time();
            String end_time = editEducationDto.getEnd_time();

            if (telephone != null && !telephone.equals("")) {//手机号不为空
                //根据手机号查找学生用户
                Student student = stuInfoRepository.findStudentByTelephone(telephone);

                if (student != null) {//存在学生
                    //根据手机号找到该学生的简历、教育背景
                    Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
                    //根据r_id+类型找到教育背景
                    Resumedetail resumedetail = resumesDetailRepository.findResumeDetailByRIdAndCategory(resumes.getId(), "教育背景");

                    if (resumes != null && resumedetail != null && resumedetail.getId() == rd_id) {//存在简历和教育背景
                        int f1 = 0, f2 = 0;//判断是否为空
                        //处理时间
                        Date start = new Date(), end = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        if (start_time != null && !start_time.equals("")) {//开始日期非空
                            start = sdf.parse(start_time);
                        } else {//空
                            f1 = 1;
                        }
                        if (end_time != null && !end_time.equals("")) {//结束日期非空
                            end = sdf.parse(end_time);
                        } else {//空
                            f2 = 1;
                        }

                        //更新DB
                        if (f1 == 1 && f2 == 1) {
                            resumesDetailRepository.updateResumedetailInfo1(title, content, "已修改", rd_id);
                        } else if (f1 == 1 && f2 == 0) {
                            resumesDetailRepository.updateResumedetailInfo2(title, content, end, "已修改", rd_id);
                        } else if (f1 == 0 && f2 == 1) {
                            resumesDetailRepository.updateResumedetailInfo3(title, content, start, "已修改", rd_id);
                        } else {
                            resumesDetailRepository.updateResumedetailInfo4(title, content, start, end, "已修改", rd_id);
                        }
                        //更新简历表的状态
                        resumesInfoRepository.updateResumesStatus("已修改", telephone);

                        Resumedetail resumedetail2 = resumesDetailRepository.findResumeDetailByRdId(rd_id, resumes.getId());
                        //填充到res
                        res.setTelephone(telephone);
                        res.setRd_id(rd_id);
                        res.setTitle(resumedetail2.getTitle());
                        res.setContent(resumedetail2.getContent());
                        res.setStart_time(resumedetail2.getStartTime());
                        res.setEnd_time(resumedetail2.getEndTime());
                        res.setRd_status(resumedetail2.getRdStatus());
                    } else {
                        //不存在简历/教育背景
                        if (resumes != null && resumedetail == null) {
                            logger.warn("该账号不存在教育背景信息");

                            //创建一条detail
                            Date now2 = new Date(System.currentTimeMillis());
                            //处理时间
                            Date start = new Date(), end = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                            if (start_time != null && !start_time.equals("")) {//开始日期非空
                                start = sdf.parse(start_time);
                            }
                            if (end_time != null && !end_time.equals("")) {//结束日期非空
                                end = sdf.parse(end_time);
                            }

                            //更新DB
                            resumesDetailRepository.addAResumesDetailRecord(resumes.getId(), start, end, title,
                                    content, "教育背景", now2, "已上传");
                            int latestId = resumesDetailRepository.findLatestResumesDetail();//最新的记录的rd_id

                            res.setTelephone(telephone);
                            res.setRd_id(latestId);
                            res.setTitle(title);
                            res.setContent(content);
                            res.setStart_time(start);
                            res.setEnd_time(end);
                            res.setRd_status("已上传");
                            res.setMemo("成功编辑第一条教育背景");
                        } else if (resumes == null) {
                            logger.warn("该账号不存在简历信息");
                            res.setMemo("该账号不存在简历信息");
                        }
                    }
                } else {//不存在账号
                    logger.warn("该账号不存在");
                    res.setTelephone(telephone);
                    res.setMemo("该账号不存在");
                }
            } else {//手机号为空
                logger.warn("请输入手机号");
                res.setTelephone(telephone);
                res.setMemo("请输入手机号");
            }
        }
        return res;
    }

    //TODO 简历编辑（项目经历）-学生
    @Override
    public GetProgramDto editProgram(EditProgramDto editProgramDto) throws ParseException, Exception {
        GetProgramDto res = new GetProgramDto();

        if (editProgramDto != null) {
            String telephone = editProgramDto.getTelephone();//手机号
            int rd_id = editProgramDto.getRd_id();
            String title = editProgramDto.getTitle();
            String old_title = editProgramDto.getOld_title();
            String content = editProgramDto.getContent();
            String start_time = editProgramDto.getStart_time();
            String end_time = editProgramDto.getEnd_time();

            if (telephone != null && !telephone.equals("")) {//手机号不为空
                //根据手机号查找学生用户
                Student student = stuInfoRepository.findStudentByTelephone(telephone);

                if (student != null) {//存在学生
                    //根据手机号找到该学生的简历、项目经历
                    Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
                    //根据r_id+类型找到项目经历
                    List<Resumedetail> list = resumesDetailRepository.findResumeDetailByRIdAndCategory2(resumes.getId(), "项目经历");

                    //遍历列表，找到那个detail
                    Resumedetail resumedetail = new Resumedetail();
                    for (Resumedetail detail : list) {
//                        if (detail.getId() == rd_id && (old_title.equals(detail.getTitle()))) {//rd_id相同且标题相同
                        if (detail.getId() == rd_id) {//rd_id相同
                            resumedetail = detail;
                        }
                    }

                    if (resumes != null && resumedetail != null) {//存在简历和项目经历
                        int f1 = 0, f2 = 0;//判断是否为空
                        //处理时间
                        Date start = new Date(), end = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        if (start_time != null && !start_time.equals("")) {//开始日期非空
                            start = sdf.parse(start_time);
                        } else {//空
                            f1 = 1;
                        }
                        if (end_time != null && !end_time.equals("")) {//结束日期非空
                            end = sdf.parse(end_time);
                        } else {//空
                            f2 = 1;
                        }

                        //更新DB
                        if (f1 == 1 && f2 == 1) {
                            resumesDetailRepository.updateResumedetailInfo1(title, content, "已修改", rd_id);
                        } else if (f1 == 1 && f2 == 0) {
                            resumesDetailRepository.updateResumedetailInfo2(title, content, end, "已修改", rd_id);
                        } else if (f1 == 0 && f2 == 1) {
                            resumesDetailRepository.updateResumedetailInfo3(title, content, start, "已修改", rd_id);
                        } else {
                            resumesDetailRepository.updateResumedetailInfo4(title, content, start, end, "已修改", rd_id);
                        }
                        //更新简历表的状态
                        resumesInfoRepository.updateResumesStatus("已修改", telephone);

                        Resumedetail resumedetail2 = resumesDetailRepository.findResumeDetailByRdId(rd_id, resumes.getId());
                        //填充到res
                        res.setTelephone(telephone);
                        res.setRd_id(rd_id);
                        res.setTitle(resumedetail2.getTitle());
                        res.setContent(resumedetail2.getContent());
                        res.setStart_time(resumedetail2.getStartTime());
                        res.setEnd_time(resumedetail2.getEndTime());
                        res.setRd_status(resumedetail2.getRdStatus());
                    } else {//不存在简历/项目经历
                        if (resumes != null && resumedetail == null) {
                            logger.warn("该账号不存在项目经历信息");

                            //创建一条detail
                            Date now2 = new Date(System.currentTimeMillis());
                            //处理时间
                            Date start = new Date(), end = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                            if (start_time != null && !start_time.equals("")) {//开始日期非空
                                start = sdf.parse(start_time);
                            }
                            if (end_time != null && !end_time.equals("")) {//结束日期非空
                                end = sdf.parse(end_time);
                            }

                            //更新DB
                            resumesDetailRepository.addAResumesDetailRecord(resumes.getId(), start, end, title,
                                    content, "项目经历", now2, "已上传");
                            int latestId = resumesDetailRepository.findLatestResumesDetail();//最新的记录的rd_id

                            res.setTelephone(telephone);
                            res.setRd_id(latestId);
                            res.setTitle(title);
                            res.setContent(content);
                            res.setStart_time(start);
                            res.setEnd_time(end);
                            res.setRd_status("已上传");
                            res.setMemo("成功编辑第一条项目经历");
                        } else if (resumes == null) {
                            logger.warn("该账号不存在简历信息");
                            res.setMemo("该账号不存在简历信息");
                        }
                    }
                } else {//不存在账号
                    logger.warn("该账号不存在");
                    res.setTelephone(telephone);
                    res.setMemo("该账号不存在");
                }
            } else {//手机号为空
                logger.warn("请输入手机号");
                res.setTelephone(telephone);
                res.setMemo("请输入手机号");
            }
        }
        return res;
    }

    //TODO 简历编辑（专业技能）-学生
    @Override
    public GetSkillsDto editSkills(EditSkillsDto editSkillsDto) throws ParseException, Exception {
        GetSkillsDto res = new GetSkillsDto();

        if (editSkillsDto != null) {
            String telephone = editSkillsDto.getTelephone();//手机号
            int rd_id = editSkillsDto.getRd_id();
            String content = editSkillsDto.getContent();
            String old_content = editSkillsDto.getOld_content();

            if (telephone != null && !telephone.equals("")) {//手机号不为空
                //根据手机号查找学生用户
                Student student = stuInfoRepository.findStudentByTelephone(telephone);

                if (student != null) {//存在学生
                    //根据手机号找到该学生的简历、专业技能
                    Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
//                    Resumedetail resumedetail = resumesDetailRepository.findResumeDetailByRdId(rd_id, resumes.getId());

                    //根据r_id+类型找到专业技能
                    List<Resumedetail> list = resumesDetailRepository.findResumeDetailByRIdAndCategory2(resumes.getId(), "专业技能");

                    //遍历列表，找到那个detail
                    Resumedetail resumedetail = null;
                    for (Resumedetail detail : list) {
                        if (detail.getId() == rd_id) {//rd_id相同
                            resumedetail = detail;
                        }
                    }

                    if (resumes != null && resumedetail != null) {//存在简历和专业技能
                        //更新DB
                        resumesDetailRepository.updateResumedetailInfo5(content, "已修改", rd_id);
                        //更新简历表的状态
                        resumesInfoRepository.updateResumesStatus("已修改", telephone);

                        Resumedetail resumedetail2 = resumesDetailRepository.findResumeDetailByRdId(rd_id, resumes.getId());
                        //填充到res
                        res.setTelephone(telephone);
                        res.setRd_id(rd_id);
                        res.setContent(resumedetail2.getContent());
                        res.setRd_status(resumedetail2.getRdStatus());
                    } else {//不存在简历/专业技能
                        if (resumes != null && resumedetail == null) {
                            logger.warn("该账号不存在专业技能信息");
                            //创建一条detail
                            Date now2 = new Date(System.currentTimeMillis());
                            resumesDetailRepository.addAProfessionalResumesDetailRecord(resumes.getId(),
                                    content, "专业技能", now2, "已上传");
                            int latestId = resumesDetailRepository.findLatestResumesDetail();//最新的记录的rd_id

                            res.setTelephone(telephone);
                            res.setRd_id(latestId);
                            res.setContent(content);
                            res.setRd_status("已上传");
                            res.setMemo("成功编辑第一条专业技能");
                        } else if (resumes == null) {
                            logger.warn("该账号不存在简历信息");
                            res.setMemo("该账号不存在简历信息");
                        }
                    }
                } else {//不存在账号
                    logger.warn("该账号不存在");
                    res.setTelephone(telephone);
                    res.setMemo("该账号不存在");
                }
            } else {//手机号为空
                logger.warn("请输入手机号");
                res.setTelephone(telephone);
                res.setMemo("请输入手机号");
            }
        }
        return res;
    }

    //TODO 简历详情删除-学生
    @Override
    public DeleteDetailCallbackDto deleteDetail(DeleteDetailDto deleteDetailDto) throws ParseException, Exception {
        DeleteDetailCallbackDto res = new DeleteDetailCallbackDto();

        if (deleteDetailDto != null) {
            //1.有数据，获取传入的数据
            String telephone = deleteDetailDto.getTelephone();//手机号
            int rd_id = deleteDetailDto.getRd_id();
            System.out.println("要删除的简历详情id是：" + rd_id);

            if (telephone != null && !telephone.equals("")) {
                //2.手机号不为空，根据手机号查找学生用户
                Student student = stuInfoRepository.findStudentByTelephone(telephone);

                if (student != null) {
                    //3.存在学生，根据手机号找到该学生的简历、项目经历
                    Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
                    Resumedetail resumedetail = null;

                    if (resumes != null) {
                        //4.存在简历，找是否存在简历细节
                        resumedetail = resumesDetailRepository.findResumeDetailByRdId(rd_id, resumes.getId());
                    }

                    if (resumes != null && resumedetail != null) {
                        //5.存在简历和简历细节
                        if (resumedetail.getId() == rd_id && !resumedetail.getRdStatus().equals("已删除")) {
                            //6.当前的rd是该学生的简历详情id，且不是已删除的简历详情，就更新DB
                            resumesDetailRepository.deleteResumedetailByRdId(rd_id);

                            //7.更新简历状态
                            resumesInfoRepository.updateResumesStatus("已修改", telephone);

                            //填充到res
                            res.setTelephone(telephone);
                            res.setRd_id(rd_id);
                            res.setMemo("删除成功！");
                        } else {//该简历详情不是该学生的
                            logger.warn("该简历详情不是该学生的");
                            res.setTelephone(telephone);
                            res.setRd_id(0);
                            res.setMemo("该简历详情不是该学生的");
                        }
                    } else {//不存在简历/简历详情
                        if (resumes == null) {
                            logger.warn("该账号不存在简历信息");
                            res.setTelephone(telephone);
                            res.setRd_id(0);
                            res.setMemo("该账号不存在简历信息");
                        } else if (resumedetail == null) {
                            logger.warn("该账号不存在简历详情");
                            res.setTelephone(telephone);
                            res.setRd_id(0);
                            res.setMemo("该账号不存在简历详情");
                        }
                    }
                } else {//不存在账号
                    logger.warn("该账号不存在");
                    res.setTelephone(telephone);
                    res.setRd_id(0);
                    res.setMemo("该账号不存在");
                }
            } else {//手机号为空
                logger.warn("请输入手机号");
                res.setTelephone(telephone);
                res.setRd_id(0);
                res.setMemo("请输入手机号");
            }
        }
        return res;
    }

    //TODO 简历详情增加-学生
    @Override
    public AddDetailCallbackDto addDetail(AddDetailDto addDetailDto) throws ParseException, Exception {
        AddDetailCallbackDto res = new AddDetailCallbackDto();

        if (addDetailDto != null) {
            //1.有数据，获取传入的数据
            String stu_id = addDetailDto.getTelephone();
            int r_id = addDetailDto.getR_id();
            String category = addDetailDto.getCategory();

            //2.根据种类判断
            if (category.equals("专业技能")) {
                String content = addDetailDto.getContent();

                //3.判断是否存在该resume简历，以及该简历是否是该学生的
                Resumes resumes = resumesInfoRepository.checkIsTheResumeOfStu(r_id, stu_id);

                if (resumes != null) {
                    //4.存在且是该学生的兼职，获取当前时间
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年月日格式
                    Date now = sdf2.parse(sdf2.format(new Date()));

                    //5.创建resumedetail记录
                    resumesDetailRepository.addAResumesDetailRecord2(r_id, content, category, now, "已上传");//新增一条记录的状态是”已上传“

                    //6.获取刚刚创建的记录
                    Resumedetail resumedetail = resumesDetailRepository.findLatestResumesDetailWithAdd();
                    if (resumedetail != null) {
                        //7.更新简历状态
                        resumesInfoRepository.updateResumesStatus("已修改", stu_id);

                        //8.构造res
                        res.setTelephone(resumedetail.getR().getStu().getId());
                        res.setRd_id(resumedetail.getId());
                        res.setR_id(resumedetail.getR().getId());
                        res.setContent(resumedetail.getContent());
                        res.setCreate_time(resumedetail.getCreateTime());
                        res.setRd_status(resumedetail.getRdStatus());
                        res.setMemo("添加成功");
                    } else {
                        //创建失败
                        logger.warn("添加失败");
                        AddDetailCallbackDto dto = new AddDetailCallbackDto();
                        dto.setTelephone("0");//学生账号
                        dto.setR_id(r_id);
                        dto.setMemo("添加失败");
                    }
                } else {
                    logger.warn("不存在简历");
                    AddDetailCallbackDto dto = new AddDetailCallbackDto();
                    dto.setTelephone("0");//学生账号
                    dto.setR_id(r_id);
                    dto.setMemo("不存在简历");
                }
            } else {
                //其他三种detail

                String date = addDetailDto.getDate();
                String title = addDetailDto.getTitle();
                String content = addDetailDto.getContent();

                //3.判断是否存在该resume简历，以及该简历是否是该学生的
                Resumes resumes = resumesInfoRepository.checkIsTheResumeOfStu(r_id, stu_id);

                if (resumes != null) {
                    //4.存在且是该学生的兼职,处理日期
                    String[] arr = date.split("-");
                    String start = arr[0].substring(0, 4) + "-" + arr[0].substring(5, 7) + "-01";
                    String end = arr[1].substring(0, 4) + "-" + arr[0].substring(5, 7) + "-01";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//年月日格式
                    Date start_time = sdf.parse(start);
                    Date end_time = sdf.parse(end);

                    //5.获取当前时间
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年月日格式
                    Date now = sdf2.parse(sdf2.format(new Date()));

                    //6.创建resumedetail记录
                    resumesDetailRepository.addAResumesDetailRecord(r_id, start_time, end_time, title,
                            content, category, now, "已上传");//新增一条记录的状态是”已上传“

                    //7.获取刚刚创建的记录
                    Resumedetail resumedetail = resumesDetailRepository.findLatestResumesDetailWithAdd();
                    if (resumedetail != null) {
                        //8.构造res
                        res.setTelephone(resumedetail.getR().getStu().getId());
                        res.setRd_id(resumedetail.getId());
                        res.setR_id(resumedetail.getR().getId());
                        res.setStart_time(resumedetail.getStartTime());
                        res.setEnd_time(resumedetail.getEndTime());
                        res.setTitle(resumedetail.getTitle());
                        res.setContent(resumedetail.getContent());
                        res.setCreate_time(resumedetail.getCreateTime());
                        res.setRd_status(resumedetail.getRdStatus());
                        res.setMemo("添加成功");
                    } else {
                        //创建失败
                        logger.warn("添加失败");
                        AddDetailCallbackDto dto = new AddDetailCallbackDto();
                        dto.setTelephone("0");//学生账号
                        dto.setR_id(r_id);
                        dto.setMemo("添加失败");
                    }
                } else {
                    logger.warn("不存在简历");
                    AddDetailCallbackDto dto = new AddDetailCallbackDto();
                    dto.setTelephone("0");//学生账号
                    dto.setR_id(r_id);
                    dto.setMemo("不存在简历");
                }
            }
        } else {
            logger.warn("请检查输入");
            AddDetailCallbackDto dto = new AddDetailCallbackDto();
            dto.setTelephone("0");//学生账号
            dto.setR_id(0);
            dto.setMemo("请检查输入");
        }
        return res;
    }

    @Override
    public DeleteResumeCallbackDto deleteResume(DeleteResumeDto deleteResumeDto) throws ParseException, Exception {
        DeleteResumeCallbackDto res = new DeleteResumeCallbackDto();

        if (deleteResumeDto != null) {
            String telephone = deleteResumeDto.getTelephone();//手机号

            if (telephone != null && !telephone.equals("")) {//手机号不为空
                //根据手机号查找学生用户
                Student student = stuInfoRepository.findStudentByTelephone(telephone);

                if (student != null) {//存在学生
                    //根据手机号找到该学生的简历、项目经历
                    Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);

                    if (resumes != null) {//存在简历
                        //判断是否存在简历详情，若存在，修改简历详情的状态
                        List<Resumedetail> list = resumesDetailRepository.getAllResumedetailByRId(resumes.getId());
                        if (list.size() > 0) {//存在简历详情
                            int flag = 0;//判断能否删除，flag=1表示不能删除
                            for (Resumedetail detail : list) {
                                if (!detail.getRdStatus().equals("已删除")) {//有未删除的简历详情，就不能删除简历
                                    flag = 1;
                                }
                            }
                            if (flag == 1) {//不能删除简历
                                logger.warn("当前无法删除该简历");
                                res.setTelephone(telephone);
                                res.setMemo("当前无法删除该简历");
                            } else {
                                //删除简历
                                resumesInfoRepository.deleteResumeByRId(resumes.getId());

                                //填充到res
                                res.setTelephone(telephone);
                                res.setMemo("删除成功！");
                            }
                        } else {//是空简历
                            //直接删除简历
                            resumesInfoRepository.deleteResumeByRId(resumes.getId());

                            //填充到res
                            res.setTelephone(telephone);
                            res.setMemo("删除成功！");
                        }
                    } else {//不存在简历
                        logger.warn("该账号不存在简历信息");
                        res.setTelephone(telephone);
                        res.setMemo("该账号不存在简历信息");
                    }
                } else {//不存在账号
                    logger.warn("该账号不存在");
                    res.setTelephone(telephone);
                    res.setMemo("该账号不存在");
                }
            } else {//手机号为空
                logger.warn("请输入手机号");
                res.setTelephone(telephone);
                res.setMemo("请输入手机号");
            }
        }
        return res;
    }

    @Override
    public List<IntentionDto> getIntention(String telephone) throws ParseException {
        List<IntentionDto> res = new ArrayList<>();

        if (telephone != null && !telephone.equals("")) {
            //根据手机号查找学生用户
            Student student = stuInfoRepository.findStudentByTelephone(telephone);
            if (student != null) {//存在学生
                //找到该用户的所有意向兼职
                List<Intention> list = intentionRepository.getIntentionsByStuId(telephone);

                if (list != null && list.size() > 0) {
                    //有意向兼职
                    for (Intention i : list) {
                        IntentionDto dto = new IntentionDto();
                        dto.setI_id(i.getId());
                        dto.setContent(i.getContent());
                        dto.setStu_id(i.getStu().getId());
                        dto.setMemo("存在意向兼职");
                        res.add(dto);
                    }
                } else {
                    IntentionDto dto = new IntentionDto();
                    logger.warn("暂无意向兼职");
                    dto.setMemo("暂无意向兼职");
                    res.add(dto);
                }
            } else {//不存在账号
                logger.warn("该账号不存在");
                IntentionDto dto = new IntentionDto();
                dto.setStu_id(telephone);
                dto.setMemo("该账号不存在");
                res.add(dto);
            }
        } else {//手机号为空
            logger.warn("请输入手机号");
            IntentionDto dto = new IntentionDto();
            dto.setStu_id(telephone);
            dto.setMemo("请输入手机号");
            res.add(dto);
        }

        return res;
    }

    @Override
    public List<IntentionDto> editIntention(EditIntentionDto editIntentionDto) throws ParseException {
        List<IntentionDto> res = new ArrayList<>();

        if (editIntentionDto != null) {
            //获取数据
            String stu_id = editIntentionDto.getTelephone();
            String[] input = editIntentionDto.getIntentions();
            List<String> intentions = new ArrayList<>();
            for (int i = 0; i < input.length; i++) {//String数组转List
                intentions.add(input[i]);
            }

            //根据手机号查找学生用户
            Student student = stuInfoRepository.findStudentByTelephone(stu_id);
            if (student != null) {//存在学生
                if (intentions != null && intentions.size() > 0) {//输入的不为空
                    //获取该用户的所有意向兼职
                    List<IntentionDto> all = getIntention(stu_id);
                    //存放原来选择的意向兼职
                    List<String> before = new ArrayList<>();
                    //编辑之前有意向兼职
                    if (all != null && all.size() > 0) {
                        //从DB中删除该学生所有的
                        intentionRepository.removeAllIntention(stu_id);
//                        for (IntentionDto dto : all) {
//                            String str = dto.getContent();
//                            before.add(str);
//                            intentionRepository.removeOneIntention(stu_id, str);
//                        }
//                        System.out.println("删除的意向兼职:" + before.toString());//test

//                        //获取两个List的差集
//                        List<String> reduce = intentions.stream().filter(item ->
//                                !before.contains(item)).collect(toList());

                        //DB插入元素
                        for (int i = 0; i < intentions.size(); i++) {
                            intentionRepository.addIntention(stu_id, intentions.get(i));
                        }
                    } else {
                        //之前没兼职
                        for (int i = 0; i < intentions.size(); i++) {
                            intentionRepository.addIntention(stu_id, intentions.get(i));
                        }
                    }
                } else {
                    //没选择兼职，就不动
                }

                //获取所有意向兼职
                List<Intention> list = intentionRepository.getIntentionsByStuId(stu_id);

                if (list != null && list.size() > 0) {
                    //有意向兼职
                    for (Intention i : list) {
                        IntentionDto dto = new IntentionDto();
                        dto.setI_id(i.getId());
                        dto.setContent(i.getContent());
                        dto.setStu_id(i.getStu().getId());
                        dto.setMemo("存在意向兼职");
                        res.add(dto);
                    }
                }
            } else {
                logger.warn("不存在该用户");
                IntentionDto dto = new IntentionDto();
                dto.setMemo("不存在该用户");
                res.add(dto);
            }
        } else {
            logger.warn("参数传入错误");
            IntentionDto dto = new IntentionDto();
            dto.setMemo("参数传入错误");
            res.add(dto);
        }
        return res;
    }

    //TODO 个人信息编辑-兼职发布者/管理员
    @Override
    public EmpInfoDto editEmpInfo(EditEmpInfoDto editEmpInfoDto) throws ParttimeServiceException, ParseException {
        EmpInfoDto res = new EmpInfoDto();

        if (editEmpInfoDto != null) {
            //有输入
            String telephone = editEmpInfoDto.getTelephone();
            int gender = editEmpInfoDto.getGender();
            int age = editEmpInfoDto.getAge();
            String birth_year = editEmpInfoDto.getBirth_year();
            String birth_month = editEmpInfoDto.getBirth_month();
            String emails = editEmpInfoDto.getEmails();
            String unit_descriptions = editEmpInfoDto.getUnit_descriptions();
            String unit_loc = editEmpInfoDto.getUnit_loc();

            //查找该用户是否存在
            Employer emp = empInfoRepository.findEmployerByTelephone(telephone);
            if (emp != null) {//存在
                //修改用户信息
                empInfoRepository.editEmpInfo(gender, age, emails, telephone);
                unitRepository.editEmpUnitInfo(unit_descriptions, unit_loc, emp.getU().getId());

                //将出生年份、月份更新到缓存
                UserBirthDto userBirthDto = new UserBirthDto();
                userBirthDto.setTelephone(telephone);
                userBirthDto.setBirth_year(birth_year);
                userBirthDto.setBirth_month(birth_month);
                redisTemplate.opsForValue().set(UserBirthDto.cacheKey(telephone), JSON.toJSONString(userBirthDto));
                logger.warn("存储该兼职发布者的出生年月[{}]", UserBirthDto.cacheKey(telephone));

                //获取更新后的用户信息
                Employer emp_new = empInfoRepository.findEmployerByTelephone(telephone);

                //填充返回的dto
                res.setEmp_name(emp_new.getEmpName());
                res.setTelephone(emp_new.getId());
                res.setAge(emp_new.getAge());
                res.setGender(emp_new.getGender());
                res.setEmails(emp_new.getEmails());
                res.setJno(emp_new.getJno());
                res.setUnit_name(emp_new.getU().getUnitName());
                res.setUnit_descriptions(emp_new.getU().getDescriptions());
                res.setUnit_loc(emp_new.getU().getLoc());
                res.setJob_nums(emp_new.getU().getJobNums());
                res.setHead(emp_new.getHead());
                res.setMemo("修改个人信息成功");
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        } else {
            logger.warn("请检查输入的信息是否完整");
            res.setMemo("请检查输入的信息是否完整");
        }
        return res;
    }

    @Override
    public EmpInfoDto modifyEmpPwd(ModifyPwdDto modifyPwdDto) throws ParttimeServiceException {
        EmpInfoDto res = new EmpInfoDto();

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
            Employer emp = empInfoRepository.findEmployerByTelephone(telephone);
            if (emp != null) {//存在
                //判断输入的旧密码和真正的旧密码是否相同
                old_pwd = SaSecureUtil.md5BySalt(old_pwd, "emp");//md5加盐加密后的密码
                if (emp.getPwd().equals(old_pwd)) {//相同
                    //更新DB
                    String md5pwd = SaSecureUtil.md5BySalt(new_pwd, "emp");//md5加盐加密后的密码
                    empInfoRepository.modifyEmpPwd(md5pwd, telephone);

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
        } else {
            logger.warn("请检查输入的信息是否完整");
            res.setTelephone("0");
            res.setMemo("请检查输入的信息是否完整");
        }
        return res;
    }

    /* ↓下面是桶的操作↓ */
    @Override
    public Boolean createBucket(String bucketName) throws ParttimeServiceException, Exception {
        return minIO.createBucket(bucketName);
    }

    @Override
    public Boolean deleteBucket(String bucketName) throws ParttimeServiceException, Exception {
        return minIO.deleteBucket(bucketName);
    }

    @Override
    public String upload(MultipartFile file) throws ParttimeServiceException, Exception {
        return minIO.uploadFile(file, "parttime");//上传图片文件
    }

    @Override
    public String download(String fileName, HttpServletResponse res) throws ParttimeServiceException, Exception {
        if (minIO.downloadFile("parttime", fileName, res) != null) return "下载成功";
        else return "下载失败";
    }

    @Override
    public String deleteFile(String fileName) throws ParttimeServiceException, Exception {
        return minIO.deleteFile("parttime", fileName);
    }

    //判断是否是日期格式
    public boolean isDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会较宽松地验证日期。如2007/02/29会被接受并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    //根据文件后缀是否是照片类型的文件
    public boolean isPicture(String imgName) {
        boolean flag = false;
        if (StringUtils.isBlank(imgName)) {
            return false;
        }
        String[] arr = {"jpeg", "jpg", "png"};//可识别的图片后缀
        for (String item : arr) {
            if (item.equals(imgName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

}
