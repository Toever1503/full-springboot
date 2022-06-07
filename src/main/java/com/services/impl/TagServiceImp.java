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
    public Page<TagEntity> filter(Pageable page, Specification<TagEntity> specs) {
        return null;
    }

    @Override
    public TagEntity findById(Long id) {
        return tagRepository.findById(id).orElseThrow(()->new RuntimeException("Not Found"));
    }

    @Override
    public TagEntity add(TagModel model) {
        TagEntity tag = new TagEntity();
        if(tag.getTagName()==null){
            return null;
        }else {
            tag.setTagName(model.getTagName());
        }
        if(model.getSlug()!=null){
            tag.setSlug(model.getSlug());
        }
        else
            tag.setSlug(ASCIIConverter.utf8ToAscii(model.getTagName()));
        return tagRepository.save(tag);
    }

    @Override
    public List<TagEntity> add(List<TagModel> model) {
        return null;
    }

    @Override
    public TagEntity update(TagModel model) {
        if(model.getId()!=null){
            TagEntity tag = this.findById(model.getId());
            if(model.getTagName()!= null){
                tag.setTagName(model.getTagName());
            }
            if(model.getSlug()!=null){
                tag.setSlug(model.getSlug());
            }else
                tag.setSlug(ASCIIConverter.utf8ToAscii(tag.getTagName()));
            return tagRepository.save(tag);
        }else
            return null;
    }

    @Override
    public boolean deleteById(Long id) {
        if(this.findById(id)!=null){
            tagRepository.deleteById(id);
            return true;
        }else
            return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(this::deleteById);
        return true;
    }

    @Override
    public TagEntity findBySlug(String slug) {
        return tagRepository.findFirstBySlug(slug);
    }
}
