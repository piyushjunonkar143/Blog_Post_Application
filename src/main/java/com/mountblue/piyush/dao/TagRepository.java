package com.mountblue.piyush.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mountblue.piyush.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    List<Tag> findByName(String name);
    @Query("SELECT t.id FROM Tag t WHERE t.name = :tagName")
    List<Integer> findTagIdsByName(@Param("tagName") String tagName);
}
