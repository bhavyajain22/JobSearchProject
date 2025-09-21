package com.jobflow.alerts.controller;

import com.jobflow.alerts.model.SavedSearch;
import com.jobflow.alerts.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService service;

    public AlertController(AlertService service) {
        this.service = service;
    }

    @PostMapping
    public SavedSearch create(@RequestParam String email, @RequestParam String prefId) {
        return service.saveSearch(email, prefId);
    }
}
