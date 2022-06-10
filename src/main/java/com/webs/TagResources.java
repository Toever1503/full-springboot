package com.webs;

import com.dtos.ResponseDto;
import com.dtos.TagDto;
import com.models.TagModel;
import com.services.ITagService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tags")
@Validated
public class TagResources {
    final ITagService tagService;

    public TagResources(ITagService tagService) {
        this.tagService = tagService;
    }
    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/all")
    public ResponseDto getAllTags(Pageable pageable) {
        return ResponseDto.of(tagService.findAll(pageable).map(TagDto::toTagDto), "Get all tags");
    }
    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/{id}")
    public ResponseDto getTagById(@PathVariable("id") Long id) {
        return ResponseDto.of(TagDto.toTagDto(tagService.findById(id)), "Get all tags");
    }
    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/slug")
    public ResponseDto getTagBySlug(@RequestParam("slug") String slug) {
        return ResponseDto.of(TagDto.toTagDto(tagService.findBySlug(slug)), "Get tag by slug");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping
    public ResponseDto addTag(@RequestBody @Valid TagModel model) {
        try {
            return ResponseDto.of(TagDto.toTagDto(tagService.add(model)), "Add tag");
        } catch (Exception e) {
            throw new RuntimeException("Slug has existed, Please add another slug");
        }
    }
    @Transactional(rollbackFor = RuntimeException.class)
    @PutMapping("{id}")
    public ResponseDto updateTag(@PathVariable Long id, @RequestBody @Valid TagModel model) {
        model.setId(id);
        try {
            return ResponseDto.of(TagDto.toTagDto(tagService.update(model)), "Update tag");
        } catch (Exception e) {
            throw new RuntimeException("Slug has existed, Please add another slug");
        }
    }
    @Transactional(rollbackFor = RuntimeException.class)
    @DeleteMapping("/delete/{id}")
    public ResponseDto deleteTag(@PathVariable("id") Long id) {
        return ResponseDto.of(tagService.deleteById(id), "Delete tag");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("search")
    public ResponseDto search(@RequestParam String q, Pageable page){
        return ResponseDto.of(tagService.search(q, page).map(TagDto::toTagDto), "Search tag");
    }
}
