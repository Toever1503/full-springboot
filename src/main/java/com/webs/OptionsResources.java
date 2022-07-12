package com.webs;

import com.dtos.OptionsDto;
import com.dtos.ResponseDto;
import com.models.OptionsModel;
import com.services.IOptionsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/options")
public class OptionsResources {
    private final IOptionsService optionsService;

    public OptionsResources(IOptionsService optionsService) {
        this.optionsService = optionsService;
    }

    @Transactional
    @PostMapping
    public ResponseDto settingUpdateHomePage(OptionsModel model, HttpServletRequest request) {
        return ResponseDto.of(OptionsDto.toDto(this.optionsService.settingUpdateHomePage(model, request)), "Options added");
    }

}
