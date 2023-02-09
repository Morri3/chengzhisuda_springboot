package com.zyq.parttime.service.impl;

import com.zyq.parttime.entity.Student;
import com.zyq.parttime.exception.ParttimeServiceException;
import com.zyq.parttime.form.LoginDto;
import com.zyq.parttime.form.StuInfoDto;
import com.zyq.parttime.repository.LogAndRegRepository;
import com.zyq.parttime.service.LogAndRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogAndRegServiceImpl implements LogAndRegService {
    private final Logger logger = LoggerFactory.getLogger(LogAndRegServiceImpl.class);

    @Autowired
    private LogAndRegRepository logAndRegRepository;

    @Override
    public StuInfoDto loginStu(LoginDto loginDto) throws ParttimeServiceException{
        StuInfoDto res=new StuInfoDto();//存结果

        if(loginDto!=null){
            String telephone=loginDto.getTelephone();
            String pwd=loginDto.getPwd();

            //查看是否存在该用户
            Student stu=logAndRegRepository.findStudentByTelephone(telephone);
            if(stu!=null){//存在学生


            }else{//不存在该学生
                logger.warn("不存在该学生");

            }
        }
        return res;
    }

}
