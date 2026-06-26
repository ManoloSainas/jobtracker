package com.manolo.jobtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.manolo.jobtracker")
@EnableJpaRepositories(basePackages = "com.manolo.jobtracker.repository")
@EntityScan(basePackages = "com.manolo.jobtracker.model")
public class JobtrackerApplication {

	public static void main(String[] args) {

		try{

			SpringApplication.run(JobtrackerApplication.class, args);
		}catch(Exception e){

			e.printStackTrace();
		}
	}

}
