package com.jobtracker.job_service.repository;

import com.jobtracker.job_service.model.ExperienceLevel;
import com.jobtracker.job_service.model.Job;
import com.jobtracker.job_service.model.JobType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
public class JobRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JobRepository jobRepository;

    private Job sampleJob1;
    private Job sampleJob2;
    private Job inactiveJob;

    @BeforeEach
    void setUp() {
        sampleJob1 = Job.builder()
                .title("Software Engineer")
                .description("Develop amazing software")
                .company("Tech Corp")
                .location("San Francisco, CA")
                .salary(120000.0)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .status(JobStatus.ACTIVE)
                .userId(1L)
                .build();

        sampleJob2 = Job.builder()
                .title("Data Scientist")
                .description("Analyze data and build models")
                .company("Data Inc")
                .location("New York, NY")
                .salary(110000.0)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.MID_LEVEL)
                .status(JobStatus.ACTIVE)
                .userId(2L)
                .build();

        inactiveJob = Job.builder()
                .title("Project Manager")
                .description("Manage projects")
                .company("Mgmt Corp")
                .location("Chicago, IL")
                .salary(90000.0)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .status(JobStatus.INACTIVE)
                .userId(1L)
                .build();
    }

    @Test
    void shouldSaveJob() {
        // When
        Job savedJob = jobRepository.save(sampleJob1);

        // Then
        assertNotNull(savedJob.getId());
        assertEquals("Software Engineer", savedJob.getTitle());
        assertEquals("Tech Corp", savedJob.getCompany());
        assertNotNull(savedJob.getCreatedAt());
        assertNotNull(savedJob.getUpdatedAt());
    }

    @Test
    void shouldFindJobById() {
        // Given
        Job savedJob = entityManager.persistAndFlush(sampleJob1);

        // When
        Optional<Job> foundJob = jobRepository.findById(savedJob.getId());

        // Then
        assertTrue(foundJob.isPresent());
        assertEquals("Software Engineer", foundJob.get().getTitle());
        assertEquals("Tech Corp", foundJob.get().getCompany());
    }

    @Test
    void shouldFindJobsByUserId() {
        // Given
        entityManager.persistAndFlush(sampleJob1);
        entityManager.persistAndFlush(inactiveJob);
        entityManager.persistAndFlush(sampleJob2);

        // When
        List<Job> userJobs = jobRepository.findByUserId(1L);

        // Then
        assertEquals(2, userJobs.size());
        assertTrue(userJobs.stream().allMatch(job -> job.getUserId().equals(1L)));
    }

    @Test
    void shouldFindActiveJobsByUserId() {
        // Given
        entityManager.persistAndFlush(sampleJob1);
        entityManager.persistAndFlush(inactiveJob);
        entityManager.persistAndFlush(sampleJob2);

        // When
        List<Job> activeUserJobs = jobRepository.findByUserIdAndStatus(1L, JobStatus.ACTIVE);

        // Then
        assertEquals(1, activeUserJobs.size());
        assertEquals("Software Engineer", activeUserJobs.get(0).getTitle());
        assertEquals(JobStatus.ACTIVE, activeUserJobs.get(0).getStatus());
    }

    @Test
    void shouldFindJobsByStatus() {
        // Given
        entityManager.persistAndFlush(sampleJob1);
        entityManager.persistAndFlush(sampleJob2);
        entityManager.persistAndFlush(inactiveJob);

        // When
        List<Job> activeJobs = jobRepository.findByStatus(JobStatus.ACTIVE);

        // Then
        assertEquals(2, activeJobs.size());
        assertTrue(activeJobs.stream().allMatch(job -> job.getStatus() == JobStatus.ACTIVE));
    }

    @Test
    void shouldFindJobsByCompany() {
        // Given
        entityManager.persistAndFlush(sampleJob1);
        entityManager.persistAndFlush(sampleJob2);

        // When
        List<Job> techCorpJobs = jobRepository.findByCompany("Tech Corp");

        // Then
        assertEquals(1, techCorpJobs.size());
        assertEquals("Tech Corp", techCorpJobs.get(0).getCompany());
    }

    @Test
    void shouldFindJobsByTitleContaining() {
        // Given
        entityManager.persistAndFlush(sampleJob1);
        entityManager.persistAndFlush(sampleJob2);

        // When
        List<Job> engineerJobs = jobRepository.findByTitleContainingIgnoreCase("engineer");

        // Then
        assertEquals(1, engineerJobs.size());
        assertEquals("Software Engineer", engineerJobs.get(0).getTitle());
    }

    @Test
    void shouldFindJobsByLocationContaining() {
        // Given
        entityManager.persistAndFlush(sampleJob1);
        entityManager.persistAndFlush(sampleJob2);

        // When
        List<Job> californiaJobs = jobRepository.findByLocationContainingIgnoreCase("california");

        // Then
        assertEquals(1, californiaJobs.size());
        assertEquals("San Francisco, CA", californiaJobs.get(0).getLocation());
    }

    @Test
    void shouldFindJobsBySalaryRange() {
        // Given
        entityManager.persistAndFlush(sampleJob1); // 120000
        entityManager.persistAndFlush(sampleJob2); // 110000
        entityManager.persistAndFlush(inactiveJob); // 90000

        // When
        List<Job> highSalaryJobs = jobRepository.findBySalaryBetween(100000.0, 130000.0);

        // Then
        assertEquals(2, highSalaryJobs.size());
        assertTrue(highSalaryJobs.stream()
                .allMatch(job -> job.getSalary() >= 100000.0 && job.getSalary() <= 130000.0));
    }

    @Test
    void shouldFindJobsWithPagination() {
        // Given
        entityManager.persistAndFlush(sampleJob1);
        entityManager.persistAndFlush(sampleJob2);
        entityManager.persistAndFlush(inactiveJob);

        // When
        Pageable pageable = PageRequest.of(0, 2);
        Page<Job> jobPage = jobRepository.findByStatus(JobStatus.ACTIVE, pageable);

        // Then
        assertEquals(2, jobPage.getContent().size());
        assertEquals(2, jobPage.getTotalElements());
        assertEquals(1, jobPage.getTotalPages());
    }

    @Test
    void shouldDeleteJob() {
        // Given
        Job savedJob = entityManager.persistAndFlush(sampleJob1);
        Long jobId = savedJob.getId();

        // When
        jobRepository.deleteById(jobId);
        entityManager.flush();

        // Then
        Optional<Job> deletedJob = jobRepository.findById(jobId);
        assertFalse(deletedJob.isPresent());
    }

    @Test
    void shouldUpdateJob() {
        // Given
        Job savedJob = entityManager.persistAndFlush(sampleJob1);

        // When
        savedJob.setTitle("Senior Software Engineer");
        savedJob.setSalary(140000.0);
        Job updatedJob = jobRepository.save(savedJob);

        // Then
        assertEquals("Senior Software Engineer", updatedJob.getTitle());
        assertEquals(140000.0, updatedJob.getSalary());
        assertNotNull(updatedJob.getUpdatedAt());
    }
}
