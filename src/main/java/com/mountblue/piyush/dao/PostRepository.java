package com.mountblue.piyush.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mountblue.piyush.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findById(int postId);

    @Modifying
    @Query("UPDATE Post p SET p.title = :title, p.content = :content, p.excerpt = :excerpt, "
            + "p.updatedAt = :updatedAt WHERE p.id = :id")
    void updatePost(
            @Param("id") int id,
            @Param("title") String title,
            @Param("excerpt") String excerpt,
            @Param("content") String content,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Query("SELECT p.id FROM Post p WHERE p.publishedAt = :publishedAt")
    Collection<? extends Integer> findIdsByPublishedAt(@Param("publishedAt") LocalDate publishedAt);

    @Query("SELECT p.id FROM Post p WHERE p.author = :author")
    Collection<? extends Integer> findIdsByAuthor(@Param("author") String author);

    Page<Post> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrContentContainingIgnoreCaseOrTagsNameContainingIgnoreCase(String title,
                                                                                                                                      String author, String content, String tags, Pageable pageable);
    String deleteById(int postId);
    Page<Post> findAllByIdInOrTagsIn(List<?> filters, List<?> filters3, Pageable pageable);

}
