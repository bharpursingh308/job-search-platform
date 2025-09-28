package com.jobtracker.job_service.controller;

import com.jobtracker.job_service.dto.JobSearchRequest;
import com.jobtracker.job_service.model.JobStatus;
import com.jobtracker.job_service.model.JobType;
import com.jobtracker.job_service.model.ExperienceLevel;

import com.jobtracker.job_service.dto.JobDto;
import com.jobtracker.job_service.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
public class JobControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JobService jobService;

        @Autowired
        private ObjectMapper objectMapper;

        private JobDto sampleJobDto;

        @BeforeEach
        void setUp() {
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
        void shouldCreateJobSuccessfully() throws Exception {
                // Given
                JobDto createRequest = JobDto.builder()
                                .title("Software Engineer")
                                .description("Develop amazing software")
                                .company("Tech Corp")
                                .location("San Francisco, CA")
                                .salary(120000.0)
                                .jobType(JobType.FULL_TIME)
                                .experienceLevel(ExperienceLevel.SENIOR)
                                .userId(1L)
                                .build();

                when(jobService.createJob(any(JobDto.class))).thenReturn(sampleJobDto);

                // When & Then
                mockMvc.perform(post("/api/v1/jobs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Software Engineer"))
                                .andExpect(jsonPath("$.company").value("Tech Corp"))
                                .andExpect(jsonPath("$.salary").value(120000.0));
        }

        @Test
        void shouldReturnBadRequestWhenCreatingJobWithInvalidData() throws Exception {
                // Given
                JobDto invalidRequest = JobDto.builder()
                                .title("") // Invalid: empty title
                                .description("Develop amazing software")
                                .company("Tech Corp")
                                .build();

                // When & Then
                mockMvc.perform(post("/api/v1/jobs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldGetJobByIdSuccessfully() throws Exception {
                // Given
                when(jobService.getJobById(1L)).thenReturn(sampleJobDto);

                // When & Then
                mockMvc.perform(get("/api/v1/jobs/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Software Engineer"))
                                .andExpect(jsonPath("$.company").value("Tech Corp"));
        }

        @Test
        void shouldReturnNotFoundWhenJobDoesNotExist() throws Exception {
                // Given
                when(jobService.getJobById(999L))
                                .thenThrow(new java.util.NoSuchElementException("Job not found with id: 999"));

                // When & Then
                mockMvc.perform(get("/api/v1/jobs/{id}", 999L))
                                .andExpect(status().isNotFound());
        }

        @Test
        void shouldUpdateJobSuccessfully() throws Exception {
                // Given
                JobDto updateRequest = JobDto.builder()
                                .title("Senior Software Engineer")
                                .description("Develop amazing software")
                                .company("Tech Corp")
                                .location("San Francisco, CA")
                                .salary(140000.0)
                                .jobType(JobType.FULL_TIME)
                                .experienceLevel(ExperienceLevel.SENIOR)
                                .userId(1L)
                                .build();

                JobDto updatedJob = JobDto.builder()
                                .id(1L)
                                .title("Senior Software Engineer")
                                .description("Develop amazing software")
                                .company("Tech Corp")
                                .location("San Francisco, CA")
                                .salary(140000.0)
                                .jobType(JobType.FULL_TIME)
                                .experienceLevel(ExperienceLevel.SENIOR)
                                .userId(1L)
                                .build();

                when(jobService.updateJob(eq(1L), any(JobDto.class), eq(1L))).thenReturn(updatedJob);

                // When & Then
                mockMvc.perform(put("/api/v1/jobs/{id}", 1L)
                                .header("User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Senior Software Engineer"))
                                .andExpect(jsonPath("$.salary").value(140000.0));
        }

        @Test
        void shouldReturnUnauthorizedWhenUpdatingOtherUsersJob() throws Exception {
                // Given
                JobDto updateRequest = JobDto.builder()
                                .title("Updated Title")
                                .description("Updated description")
                                .company("Updated Company")
                                .userId(2L)
                                .build();

                when(jobService.updateJob(eq(1L), any(JobDto.class), eq(2L)))
                                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN,
                                                "User 2 is not authorized to access job 1"));

                // When & Then
                mockMvc.perform(put("/api/v1/jobs/{id}", 1L)
                                .header("User-Id", "2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isForbidden());
        }

        @Test
        void shouldDeleteJobSuccessfully() throws Exception {
                // When & Then
                mockMvc.perform(delete("/api/v1/jobs/{id}", 1L)
                                .header("User-Id", "1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnUnauthorizedWhenDeletingOtherUsersJob() throws Exception {
                // Given
                doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "User 2 is not authorized to access job 1"))
                                .when(jobService).deleteJob(1L, 2L);

                // When & Then
                mockMvc.perform(delete("/api/v1/jobs/{id}", 1L)
                                .header("User-Id", "2"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void shouldGetJobsByUserIdSuccessfully() throws Exception {
                // Given
                List<JobDto> userJobs = Collections.singletonList(sampleJobDto);
                when(jobService.getJobsByUserId(1L)).thenReturn(userJobs);

                // When & Then
                mockMvc.perform(get("/api/v1/jobs/user/{userId}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].title").value("Software Engineer"));
        }

        @Test
        void shouldSearchJobsSuccessfully() throws Exception {
                // Given
                Page<JobDto> jobPage = new PageImpl<>(Collections.singletonList(sampleJobDto));
                when(jobService.searchJobs(any(JobSearchRequest.class))).thenReturn(jobPage);

                // When & Then
                mockMvc.perform(get("/api/v1/jobs/search")
                                .param("title", "Software")
                                .param("company", "Tech")
                                .param("location", "San Francisco")
                                .param("minSalary", "100000")
                                .param("maxSalary", "150000")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.content[0].title").value("Software Engineer"));
        }

        @Test
        void shouldGetAllActiveJobsSuccessfully() throws Exception {
                // Given
                Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
                Page<JobDto> jobPage = new PageImpl<>(Collections.singletonList(sampleJobDto), pageable, 1);
                when(jobService.getAllActiveJobs(any(Pageable.class))).thenReturn(jobPage);

                // When & Then
                mockMvc.perform(get("/api/v1/jobs")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.content[0].title").value("Software Engineer"));
        }

        @Test
        void shouldChangeJobStatusSuccessfully() throws Exception {
                // Given
                JobDto updatedJob = JobDto.builder()
                                .id(1L)
                                .title("Software Engineer")
                                .status(JobStatus.INACTIVE)
                                .build();

                when(jobService.changeJobStatus(1L, JobStatus.INACTIVE, 1L)).thenReturn(updatedJob);

                // When & Then
                mockMvc.perform(patch("/api/v1/jobs/{id}/status", 1L)
                                .header("User-Id", "1")
                                .param("status", "INACTIVE"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.status").value("INACTIVE"));
        }

        @Test
        void shouldReturnBadRequestWhenMissingUserIdHeader() throws Exception {
                // Given
                JobDto updateRequest = JobDto.builder()
                                .title("Updated Title")
                                .description("Updated description")
                                .company("Updated Company")
                                .userId(1L)
                                .build();

                // When & Then - Missing User-Id header should cause 400 Bad Request
                mockMvc.perform(put("/api/v1/jobs/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isBadRequest());
        }
}
