package com.jobtracker.job_service.service;

import com.jobtracker.job_service.model.ExperienceLevel;
import com.jobtracker.job_service.model.Job;
import com.jobtracker.job_service.model.JobStatus;
import com.jobtracker.job_service.model.JobType;
import com.jobtracker.job_service.repository.JobRepository;
import com.jobtracker.job_service.service.JobService;
import com.jobtracker.job_service.mapper.JobMapper;
import com.jobtracker.job_service.dto.JobDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobService jobService;

    private Job sampleJob;
    private JobDto sampleJobDto;

    @BeforeEach
    void setUp() {
        sampleJob = Job.builder()
                .id(1L)
                .title("Software Engineer")
                .description("Develop amazing software")
                .company("Tech Corp")
                .location("San Francisco, CA")
                .salary(120000.0)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .status(JobStatus.ACTIVE)
                .userId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleJobDto = JobDto.builder()
                .id(1L)
                .title("Software Engineer")
                .description("Develop amazing software")
                .company("Tech Corp")
                .location("San Francisco, CA")
                .salary(120000.0)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .status(JobStatus.ACTIVE)
                .userId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateJobSuccessfully() {
        // Given
        when(jobMapper.toEntity(sampleJobDto)).thenReturn(sampleJob);
        when(jobRepository.save(any(Job.class))).thenReturn(sampleJob);
        when(jobMapper.toDto(sampleJob)).thenReturn(sampleJobDto);

        // When
        JobDto createdJob = jobService.createJob(sampleJobDto);

        // Then
        assertNotNull(createdJob);
        assertEquals("Software Engineer", createdJob.getTitle());
        assertEquals("Tech Corp", createdJob.getCompany());
        verify(jobRepository).save(any(Job.class));
        verify(jobMapper).toEntity(sampleJobDto);
        verify(jobMapper).toDto(sampleJob);
    }

    @Test
    void shouldGetJobByIdSuccessfully() {
        // Given
        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(jobMapper.toDto(sampleJob)).thenReturn(sampleJobDto);

        // When
        JobDto foundJob = jobService.getJobById(1L);

        // Then
        assertNotNull(foundJob);
        assertEquals("Software Engineer", foundJob.getTitle());
        verify(jobRepository).findById(1L);
        verify(jobMapper).toDto(sampleJob);
    }

    @Test
    void shouldUpdateJobSuccessfully() {

        // Given
        Long id = 100L;

        // When
        when(jobRepository.findById(id)).thenReturn(Optional.of(sampleJob));
        when(jobMapper.toDto(sampleJob)).thenReturn(sampleJobDto);
        when(jobRepository.save(any(Job.class))).thenReturn(sampleJob);
        when(jobMapper.toDto(sampleJob)).thenReturn(sampleJobDto);

        // Then
        JobDto updatedJob = jobService.updateJob(id, sampleJobDto);
        assertNotNull(updatedJob);
        assertEquals("Software Engineer", updatedJob.getTitle());
        verify(jobRepository).findById(id);
        verify(jobMapper).toDto(sampleJob);
        verify(jobRepository).save(any(Job.class));
        verify(jobMapper).toDto(sampleJob);

    }

    @Test
    void shouldDeleteJobSuccessfully() {

        // Given
        Long id = 100L;

        // When
        when(jobRepository.findById(id)).thenReturn(Optional.of(sampleJob));
        jobService.deleteJob(id);

        // Then
        verify(jobRepository).deleteById(id);

    }

    @Test
    void shouldFindJobsWithPaginationSuccessfully() {

        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        when(jobRepository.findByStatus(JobStatus.ACTIVE, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(sampleJob)));
        Page<JobDto> jobPage = jobService.getAllActiveJobs(pageable);
        assertNotNull(jobPage);
        assertEquals(1, jobPage.getTotalElements());

        // Then
        verify(jobRepository).findByStatus(JobStatus.ACTIVE, pageable);
        verify(jobMapper).toDto(sampleJob);
    }

    @Test
    void shouldFindJobsByCompanySuccessfully() {

        // Given
        String company = "Tech Corp";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        when(jobRepository.findByCompany(company, pageable)).thenReturn(new PageImpl<>(Arrays.asList(sampleJob)));
        Page<JobDto> jobPage = jobService.findJobsByCompany(company, pageable);
        assertNotNull(jobPage);
        assertEquals(1, jobPage.getTotalElements());

        // Then
        verify(jobRepository).findByCompany(company, pageable);
        verify(jobMapper).toDto(sampleJob);
    }

    @Test
    void shouldFindJobsByTitleContainingSuccessfully() {
        // Given
        String title = "Software Engineer";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        when(jobRepository.findByTitleContaining(title, pageable)).thenReturn(new PageImpl<>(Arrays.asList(sampleJob)));
        Page<JobDto> jobPage = jobService.findByTitleContaining(title, pageable);
        assertNotNull(jobPage);
        assertEquals(1, jobPage.getTotalElements());

        // Then
        verify(jobRepository).findByTitleContaining(title, pageable);
        verify(jobMapper).toDto(sampleJob);
    }

    @Test
    void shouldFindJobsWithFiltersSuccessfully() {

        // Given
        String title = "Software Engineer";
        String company = "Tech Corp";
        String location = "San Francisco, CA";
        Double minSalary = 100000.0;
        Double maxSalary = 150000.0;
        JobStatus status = JobStatus.ACTIVE;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        when(jobRepository.findJobsWithFilters(title, company, location, minSalary, maxSalary, status, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(sampleJob)));
        Page<JobDto> jobPage = jobService.findJobsWithFilters(title, company, location, minSalary, maxSalary, status,
                pageable);
        assertNotNull(jobPage);
        assertEquals(1, jobPage.getTotalElements());

        // Then
        verify(jobRepository).findJobsWithFilters(title, company, location, minSalary, maxSalary, status, pageable);
        verify(jobMapper).toDto(sampleJob);
    }
}
