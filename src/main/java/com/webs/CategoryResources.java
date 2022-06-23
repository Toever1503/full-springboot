package com.webs;

import com.dtos.CategoryDto;
import com.dtos.ResponseDto;
import com.entities.RoleEntity;
import com.models.CategoryModel;
import com.services.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.stream.Collectors;

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
        return ResponseDto.of(categoryService.findAll(pageable).map(c -> CategoryDto.toDto(c, false)), "get all categories success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/{id}")
    public ResponseDto findById(@PathVariable("id") Long id) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.findById(id), false), "get category success");
    }

    @Operation(summary = "Get all child category", description = "Get all child category by parent category ID")
    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/{id}/children")
    public ResponseDto findChildrenById(@PathVariable("id") Long id) {
        return ResponseDto.of(this.categoryService.findChildrenById(id).stream().map(c -> CategoryDto.toDto(c, false)).collect(Collectors.toList()), "get category children success");
    }

    @Operation(summary = "Get all parent category", description = "Get all category which hasn't parent")
    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/all-parent-categories")
    public ResponseDto findAllParentCategories() {
        return ResponseDto.of(this.categoryService.findChildrenById(null).stream().map(c -> CategoryDto.toDto(c, true)).collect(Collectors.toList()), "get category children success");
    }


    @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping
    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    public ResponseDto add(@Valid @RequestBody CategoryModel model) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.add(model), false), "add category success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @PutMapping
    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    public ResponseDto update(@Valid @RequestBody CategoryModel model) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.update(model), false), "update category success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @DeleteMapping("/{id}")
    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    public ResponseDto deleteById(@PathVariable("id") Long id) {
        return ResponseDto.of(categoryService.deleteById(id), "delete category success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/slug/{slug}")
    public ResponseDto findBySlug(@PathVariable String slug) {
        return ResponseDto.of(CategoryDto.toDto(categoryService.findBySlug(slug), true), "get category by slug success");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("search/{q}")
    public ResponseDto search(@PathVariable String q, Pageable page) {
        return ResponseDto.of(categoryService.search(q, page).map(c -> CategoryDto.toDto(c, false)), "search category success");
    }

    @Transactional
    @GetMapping("get-all-categories")
    public ResponseDto getAllCategories() {
        return ResponseDto.of(this.categoryService.findAll().stream().map(c -> CategoryDto.toDto(c, false)).collect(Collectors.toList()), "get all categories success");
    }

}
