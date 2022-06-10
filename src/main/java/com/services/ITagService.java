package com.services;

import com.entities.TagEntity;
import com.models.TagModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.DoubleStream;

public interface ITagService extends IBaseService<TagEntity, TagModel,Long>{

    TagEntity findBySlug(String slug);

    Page<TagEntity> search(String q, Pageable page);
}
