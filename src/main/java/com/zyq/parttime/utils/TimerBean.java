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

    /*每5s执行一次定时任务*/
    @Scheduled(cron = "0/5 * * * * ?")
    public void task() throws ParseException {
        //1.获取所有兼职
        List<Parttimes> allParttimes = positionRepository.getAllPositions();

        //2.有数据就判断，否则不做任何事
        if (allParttimes.size() > 0) {

            //3.遍历每个报名
            for (Parttimes p : allParttimes) {

                //4.判断兼职是否超过报名DDL
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = sdf.parse(sdf.format(new Date()));
                if (now.compareTo(p.getSignupDdl()) >= 0 && p.getPositionStatus().equals("已发布")) {
                    //4-1.已经过了报名DDL，且状态是“已发布”，就把兼职状态改为“已结束”
                    positionRepository.updatePositionStatus("已结束", now, p.getId());

                } else if (now.compareTo(p.getSignupDdl()) < 0) {
                    //4-2.还没过DDL，可以继续报名，继续后面的判断

                    //5.获取录取数、名额数
                    int n2 = signupRepository.getNumOfSpecialStatus(p.getId(), "已录取");
                    int n3 = signupRepository.getNumOfSpecialStatus(p.getId(), "已结束");
                    int num_employment = n2 + n3;
                    int num = positionRepository.getNumOfPosition(p.getId());//名额数

                    //6.兼职是已发布状态才进入该if语句
                    if (num_employment == num && p.getPositionStatus().equals("已发布")) {
                        //6-1.招满了
                        System.out.println("有兼职招满啦~开始下架");
                        positionRepository.updatePositionStatus("已招满", now, p.getId());
                    } else {
                        //6-2.没招满
                        System.out.println("例行检查ing...");
                    }
                }
            }
        } else {
            //没数据，不做任何事
            System.out.println("例行检查ing...");
        }
    }
}