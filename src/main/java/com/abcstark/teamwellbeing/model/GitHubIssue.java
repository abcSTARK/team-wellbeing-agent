package com.abcstark.teamwellbeing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a GitHub issue for team wellbeing analysis.
 */
public class GitHubIssue {
    
    @JsonProperty("issue_id")
    private Long issueId;
    
    @JsonProperty("number")
    private int number;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("body")
    private String body;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("author")
    private String author;
    
    @JsonProperty("assignees")
    private List<String> assignees;
    
    @JsonProperty("labels")
    private List<String> labels;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("closed_at")
    private LocalDateTime closedAt;
    
    @JsonProperty("repository")
    private String repository;
    
    @JsonProperty("comments_count")
    private int commentsCount;

    // Constructors
    public GitHubIssue() {}

    public GitHubIssue(Long issueId, int number, String title, String state, 
                      String author, LocalDateTime createdAt, String repository) {
        this.issueId = issueId;
        this.number = number;
        this.title = title;
        this.state = state;
        this.author = author;
        this.createdAt = createdAt;
        this.repository = repository;
    }

    // Getters and Setters
    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<String> assignees) {
        this.assignees = assignees;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    @Override
    public String toString() {
        return "GitHubIssue{" +
                "number=" + number +
                ", title='" + title + '\'' +
                ", state='" + state + '\'' +
                ", author='" + author + '\'' +
                ", repository='" + repository + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}