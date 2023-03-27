package com.zyq.parttime.repository.mark;

import com.zyq.parttime.entity.Mark;
import com.zyq.parttime.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Integer>, JpaSpecificationExecutor<Mark> {
    @Query(value = "select * from mark where s_id=?1", nativeQuery = true)
    Mark getMark(int s_id);

//    @Transactional
//    @Modifying
//    @Query(value = "insert into resumes(stu_id, pic_url,upload_time,create_time,r_status) values(?1,?2,?3,?4,?5)", nativeQuery = true)
//    void createAResumeRecord(String stu_id, String pic_url, Date upload_time, Date create_time, String r_status);
}
