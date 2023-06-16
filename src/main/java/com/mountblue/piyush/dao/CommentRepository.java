package com.mountblue.piyush.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mountblue.piyush.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByPostId(int postId);

    Comment findById(int commentId);

    @Modifying
    @Query("UPDATE Comment c SET c.name = :name, c.email = :email, c.comment = :comment, "
            + "c.updatedAt = :updatedAt WHERE c.id = :id")
    void updateComment(@Param("id") int id, @Param("name") String name, @Param("email") String email,
                       @Param("comment") String comment, @Param("updatedAt") LocalDateTime updatedAt);

    String deleteById(int commentId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c IN :commentList")
    boolean deleteAllComments(@Param("commentList") List<Comment> commentList);
}
