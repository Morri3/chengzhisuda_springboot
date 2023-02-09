package com.zyq.parttime.repository;

import com.zyq.parttime.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface LogAndRegRepository  extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
    @Query(value = "select * from student where telephone = ?1", nativeQuery = true)
    Student findStudentByTelephone(String telephone);

}
