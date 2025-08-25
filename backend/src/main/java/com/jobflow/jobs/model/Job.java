package com.jobflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Job {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("company")
    private String company;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("salary")
    private String salary;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("posted")
    private String posted;

    // Default constructor
    public Job() {}

    // Constructor with parameters
    public Job(Long id, String title, String company, String location, String salary, String type, String posted) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.type = type;
        this.posted = posted;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosted() {
        return posted;
    }

    public void setPosted(String posted) {
        this.posted = posted;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", salary='" + salary + '\'' +
                ", type='" + type + '\'' +
                ", posted='" + posted + '\'' +
                '}';
    }
} 