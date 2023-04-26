package com.zyq.parttime.repository.userinfomanage;

import com.zyq.parttime.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface StuInfoRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
    @Query(value = "select * from student where telephone = ?1", nativeQuery = true)
    Student findStudentByTelephone(String telephone);

    @Transactional
    @Modifying
    @Query(value = "update student set gender=?1,age=?2,emails=?3,entrance_date=?4, graduation_date=?5,grade=?6 " +
            "where telephone=?7", nativeQuery = true)
    void editStuInfo(int gender, int age, String emails, Date entrance, Date graduation, String grade, String telephone);

    @Transactional
    @Modifying
    @Query(value = "update student set pwd=?1 where telephone=?2", nativeQuery = true)
    void modifyStuPwd(String pwd, String telephone);

    @Query(value = "select * from student", nativeQuery = true)
    List<Student> getAllStudents();
}
