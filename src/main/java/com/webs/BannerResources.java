package com.webs;

import com.dtos.BannerDto;
import com.dtos.ResponseDto;
import com.models.BannerModel;
import com.services.IBannerService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/banner")
public class BannerResources {
    private final IBannerService bannerService;

    public BannerResources(IBannerService bannerService) {
        this.bannerService = bannerService;
    }

    @Transactional
    @GetMapping
    public ResponseDto getAll(Pageable pageable) {
        return ResponseDto.of(this.bannerService.findAll(pageable).map(BannerDto::toDto), "Get banner list");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getBannerById(@PathVariable("id") Long id) {
        return ResponseDto.of(BannerDto.toDto(this.bannerService.findById(id)), "Get banner by id");
    }

    @Transactional
    @PostMapping
    public ResponseDto addBanner(@ModelAttribute BannerModel model) {
        return ResponseDto.of(BannerDto.toDto(this.bannerService.add(model)), "Add banner");
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseDto editBanner(@PathVariable("id") Long id, @ModelAttribute BannerModel model) {
        model.setId(id);
        return ResponseDto.of(BannerDto.toDto(this.bannerService.update(model)), "Edit banner");
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteBanner(@PathVariable("id") Long id) {
        return ResponseDto.of(this.bannerService.deleteById(id), "Delete banner");
    }

    @Transactional
    @PutMapping("/{id}/status")
    public ResponseDto updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        return ResponseDto.of(this.bannerService.updateStatus(id, status), "Update banner status");
    }
}
