package com.repositories;

import com.entities.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITagRepository extends JpaRepository<TagEntity, Long> {
    TagEntity findFirstBySlug(String slug);

    @Query("select t from TagEntity t where t.tagName like %?1%")
    Page<TagEntity> search(String q, Pageable page);
}
