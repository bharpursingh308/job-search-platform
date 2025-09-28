package com.jobtracker.job_service.controller;

import com.jobtracker.job_service.dto.JobDto;
import com.jobtracker.job_service.dto.JobSearchRequest;
import com.jobtracker.job_service.model.JobStatus;
import com.jobtracker.job_service.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobDto> createJob(@Valid @RequestBody JobDto jobDto) {
        log.info("Creating job with title: {}", jobDto.getTitle());
        JobDto createdJob = jobService.createJob(jobDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDto> getJobById(@PathVariable Long id) {
        log.info("Fetching job with id: {}", id);
        JobDto job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDto> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobDto jobDto,
            @RequestHeader("User-Id") Long userId) {
        log.info("Updating job {} by user {}", id, userId);
        JobDto updatedJob = jobService.updateJob(id, jobDto, userId);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        log.info("Deleting job {} by user {}", id, userId);
        jobService.deleteJob(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobDto>> getJobsByUserId(@PathVariable Long userId) {
        log.info("Fetching jobs for user: {}", userId);
        List<JobDto> jobs = jobService.getJobsByUserId(userId);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping
    public ResponseEntity<Page<JobDto>> getAllActiveJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        log.info("Fetching active jobs with pagination: page={}, size={}", page, size);
        Page<JobDto> jobs = jobService.getAllActiveJobs(pageable);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobDto>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        JobSearchRequest searchRequest = JobSearchRequest.builder()
                .title(title)
                .company(company)
                .location(location)
                .minSalary(minSalary)
                .maxSalary(maxSalary)
                .page(page)
                .size(size)
                .build();

        log.info("Searching jobs with criteria: {}", searchRequest);
        Page<JobDto> jobs = jobService.searchJobs(searchRequest);
        return ResponseEntity.ok(jobs);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobDto> changeJobStatus(
            @PathVariable Long id,
            @RequestParam JobStatus status,
            @RequestHeader("User-Id") Long userId) {
        log.info("Changing status of job {} to {} by user {}", id, status, userId);
        JobDto updatedJob = jobService.changeJobStatus(id, status, userId);
        return ResponseEntity.ok(updatedJob);
    }

    @GetMapping("/company/{company}")
    public ResponseEntity<List<JobDto>> getJobsByCompany(@PathVariable String company) {
        log.info("Fetching jobs for company: {}", company);
        List<JobDto> jobs = jobService.getJobsByCompany(company);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/salary-range")
    public ResponseEntity<List<JobDto>> getJobsBySalaryRange(
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary) {
        log.info("Fetching jobs with salary range: {} - {}", minSalary, maxSalary);
        List<JobDto> jobs = jobService.getJobsBySalaryRange(minSalary, maxSalary);
        return ResponseEntity.ok(jobs);
    }
}
