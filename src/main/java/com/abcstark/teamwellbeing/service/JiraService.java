package com.abcstark.teamwellbeing.service;

import com.abcstark.teamwellbeing.config.IntegrationProperties;
import com.abcstark.teamwellbeing.model.JiraIssue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for integrating with Jira API to collect issues
 * for team wellbeing analysis.
 */
@Service
public class JiraService {

    private static final Logger logger = LoggerFactory.getLogger(JiraService.class);
    private static final DateTimeFormatter JIRA_DATE_FORMAT = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private final IntegrationProperties integrationProperties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public JiraService(IntegrationProperties integrationProperties, WebClient.Builder webClientBuilder) {
        this.integrationProperties = integrationProperties;
        this.objectMapper = new ObjectMapper();
        
        // Initialize WebClient with base URL and authentication
        String jiraUrl = integrationProperties.getJira().getUrl();
        if (jiraUrl != null && !jiraUrl.startsWith("https://your-company")) {
            this.webClient = webClientBuilder
                    .baseUrl(jiraUrl)
                    .defaultHeaders(this::setAuthHeaders)
                    .build();
        } else {
            this.webClient = webClientBuilder.build();
        }
    }

    /**
     * Sets authentication headers for Jira API requests.
     * 
     * @param headers The HTTP headers
     */
    private void setAuthHeaders(HttpHeaders headers) {
        String username = integrationProperties.getJira().getUsername();
        String token = integrationProperties.getJira().getToken();
        
        if (username != null && token != null && 
            !username.startsWith("your-jira") && !token.startsWith("your-jira")) {
            String auth = username + ":" + token;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json");
        }
    }

    /**
     * Fetches recent issues from the default Jira project.
     * 
     * @return List of JiraIssue objects
     */
    public List<JiraIssue> getRecentIssues() {
        String projectKey = integrationProperties.getJira().getProjectKey();
        return getRecentIssues(projectKey);
    }

    /**
     * Fetches recent issues from a specific Jira project.
     * 
     * @param projectKey The Jira project key
     * @return List of JiraIssue objects
     */
    public List<JiraIssue> getRecentIssues(String projectKey) {
        List<JiraIssue> issues = new ArrayList<>();

        if (webClient == null || !isConfigured()) {
            logger.warn("Jira client not properly configured. Check URL, username, and token.");
            return issues;
        }

        try {
            // Build JQL query to fetch recent issues
            String jql = String.format("project = %s ORDER BY updated DESC", projectKey);
            String url = String.format("/rest/api/3/search?jql=%s&maxResults=50", 
                                     java.net.URLEncoder.encode(jql, "UTF-8"));

            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                        logger.error("Error response from Jira API: {}", clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Jira API error: " + clientResponse.statusCode()));
                    })
                    .bodyToMono(String.class);

            String responseBody = response.block();
            if (responseBody != null) {
                issues = parseJiraIssues(responseBody, projectKey);
                logger.info("Successfully fetched {} issues from Jira project '{}'", issues.size(), projectKey);
            }

        } catch (Exception e) {
            logger.error("Error fetching issues from Jira project '{}'", projectKey, e);
        }

