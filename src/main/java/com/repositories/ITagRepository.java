package com.repositories;

import com.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITagRepository extends JpaRepository<TagEntity,Long> {
    TagEntity findFirstBySlug(String slug);
}
