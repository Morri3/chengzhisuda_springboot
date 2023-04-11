package com.zyq.parttime.repository.intention;

import com.zyq.parttime.entity.Intention;
import com.zyq.parttime.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface IntentionRepository extends JpaRepository<Intention, Integer>, JpaSpecificationExecutor<Intention> {
    @Query(value = "select * from intention where stu_id=?1", nativeQuery = true)
    List<Intention> getIntentionsByStuId(String stu_id);

    @Transactional
    @Modifying
    @Query(value = "insert into intention(stu_id,content) values(?1,?2)", nativeQuery = true)
    void addIntention(String stu_id, String content);

    @Transactional
    @Modifying
    @Query(value = "delete from intention where stu_id=?1 and content=?2", nativeQuery = true)
    void removeOneIntention(String stu_id, String content);

}
