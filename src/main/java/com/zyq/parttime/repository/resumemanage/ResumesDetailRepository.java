package com.zyq.parttime.repository.resumemanage;

import com.zyq.parttime.entity.Resumedetail;
import com.zyq.parttime.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

public interface ResumesDetailRepository extends JpaRepository<Resumedetail, Integer>, JpaSpecificationExecutor<Resumedetail> {
    @Query(value = "select rd_id from resumedetail order by create_time desc limit 0,1", nativeQuery = true)
    int findLatestResumesDetail();

    @Transactional
    @Modifying
    @Query(value = "insert into resumedetail(r_id, start_time, end_time, title, content, category, create_time) " +
            "values(?1,?2,?3,?4,?5,?6,?7)", nativeQuery = true)
    void addAResumesDetailRecord(int r_id, Date start_time, Date end_time, String title, String content, String category,
                                 Date create_time);

    @Transactional
    @Modifying
    @Query(value = "insert into resumedetail(r_id, content, category, create_time) values(?1,?2,?3,?4)", nativeQuery = true)
    void addAProfessionalResumesDetailRecord(int r_id, String content, String category, Date create_time);

}