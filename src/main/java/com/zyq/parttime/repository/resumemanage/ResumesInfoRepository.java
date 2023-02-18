package com.zyq.parttime.repository.resumemanage;

import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Resumedetail;
import com.zyq.parttime.entity.Resumes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResumesInfoRepository extends JpaRepository<Resumes, Integer>, JpaSpecificationExecutor<Resumes> {
    @Query(value = "select * from resumes where stu_id = ?1", nativeQuery = true)
    Resumes findResumesByStuId(String telephone);

    @Query(value = "select * from resumedetail where r_id = ?1 and category =?2", nativeQuery = true)
    List<Resumedetail> findResumeDetailListByRId(int r_id, String category);

//    @Transactional
//    @Modifying
//    @Query(value = "insert into student(telephone, pwd, sno, school_name, gender,emails, stu_name, age, reg_date," +
//            " entrance_date, graduation_date, head, grade) values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,?13)", nativeQuery = true)
//    void registerByStu(String telephone, String pwd, String sno, String school_name, int gender, String emails,
//                       String stu_name, int age, Date reg, Date entrance_date, Date graduation_date, long head,
//                       String grade);
}
