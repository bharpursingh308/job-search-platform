package com.jobtracker.job_service.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JobTest {

    //private ValidatorFactory factory;
    @Autowired
    private Validator validator;

    //@BeforeEach
    //void setUp() {
    //    factory = Validation.buildDefaultValidatorFactory();
    //    validator = factory.getValidator();
    //}

    //@AfterEach
    //void tearDown() {
    //    factory.close();
    //}

    @Test
    void validatorShouldBeInjected() {
        assertNotNull(validator, "Validator should not be null");
    }

    @Test
    void shouldCreateJobWithValidData() {
        // Given
        Job job = Job.builder()
                .title("Software Engineer")
                .description("Develop amazing software")
                .company("Tech Corp")
                .location("San Francisco, CA")
                .salary(120000.0)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .userId(1L)
                .build();

        // When & Then
        assertNotNull(job);
        assertEquals("Software Engineer", job.getTitle());
        assertEquals("Develop amazing software", job.getDescription());
        assertEquals("Tech Corp", job.getCompany());
        assertEquals("San Francisco, CA", job.getLocation());
        assertEquals(120000.0, job.getSalary());
        assertEquals(JobType.FULL_TIME, job.getJobType());
        assertEquals(ExperienceLevel.SENIOR, job.getExperienceLevel());
        assertEquals(1L, job.getUserId());
    }

    @Test
    void shouldFailValidationWhenTitleIsEmpty() {
        // Given
        Job job = Job.builder()
                .title("")
                .description("Develop amazing software")
                .company("Tech Corp")
                .location("San Francisco, CA")
                .userId(1L)
                .build();

        // When
        Set<ConstraintViolation<Job>> violations = validator.validate(job);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldFailValidationWhenDescriptionIsEmpty() {
        // Given
        Job job = Job.builder()
                .title("Software Engineer")
                .description("")
                .company("Tech Corp")
                .location("San Francisco, CA")
                .userId(1L)
                .build();

        // When
        Set<ConstraintViolation<Job>> violations = validator.validate(job);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldFailValidationWhenCompanyIsEmpty() {
        // Given
        Job job = Job.builder()
                .title("Software Engineer")
                .description("Develop amazing software")
                .company("")
                .location("San Francisco, CA")
                .userId(1L)
                .build();

        // When
        Set<ConstraintViolation<Job>> violations = validator.validate(job);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("company")));
    }

    @Test
    void shouldFailValidationWhenUserIdIsNull() {
        // Given
        Job job = Job.builder()
                .title("Software Engineer")
                .description("Develop amazing software")
                .company("Tech Corp")
                .location("San Francisco, CA")
                .userId(null)
                .build();

        // When
        Set<ConstraintViolation<Job>> violations = validator.validate(job);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
    }

    @Test
    void shouldSetCreatedAtAndUpdatedAtAutomatically() {
        // Given
        Job job = new Job();
        job.onCreate();
        job.onUpdate();

        // When & Then
        assertNotNull(job.getCreatedAt());
        assertNotNull(job.getUpdatedAt());
        assertTrue(job.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(job.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void shouldUpdateUpdatedAtWhenModified() throws InterruptedException {
        // Given
        Job job = new Job();
        job.onCreate();
        LocalDateTime originalUpdatedAt = job.getUpdatedAt();

        // Wait a bit to ensure timestamp difference
        Thread.sleep(10);

        // When
        job.onUpdate();

        // Then
        assertTrue(job.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldHaveDefaultStatusAsActive() {
        // Given & When
        Job job = new Job();

        // Then
        assertEquals(JobStatus.ACTIVE, job.getStatus());
    }

}
