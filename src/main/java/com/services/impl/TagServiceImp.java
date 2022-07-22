package com.services.impl;

import com.entities.TagEntity;
import com.models.TagModel;
import com.repositories.ITagRepository;
import com.services.ITagService;
import com.utils.ASCIIConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImp implements ITagService {

    final ITagRepository tagRepository;

    public TagServiceImp(ITagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagEntity> findAll() {
        return null;
    }

    @Override
    public Page<TagEntity> findAll(Pageable page) {
        return tagRepository.findAll(page);
    }

    @Override
    public List<TagEntity> findAll(Specification<TagEntity> specs) {
        return null;
    }

    @Override
    public Page<TagEntity> filter(Pageable page, Specification<TagEntity> specs) {
        return null;
    }

    @Override
    public TagEntity findById(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ có id: "+id));
    }

    @Override
    public TagEntity add(TagModel model) {
        TagEntity tag = TagEntity.builder()
                .tagName(model.getTagName())
                .build();
        return tagRepository.save(tag);
    }

    @Override
    public List<TagEntity> add(List<TagModel> model) {
        return null;
    }

    @Override
    public TagEntity update(TagModel model) {
        TagEntity tag = this.findById(model.getId());
        tag.setTagName(model.getTagName());
        return tagRepository.save(tag);
    }

    @Override
    public boolean deleteById(Long id) {
        if (this.findById(id) != null) {
            tagRepository.deleteById(id);
            return true;
        } else
            return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(this::deleteById);
        return true;
    }


    @Override
    public Page<TagEntity> search(String q, Pageable page) {
        return this.tagRepository.search(q, page);
    }
}
