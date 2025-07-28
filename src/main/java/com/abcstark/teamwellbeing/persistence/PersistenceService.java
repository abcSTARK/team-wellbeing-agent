package com.abcstark.teamwellbeing.persistence;

import com.abcstark.teamwellbeing.model.GitHubIssue;
import com.abcstark.teamwellbeing.model.JiraIssue;
import com.abcstark.teamwellbeing.model.SlackMessage;

import java.util.List;

/**
 * Interface for persisting team wellbeing data.
 * This serves as a placeholder for future database implementation.
 */
public interface PersistenceService {

    /**
     * Saves Slack messages to persistent storage.
     * 
     * @param messages List of Slack messages to save
     */
    void saveSlackMessages(List<SlackMessage> messages);

    /**
     * Saves GitHub issues to persistent storage.
     * 
     * @param issues List of GitHub issues to save
     */
    void saveGitHubIssues(List<GitHubIssue> issues);

    /**
     * Saves Jira issues to persistent storage.
     * 
     * @param issues List of Jira issues to save
     */
    void saveJiraIssues(List<JiraIssue> issues);

    /**
     * Retrieves Slack messages within a date range.
     * 
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return List of Slack messages
     */
    List<SlackMessage> getSlackMessages(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    /**
     * Retrieves GitHub issues within a date range.
     * 
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return List of GitHub issues
     */
    List<GitHubIssue> getGitHubIssues(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    /**
     * Retrieves Jira issues within a date range.
     * 
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return List of Jira issues
     */
    List<JiraIssue> getJiraIssues(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    /**
     * Clears all persisted data (useful for testing).
     */
    void clearAllData();
}