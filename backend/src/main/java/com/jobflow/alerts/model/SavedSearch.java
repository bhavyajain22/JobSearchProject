package com.jobflow.alerts.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "saved_search")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String prefId;

    @Enumerated(EnumType.STRING)
    private AlertChannel channel;

    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertFrequency frequency = AlertFrequency.DAILY;

    private Instant createdAt = Instant.now();
    private Instant lastSentAt;

    public AlertFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(AlertFrequency frequency) {
        this.frequency = frequency;
    }

    public enum AlertFrequency {
        DAILY,
        EVERY_3_DAYS,
        WEEKLY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefId() {
        return prefId;
    }

    public void setPrefId(String prefId) {
        this.prefId = prefId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(Instant lastSentAt) {
        this.lastSentAt = lastSentAt;
    }

    public AlertChannel getChannel() {
        return channel;
    }
    public void setChannel(AlertChannel channel) {
        this.channel = channel;
    }

    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }


}
