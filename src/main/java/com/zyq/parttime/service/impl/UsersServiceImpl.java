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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
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
    private ResumesDetailRepository resumesDetailRepository;
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
                    for (Resumedetail item : campusExpList) {
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
            } else {//不存在简历
                logger.warn("获取简历失败");
                res.setTelephone(telephone);
                res.setMemo("请填写简历");
            }
        }
        return res;
    }

    @Override
    public ResumeUploadCallbackDto uploadResume(MultipartFile file, String telephone, Date upload_time)
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
                //填充照片url
                res.setPic_url(pic_url);

                //填充上传时间
                res.setUpload_time(upload_time);

                //图片url存入DB，即创建resume记录
                Date now = new Date(System.currentTimeMillis());//当前时间
                resumesInfoRepository.createAResumeRecord(telephone, pic_url, now);
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                        Date start_time = sdf.parse(start);//用于存到DB
                        Date end_time = sdf.parse(end);//用于存到DB

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
                                content, "教育背景", now2);
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
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                            Date start_time = sdf.parse(start);//用于存到DB
                            Date end_time = sdf.parse(end);//用于存到DB

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
                                    content, "项目经历", now2);
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

                        //项目内容【1~N行】
                        String content = "";
                        //只要不是校园经历字样content
                        while (!(words.get(k)).get("words").equals("校园经历")) {
                            content += (words.get(k).get("words")).toString() + "。";
                            k++;
                        }

                        //创建一个detail记录
                        Date now2 = new Date(System.currentTimeMillis());
                        resumesDetailRepository.addAProfessionalResumesDetailRecord(r_id, content, "专业技能", now2);
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
                        list.add(dto);//加到列表中
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
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
                            Date start_time = sdf.parse(start);//用于存到DB
                            Date end_time = sdf.parse(end);//用于存到DB

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
                                    content, "校园经历", now2);
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
                    }
                }
            } else {//不存在学生
                logger.warn("该账号不存在");
                res.setTelephone(telephone);
                res.setMemo("该账号不存在");
            }
        }
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
                        resumesInfoRepository.updateResumesInfo(telephone, exp, current_area);

                        //找到简历的详细信息
                        GetResumeDto request = new GetResumeDto();
                        request.setTelephone(telephone);
                        ResumeInfoDto dto = this.getResume(request);//返回的详细信息dto

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
