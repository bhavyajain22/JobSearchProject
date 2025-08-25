package com.jobflow.preferences.controller;

import com.jobflow.preferences.dto.PreferenceDto;
import com.jobflow.preferences.service.PreferenceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @PostMapping
    public Map<String, String> save(@RequestBody PreferenceDto dto) {
        String id = preferenceService.save(dto);
        return Map.of("prefId", id);
    }
}
