package com.tu00128.taskmgmt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class TaskManagementApplication {

    private static final Logger log = LoggerFactory.getLogger(TaskManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TaskManagementApplication.class);
        Environment env = app.run(args).getEnvironment();

        log.info("=========================================");
        log.info("Task Management Application Started!");
        log.info("Active Profile: {}", env.getProperty("spring.profiles.active"));
        log.info("Server Port: {}", env.getProperty("server.port"));
        log.info("Database URL: {}", env.getProperty("spring.datasource.url"));
        log.info("=========================================");
        log.info("Swagger UI: http://localhost:{}/swagger-ui.html", env.getProperty("server.port"));
        log.info("=========================================");
    }
}