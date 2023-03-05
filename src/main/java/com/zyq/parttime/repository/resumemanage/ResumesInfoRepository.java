package com.zyq.parttime.repository.resumemanage;

import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Resumedetail;
import com.zyq.parttime.entity.Resumes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface ResumesInfoRepository extends JpaRepository<Resumes, Integer>, JpaSpecificationExecutor<Resumes> {
    @Query(value = "select * from resumes where stu_id = ?1", nativeQuery = true)
    Resumes findResumesByStuId(String telephone);

    @Transactional
    @Modifying
    @Query(value = "insert into resumes(stu_id, pic_url,upload_time,create_time,r_status) values(?1,?2,?3,?4,?5)", nativeQuery = true)
    void createAResumeRecord(String stu_id, String pic_url, Date upload_time, Date create_time, String r_status);

    @Query(value = "select r_id from resumes order by create_time desc limit 0,1", nativeQuery = true)
    int findLatestResumes();

    @Transactional
    @Modifying
    @Query(value = "update resumes set exp=?1,current_area=?2,r_status=?3 where stu_id=?4", nativeQuery = true)
    void updateResumesInfo(String current_area, String exp, String r_status, String telephone);

    @Transactional
    @Modifying
    @Query(value = "update resumes set r_status=?1 where stu_id=?2", nativeQuery = true)
    void updateResumesStatus(String r_status, String telephone);

    @Transactional
    @Modifying
    @Query(value = "update resumes set r_status='已删除' where r_id=?1", nativeQuery = true)
    void deleteResumeByRId(int r_id);

}
