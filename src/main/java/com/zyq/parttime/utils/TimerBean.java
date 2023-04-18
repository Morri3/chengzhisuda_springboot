package com.zyq.parttime.utils;

import com.zyq.parttime.entity.Parttimes;
import com.zyq.parttime.repository.position.PositionRepository;
import com.zyq.parttime.repository.position.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class TimerBean {
    @Autowired
    private SignupRepository signupRepository;
    @Autowired
    private PositionRepository positionRepository;

    /*每5s执行一次*/
    @Scheduled(cron = "0/5 * * * * ?")
    public void task() throws ParseException {
        //获取所有兼职
        List<Parttimes> allParttimes = positionRepository.getAllPositions();

        //有数据就判断，否则不做任何事
        if (allParttimes.size() > 0) {
            System.out.println("正在进行定时任务...");

            //遍历每个报名
            for (Parttimes p : allParttimes) {
                int num_employment = signupRepository.getNumOfEmployment(p.getId());//根据p_id找已录取数
                int num = positionRepository.getNumOfPosition(p.getId());//名额数

                //已发布状态再进入该if语句
                if (num_employment == num && p.getPositionStatus().equals("已发布")) {
                    System.out.println("有兼职招满啦~开始下架");
                    //满了
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now=sdf.parse(sdf.format(new Date()));
                    positionRepository.updatePositionStatus("已招满", now,p.getId());
                } else {
                    System.out.println("例行检查ing...");
                }
            }
        } else {
            System.out.println("例行检查ing...");
        }
    }

}