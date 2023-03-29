package com.zyq.parttime.repository.mark;

import com.zyq.parttime.entity.Mark;
import com.zyq.parttime.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Integer>, JpaSpecificationExecutor<Mark> {
    @Query(value = "select * from mark where s_id=?1", nativeQuery = true)
    Mark getMark(int s_id);

    @Query(value = "select * from mark order by create_time limit 0,1", nativeQuery = true)
    Mark getLatestMark();

    @Transactional
    @Modifying
    @Query(value = "insert into mark(s_id,total_score,pf,pl,we,lt,pt,ods,dsps,create_time) " +
            "values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10)", nativeQuery = true)
    void post(int s_id, float total_score, int pf, int pl, int we, int lt, int pt, int ods, int dsps,
              Date create_time);

}
