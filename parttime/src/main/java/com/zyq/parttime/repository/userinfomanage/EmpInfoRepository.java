package com.zyq.parttime.repository.userinfomanage;

import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface EmpInfoRepository extends JpaRepository<Employer, Integer>, JpaSpecificationExecutor<Employer> {
    @Query(value = "select * from employer where telephone = ?1", nativeQuery = true)
    Employer findEmployerByTelephone(String telephone);

//    @Transactional
//    @Modifying
//    @Query(value = "insert into student(telephone, pwd, sno, school_name, gender,emails, stu_name, age, reg_date," +
//            " entrance_date, graduation_date, head, grade) values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,?13)", nativeQuery = true)
//    void registerByStu(String telephone, String pwd, String sno, String school_name, int gender, String emails,
//                       String stu_name, int age, Date reg, Date entrance_date, Date graduation_date, long head,
//                       String grade);
}
