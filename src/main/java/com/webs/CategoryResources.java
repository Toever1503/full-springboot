package com.webs;

import com.dtos.CategoryDto;
import com.dtos.ResponseDto;
import com.models.CategoryModel;
import com.services.ICategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/categories")
public class CategoryResources {
    private final ICategoryService categoryService;

    public CategoryResources(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Transactional
    @GetMapping
    public ResponseDto getAll(Pageable pageable) {
        return ResponseDto.of(categoryService.findAll(pageable).map(CategoryDto::toDto), "get all categorys success");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto findById(@PathVariable("id") Long id) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.findById(id)), "get category success");
    }

    @Transactional
    @GetMapping("/{id}/children")
    public ResponseDto findChildrenById(@PathVariable("id") Long id, Pageable pageable) {
        return ResponseDto.of(this.categoryService.findChildrenById(id, pageable).map(CategoryDto::toDto), "get category children success");
    }

    @Transactional
    @PostMapping
    public ResponseDto add(@RequestBody CategoryModel model) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.add(model)), "add category success");
    }

    @Transactional
    @PutMapping
    public ResponseDto update(@RequestBody CategoryModel model) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.update(model)), "update category success");
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteById(@PathVariable("id") Long id) {
        return ResponseDto.of(categoryService.deleteById(id), "delete category success");
    }

    @Transactional
    @GetMapping("/slug")
    public ResponseDto findBySlug(@ModelAttribute("slug") String slug) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.findBySlug(slug)), "get category by slug success");
    }
    @Transactional
    @GetMapping("search/{q}")
    public ResponseDto search(@PathVariable String q, Pageable page) {
        return ResponseDto.of(categoryService.search(q, page).map(CategoryDto::toDto), "search category success");
    }
}
