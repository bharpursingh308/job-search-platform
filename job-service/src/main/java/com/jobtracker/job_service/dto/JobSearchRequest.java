package com.jobtracker.job_service.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchRequest {
    private String title;
    private String company;
    private String location;
    private Double minSalary;
    private Double maxSalary;
    private int page;
    private int size;
}
