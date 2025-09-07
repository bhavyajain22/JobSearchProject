package com.jobflow.jobs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Preferences {
    
    @JsonProperty("notifications")
    private boolean notifications;
    
    @JsonProperty("darkMode")
    private boolean darkMode;
    
    @JsonProperty("language")
    private String language;
    
    @JsonProperty("privacy")
    private String privacy;
    
    @JsonProperty("theme")
    private String theme;

    // Default constructor
    public Preferences() {
        this.notifications = true;
        this.darkMode = false;
        this.language = "en";
        this.privacy = "public";
        this.theme = "light";
    }

    // Constructor with parameters
    public Preferences(boolean notifications, boolean darkMode, String language, String privacy, String theme) {
        this.notifications = notifications;
        this.darkMode = darkMode;
        this.language = language;
        this.privacy = privacy;
        this.theme = theme;
    }

    // Getters and Setters
    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        return "Preferences{" +
                "notifications=" + notifications +
                ", darkMode=" + darkMode +
                ", language='" + language + '\'' +
                ", privacy='" + privacy + '\'' +
                ", theme='" + theme + '\'' +
                '}';
    }
} 