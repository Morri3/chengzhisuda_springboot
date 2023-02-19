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

    @Query(value = "select * from resumedetail where r_id = ?1 and category =?2", nativeQuery = true)
    List<Resumedetail> findResumeDetailListByRId(int r_id, String category);

    @Transactional
    @Modifying
    @Query(value = "insert into resumes(telephone, pic_url) values(?1,?2)", nativeQuery = true)
    void createAResumeRecord(String telephone, String pic_url);

    @Query(value = "select * from resumes order by create_time desc limit 0,1", nativeQuery = true)
    int findLatestResumes();
}
