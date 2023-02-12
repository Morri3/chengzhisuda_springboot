package com.zyq.parttime.repository.logandreg;

import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

public interface LogAndRegByEmpRepository extends JpaRepository<Employer, Integer>, JpaSpecificationExecutor<Employer> {
    @Query(value = "select * from employer where telephone = ?1", nativeQuery = true)
    Employer findEmployerByTelephone(String telephone);

    @Transactional
    @Modifying
    @Query(value = "insert into employer(telephone, u_id, pwd, jno, gender, " +
            "emails, emp_name, age, reg_date, head, emp_grade) " +
            "values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11)", nativeQuery = true)
    void registerByEmp(String telephone, int u_id, String pwd, String jno, int gender, String emails,
                       String emp_name, int age, Date reg_date, long head, boolean emp_grade);

}
