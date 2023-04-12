package com.zyq.parttime.repository.position;

import com.zyq.parttime.entity.Position;
import com.zyq.parttime.entity.Resumes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Integer>, JpaSpecificationExecutor<Position> {
    @Query(value = "select * from position", nativeQuery = true)
    List<Position> getAllPositions();

//    @Query(value = "select * from position", nativeQuery = true)
//    List<Position> getAllPositionsByIntentions(String[] intentions);

    @Query(value = "select * from position where category=?1", nativeQuery = true)
    List<Position> getPositionByIntention(String intention);

    @Query(value = "select * from position where p_id=?1", nativeQuery = true)
    Position getPosition(int p_id);
//    @Transactional
//    @Modifying
//    @Query(value = "insert into resumes(stu_id, pic_url,upload_time,create_time,r_status) values(?1,?2,?3,?4,?5)", nativeQuery = true)
//    void createAResumeRecord(String stu_id, String pic_url, Date upload_time, Date create_time, String r_status);
}
