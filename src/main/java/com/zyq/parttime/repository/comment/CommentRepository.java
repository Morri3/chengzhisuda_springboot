package com.zyq.parttime.repository.comment;

import com.zyq.parttime.entity.Comment;
import com.zyq.parttime.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {
    @Query(value = "select * from comments where s_id=?1", nativeQuery = true)
    Comment getComment(int s_id);

    @Query(value = "select * from comments", nativeQuery = true)
    List<Comment> getAllComment();

    @Query(value = "select * from comments order by create_time limit 0,1", nativeQuery = true)
    Comment getLatestComment();

    @Query(value = "select * from comments where p_id=?1", nativeQuery = true)
    Comment getCommentByPId(int p_id);

    @Transactional
    @Modifying
    @Query(value = "insert into comments(s_id,content,create_time) values(?1,?2,?3)", nativeQuery = true)
    void post(int s_id, String content, Date create_time);
}
