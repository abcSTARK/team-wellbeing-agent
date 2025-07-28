package com.abcstark.teamwellbeing.scheduled;

import com.abcstark.teamwellbeing.config.SchedulingProperties;
import com.abcstark.teamwellbeing.model.GitHubIssue;
import com.abcstark.teamwellbeing.model.JiraIssue;
import com.abcstark.teamwellbeing.model.SlackMessage;
import com.abcstark.teamwellbeing.service.GitHubService;
import com.abcstark.teamwellbeing.service.JiraService;
import com.abcstark.teamwellbeing.service.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task component that periodically collects data from all integrations
 * and processes it for team wellbeing analysis.
 */
@Component
public class DataCollectionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DataCollectionScheduler.class);

    private final SlackService slackService;
    private final GitHubService gitHubService;
    private final JiraService jiraService;
    private final SchedulingProperties schedulingProperties;

    @Autowired
    public DataCollectionScheduler(SlackService slackService, 
                                 GitHubService gitHubService,
                                 JiraService jiraService,
                                 SchedulingProperties schedulingProperties) {
        this.slackService = slackService;
        this.gitHubService = gitHubService;
        this.jiraService = jiraService;
        this.schedulingProperties = schedulingProperties;
    }

    /**
     * Scheduled task that collects data from all configured integrations.
     * Runs at a configurable interval defined in application.yml.
     */
    @Scheduled(fixedDelayString = "#{@schedulingProperties.dataCollectionInterval}", 
               initialDelayString = "#{@schedulingProperties.initialDelay}")
    public void collectTeamWellbeingData() {
        logger.info("Starting scheduled data collection at {}", LocalDateTime.now());
        
        try {
            // Collect Slack data
            collectSlackData();
            
            // Collect GitHub data
            collectGitHubData();
            
            // Collect Jira data
            collectJiraData();
            
            logger.info("Completed scheduled data collection at {}", LocalDateTime.now());
            
        } catch (Exception e) {
            logger.error("Error during scheduled data collection", e);
        }
    }

    /**
     * Collects data from Slack.
     */
    private void collectSlackData() {
        try {
            logger.info("Collecting Slack data...");
            
            List<SlackMessage> messages = slackService.getRecentMessages();
            logger.info("Collected {} Slack messages", messages.size());
            
            // TODO: Persist messages to database
            // persistenceService.saveSlackMessages(messages);
            
            // Log sample data for demonstration
            if (!messages.isEmpty()) {
                SlackMessage sample = messages.get(0);
                logger.info("Sample Slack message: {}", sample);
            }
            
        } catch (Exception e) {
            logger.error("Error collecting Slack data", e);
        }
    }

    /**
     * Collects data from GitHub.
     */
    private void collectGitHubData() {
        try {
            logger.info("Collecting GitHub data...");
            
            List<GitHubIssue> issues = gitHubService.getRecentIssues();
            logger.info("Collected {} GitHub issues", issues.size());
            
            // TODO: Persist issues to database
            // persistenceService.saveGitHubIssues(issues);
            
            // Log sample data for demonstration
            if (!issues.isEmpty()) {
                GitHubIssue sample = issues.get(0);
                logger.info("Sample GitHub issue: {}", sample);
            }
            
            // Get and log repository statistics
            String stats = gitHubService.getRepositoryStatistics();
            logger.info("GitHub Repository Statistics:\n{}", stats);
            
        } catch (Exception e) {
            logger.error("Error collecting GitHub data", e);
        }
    }

    /**
     * Collects data from Jira.
     */
    private void collectJiraData() {
        try {
            logger.info("Collecting Jira data...");
            
            List<JiraIssue> issues = jiraService.getRecentIssues();
            logger.info("Collected {} Jira issues", issues.size());
            
            // TODO: Persist issues to database
            // persistenceService.saveJiraIssues(issues);
            
            // Log sample data for demonstration
            if (!issues.isEmpty()) {
                JiraIssue sample = issues.get(0);
                logger.info("Sample Jira issue: {}", sample);
            }
            
            // Get and log project statistics
            String stats = jiraService.getProjectStatistics();
            logger.info("Jira Project Statistics:\n{}", stats);
            
        } catch (Exception e) {
            logger.error("Error collecting Jira data", e);
        }
    }

    /**
     * Manual trigger for data collection (useful for testing or immediate collection).
     */
    public void triggerDataCollection() {
        logger.info("Manually triggered data collection");
        collectTeamWellbeingData();
    }

    /**
     * Tests all integration connections.
     */
    public void testAllConnections() {
        logger.info("Testing all integration connections...");
        
        boolean slackOk = slackService.testConnection();
        boolean githubOk = gitHubService.testConnection();
        boolean jiraOk = jiraService.testConnection();
        
        logger.info("Connection test results - Slack: {}, GitHub: {}, Jira: {}", 
                   slackOk, githubOk, jiraOk);
    }
}