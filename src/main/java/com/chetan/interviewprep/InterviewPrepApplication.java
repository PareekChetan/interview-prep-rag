package com.chetan.interviewprep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This annotation is what makes it "Spring Boot" - it tells Spring to:
// 1. Scan this package (and sub-packages) for components (@Service, @RestController, etc.)
// 2. Auto-configure things like the web server and database connection based on
//    what's on the classpath and in application.properties
@SpringBootApplication
public class InterviewPrepApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewPrepApplication.class, args);
    }

}
