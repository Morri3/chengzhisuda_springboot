package com.zyq.parttime.repository.comment;

import com.zyq.parttime.entity.Comment;
import com.zyq.parttime.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {
    @Query(value = "select * from comments where s_id=?1", nativeQuery = true)
    Comment getComment(int s_id);

//    @Transactional
//    @Modifying
//    @Query(value = "insert into resumes(stu_id, pic_url,upload_time,create_time,r_status) values(?1,?2,?3,?4,?5)", nativeQuery = true)
//    void createAResumeRecord(String stu_id, String pic_url, Date upload_time, Date create_time, String r_status);
}