        return issues;
    }

    /**
     * Fetches issues assigned to a specific user.
     * 
     * @param username The Jira username
     * @return List of JiraIssue objects
     */
    public List<JiraIssue> getIssuesForUser(String username) {
        List<JiraIssue> issues = new ArrayList<>();

        if (webClient == null || !isConfigured()) {
            logger.warn("Jira client not properly configured.");
            return issues;
        }

        try {
            String projectKey = integrationProperties.getJira().getProjectKey();
            String jql = String.format("project = %s AND assignee = %s ORDER BY updated DESC", 
                                     projectKey, username);
            String url = String.format("/rest/api/3/search?jql=%s&maxResults=50", 
                                     java.net.URLEncoder.encode(jql, "UTF-8"));

            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);

            String responseBody = response.block();
            if (responseBody != null) {
                issues = parseJiraIssues(responseBody, projectKey);
                logger.info("Successfully fetched {} issues for user '{}'", issues.size(), username);
            }

        } catch (Exception e) {
            logger.error("Error fetching issues for user '{}'", username, e);
        }

        return issues;
    }

    /**
     * Gets project statistics including total issues, open issues, etc.
     * 
     * @return Project statistics as a formatted string
     */
    public String getProjectStatistics() {
        if (webClient == null || !isConfigured()) {
            return "Jira client not properly configured. Check URL, username, and token.";
        }

        try {
            String projectKey = integrationProperties.getJira().getProjectKey();
            
            // Get project information
            Mono<String> projectResponse = webClient.get()
                    .uri("/rest/api/3/project/" + projectKey)
                    .retrieve()
                    .bodyToMono(String.class);

            String projectBody = projectResponse.block();
            
            // Get issue counts for different statuses
            String openJql = String.format("project = %s AND status in ('To Do', 'In Progress', 'Open')", projectKey);
            String doneJql = String.format("project = %s AND status in ('Done', 'Closed', 'Resolved')", projectKey);
            
            int openCount = getIssueCount(openJql);
            int doneCount = getIssueCount(doneJql);
            int totalCount = openCount + doneCount;

            String stats = String.format(
                "Project: %s\n" +
                "Total Issues: %d\n" +
                "Open Issues: %d\n" +
                "Completed Issues: %d",
                projectKey, totalCount, openCount, doneCount
            );

            if (projectBody != null) {
                JsonNode projectNode = objectMapper.readTree(projectBody);
                String projectName = projectNode.path("name").asText();
                stats = "Project Name: " + projectName + "\n" + stats;
            }

            logger.info("Project statistics: {}", stats);
            return stats;

        } catch (Exception e) {
            logger.error("Error fetching project statistics", e);
            return "Error fetching project statistics: " + e.getMessage();
        }
    }

    /**
     * Gets the count of issues for a given JQL query.
     * 
     * @param jql The JQL query
     * @return The count of issues
     */
    private int getIssueCount(String jql) {
        try {
            String url = String.format("/rest/api/3/search?jql=%s&maxResults=0", 
                                     java.net.URLEncoder.encode(jql, "UTF-8"));
            
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);

            String responseBody = response.block();
            if (responseBody != null) {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                return rootNode.path("total").asInt();
            }
        } catch (Exception e) {
            logger.warn("Error getting issue count for JQL: {}", jql, e);
        }
        return 0;
    }

    /**
     * Parses Jira API response to extract issues.
     * 
     * @param responseBody The JSON response from Jira API
     * @param projectKey The project key
     * @return List of JiraIssue objects
     */
    private List<JiraIssue> parseJiraIssues(String responseBody, String projectKey) {
        List<JiraIssue> issues = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode issuesNode = rootNode.path("issues");

            for (JsonNode issueNode : issuesNode) {
                JiraIssue issue = parseJiraIssue(issueNode, projectKey);
                if (issue != null) {
                    issues.add(issue);
                }
            }

        } catch (Exception e) {
            logger.error("Error parsing Jira issues response", e);
        }

        return issues;
    }

    /**
     * Parses a single Jira issue from JSON.
     * 
     * @param issueNode The JSON node representing the issue
     * @param projectKey The project key
     * @return JiraIssue object or null if parsing fails
     */
    private JiraIssue parseJiraIssue(JsonNode issueNode, String projectKey) {
        try {
            JiraIssue issue = new JiraIssue();
            
            issue.setIssueId(issueNode.path("id").asText());
            issue.setKey(issueNode.path("key").asText());
            issue.setProjectKey(projectKey);

            JsonNode fields = issueNode.path("fields");
            
            issue.setSummary(fields.path("summary").asText());
            issue.setDescription(fields.path("description").asText());
            
            // Status
            JsonNode status = fields.path("status");
            if (!status.isMissingNode()) {
                issue.setStatus(status.path("name").asText());
            }
            
            // Priority
            JsonNode priority = fields.path("priority");
            if (!priority.isMissingNode()) {
                issue.setPriority(priority.path("name").asText());
            }
            
            // Issue Type
            JsonNode issueType = fields.path("issuetype");
            if (!issueType.isMissingNode()) {
                issue.setIssueType(issueType.path("name").asText());
            }
            
            // Reporter
            JsonNode reporter = fields.path("reporter");
            if (!reporter.isMissingNode()) {
                issue.setReporter(reporter.path("displayName").asText());
            }
            
            // Assignee
            JsonNode assignee = fields.path("assignee");
            if (!assignee.isMissingNode()) {
                issue.setAssignee(assignee.path("displayName").asText());
            }
            
            // Labels
            JsonNode labels = fields.path("labels");
            if (labels.isArray()) {
                List<String> labelList = new ArrayList<>();
                for (JsonNode label : labels) {
                    labelList.add(label.asText());
                }
                issue.setLabels(labelList);
            }
            
            // Components
            JsonNode components = fields.path("components");
            if (components.isArray()) {
                List<String> componentList = new ArrayList<>();
                for (JsonNode component : components) {
                    componentList.add(component.path("name").asText());
                }
                issue.setComponents(componentList);
            }
            
            // Story Points
            JsonNode storyPoints = fields.path("customfield_10016"); // Common story points field
            if (!storyPoints.isMissingNode() && !storyPoints.isNull()) {
                issue.setStoryPoints(storyPoints.asDouble());
            }
            
            // Time spent
            JsonNode timeSpent = fields.path("timespent");
            if (!timeSpent.isMissingNode() && !timeSpent.isNull()) {
                issue.setTimeSpent(timeSpent.asLong());
            }
            
            // Timestamps
            String createdStr = fields.path("created").asText();
            if (!createdStr.isEmpty()) {
                issue.setCreated(LocalDateTime.parse(createdStr, JIRA_DATE_FORMAT));
            }
            
            String updatedStr = fields.path("updated").asText();
            if (!updatedStr.isEmpty()) {
                issue.setUpdated(LocalDateTime.parse(updatedStr, JIRA_DATE_FORMAT));
            }
            
            String resolvedStr = fields.path("resolved").asText();
            if (!resolvedStr.isEmpty()) {
                issue.setResolved(LocalDateTime.parse(resolvedStr, JIRA_DATE_FORMAT));
            }

            return issue;

        } catch (Exception e) {
            logger.warn("Error parsing individual Jira issue", e);
            return null;
        }
    }

    /**
     * Checks if Jira is properly configured.
     * 
     * @return true if configured, false otherwise
     */
    private boolean isConfigured() {
        String url = integrationProperties.getJira().getUrl();
        String username = integrationProperties.getJira().getUsername();
        String token = integrationProperties.getJira().getToken();
        
        return url != null && !url.startsWith("https://your-company") &&
               username != null && !username.startsWith("your-jira") &&
               token != null && !token.startsWith("your-jira");
    }

    /**
     * Tests the Jira connection and configuration.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        if (!isConfigured()) {
            logger.info("Jira not properly configured. Check URL, username, and token.");
            return false;
        }

        try {
            // Test connection by getting current user info
            Mono<String> response = webClient.get()
                    .uri("/rest/api/3/myself")
                    .retrieve()
                    .bodyToMono(String.class);

            String responseBody = response.block();
            if (responseBody != null) {
                JsonNode userNode = objectMapper.readTree(responseBody);
                String displayName = userNode.path("displayName").asText();
                logger.info("Jira connection test successful. Connected as: {}", displayName);
                return true;
            }

        } catch (Exception e) {
            logger.error("Jira connection test failed", e);
        }

        return false;
    }
}