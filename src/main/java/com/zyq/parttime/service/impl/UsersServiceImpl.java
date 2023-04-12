package com.zyq.parttime.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baidu.aip.ocr.AipOcr;
import com.zyq.parttime.entity.*;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.intention.EditIntentionDto;
import com.zyq.parttime.form.intention.IntentionDto;
import com.zyq.parttime.form.resumemanage.*;
import com.zyq.parttime.form.userinfomanage.*;
import com.zyq.parttime.minio.MinIO;
import com.zyq.parttime.repository.intention.IntentionRepository;
import com.zyq.parttime.repository.resumemanage.ResumesDetailRepository;
import com.zyq.parttime.repository.resumemanage.ResumesInfoRepository;
import com.zyq.parttime.repository.userinfomanage.EmpInfoRepository;
import com.zyq.parttime.repository.userinfomanage.StuInfoRepository;
import com.zyq.parttime.service.UsersService;
import com.zyq.parttime.utils.Constant;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toList;

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
    private MinIO minIO;

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public StuInfoDto getStuInfo(String telephone) throws ParttimeServiceException {
        StuInfoDto res = new StuInfoDto();

        if (telephone != null) {
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
                Date entrance_date = stu.getEntranceDate();
                Date graduation_date = stu.getGraduationDate();
                String head = stu.getHead();

                res.setStu_name(stu_name);
                res.setGender(gender);
                res.setTelephone(telephone);
                res.setEmails(emails);
                res.setAge(age);
                res.setSchool_name(school_name);
                res.setSno(sno);
                res.setEntrance_date(entrance_date);
                res.setGraduation_date(graduation_date);
                res.setGrade(grade);
                res.setHead(head);
            } else {//不存在
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }
        return res;
    }

    @Override
    public EmpInfoDto getEmpInfo(String telephone) throws ParttimeServiceException {
        EmpInfoDto res = new EmpInfoDto();

        if (telephone != null) {
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
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
            Date entrance = sdf2.parse(entrance_date);
            Date graduation = sdf2.parse(graduation_date);

            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(entrance);
            c2.setTime(graduation);
            int plus = c2.get(Calendar.DATE) - c1.get(Calendar.DATE);
            int res2 = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
            int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
            if (res2 > 0) {
                res2 = 1;
            } else if (res2 == 0) {
                if (plus <= 0) res2 = 0;
                else res2 = 1;
            } else {
                res2 = 0;
            }
            int diff = year + res2;

            //把int转为字符串类型
            String grade = "";
            if (diff == 1) grade = "大一";
            else if (diff == 2) grade = "大二";
            else if (diff == 3) grade = "大三";
            else if (diff == 4) grade = "大四";
            else if (diff == 5) grade = "大五（五年制）";
            else if (diff > 5) grade = null;
            System.out.println("年级:" + grade);

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

    @Override
    public ResumeInfoDto getResume(String telephone) throws ParttimeServiceException {
        ResumeInfoDto res = new ResumeInfoDto();

        if (telephone != null) {
//            //获取学生账号
//            String telephone = getResumeDto.getTelephone();

            //根据学生账号查找该学生的resumes
            Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
            if (resumes != null) {//存在简历
                //填充简历的基本信息
                res.setTelephone(telephone);
                res.setCurrent_area(resumes.getCurrentArea());
                res.setExp(resumes.getExp());
                res.setUpload_time(resumes.getUploadTime());

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

                //状态
                res.setStatus(resumes.getrStatus());
                res.setMemo("存在简历");
            } else {//不存在简历
                logger.warn("获取简历失败");
                res.setTelephone(telephone);
                res.setMemo("请填写简历");
            }
        }
        return res;
    }

    @Override
    public ResumeInfoDto createResume(String telephone, String upload_time) throws ParttimeServiceException, Exception {
        ResumeInfoDto res = new ResumeInfoDto();

        //有输入
        if (telephone != null) {
            //根据手机号查找学生用户
            Student student = stuInfoRepository.findStudentByTelephone(telephone);

            //存在学生
            if (student != null) {
                //判断是否存在简历，不存在就创建
                Resumes resumes = resumesInfoRepository.findResumesByStuId(student.getId());
                if (resumes == null) {//不存在，创建
                    //String转Date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date time = new Date();
                    try {
                        time = sdf.parse(upload_time);
                        resumesInfoRepository.createAResumeRecord(student.getId(), null,
                                null, time, "已创建");
                        //找到该用户的简历
                        resumes = resumesInfoRepository.findResumesByStuId(student.getId());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println(resumes.toString());//test

                //填充简历的基本信息
                res.setTelephone(telephone);
                res.setCurrent_area(resumes.getCurrentArea());
                res.setExp(resumes.getExp());
                res.setUpload_time(resumes.getUploadTime());

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

                //状态
                res.setStatus(resumes.getrStatus());
                res.setMemo("存在简历");
            } else {
                logger.warn("不存在该学生");
                res.setMemo("不存在该学生");
            }
        }
        return res;
    }

    @Override
    public String uploadResumeWithStuInfo(String telephone, String upload_time) throws ParttimeServiceException, Exception {
        String res = "";

        if (telephone != null) {
            //根据手机号查找学生用户
            Student student = stuInfoRepository.findStudentByTelephone(telephone);

            if (student != null) {//存在学生
                //判断是否存在简历，不存在就创建
                Resumes resumes = resumesInfoRepository.findResumesByStuId(student.getId());
                if (resumes == null) {//不存在，创建
                    //String转Date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date time = new Date();
                    try {
                        time = sdf.parse(upload_time);
                        resumesInfoRepository.createAResumeRecord(student.getId(), null,
                                null, time, "已创建");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
//                else {//存在
//                    //时间Date转String
//                    Date date = new Date();
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String dateStr = sdf.format(date);

                //将学号存入redis缓存，这个缓存永远只有一条记录
                redisTemplate.opsForValue().set(ResumeStuIdDto.cacheKey(1), telephone);
                logger.warn("存储该学生的学号[{}]", ResumeStuIdDto.cacheKey(1));

                //将学号+上传日期存入redis缓存，会有多条记录
                redisTemplate.opsForValue().set(ResumeCacheDto.cacheKey(telephone), upload_time);
                logger.warn("存储该学生简历的信息[{}]", ResumeCacheDto.cacheKey(telephone));
//                }
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

    @Override
    public ResumeUploadCallbackDto uploadResume(MultipartFile file)
            throws ParttimeServiceException, Exception {
        ResumeUploadCallbackDto res = new ResumeUploadCallbackDto();

//        if (uploadInputDto != null) {
//            File file = uploadInputDto.getFile();
//            String telephone = uploadInputDto.getTelephone();
//            String upload_time = uploadInputDto.getUpload_time();

        //File转MultipartFile
//        InputStream inputStream = new FileInputStream(file);
//        MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
//            MultipartFile multipartFile = FileUtils.fileToMultipartFile(file);

        //先获取当前的用户的学号
        String telephone = (String) redisTemplate.opsForValue().get(ResumeStuIdDto.cacheKey(1));
        //再通过学号来找他的upload_time
        String dateStr = (String) redisTemplate.opsForValue().get(ResumeCacheDto.cacheKey(telephone));
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date upload_time = new Date();
        try {
            upload_time = sdf3.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("telephone:" + telephone);//test
        System.out.println("upload_time:" + upload_time);//test

        if (ObjectUtils.isEmpty(file) || file.getSize() <= 0) {
            res.setTelephone(telephone);
            res.setMemo("上传文件大小为空");
            return res;
        }

        if (telephone != null && !telephone.equals("")) {//手机号不为空
            //根据手机号查找学生用户
            Student student = stuInfoRepository.findStudentByTelephone(telephone);

            if (student != null) {//存在学生
                //把图片存入minio，并返回图片的url
                String pic_url = this.upload(file);
                //填充照片url
                res.setPic_url(pic_url);

                //填充上传时间
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date uploadTime = sdf.parse(upload_time);
//                res.setUpload_time(uploadTime);
                res.setUpload_time(upload_time);

                //图片url存入DB，即创建resume记录
//                Date now = new Date(System.currentTimeMillis());//当前时间
                resumesInfoRepository.modifyResumeRecord(pic_url, upload_time, "已上传", telephone);
//                resumesInfoRepository.createAResumeRecord(telephone, pic_url, upload_time, now, "已上传");

                //获取刚刚创建的resume的id
                int r_id = resumesInfoRepository.findLatestResumes();

                //调用api解析图片中的文字
                AipOcr client = new AipOcr(Constant.APP_ID, Constant.API_KEY, Constant.SECRET_KEY);//获取百度云OCR的客户端
                HashMap<String, String> options = new HashMap<>(4);//设置选项
                options.put("language_type", "CHN_ENG");//语言类型：中文+英语
                options.put("detect_direction", "true");
                options.put("detect_language", "true");
                options.put("probability", "true");

                // 参数为二进制数组
                byte[] buf = new byte[0];
                try {
                    buf = file.getBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    res.setMemo("获取文件字节数据异常" + e.getMessage());
                }
                JSONObject result = client.basicGeneral(buf, options);
//                String jsonData = "";
//                try {
//                    jsonData = result.toString(2);//最终的json字符串结果
//                } catch (JSONException e) {
//                    res.setMemo("获取json数据异常" + e.getMessage());
//                }

                //解析文字的处理
                ArrayList<JSONObject> words = new ArrayList<>();//存放识别出的文字的列表
                JSONArray arr = (JSONArray) result.get("words_result");//获取识别结果JSON数组
                //遍历JSON数组
                for (int i = 0; i < arr.length(); i++) {
                    words.add((JSONObject) arr.get(i));//添加到words中
                }

                //个人信息部分
//                String stu_name = ((words.get(0).get("words")).toString().split("："))[1];//姓名
//                String birth = ((words.get(1).get("words")).toString().split("："))[1];//出生年月
//                String emails = ((words.get(2).get("words")).toString().split("："))[1];//邮箱
                String phone = ((words.get(3).get("words")).toString().split("："))[1];//联系方式
                String current_area = ((words.get(4).get("words")).toString().split("："))[1];//现居地
                String exp = ((words.get(5).get("words")).toString().split("："))[1];//工作经验

                //把现居地、工作经验填充到res
                res.setTelephone(phone);
                res.setCurrent_area(current_area);
                res.setExp(exp);

                //求职意向
                String intended = "";//求职意向岗位
                if (words.get(6).get("words").equals("求职意向")) {
                    intended = ((words.get(7).get("words")).toString().split("："))[1];
                }
                //把求职意向填充到res
                res.setIntended(intended);

                //其余部分
                for (int i = 8; i < words.size(); i++) {//遍历列表
                    //教育背景【以本科生为例，设定为只有本科一段教育经历】
                    ResumeDetailDto dto1 = new ResumeDetailDto();
                    if (words.get(i).get("words").equals("教育背景")) {
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
                        dto1.setR_id(r_id);
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

                    //项目经历【1~N个】
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
                            System.out.println(((words.get(j).get("words").toString())));//test
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
                            dto.setR_id(r_id);
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

                    //专业技能【1~N个】
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
                            dto.setR_id(r_id);
                            dto.setContent(content);
                            dto.setCategory("专业技能");
                            dto.setHasContent(1);
                            dto.setStatus("已上传");
                            list.add(dto);//加到列表中
                        }
                        res.setProfessionalSkillList(list);//set到res中
                    }

                    //校园经历【1~N个，多为1个】
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

                            //项目内容【1~N行】
                            int j = k + 2;
                            String content = "";
                            System.out.println(((words.get(j).get("words").toString())));//test
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
                            dto.setR_id(r_id);
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
                    }
                }
            } else {//不存在学生
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }
//        }
        return res;
    }

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
                    Resumedetail resumedetail = resumesDetailRepository.findResumeDetailByRIdAndCategory(resumes.getId(), "校园经历");

                    if (resumes != null && resumedetail != null && resumedetail.getId() == rd_id) {//存在简历和校园经历
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
                    } else {//不存在简历/教育背景
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

    @Override
    public DeleteDetailCallbackDto deleteDetail(DeleteDetailDto deleteDetailDto) throws ParseException, Exception {
        DeleteDetailCallbackDto res = new DeleteDetailCallbackDto();

        if (deleteDetailDto != null) {
            String telephone = deleteDetailDto.getTelephone();//手机号
            int rd_id = deleteDetailDto.getRd_id();
            System.out.println("要删除的简历详情id是：" + rd_id);

            if (telephone != null && !telephone.equals("")) {//手机号不为空
                //根据手机号查找学生用户
                Student student = stuInfoRepository.findStudentByTelephone(telephone);

                if (student != null) {//存在学生
                    //根据手机号找到该学生的简历、项目经历
                    Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
                    Resumedetail resumedetail = null;
                    if (resumes != null)
                        resumedetail = resumesDetailRepository.findResumeDetailByRdId(rd_id, resumes.getId());

                    if (resumes != null && resumedetail != null) {//存在简历和项目经历
                        if (resumedetail.getId() == rd_id && !resumedetail.getRdStatus().equals("已删除")) {//当前的rd是该学生的简历详情id，且不是已删除的简历详情
                            //更新DB
                            resumesDetailRepository.deleteResumedetailByRdId(rd_id);

                            //填充到res
                            res.setTelephone(telephone);
                            res.setMemo("删除成功！");
                        } else {//该简历详情不是该学生的
                            logger.warn("该简历详情不是该学生的");
                            res.setTelephone(telephone);
                            res.setMemo("该简历详情不是该学生的");
                        }
                    } else {//不存在简历/简历详情
                        logger.warn("该账号不存在简历信息或简历详情");
                        res.setTelephone(telephone);
                        res.setMemo("该账号不存在简历信息或简历详情");
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
        return minIO.uploadFile(file, "parttime");
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
}
