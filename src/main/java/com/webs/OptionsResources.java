package com.webs;

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
    public ResponseDto addOptions(OptionsModel model, HttpServletRequest request) {
        return ResponseDto.of(this.optionsService.addOptions(model, request), "Options added");
    }

//    @Transactional
//    @PatchMapping("/{id}")
//    public ResponseDto updateOptions(@PathVariable Long id, @RequestBody OptionsModel model, HttpServletRequest request) {
//        return ResponseDto.of(this.optionsService.updateOptions(id, model, request), "Options updated");
//    }

}
