package com.webs;

import com.dtos.CategoryDto;
import com.dtos.ECategoryType;
import com.dtos.ResponseDto;
import com.entities.CategoryEntity;
import com.entities.CategoryEntity_;
import com.entities.RoleEntity;
import com.models.CategoryModel;
import com.models.specifications.CategorySpecification;
import com.services.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@Validated
public class CategoryResources {
    private final ICategoryService categoryService;

    public CategoryResources(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Transactional
    @GetMapping
    public ResponseDto getAll(Pageable pageable) {
        return ResponseDto.of(categoryService.findAll(pageable).map(c -> CategoryDto.toDto(c, false)), "get all categories success");
    }

    @Operation(summary = "Get all child category", description = "Get all child category by parent category ID")
    @Transactional
    @GetMapping("/{id}/children")
    public ResponseDto findChildrenById(@PathVariable("id") Long id) {
        return ResponseDto.of(this.categoryService.findChildrenById(id).stream().map(c -> CategoryDto.toDto(c, false)).collect(Collectors.toList()), "get category children success");
    }

    @Operation(summary = "Get all parent category", description = "Get all category which hasn't parent")
    @Transactional
    @GetMapping("/all-parent-categories")
    public ResponseDto findAllParentCategories() {
        return ResponseDto.of(this.categoryService.findChildrenById(null).stream().map(c -> CategoryDto.toDto(c, true)).collect(Collectors.toList()), "get category children success");
    }

    @Transactional
    @PostMapping
    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    public ResponseDto add(CategoryModel model) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.add(model), false), "add category success");
    }

    @Transactional
    @PutMapping("/{id}")
    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    public ResponseDto update(CategoryModel model, @PathVariable("id") Long id) {
        model.setId(model.getId());
        return ResponseDto.of(CategoryDto.toDto(categoryService.update(model), false), "update category success");
    }

    @Transactional
    @DeleteMapping("/{id}")
    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    public ResponseDto deleteById(@PathVariable("id") Long id) {
        return ResponseDto.of(categoryService.deleteById(id), "delete category success");
    }

    @Transactional
    @GetMapping("public/status/{status}")
    public ResponseDto filterByStatus(@PathVariable Boolean status) {
        List<CategoryEntity> categories = categoryService.findAll(Specification.where(CategorySpecification.byStatus(status)));
        return ResponseDto.of(categories.stream()
                .map(c -> CategoryDto.toDto(c, false)), "get category by status success");
    }

    @Transactional
    @GetMapping("search/{q}")
    public ResponseDto search(@PathVariable String q, Pageable page) {
        return ResponseDto.of(categoryService.search(q, page).map(c -> CategoryDto.toDto(c, false)), "search category success");
    }

    @PatchMapping("change-status/{id}")
    @Transactional
    public ResponseDto changeStatus(@PathVariable Long id) {
        return ResponseDto.of(categoryService.changeStatus(id), "change status success");
    }

    @Operation(summary = "Admin Get all categories", description = "Admin Get all categories")
    @Transactional
    @GetMapping("get-all-categories")
    public ResponseDto getAll() {
        List<CategoryEntity> categories = this.categoryService.findAll();
        return ResponseDto.of(categories
                .stream().map(c -> CategoryDto.toDto(c, false)).collect(Collectors.toList()), "get all categories");
    }

    @Operation(summary = "Get all categories", description = "Get all categories")
    @Transactional
    @GetMapping("public/get-all-categories")
    public ResponseDto getAllCategories() {
        List<CategoryEntity> categories = this.categoryService.getAllCategories();
        return ResponseDto.of(categories
                .stream().map(c -> CategoryDto.toDto(c, false)).collect(Collectors.toList()), "get all categories");
    }

    @Operation(summary = "Get category by slug", description = "use slug to find category")
    @Transactional
    @GetMapping("public/{slug}")
    public ResponseDto getDetailCategory(@PathVariable @Valid @NotBlank String slug) {
        return ResponseDto.of(CategoryDto.toDto(this.categoryService.findOne(
                Specification.where(CategorySpecification.equal(CategoryEntity_.SLUG, slug).and(
                        CategorySpecification.equal(CategoryEntity_.STATUS, true)
                ))
        ), true), "Find category by slug: ".concat(slug));
    }

    @Transactional
    @GetMapping("public/get-by-id/{id}")
    public ResponseDto findById(@PathVariable("id") Long id) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.findById(id), true), "get category id: ".concat(id.toString()));
    }


}
