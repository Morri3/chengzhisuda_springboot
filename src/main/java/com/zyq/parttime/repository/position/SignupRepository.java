package com.zyq.parttime.repository.position;

import com.zyq.parttime.entity.Position;
import com.zyq.parttime.entity.Signup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface SignupRepository extends JpaRepository<Signup, Integer>, JpaSpecificationExecutor<Signup> {
    @Query(value = "select * from signup order by update_time limit 0,1", nativeQuery = true)
    Signup getLatestSignup(Date update_time);

    @Query(value = "select * from signup where stu_id=?1 and p_id=?2", nativeQuery = true)
    Signup findExistsSignup(String stu_id, int p_id);

    @Query(value = "select * from signup where s_id=?1", nativeQuery = true)
    Signup findSignup(int s_id);

    @Query(value = "select * from signup where stu_id=?1", nativeQuery = true)
    List<Signup> getAllSignup(String stu_id);


    @Transactional
    @Modifying
    @Query(value = "insert into signup(stu_id,p_id,signup_status,create_time,update_time) values(?1,?2,?3,?4,?5)", nativeQuery = true)
    void createASignupRecord(String stu_id, int p_id, String signup_status, Date create_time, Date update_time);

    @Transactional
    @Modifying
    @Query(value = "update signup set signup_status='已删除' where s_id=?1", nativeQuery = true)
    void cancelSignup(int s_id);

}
