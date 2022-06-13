package com.webs;

import com.dtos.CategoryDto;
import com.dtos.ResponseDto;
import com.models.CategoryModel;
import com.services.ICategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/categories")
@Validated
public class CategoryResources {
    private final ICategoryService categoryService;

    public CategoryResources(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping
    public ResponseDto getAll(Pageable pageable) {
        return ResponseDto.of(categoryService.findAll(pageable).map(CategoryDto::toDto), "get all categorys success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/{id}")
    public ResponseDto findById(@PathVariable("id") Long id) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.findById(id)), "get category success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/{id}/children")
    public ResponseDto findChildrenById(@PathVariable("id") Long id, Pageable pageable) {
        return ResponseDto.of(this.categoryService.findChildrenById(id, pageable).map(CategoryDto::toDto), "get category children success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping
    public ResponseDto add(@Valid @RequestBody CategoryModel model) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.add(model)), "add category success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @PutMapping
    public ResponseDto update(@Valid @RequestBody CategoryModel model) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.update(model)), "update category success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @DeleteMapping("/{id}")
    public ResponseDto deleteById(@PathVariable("id") Long id) {
        return ResponseDto.of(categoryService.deleteById(id), "delete category success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/slug")
    public ResponseDto findBySlug(@ModelAttribute("slug") String slug) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.findBySlug(slug)), "get category by slug success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("search/{q}")
    public ResponseDto search(@PathVariable String q, Pageable page) {
        return ResponseDto.of(categoryService.search(q, page).map(CategoryDto::toDto), "search category success");
    }
}
