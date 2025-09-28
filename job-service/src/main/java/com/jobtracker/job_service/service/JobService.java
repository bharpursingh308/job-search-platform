package com.jobtracker.job_service.service;

import com.jobtracker.job_service.dto.JobDto;
import com.jobtracker.job_service.mapper.JobMapper;
import com.jobtracker.job_service.model.Job;
import com.jobtracker.job_service.model.JobStatus;
import com.jobtracker.job_service.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.jobtracker.job_service.dto.JobSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    public JobDto createJob(JobDto jobDto) {
        log.info("Creating new job with title: {}", jobDto.getTitle());

        Job job = jobMapper.toEntity(jobDto);
        Job savedJob = jobRepository.save(job);

        log.info("Job created successfully with id: {}", savedJob.getId());
        return jobMapper.toDto(savedJob);
    }

    @Transactional(readOnly = true)
    public JobDto getJobById(Long jobId) {
        log.info("Fetching job with id: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Job not found with id: " + jobId));

        return jobMapper.toDto(job);
    }

    public JobDto updateJob(Long jobId, JobDto jobDto, Long userId) {
        log.info("Updating job with id: {} by user: {}", jobId, userId);

        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Job not found with id: " + jobId));

        // Check if the user owns this job
        if (!existingJob.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User " + userId + " is not authorized to access job " + jobId);
        }

        // Update only non-null fields
        jobMapper.updateEntityFromDto(jobDto, existingJob);

        Job updatedJob = jobRepository.save(existingJob);
        log.info("Job updated successfully with id: {}", updatedJob.getId());

        return jobMapper.toDto(updatedJob);
    }

    public JobDto updateJob(Long jobId, JobDto jobDto) {
        return updateJob(jobId, jobDto, jobDto.getUserId());
    }

    public void deleteJob(Long jobId, Long userId) {
        log.info("Deleting job with id: {} by user: {}", jobId, userId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Job not found with id: " + jobId));

        // Check if the user owns this job
        if (!job.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User " + userId + " is not authorized to access job " + jobId);
        }

        jobRepository.deleteById(jobId);
        log.info("Job deleted successfully with id: {}", jobId);
    }

    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Job not found with id: " + jobId));
        deleteJob(jobId, job.getUserId());
    }

    @Transactional(readOnly = true)
    public List<JobDto> getJobsByUserId(Long userId) {
        log.info("Fetching jobs for user: {}", userId);

        List<Job> jobs = jobRepository.findByUserId(userId);

        return jobs.stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<JobDto> getAllActiveJobs(Pageable pageable) {
        log.info("Fetching all active jobs with pagination: {}", pageable);

        Page<Job> jobPage = jobRepository.findByStatus(JobStatus.ACTIVE, pageable);

        return jobPage.map(jobMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<JobDto> searchJobs(JobSearchRequest searchRequest) {
        log.info("Searching jobs with criteria: {}", searchRequest);

        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize());

        Page<Job> jobPage = jobRepository.findJobsWithFilters(
                searchRequest.getTitle(),
                searchRequest.getCompany(),
                searchRequest.getLocation(),
                searchRequest.getMinSalary(),
                searchRequest.getMaxSalary(),
                JobStatus.ACTIVE,
                pageable);

        return jobPage.map(jobMapper::toDto);
    }

    public JobDto changeJobStatus(Long jobId, JobStatus newStatus, Long userId) {
        log.info("Changing status of job {} to {} by user {}", jobId, newStatus, userId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Job not found with id: " + jobId));

        // Check if the user owns this job
        if (!job.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User " + userId + " is not authorized to access job " + jobId);
        }

        job.setStatus(newStatus);
        Job updatedJob = jobRepository.save(job);

        log.info("Job status changed successfully for job: {}", jobId);
        return jobMapper.toDto(updatedJob);
    }

    @Transactional(readOnly = true)
    public List<JobDto> getJobsByCompany(String company) {
        log.info("Fetching jobs for company: {}", company);

        Page<Job> jobPage = jobRepository.findByCompany(company, PageRequest.of(0, Integer.MAX_VALUE));

        return jobPage.getContent().stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<JobDto> findJobsByCompany(String company, Pageable pageable) {
        log.info("Fetching jobs for company: {} with pageable: {}", company, pageable);

        Page<Job> jobPage = jobRepository.findByCompany(company, pageable);

        return jobPage.map(jobMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<JobDto> getJobsBySalaryRange(Double minSalary, Double maxSalary) {
        log.info("Fetching jobs with salary range: {} - {}", minSalary, maxSalary);

        List<Job> jobs = jobRepository.findBySalaryBetween(minSalary, maxSalary);

        return jobs.stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<JobDto> findByTitleContaining(String title, Pageable pageable) {
        log.info("Fetching jobs with title containing: {} with pageable: {}", title, pageable);

        Page<Job> jobPage = jobRepository.findByTitleContaining(title, pageable);

        return jobPage.map(jobMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<JobDto> findJobsWithFilters(String title,
            String company,
            String location,
            Double minSalary,
            Double maxSalary,
            JobStatus status,
            Pageable pageable) {
        log.info(
                "Fetching jobs with filters title={}, company={}, location={}, minSalary={}, maxSalary={}, status={}, pageable={}",
                title, company, location, minSalary, maxSalary, status, pageable);

        Page<Job> jobPage = jobRepository.findJobsWithFilters(title, company, location, minSalary, maxSalary, status,
                pageable);

        return jobPage.map(jobMapper::toDto);
    }
}
