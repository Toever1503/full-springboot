package com.webs;

import com.dtos.OptionsDto;
import com.dtos.ResponseDto;
import com.entities.RoleEntity;
import com.models.OptionsModel;
import com.services.IOptionsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/options")
public class OptionsResources {
    private final IOptionsService optionsService;

    public OptionsResources(IOptionsService optionsService) {
        this.optionsService = optionsService;
    }


    @Transactional
    @GetMapping("get-options-by-key")
    public ResponseDto getOptionsByKey(@RequestParam String key) {
        return ResponseDto.of(OptionsDto.toDto(this.optionsService.getOptionByKey(key)), "Lấy slide và banner theo key: " + key);
    }

    @Transactional
    @GetMapping("get-options-by-keys")
    public ResponseDto getOptionsByKeys(@RequestParam List<String> keys) {
        return ResponseDto.of(this.optionsService.getOptionsByKeys(keys).stream().map(OptionsDto::toDto).collect(Collectors.toList()), "Lấy danh sách slide và banner theo danh sách key: " + keys);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @Transactional
    @PostMapping
    public ResponseDto settingUpdateHomePage(OptionsModel model, HttpServletRequest request) {
        return ResponseDto.of(OptionsDto.toDto(this.optionsService.settingUpdateHomePage(model, request)), "Thêm slide và banner");
    }

}
