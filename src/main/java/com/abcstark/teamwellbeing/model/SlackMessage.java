package com.abcstark.teamwellbeing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Represents a Slack message for team wellbeing analysis.
 */
public class SlackMessage {
    
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("channel_id")
    private String channelId;
    
    @JsonProperty("channel_name")
    private String channelName;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("thread_ts")
    private String threadTs;
    
    @JsonProperty("reaction_count")
    private int reactionCount;

    // Constructors
    public SlackMessage() {}

    public SlackMessage(String messageId, String channelId, String channelName, 
                       String userId, String username, String text, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.channelName = channelName;
        this.userId = userId;
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getThreadTs() {
        return threadTs;
    }

    public void setThreadTs(String threadTs) {
        this.threadTs = threadTs;
    }

    public int getReactionCount() {
        return reactionCount;
    }

    public void setReactionCount(int reactionCount) {
        this.reactionCount = reactionCount;
    }

    @Override
    public String toString() {
        return "SlackMessage{" +
                "messageId='" + messageId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", reactionCount=" + reactionCount +
                '}';
    }
}