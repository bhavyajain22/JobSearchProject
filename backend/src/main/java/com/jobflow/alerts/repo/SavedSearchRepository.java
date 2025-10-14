package com.jobflow.alerts.repo;

import com.jobflow.alerts.model.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, String> {
    List<SavedSearch> findByContact(String contact);
    List<SavedSearch> findByPrefId(String prefId);
}
