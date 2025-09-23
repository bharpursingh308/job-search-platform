package com.jobtracker.job_service.repository;

import com.jobtracker.job_service.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByUserId(Long userId);

    List<Job> findByUserIdAndStatus(Long userId, JobStatus status);

    List<Job> findByStatus(JobStatus status);

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    List<Job> findByCompany(String company);

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findBySalaryBetween(Double minSalary, Double maxSalary);

    @Query("SELECT j FROM Job j WHERE " +
            "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:company IS NULL OR LOWER(j.company) LIKE LOWER(CONCAT('%', :company, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:minSalary IS NULL OR j.salary >= :minSalary) AND " +
            "(:maxSalary IS NULL OR j.salary <= :maxSalary) AND " +
            "j.status = :status")
    Page<Job> findJobsWithFilters(
            @Param("title") String title,
            @Param("company") String company,
            @Param("location") String location,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            @Param("status") JobStatus status,
            Pageable pageable
    );
}
