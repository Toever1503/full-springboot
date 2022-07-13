package com.webs;

import com.dtos.OptionsDto;
import com.dtos.ResponseDto;
import com.models.OptionsModel;
import com.services.IOptionsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

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
        return ResponseDto.of(this.optionsService.getOptionByKey(key), "Get option by key: " + key);
    }

    @Transactional
    @GetMapping("get-options-by-keys")
    public ResponseDto getOptionsByKeys(@RequestParam List<String> keys) {
        return ResponseDto.of(this.optionsService.getOptionsByKeys(keys), "Get options by keys: " + keys);
    }

    @Transactional
    @PostMapping
    public ResponseDto settingUpdateHomePage(OptionsModel model, HttpServletRequest request) {
        return ResponseDto.of(OptionsDto.toDto(this.optionsService.settingUpdateHomePage(model, request)), "Options added");
    }

}
