package com.webs;


import com.config.elasticsearch.ERepositories.IEProductRepository;
import com.dtos.*;
import com.entities.RoleEntity;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;
import com.models.elasticsearch.EProductFilterModel;
import com.repositories.IProductRepository;
import com.services.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import com.models.filters.ProductFilter;
import com.models.specifications.ProductSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("products")
@RestController
@Transactional
@Lazy
public class ProductResources {

    private final IProductService productService;

    private final IEProductRepository eProductRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ProductResources(IProductService productService, IEProductRepository eProductRepository) {
        this.productService = productService;
        this.eProductRepository = eProductRepository;
    }

    @GetMapping("public/get-all")
    public ResponseDto getAll(Pageable page) {
        return ResponseDto.of(eProductRepository.findAll(page), "Lấy toàn bộ sản phẩm");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto findById(@PathVariable Long id, @RequestParam(required = false) boolean force) {
        if (force)
            return ResponseDto.of(ProductDto.toDto(this.productService.findById(id)), "Lấy sản phẩm theo id: ".concat(id.toString()));
        return ResponseDto.of(this.eProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id: ".concat(id.toString()))), "Lấy sản phẩm theo id: ".concat(id.toString()));
    }


    @Transactional
    @PostMapping("variations/{productId}")
    public ResponseDto saveVariations(@PathVariable Long productId, @RequestBody @Valid List<ProductVariationModel> models) {
        ProductDto dto = productService.saveDtoOnElasticsearch(this.productService.saveVariations(productId, models));
        return ResponseDto.of(dto, "Lưu biến thể cho sản phẩm có id: ".concat(productId.toString()));
    }

    @Transactional
    @PostMapping("skus/{productId}")
    public ResponseDto saveSkus(@PathVariable Long productId, @Valid @RequestPart List<ProductSkuModel> models, HttpServletRequest req) {
        ProductDto dto = productService.saveDtoOnElasticsearch(this.productService.saveSkus(req, productId, models));
        return ResponseDto.of(dto, "Lưu sku cho sản phẩm có id: ".concat(productId.toString()));
    }

    @Transactional
    @PostMapping
    public ResponseDto createProduct(@Valid @RequestPart("product") ProductModel productModel,
                                     @RequestPart("image") MultipartFile image,
                                     @RequestPart(name = "attachFiles[]", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(null);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(this.productService.saveDtoOnElasticsearch(productService.add(productModel)), "Tạo sản phẩm");
    }

    @Transactional
    @GetMapping("variations/{id}")
    public ResponseDto findVariations(@PathVariable Long id) {
        return ResponseDto.of(this.productService.findVariations(id).stream().map(ProductVariationDto::toDto).collect(Collectors.toList()), "Lấy danh sách biến thể cho sản phẩm có id: ".concat(id.toString()));
    }

    @Transactional
    @GetMapping("skus/{id}")
    public ResponseDto findSkus(@PathVariable Long id) {
        return ResponseDto.of(this.productService.findSkus(id).stream().map(ProductSkuDto::toDto).collect(Collectors.toList()), "Lấy sku cho sản phẩm có id: ".concat(id.toString()));
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseDto updateProduct(@PathVariable("id") Long id, @Valid @RequestPart("product") ProductModel productModel,
                                     @RequestPart(value = "image", required = false) MultipartFile image,
                                     @RequestPart(name = "attachFiles[]", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(id);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(this.productService.saveDtoOnElasticsearch(this.productService.update(productModel)), "Cập nhật sản phẩm");
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("refreshData")
    @Operation(summary = "resync data on database to  elasticsearch")
    public String refreshElasticsearch() {
        this.productService.refreshDataElasticsearch();
        return "Ok";
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("delete-all-data")
    public ResponseDto deleteAllData() {
        return ResponseDto.of(this.productService.deleteAllDataOnElasticsearch(), "Xóa toàn bộ dữ liệu elasticsearch");
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("delete-index")
    public ResponseDto deleteIndex() {
        return ResponseDto.of(this.productService.deleteIndexElasticsearch(), "Xóa index trên elasticsearch");
    }


    @PostMapping("/filter")
    public ResponseDto filterProduct(@RequestBody ProductFilter productFilter, Pageable pageable) {
        return ResponseDto.of(productService.findAll(pageable, ProductSpecification.filter(productFilter)), "Lọc sản phẩm");
    }

    @Operation(summary = "filter products")
    @PostMapping("public/filter")
    public ResponseDto filterProduct(@RequestBody EProductFilterModel filterModel, Pageable page) {
        log.info("Product filterModel: {}", filterModel);
        return ResponseDto.of(this.productService.eFilter(page, filterModel), "Lọc sản phẩm");
    }

    @Operation(summary = "find product by id and related data")
    @GetMapping("public/{id}")
    public ResponseDto findProductById(@PathVariable Long id, Pageable page) {
        log.info("find product by id: {}", id);
        this.productService.saveDtoOnElasticsearch(this.productService.findById(id));
        return ResponseDto.of(
                this.productService.findDetailById(page, id),
                "Get product id: ".concat(id.toString()));
    }

    @Transactional
    @GetMapping("public/get-filter-data")
    public ResponseDto getFilterData() {
        return ResponseDto.of(this.productService.getFilterData(), "Lấy dữ liệu lọc");
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseDto deleteProduct(@PathVariable Long id) {
        return ResponseDto.of(this.productService.deleteById(id), "Xóa sản phẩm");
    }

    @Transactional
    @GetMapping("/get-product-by-categoryId/{id}")
    public ResponseDto getProductByCategoryId(@PathVariable Long id, Pageable page) {
        return ResponseDto.of(eProductRepository.findByCategoryId(id, page), "Lấy toàn bộ sản phẩm");
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @PatchMapping("change-status/{productId}")
    public ResponseDto changeProductStatus(@PathVariable Long productId, @RequestParam EProductStatus status){
        return ResponseDto.of(this.productService.changeProductStatus(productId, status), "Thay đổi trạng thái sản phẩm");
    }

    @GetMapping("public/auto-complete")
    public ResponseDto autoComplete(@RequestParam @Valid @NotNull @NotBlank String keyword, Pageable page) {
        return ResponseDto.of(this.productService.autoComplete(keyword, page), "o");
    }

}
