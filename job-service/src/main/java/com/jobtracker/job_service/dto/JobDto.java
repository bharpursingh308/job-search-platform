package com.jobtracker.job_service.dto;

import com.jobtracker.job_service.model.JobType;
import com.jobtracker.job_service.model.ExperienceLevel;
import com.jobtracker.job_service.model.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Company is required")
    private String company;

    private String location;

    @Positive(message = "Salary must be positive")
    private Double salary;

    private JobType jobType;

    private ExperienceLevel experienceLevel;

    @Builder.Default
    private JobStatus status = JobStatus.ACTIVE;

    @NotNull(message = "User ID is required")
    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
