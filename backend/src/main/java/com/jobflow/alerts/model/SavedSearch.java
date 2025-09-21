// backend/src/main/java/com/jobflow/alerts/model/SavedSearch.java
package com.jobflow.alerts.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SavedSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userEmail;      // where alerts are sent
    private String prefId;         // link to a Preference
    private Instant createdAt;
    private Instant lastRun;       // last time we sent alerts
}
