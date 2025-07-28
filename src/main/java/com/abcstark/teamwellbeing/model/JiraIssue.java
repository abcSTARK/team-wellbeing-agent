package com.abcstark.teamwellbeing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a Jira issue for team wellbeing analysis.
 */
public class JiraIssue {
    
    @JsonProperty("issue_id")
    private String issueId;
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("summary")
    private String summary;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("priority")
    private String priority;
    
    @JsonProperty("issue_type")
    private String issueType;
    
    @JsonProperty("reporter")
    private String reporter;
    
    @JsonProperty("assignee")
    private String assignee;
    
    @JsonProperty("labels")
    private List<String> labels;
    
    @JsonProperty("components")
    private List<String> components;
    
    @JsonProperty("created")
    private LocalDateTime created;
    
    @JsonProperty("updated")
    private LocalDateTime updated;
    
    @JsonProperty("resolved")
    private LocalDateTime resolved;
    
    @JsonProperty("project_key")
    private String projectKey;
    
    @JsonProperty("story_points")
    private Double storyPoints;
    
    @JsonProperty("time_spent")
    private Long timeSpent; // in seconds

    // Constructors
    public JiraIssue() {}

    public JiraIssue(String issueId, String key, String summary, String status, 
                    String reporter, String assignee, LocalDateTime created, String projectKey) {
        this.issueId = issueId;
        this.key = key;
        this.summary = summary;
        this.status = status;
        this.reporter = reporter;
        this.assignee = assignee;
        this.created = created;
        this.projectKey = projectKey;
    }

    // Getters and Setters
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public LocalDateTime getResolved() {
        return resolved;
    }

    public void setResolved(LocalDateTime resolved) {
        this.resolved = resolved;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public Double getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Double storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    @Override
    public String toString() {
        return "JiraIssue{" +
                "key='" + key + '\'' +
                ", summary='" + summary + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", assignee='" + assignee + '\'' +
                ", projectKey='" + projectKey + '\'' +
                ", created=" + created +
                '}';
    }
}