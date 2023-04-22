package com.zyq.parttime.repository.userinfomanage;

import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface EmpInfoRepository extends JpaRepository<Employer, Integer>, JpaSpecificationExecutor<Employer> {
    @Query(value = "select * from employer where telephone = ?1", nativeQuery = true)
    Employer findEmployerByTelephone(String telephone);

    @Transactional
    @Modifying
    @Query(value = "update employer set gender=?1,age=?2,emails=?3 where telephone=?4", nativeQuery = true)
    void editEmpInfo(int gender, int age, String emails, String telephone);

    @Transactional
    @Modifying
    @Query(value = "update employer set pwd=?1 where telephone=?2", nativeQuery = true)
    void modifyEmpPwd(String pwd, String telephone);

}
