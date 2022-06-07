package com.webs;

import com.dtos.ResponseDto;
import com.dtos.TagDto;
import com.models.TagModel;
import com.services.ITagService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tags")
public class TagResources {
    final ITagService tagService;

    public TagResources(ITagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/all")
    public ResponseDto getAllTags(Pageable pageable){
        return ResponseDto.of(tagService.findAll(pageable).map(TagDto::toTagDto),"Get all tags");
    }
    @GetMapping("/{id}")
    public ResponseDto getTagById(@PathVariable("id") Long id){
        return ResponseDto.of(TagDto.toTagDto(tagService.findById(id)),"Get all tags");
    }

    @GetMapping("/slug")
    public ResponseDto getTagBySlug(@RequestParam("slug") String slug){
        return ResponseDto.of(TagDto.toTagDto(tagService.findBySlug(slug)),"Get tag by slug");
    }

    @PostMapping
    public ResponseDto addTag(@RequestBody @Valid TagModel model){
        try{
           return ResponseDto.of(TagDto.toTagDto(tagService.add(model)),"Add tag");
        }catch (Exception e){
            System.out.println(e);
        }
        return ResponseDto.of(null,"Please add another slug, Add tag");
    }

    @PutMapping
    public ResponseDto updateTag(@RequestBody @Valid TagModel model){
        try{
            return ResponseDto.of(TagDto.toTagDto(tagService.update(model)),"Update tag");
        }catch (Exception e){
            System.out.println(e);
        }
        return ResponseDto.of(null,"Duplicate slug found, Update tag");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDto deleteTag(@PathVariable("id") Long id){
        return ResponseDto.of(tagService.deleteById(id),"Delete tag");
    }
}
