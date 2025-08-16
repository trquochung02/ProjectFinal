package com.team3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InterviewManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewManagementApplication.class, args);
    }

}
