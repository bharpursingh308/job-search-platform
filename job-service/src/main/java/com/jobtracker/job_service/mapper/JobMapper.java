package com.jobtracker.job_service.mapper;

import com.jobtracker.job_service.dto.JobDto;
import com.jobtracker.job_service.model.Job;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {
    public JobDto toDto(Job job) {
        if (job == null) {
            return null;
        }

        return JobDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .company(job.getCompany())
                .location(job.getLocation())
                .salary(job.getSalary())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .status(job.getStatus())
                .userId(job.getUserId())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    public Job toEntity(JobDto jobDto) {
        if (jobDto == null) {
            return null;
        }

        return Job.builder()
                .id(jobDto.getId())
                .title(jobDto.getTitle())
                .description(jobDto.getDescription())
                .company(jobDto.getCompany())
                .location(jobDto.getLocation())
                .salary(jobDto.getSalary())
                .jobType(jobDto.getJobType())
                .experienceLevel(jobDto.getExperienceLevel())
                .status(jobDto.getStatus())
                .userId(jobDto.getUserId())
                .build();
    }

    public void updateEntityFromDto(JobDto dto, Job entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getCompany() != null) {
            entity.setCompany(dto.getCompany());
        }
        if (dto.getLocation() != null) {
            entity.setLocation(dto.getLocation());
        }
        if (dto.getSalary() != null) {
            entity.setSalary(dto.getSalary());
        }
        if (dto.getJobType() != null) {
            entity.setJobType(dto.getJobType());
        }
        if (dto.getExperienceLevel() != null) {
            entity.setExperienceLevel(dto.getExperienceLevel());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }
}
