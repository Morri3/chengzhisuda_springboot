package com.zyq.parttime.repository.resumemanage;

import com.zyq.parttime.entity.Resumedetail;
import com.zyq.parttime.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface ResumesDetailRepository extends JpaRepository<Resumedetail, Integer>, JpaSpecificationExecutor<Resumedetail> {
    @Query(value = "select rd_id from resumedetail order by create_time desc limit 0,1", nativeQuery = true)
    int findLatestResumesDetail();

    @Query(value = "select * from resumedetail where r_id = ?1 and category =?2", nativeQuery = true)
    List<Resumedetail> findResumeDetailListByRIdAndCategory(int r_id, String category);

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

    @Query(value = "select * from resumedetail where rd_id = ?1", nativeQuery = true)
    Resumedetail findResumeDetailByRdId(int rd_id);

    @Transactional
    @Modifying
    @Query(value = "update resumedetail set title=?1,content=?2 where rd_id=?3", nativeQuery = true)
    void updateResumedetailInfo1(String title, String content, int rd_id);

    @Transactional
    @Modifying
    @Query(value = "update resumedetail set title=?1,content=?2,end_time=?3 where rd_id=?4", nativeQuery = true)
    void updateResumedetailInfo2(String title, String content, Date end_time, int rd_id);

    @Transactional
    @Modifying
    @Query(value = "update resumedetail set title=?1,content=?2,start_time=?3 where rd_id=?4", nativeQuery = true)
    void updateResumedetailInfo3(String title, String content, Date start_time, int rd_id);

    @Transactional
    @Modifying
    @Query(value = "update resumedetail set title=?1,content=?2,start_time=?3,end_time=?4 where rd_id=?5", nativeQuery = true)
    void updateResumedetailInfo4(String title, String content, Date start_time, Date end_time, int rd_id);

    @Transactional
    @Modifying
    @Query(value = "update resumedetail set content=?1 where rd_id=?2", nativeQuery = true)
    void updateResumedetailInfo5(String content, int rd_id);

}