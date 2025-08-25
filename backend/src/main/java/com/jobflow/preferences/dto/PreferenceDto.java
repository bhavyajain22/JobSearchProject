package com.jobflow.preferences.dto;

public class PreferenceDto {
    private String jobTitle;
    private String experience;
    private String location;
    private boolean remoteOnly;

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isRemoteOnly() {
        return remoteOnly;
    }

    public void setRemoteOnly(boolean remoteOnly) {
        this.remoteOnly = remoteOnly;
    }

    public String getJobTitle() {
        return jobTitle;
    }


    // getters/setters
}
