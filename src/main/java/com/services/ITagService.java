package com.services;

import com.entities.TagEntity;
import com.models.TagModel;

import java.util.List;

public interface ITagService extends IBaseService<TagEntity, TagModel,Long>{

    TagEntity findBySlug(String slug);

}
