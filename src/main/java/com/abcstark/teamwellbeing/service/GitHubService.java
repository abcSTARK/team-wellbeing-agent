package com.abcstark.teamwellbeing.service;

import com.abcstark.teamwellbeing.config.IntegrationProperties;
import com.abcstark.teamwellbeing.model.GitHubIssue;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for integrating with GitHub API to collect repository issues
 * for team wellbeing analysis.
 */
@Service
public class GitHubService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

    private final IntegrationProperties integrationProperties;
    private GitHub github;

    @Autowired
    public GitHubService(IntegrationProperties integrationProperties) {
        this.integrationProperties = integrationProperties;
        initializeGitHubClient();
    }

    /**
     * Initializes the GitHub client with the configured token.
     */
    private void initializeGitHubClient() {
        try {
            String token = integrationProperties.getGithub().getToken();
            if (token != null && !token.startsWith("your-github")) {
                this.github = new GitHubBuilder().withOAuthToken(token).build();
            }
        } catch (IOException e) {
            logger.error("Failed to initialize GitHub client", e);
        }
    }

    /**
     * Fetches recent issues from the default GitHub repository.
     * 
     * @return List of GitHubIssue objects
     */
    public List<GitHubIssue> getRecentIssues() {
        String owner = integrationProperties.getGithub().getOwner();
        String repository = integrationProperties.getGithub().getRepository();
        return getRecentIssues(owner, repository);
    }

    /**
     * Fetches recent issues from a specific GitHub repository.
     * 
     * @param owner The repository owner
     * @param repositoryName The repository name
     * @return List of GitHubIssue objects
     */
    public List<GitHubIssue> getRecentIssues(String owner, String repositoryName) {
        List<GitHubIssue> issues = new ArrayList<>();

        if (github == null) {
            logger.warn("GitHub client not initialized. Check token configuration.");
            return issues;
        }

        try {
            GHRepository repository = github.getRepository(owner + "/" + repositoryName);
            
            // Fetch recent issues (last 50)
            List<GHIssue> ghIssues = repository.getIssues(org.kohsuke.github.GHIssueState.ALL)
                    .stream()
                    .limit(50)
                    .collect(Collectors.toList());

            for (GHIssue ghIssue : ghIssues) {
                GitHubIssue issue = convertToGitHubIssue(ghIssue, repositoryName);
                issues.add(issue);
            }

            logger.info("Successfully fetched {} issues from repository '{}/{}'", 
                       issues.size(), owner, repositoryName);

        } catch (IOException e) {
            logger.error("Error fetching issues from GitHub repository '{}/{}'", owner, repositoryName, e);
        }

        return issues;
    }

    /**
     * Fetches open issues assigned to a specific user.
     * 
     * @param username The GitHub username
     * @return List of GitHubIssue objects
     */
    public List<GitHubIssue> getOpenIssuesForUser(String username) {
        List<GitHubIssue> issues = new ArrayList<>();

        if (github == null) {
            logger.warn("GitHub client not initialized. Check token configuration.");
            return issues;
        }

        try {
            String owner = integrationProperties.getGithub().getOwner();
            String repositoryName = integrationProperties.getGithub().getRepository();
            GHRepository repository = github.getRepository(owner + "/" + repositoryName);
            
            List<GHIssue> ghIssues = repository.getIssues(org.kohsuke.github.GHIssueState.OPEN)
                    .stream()
                    .filter(issue -> {
                        try {
                            return issue.getAssignee() != null && 
                                   username.equals(issue.getAssignee().getLogin());
                        } catch (IOException e) {
                            logger.warn("Could not check assignee for issue #{}", issue.getNumber());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

            for (GHIssue ghIssue : ghIssues) {
                GitHubIssue issue = convertToGitHubIssue(ghIssue, repositoryName);
                issues.add(issue);
            }

            logger.info("Successfully fetched {} open issues for user '{}'", issues.size(), username);

        } catch (IOException e) {
            logger.error("Error fetching issues for user '{}'", username, e);
        }

        return issues;
    }

    /**
     * Gets repository statistics including total issues, open issues, etc.
     * 
     * @return Repository statistics as a formatted string
     */
    public String getRepositoryStatistics() {
        if (github == null) {
            return "GitHub client not initialized. Check token configuration.";
        }

        try {
            String owner = integrationProperties.getGithub().getOwner();
            String repositoryName = integrationProperties.getGithub().getRepository();
            GHRepository repository = github.getRepository(owner + "/" + repositoryName);

            int openIssues = repository.getOpenIssueCount();
            int totalIssues = repository.getIssues(org.kohsuke.github.GHIssueState.ALL).size();
            int closedIssues = totalIssues - openIssues;

            String stats = String.format(
                "Repository: %s/%s\n" +
                "Total Issues: %d\n" +
                "Open Issues: %d\n" +
                "Closed Issues: %d\n" +
                "Stars: %d\n" +
                "Forks: %d",
                owner, repositoryName, totalIssues, openIssues, closedIssues,
                repository.getStargazersCount(), repository.getForksCount()
            );

            logger.info("Repository statistics: {}", stats);
            return stats;

        } catch (IOException e) {
            logger.error("Error fetching repository statistics", e);
            return "Error fetching repository statistics: " + e.getMessage();
        }
    }

    /**
     * Converts a GitHub API GHIssue to our GitHubIssue model.
     * 
     * @param ghIssue The GitHub API issue
     * @param repositoryName The repository name
     * @return GitHubIssue object
     */
    private GitHubIssue convertToGitHubIssue(GHIssue ghIssue, String repositoryName) {
        GitHubIssue issue = new GitHubIssue();
        
        issue.setIssueId((long) ghIssue.getNumber());
        issue.setNumber(ghIssue.getNumber());
        issue.setTitle(ghIssue.getTitle());
        issue.setBody(ghIssue.getBody());
        issue.setState(ghIssue.getState().toString());
        issue.setRepository(repositoryName);
        issue.setCommentsCount(ghIssue.getCommentsCount());

        // Author
        try {
            if (ghIssue.getUser() != null) {
                issue.setAuthor(ghIssue.getUser().getLogin());
            }
        } catch (Exception e) {
            logger.warn("Could not fetch author for issue #{}", ghIssue.getNumber());
            issue.setAuthor("Unknown");
        }

        // Assignees
        try {
            if (ghIssue.getAssignees() != null && !ghIssue.getAssignees().isEmpty()) {
                List<String> assignees = ghIssue.getAssignees().stream()
                        .map(assignee -> {
                            try {
                                return assignee.getLogin();
                            } catch (Exception e) {
                                return "Unknown";
                            }
                        })
                        .collect(Collectors.toList());
                issue.setAssignees(assignees);
            }
        } catch (Exception e) {
            logger.warn("Could not fetch assignees for issue #{}", ghIssue.getNumber());
        }

        // Labels
        try {
            if (ghIssue.getLabels() != null && !ghIssue.getLabels().isEmpty()) {
                List<String> labels = ghIssue.getLabels().stream()
                        .map(label -> label.getName())
                        .collect(Collectors.toList());
                issue.setLabels(labels);
            }
        } catch (Exception e) {
            logger.warn("Could not fetch labels for issue #{}", ghIssue.getNumber());
        }

        // Timestamps
        try {
            if (ghIssue.getCreatedAt() != null) {
                issue.setCreatedAt(ghIssue.getCreatedAt().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        } catch (Exception e) {
            logger.warn("Could not fetch created date for issue #{}", ghIssue.getNumber());
        }
        
        try {
            if (ghIssue.getUpdatedAt() != null) {
                issue.setUpdatedAt(ghIssue.getUpdatedAt().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        } catch (Exception e) {
            logger.warn("Could not fetch updated date for issue #{}", ghIssue.getNumber());
        }
        
        try {
            if (ghIssue.getClosedAt() != null) {
                issue.setClosedAt(ghIssue.getClosedAt().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        } catch (Exception e) {
            logger.warn("Could not fetch closed date for issue #{}", ghIssue.getNumber());
        }

        return issue;
    }

    /**
     * Tests the GitHub connection and configuration.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            if (github == null) {
                logger.info("GitHub client not initialized. Check token configuration.");
                return false;
            }

            // Try to get the authenticated user
            var user = github.getMyself();
            logger.info("GitHub connection test successful. Connected as: {}", user.getLogin());
            return true;

        } catch (IOException e) {
            logger.error("GitHub connection test failed", e);
            return false;
        }
    }
}