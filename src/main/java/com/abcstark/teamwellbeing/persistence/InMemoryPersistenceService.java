package com.abcstark.teamwellbeing.persistence;

import com.abcstark.teamwellbeing.model.GitHubIssue;
import com.abcstark.teamwellbeing.model.JiraIssue;
import com.abcstark.teamwellbeing.model.SlackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of PersistenceService for demonstration purposes.
 * In a production environment, this would be replaced with a proper database implementation
 * using JPA, MongoDB, or another persistence technology.
 */
@Service
public class InMemoryPersistenceService implements PersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryPersistenceService.class);

    // In-memory storage (thread-safe collections)
    private final ConcurrentHashMap<String, SlackMessage> slackMessages = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, GitHubIssue> gitHubIssues = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, JiraIssue> jiraIssues = new ConcurrentHashMap<>();

    @Override
    public void saveSlackMessages(List<SlackMessage> messages) {
        logger.info("Saving {} Slack messages to in-memory storage", messages.size());
        
        for (SlackMessage message : messages) {
            String key = generateSlackMessageKey(message);
            slackMessages.put(key, message);
        }
        
        logger.debug("Total Slack messages in storage: {}", slackMessages.size());
    }

    @Override
    public void saveGitHubIssues(List<GitHubIssue> issues) {
        logger.info("Saving {} GitHub issues to in-memory storage", issues.size());
        
        for (GitHubIssue issue : issues) {
            String key = generateGitHubIssueKey(issue);
            gitHubIssues.put(key, issue);
        }
        
        logger.debug("Total GitHub issues in storage: {}", gitHubIssues.size());
    }

    @Override
    public void saveJiraIssues(List<JiraIssue> issues) {
        logger.info("Saving {} Jira issues to in-memory storage", issues.size());
        
        for (JiraIssue issue : issues) {
            String key = generateJiraIssueKey(issue);
            jiraIssues.put(key, issue);
        }
        
        logger.debug("Total Jira issues in storage: {}", jiraIssues.size());
    }

    @Override
    public List<SlackMessage> getSlackMessages(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Retrieving Slack messages between {} and {}", startDate, endDate);
        
        return slackMessages.values().stream()
                .filter(message -> isWithinDateRange(message.getTimestamp(), startDate, endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<GitHubIssue> getGitHubIssues(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Retrieving GitHub issues between {} and {}", startDate, endDate);
        
        return gitHubIssues.values().stream()
                .filter(issue -> isWithinDateRange(issue.getCreatedAt(), startDate, endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<JiraIssue> getJiraIssues(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Retrieving Jira issues between {} and {}", startDate, endDate);
        
        return jiraIssues.values().stream()
                .filter(issue -> isWithinDateRange(issue.getCreated(), startDate, endDate))
                .collect(Collectors.toList());
    }

    @Override
    public void clearAllData() {
        logger.info("Clearing all in-memory data");
        
        slackMessages.clear();
        gitHubIssues.clear();
        jiraIssues.clear();
        
        logger.info("All data cleared from in-memory storage");
    }

    /**
     * Gets all stored Slack messages (for debugging/monitoring).
     * 
     * @return List of all Slack messages
     */
    public List<SlackMessage> getAllSlackMessages() {
        return new ArrayList<>(slackMessages.values());
    }

    /**
     * Gets all stored GitHub issues (for debugging/monitoring).
     * 
     * @return List of all GitHub issues
     */
    public List<GitHubIssue> getAllGitHubIssues() {
        return new ArrayList<>(gitHubIssues.values());
    }

    /**
     * Gets all stored Jira issues (for debugging/monitoring).
     * 
     * @return List of all Jira issues
     */
    public List<JiraIssue> getAllJiraIssues() {
        return new ArrayList<>(jiraIssues.values());
    }

    /**
     * Gets storage statistics.
     * 
     * @return Storage statistics as a formatted string
     */
    public String getStorageStatistics() {
        return String.format(
                "In-Memory Storage Statistics:\n" +
                "Slack Messages: %d\n" +
                "GitHub Issues: %d\n" +
                "Jira Issues: %d\n" +
                "Total Records: %d",
                slackMessages.size(),
                gitHubIssues.size(),
                jiraIssues.size(),
                slackMessages.size() + gitHubIssues.size() + jiraIssues.size()
        );
    }

    /**
     * Generates a unique key for a Slack message.
     */
    private String generateSlackMessageKey(SlackMessage message) {
        return String.format("%s_%s_%s", 
                message.getChannelId(), 
                message.getMessageId(), 
                message.getTimestamp().toString());
    }

    /**
     * Generates a unique key for a GitHub issue.
     */
    private String generateGitHubIssueKey(GitHubIssue issue) {
        return String.format("%s_%d", issue.getRepository(), issue.getNumber());
    }

    /**
     * Generates a unique key for a Jira issue.
     */
    private String generateJiraIssueKey(JiraIssue issue) {
        return issue.getKey();
    }

    /**
     * Checks if a date is within the specified range.
     */
    private boolean isWithinDateRange(LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate) {
        return date != null && 
               (date.isEqual(startDate) || date.isAfter(startDate)) && 
               (date.isEqual(endDate) || date.isBefore(endDate));
    }
}