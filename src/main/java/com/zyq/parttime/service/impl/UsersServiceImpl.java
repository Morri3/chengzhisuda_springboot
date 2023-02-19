package com.zyq.parttime.service.impl;

import com.alibaba.fastjson.JSONException;
import cn.dev33.satoken.secure.SaSecureUtil;
import com.baidu.aip.ocr.AipOcr;
import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Resumedetail;
import com.zyq.parttime.entity.Resumes;
import com.zyq.parttime.entity.Student;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.resumemanage.*;
import com.zyq.parttime.form.userinfomanage.*;
import com.zyq.parttime.minio.MinIO;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UsersServiceImpl implements UsersService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private StuInfoRepository stuInfoRepository;
    @Autowired
    private EmpInfoRepository empInfoRepository;
    @Autowired
    private ResumesInfoRepository resumesInfoRepository;
    @Autowired
    private MinIO minIO;

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.bucket}")
    private String bucket;

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

    @Override
    public ResumeInfoDto getResume(GetResumeDto getResumeDto) throws ParttimeServiceException {
        ResumeInfoDto res = new ResumeInfoDto();

        if (getResumeDto != null) {
            //获取学生账号
            String telephone = getResumeDto.getTelephone();

            //根据学生账号查找该学生的resumes
            Resumes resumes = resumesInfoRepository.findResumesByStuId(telephone);
            if (resumes != null) {//存在简历
                //填充简历的基本信息
                res.setTelephone(telephone);
                res.setCurrent_area(resumes.getCurrentArea());
                res.setExp(resumes.getExp());
                res.setUpload_time(resumes.getUploadTime());

                //根据r_id查找四个子类的内容
                List<Resumedetail> campusExpList = resumesInfoRepository.findResumeDetailListByRId(resumes.getId(), "校园经历");
                List<Resumedetail> educationBgList = resumesInfoRepository.findResumeDetailListByRId(resumes.getId(), "教育背景");
                List<Resumedetail> projectExpList = resumesInfoRepository.findResumeDetailListByRId(resumes.getId(), "项目经历");
                List<Resumedetail> professionalSkillList = resumesInfoRepository.findResumeDetailListByRId(resumes.getId(), "专业技能");

                // 遍历四个子类
                //1.遍历校园经历
                List<CampusExpDto> list1 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : campusExpList) {
                        CampusExpDto dto = new CampusExpDto();
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("校园经历");
                        dto.setHasContent(1);//有内容
                        list1.add(dto);
                    }
                } else {//无内容
                    CampusExpDto dto = new CampusExpDto();
                    dto.setR_id(resumes.getId());
                    dto.setCategory("校园经历");
                    dto.setHasContent(0);//无内容
                    list1.add(dto);
                }
                //2.遍历教育背景
                List<EducationBgDto> list2 = new ArrayList<>();
                if (educationBgList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : campusExpList) {
                        EducationBgDto dto = new EducationBgDto();
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("教育背景");
                        dto.setHasContent(1);//有内容
                        list2.add(dto);
                    }
                } else {//无内容
                    EducationBgDto dto = new EducationBgDto();
                    dto.setR_id(resumes.getId());
                    dto.setCategory("教育背景");
                    dto.setHasContent(0);//无内容
                    list2.add(dto);
                }
                //3.遍历项目经历
                List<ProjectExpDto> list3 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : projectExpList) {
                        ProjectExpDto dto = new ProjectExpDto();
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("项目经历");
                        dto.setHasContent(1);//有内容
                        list3.add(dto);
                    }
                } else {//无内容
                    ProjectExpDto dto = new ProjectExpDto();
                    dto.setR_id(resumes.getId());
                    dto.setCategory("项目经历");
                    dto.setHasContent(0);//无内容
                    list3.add(dto);
                }
                //4.遍历专业技能
                List<ProfessionalSkillDto> list4 = new ArrayList<>();
                if (campusExpList.size() > 0) {//有内容
                    //遍历列表
                    for (Resumedetail item : professionalSkillList) {
                        ProfessionalSkillDto dto = new ProfessionalSkillDto();
                        dto.setR_id(item.getR().getId());
                        dto.setTitle(item.getTitle());
                        dto.setContent(item.getContent());
                        dto.setCategory("专业技能");
                        dto.setHasContent(1);//有内容
                        list4.add(dto);
                    }
                } else {//无内容
                    ProfessionalSkillDto dto = new ProfessionalSkillDto();
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
            } else {//不存在简历
                logger.warn("获取简历失败");
                res.setTelephone(telephone);
                res.setMemo("请填写简历");
            }
        }
        return res;
    }

    @Override
    public ResumeUploadCallbackDto uploadResume(MultipartFile file, String telephone)
            throws ParttimeServiceException, Exception {
        ResumeUploadCallbackDto res = new ResumeUploadCallbackDto();

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
                String encode = URLEncoder.encode(pic_url, "UTF-8");
                System.out.println("pic_url: " + pic_url);
                System.out.println("encode: " + encode);

                //图片url存入DB，即创建resume记录
                resumesInfoRepository.createAResumeRecord(telephone, pic_url);
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
                String stu_name = ((words.get(0).get("words")).toString().split("："))[1];//姓名
                String birth = ((words.get(1).get("words")).toString().split("："))[1];//出生年月
                String emails = ((words.get(2).get("words")).toString().split("："))[1];//邮箱
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

                //用于存到DB的字段
                Date start_time = new Date();
                Date end_time = new Date();

                //其余部分
                for (int i = 8; i < words.size(); i++) {//遍历列表
                    //教育背景
                    EducationBgDto dto1 = new EducationBgDto();
                    if (words.get(i).get("words").equals("教育背景")) {
                        String time = ((words.get(i + 1).get("words")).toString().split("："))[1];
                        String start = (time.split("-"))[0];
                        String end = (time.split("-"))[1];
                        //String转Date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                        start_time = sdf.parse(start);//用于存到DB
                        end_time = sdf.parse(end);//用于存到DB
                        //标题
                        String title = ((words.get(i + 2).get("words")).toString().split("："))[1];
                        //内容
                        int j = i + 3;
                        String content = "";
                        while (!(words.get(j).get("words")).equals("项目经历")) {
                            content += ((words.get(j).get("words")).toString().split("："))[1];
                        }
                        //把信息填充到dto中
                        dto1.setR_id(r_id);
                        dto1.setTitle(title);
                        dto1.setContent(content);
                        dto1.setTime(time);
                        dto1.setCategory("教育背景");
                    }
                    //项目经历
                    if (words.get(i).get("words").equals("项目经历")) {

                    }
                    //专业技能
                    if (words.get(i).get("words").equals("专业技能")) {

                    }
                    //教育背景
                    if (words.get(i).get("words").equals("教育背景")) {

                    }
                    //校园经历
                    if (words.get(i).get("words").equals("校园经历")) {

                    }
                }
//                System.out.println(stu_name);
//                System.out.println(birth);
//                System.out.println(emails);
//                System.out.println(phone);
//                System.out.println(current_area);
//                System.out.println(intended);

                //把解析出来的文字存入DB

                //把文字内容填充到res中

            } else {//不存在学生
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }
        return res;
    }

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
}
