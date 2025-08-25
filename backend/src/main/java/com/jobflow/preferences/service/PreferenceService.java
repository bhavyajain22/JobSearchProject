package com.jobflow.preferences.service;

import com.jobflow.preferences.dto.PreferenceDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PreferenceService {
    private final Map<String, PreferenceDto> store = new ConcurrentHashMap<>();

    public String save(PreferenceDto dto) {
        String id = UUID.randomUUID().toString();
        store.put(id, dto);
        return id;
    }

    public PreferenceDto get(String id) {
        return store.get(id);
    }
}
