package com.example.job_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class JobServiceApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().systemProperties().load();
		System.out.println("****************************************************************");
		System.out.println(dotenv.get("DB_URL"));
		System.out.println(dotenv.get("DB_USERNAME"));
		System.out.println(dotenv.get("DB_PASSWORD"));
		SpringApplication.run(JobServiceApplication.class, args);
	}

}
