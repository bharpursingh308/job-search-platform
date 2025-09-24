package com.jobtracker.job_service.dto;

import lombok.Data;

@Data
public class JobSearchRequest {
    private String title;
    private String company;
    private String location;
    private Double minSalary;
    private Double maxSalary;
    private int page;
    private int size;
}
