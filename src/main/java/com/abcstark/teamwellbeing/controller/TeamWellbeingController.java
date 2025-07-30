package com.abcstark.teamwellbeing.controller;

import com.abcstark.teamwellbeing.model.GitHubIssue;
import com.abcstark.teamwellbeing.model.JiraIssue;
import com.abcstark.teamwellbeing.model.SlackMessage;
import com.abcstark.teamwellbeing.model.TeamWellbeingStatus;
import com.abcstark.teamwellbeing.persistence.InMemoryPersistenceService;
import com.abcstark.teamwellbeing.scheduled.DataCollectionScheduler;
import com.abcstark.teamwellbeing.service.GitHubService;
import com.abcstark.teamwellbeing.service.JiraService;
import com.abcstark.teamwellbeing.service.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for team wellbeing data collection and monitoring.
 * Provides endpoints for testing integrations and accessing collected data.
 */
@RestController
@RequestMapping("/api/wellbeing")
public class TeamWellbeingController {

    private final SlackService slackService;
    private final GitHubService gitHubService;
    private final JiraService jiraService;
    private final DataCollectionScheduler dataCollectionScheduler;
    private final InMemoryPersistenceService persistenceService;

    @Autowired
    public TeamWellbeingController(SlackService slackService,
                                 GitHubService gitHubService,
                                 JiraService jiraService,
                                 DataCollectionScheduler dataCollectionScheduler,
                                 InMemoryPersistenceService persistenceService) {
        this.slackService = slackService;
        this.gitHubService = gitHubService;
        this.jiraService = jiraService;
        this.dataCollectionScheduler = dataCollectionScheduler;
        this.persistenceService = persistenceService;
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("service", "Team Wellbeing Agent");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tests all integration connections.
     */
    @GetMapping("/test-connections")
    public ResponseEntity<Map<String, Object>> testConnections() {
        Map<String, Object> response = new HashMap<>();
        
        boolean slackOk = slackService.testConnection();
        boolean githubOk = gitHubService.testConnection();
        boolean jiraOk = jiraService.testConnection();
        
        response.put("slack", slackOk);
        response.put("github", githubOk);
        response.put("jira", jiraOk);
        response.put("overall", slackOk && githubOk && jiraOk);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Manually triggers data collection from all integrations.
     */
    @PostMapping("/collect-data")
    public ResponseEntity<Map<String, String>> collectData() {
        try {
            dataCollectionScheduler.triggerDataCollection();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data collection triggered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Data collection failed: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Gets recent Slack messages.
     */
    @GetMapping("/slack/messages")
    public ResponseEntity<List<SlackMessage>> getSlackMessages(
            @RequestParam(defaultValue = "general") String channel) {
        
        List<SlackMessage> messages = slackService.getRecentMessages(channel);
        return ResponseEntity.ok(messages);
    }

    /**
     * Gets available Slack channels.
     */
    @GetMapping("/slack/channels")
    public ResponseEntity<List<String>> getSlackChannels() {
        List<String> channels = slackService.getAvailableChannels();
        return ResponseEntity.ok(channels);
    }

    /**
     * Gets recent GitHub issues.
     */
    @GetMapping("/github/issues")
    public ResponseEntity<List<GitHubIssue>> getGitHubIssues() {
        List<GitHubIssue> issues = gitHubService.getRecentIssues();
        return ResponseEntity.ok(issues);
    }

    /**
     * Gets GitHub repository statistics.
     */
    @GetMapping("/github/stats")
    public ResponseEntity<Map<String, String>> getGitHubStats() {
        String stats = gitHubService.getRepositoryStatistics();
        
        Map<String, String> response = new HashMap<>();
        response.put("statistics", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets GitHub issues for a specific user.
     */
    @GetMapping("/github/issues/user/{username}")
    public ResponseEntity<List<GitHubIssue>> getGitHubIssuesForUser(@PathVariable String username) {
        List<GitHubIssue> issues = gitHubService.getOpenIssuesForUser(username);
        return ResponseEntity.ok(issues);
    }

    /**
     * Gets recent Jira issues.
     */
    @GetMapping("/jira/issues")
    public ResponseEntity<List<JiraIssue>> getJiraIssues() {
        List<JiraIssue> issues = jiraService.getRecentIssues();
        return ResponseEntity.ok(issues);
    }

    /**
     * Gets Jira project statistics.
     */
    @GetMapping("/jira/stats")
    public ResponseEntity<Map<String, String>> getJiraStats() {
        String stats = jiraService.getProjectStatistics();
        
        Map<String, String> response = new HashMap<>();
        response.put("statistics", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets Jira issues for a specific user.
     */
    @GetMapping("/jira/issues/user/{username}")
    public ResponseEntity<List<JiraIssue>> getJiraIssuesForUser(@PathVariable String username) {
        List<JiraIssue> issues = jiraService.getIssuesForUser(username);
        return ResponseEntity.ok(issues);
    }

    /**
     * Gets all stored data from in-memory persistence.
     */
    @GetMapping("/data/all")
    public ResponseEntity<Map<String, Object>> getAllData() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("slackMessages", persistenceService.getAllSlackMessages());
        response.put("githubIssues", persistenceService.getAllGitHubIssues());
        response.put("jiraIssues", persistenceService.getAllJiraIssues());
        response.put("statistics", persistenceService.getStorageStatistics());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clears all stored data.
     */
    @DeleteMapping("/data/clear")
    public ResponseEntity<Map<String, String>> clearData() {
        persistenceService.clearAllData();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All data cleared successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * MCP endpoint to fetch team wellbeing status for orchestrator.
     */
    @GetMapping("/mcp/status")
    public ResponseEntity<TeamWellbeingStatus> getTeamWellbeingStatus() {
        TeamWellbeingStatus status = slackService.analyzeTeamWellbeing();
        return ResponseEntity.ok(status);
    }
}