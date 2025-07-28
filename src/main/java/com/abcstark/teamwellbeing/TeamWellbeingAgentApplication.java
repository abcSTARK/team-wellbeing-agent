package com.abcstark.teamwellbeing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class for the Team Wellbeing Agent.
 * 
 * This application collects and monitors team health data from various platforms
 * including Slack, GitHub, and Jira to provide insights into team wellbeing.
 */
@SpringBootApplication
@EnableScheduling
public class TeamWellbeingAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamWellbeingAgentApplication.class, args);
    }
}