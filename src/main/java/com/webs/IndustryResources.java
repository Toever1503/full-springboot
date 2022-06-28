package com.webs;

import com.dtos.CategoryDto;
import com.dtos.ECategoryType;
import com.dtos.IndustryDto;
import com.dtos.ResponseDto;
import com.entities.CategoryEntity;
import com.models.CategoryModel;
import com.models.specifications.CategorySpecification;
import com.services.ICategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("industries")
public class IndustryResources {

    private final ICategoryService categoryService;
    public IndustryResources(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Transactional
    @PostMapping
    public ResponseDto addIndustry(@RequestBody @Valid CategoryModel model) {
        return ResponseDto.of(IndustryDto.toDto(categoryService.addIndustry(model), false), "Create industry successfully");
    }

    @Transactional
    @PutMapping
    public ResponseDto updateCategory(@RequestBody @Valid CategoryModel model) {
        return ResponseDto.of(IndustryDto.toDto(categoryService.updateIndustry(model), false), "Create industry successfully");
    }

    @Transactional
    @GetMapping("slug/{slug}")
    public ResponseDto findBySlug(@PathVariable String slug) {
        return ResponseDto.of(IndustryDto.toDto(this.categoryService.findBySlug(slug), true), "Get by slug: ".concat(slug));
    }

    @Transactional
    @GetMapping("{id}")
    public ResponseDto findById(@PathVariable Long id) {
        CategoryEntity industry = this.categoryService.findOne(Specification.where(CategorySpecification.byType(ECategoryType.INDUSTRY).and(CategorySpecification.byId(id))));
        return ResponseDto.of(IndustryDto.toDto(industry, true), "Find industry by id: ".concat(id.toString()));
    }

    @Transactional
    @GetMapping
    public ResponseDto getAll(Pageable page) {
        Page<CategoryEntity> categoryEntities = this.categoryService.filter(page, Specification.where(CategorySpecification.byType(ECategoryType.INDUSTRY)));
        return ResponseDto.of(categoryEntities.map(c -> CategoryDto.toDto(c, false)), "Get all industries");
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseDto deleteById(@PathVariable Long id) {
        return ResponseDto.of(this.categoryService.deleteIndustryById(id), "Delete industry by id: ".concat(id.toString()));
    }
}
