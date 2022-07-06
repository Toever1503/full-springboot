package com.webs;

import com.dtos.ECategoryType;
import com.dtos.IndustryDto;
import com.dtos.ResponseDto;
import com.entities.CategoryEntity;
import com.entities.RoleEntity;
import com.models.CategoryModel;
import com.models.specifications.CategorySpecification;
import com.services.ICategoryService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;


@RestController
@RequestMapping("industries")
public class IndustryResources {

    private final ICategoryService categoryService;

    public IndustryResources(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Transactional
    @PostMapping
    public ResponseDto addIndustry(@Valid CategoryModel model) {
        return ResponseDto.of(IndustryDto.toDto(categoryService.addIndustry(model)), "Create industry successfully");
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseDto updateCategory(@PathVariable Long id, @Valid CategoryModel model) {
        model.setId(id);
        return ResponseDto.of(IndustryDto.toDto(categoryService.updateIndustry(model)), "Create industry successfully");
    }

    @Transactional
    @GetMapping("slug/{slug}")
    public ResponseDto findBySlug(@PathVariable String slug) {
        return ResponseDto.of(IndustryDto.toDto(this.categoryService.findBySlug(slug)), "Get by slug: ".concat(slug));
    }

    @Transactional
    @GetMapping("{id}")
    public ResponseDto findById(@PathVariable Long id) {
        CategoryEntity industry = this.categoryService.findOne(Specification.where(CategorySpecification.byType(ECategoryType.INDUSTRY).and(CategorySpecification.byId(id))));
        return ResponseDto.of(IndustryDto.toDto(industry), "Find industry by id: ".concat(id.toString()));
    }

    @Transactional
    @GetMapping
    public ResponseDto getAllByPage() {
        List<CategoryEntity> categoryEntities = this.categoryService.findAll(Specification.where(CategorySpecification.byType(ECategoryType.INDUSTRY)));
        return ResponseDto.of(categoryEntities.stream().map(c -> IndustryDto.toDto(c)), "Get all industries");
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("resync-data-all")
    public ResponseDto resyncAllData() {
        return ResponseDto.of(this.categoryService.resyncIndustriesOnElasticsearch(), "Resync all data");
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseDto deleteById(@PathVariable Long id) {
        return ResponseDto.of(this.categoryService.deleteIndustryById(id), "Delete industry by id: ".concat(id.toString()));
    }

    @Transactional
    @GetMapping("public/detail-industry/{slug}")
    public ResponseDto getDetailIndustry(@PathVariable @Valid @NotBlank String slug) {
        return ResponseDto.of(this.categoryService.findDetailIndustryBySLug(slug), "Find detail industry by slug: ".concat(slug));
    }
}
