package com.jobtracker.job_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class JobServiceApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().systemProperties().load();

        SpringApplication.run(JobServiceApplication.class, args);
    }

}