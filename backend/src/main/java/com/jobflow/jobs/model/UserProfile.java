package com.jobflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UserProfile {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("avatar")
    private String avatar;
    
    @JsonProperty("skills")
    private List<String> skills;
    
    @JsonProperty("experience")
    private String experience;
    
    @JsonProperty("location")
    private String location;

    // Default constructor
    public UserProfile() {}

    // Constructor with parameters
    public UserProfile(Long id, String name, String email, String avatar, List<String> skills, String experience, String location) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.skills = skills;
        this.experience = experience;
        this.location = location;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", skills=" + skills +
                ", experience='" + experience + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
} 