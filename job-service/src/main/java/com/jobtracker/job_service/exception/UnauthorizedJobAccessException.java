package com.jobtracker.job_service.exception;

public class UnauthorizedJobAccessException extends RuntimeException {
    public UnauthorizedJobAccessException(Long jobId, Long userId) {
        super("User " + userId + " is not authorized to access job " + jobId);
    }
}
