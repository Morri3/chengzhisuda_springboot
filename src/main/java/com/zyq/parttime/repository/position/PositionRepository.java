package com.zyq.parttime.repository.position;

import com.zyq.parttime.entity.Parttimes;
import com.zyq.parttime.entity.Signup;
import com.zyq.parttime.form.analyze.AnalyzePublishDto;
import com.zyq.parttime.form.analyze.GetAnalyzePublishDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PositionRepository extends JpaRepository<Parttimes, Integer>, JpaSpecificationExecutor<Parttimes> {
    @Query(value = "select * from parttimes", nativeQuery = true)
    List<Parttimes> getAllPositions();

    @Query(value = "select * from parttimes where op_id=?1", nativeQuery = true)
    List<Parttimes> getAllPositionsByEmpId(String op_id);

    @Query(value = "select * from parttimes where category=?1", nativeQuery = true)
    List<Parttimes> getAllPositionsByCategory(String category);

    @Query(value = "select * from parttimes where category=?1", nativeQuery = true)
    List<Parttimes> getPositionByIntention(String intention);

    @Query(value = "select * from parttimes where p_id=?1", nativeQuery = true)
    Parttimes getPosition(int p_id);

    //获取兼职的名额数字段的值
    @Query(value = "select num from parttimes where p_id=?1", nativeQuery = true)
    int getNumOfPosition(int p_id);

    @Transactional
    @Modifying
    @Query(value = "update parttimes set position_status=?1,update_time=?2 where p_id=?3", nativeQuery = true)
    void updatePositionStatus(String position_status, Date update_time, int p_id);

    @Transactional
    @Modifying
    @Query(value = "insert into parttimes(op_id, position_name, num, category, salary, area, exp, content, " +
            "requirement, signup_ddl, slogan, work_time, settlement, position_status, create_time, " +
            "update_time) values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,?13,?14,?15,?16)", nativeQuery = true)
    void publishAParttime(String op_id, String position_name, int num, String category, String salary,
                          String area, String exp, String content, String requirement, Date signup_ddl,
                          String slogan, String work_time, String settlement, String position_status,
                          Date create_time, Date update_time);

    @Transactional
    @Modifying
    @Query(value = "update parttimes set num=?1,category=?2,salary=?3,area=?4,exp=?5,content=?6, " +
            "requirement=?7,signup_ddl=?8,slogan=?9,work_time=?10,settlement=?11,position_status=?12,update_time=?13 " +
            "where p_id=?14", nativeQuery = true)
    void editAParttime(int num, String category, String salary, String area, String exp, String content,
                       String requirement, Date signup_ddl, String slogan, String work_time, String settlement,
                       String position_status, Date update_time, int p_id);

    @Query(value = "select * from parttimes order by create_time limit 0,1", nativeQuery = true)
    Parttimes getLatestPosition();

    @Query(value = "select * from parttimes where op_id=?1 and p_id=?2", nativeQuery = true)
    Parttimes checkIsTheManager(String op_id, int p_id);

    @Query(value = "select * from parttimes where op_id=?1", nativeQuery = true)
    List<Parttimes> getAllPositionManagedByEmp(String op_id);

    @Query(value = "select create_time,count(*) as num from parttimes group by create_time order by create_time", nativeQuery = true)
    List<Map<String, Object>> getNumOfDailyPublish();
}
