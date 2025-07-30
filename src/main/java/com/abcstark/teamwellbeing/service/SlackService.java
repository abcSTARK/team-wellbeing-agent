package com.abcstark.teamwellbeing.service;

import com.abcstark.teamwellbeing.config.IntegrationProperties;
import com.abcstark.teamwellbeing.model.SlackMessage;
import com.abcstark.teamwellbeing.model.TeamWellbeingStatus;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.model.Conversation;
import com.slack.api.model.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.slack.api.model.ConversationType;

import java.util.stream.Collectors;

/**
 * Service for integrating with Slack API to collect channel messages
 * for team wellbeing analysis.
 */
@Service
public class SlackService {

    private static final Logger logger = LoggerFactory.getLogger(SlackService.class);

    private final IntegrationProperties integrationProperties;
    private final Slack slack;
    private final ObjectMapper objectMapper;

    @Autowired
    public SlackService(IntegrationProperties integrationProperties) {
        this.integrationProperties = integrationProperties;
        this.slack = Slack.getInstance();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Fetches recent messages from the default Slack channel.
     * 
     * @return List of SlackMessage objects
     */
    public List<SlackMessage> getRecentMessages() {
        return getRecentMessages(integrationProperties.getSlack().getDefaultChannel());
    }

    /**
     * Fetches recent messages from a specific Slack channel.
     * 
     * @param channelName The name of the channel (without #)
     * @return List of SlackMessage objects
     */
    public List<SlackMessage> getRecentMessages(String channelName) {
        List<SlackMessage> messages = new ArrayList<>();
        
        try {
            String botToken = integrationProperties.getSlack().getBotToken();
            if (botToken == null || botToken.startsWith("your-slack")) {
                logger.warn("Slack bot token not configured. Loading dummy messages from sample data.");
                return loadDummyMessages(channelName);
            }

            // Find the channel ID for the given channel name
            String channelId = findChannelId(channelName);
            if (channelId == null) {
                logger.warn("Channel '{}' not found", channelName);
                return messages;
            }

            // Fetch conversation history
            ConversationsHistoryResponse response = slack.methods(botToken)
                    .conversationsHistory(req -> req
                            .channel(channelId)
                            .limit(50) // Last 50 messages
                    );

            if (response.isOk()) {
                for (Message message : response.getMessages()) {
                    if (message.getText() != null && !message.getText().isEmpty()) {
                        SlackMessage slackMessage = convertToSlackMessage(message, channelId, channelName);
                        messages.add(slackMessage);
                    }
                }
                logger.info("Successfully fetched {} messages from channel '{}'", messages.size(), channelName);
            } else {
                logger.error("Failed to fetch messages from Slack: {}", response.getError());
            }

        } catch (IOException | SlackApiException e) {
            logger.error("Error fetching messages from Slack", e);
        }

        return messages;
    }

    /**
     * Gets a list of available channels in the Slack workspace.
     * 
     * @return List of channel names
     */
    public List<String> getAvailableChannels() {
        List<String> channelNames = new ArrayList<>();
        
        try {
            String botToken = integrationProperties.getSlack().getBotToken();
            if (botToken == null || botToken.startsWith("your-slack")) {
                logger.warn("Slack bot token not configured. Returning dummy channel list.");
                return getDummyChannels();
            }

            ConversationsListResponse response = slack.methods(botToken)
                    .conversationsList(req -> req
                            .types(java.util.Arrays.asList(ConversationType.PUBLIC_CHANNEL))
                            .limit(100)
                    );

            if (response.isOk()) {
                for (Conversation conversation : response.getChannels()) {
                    channelNames.add(conversation.getName());
                }
                logger.info("Successfully fetched {} channels", channelNames.size());
            } else {
                logger.error("Failed to fetch channels from Slack: {}", response.getError());
            }

        } catch (IOException | SlackApiException e) {
            logger.error("Error fetching channels from Slack", e);
        }

        return channelNames;
    }

    /**
     * Gets dummy channel names from the sample data.
     * 
     * @return List of unique channel names from dummy data
     */
    private List<String> getDummyChannels() {
        try {
            ClassPathResource resource = new ClassPathResource("sample-slack-messages.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<SlackMessage> allMessages = objectMapper.readValue(inputStream, new TypeReference<List<SlackMessage>>() {});
                
                List<String> channelNames = allMessages.stream()
                        .map(SlackMessage::getChannelName)
                        .distinct()
                        .collect(Collectors.toList());
                
                logger.info("Found {} unique channels in dummy data", channelNames.size());
                return channelNames;
            }
        } catch (IOException e) {
            logger.error("Error loading dummy channels from sample-slack-messages.json", e);
            List<String> defaultChannels = new ArrayList<>();
            defaultChannels.add("general");
            return defaultChannels;
        }
    }

    /**
     * Finds the channel ID for a given channel name.
     * 
     * @param channelName The name of the channel
     * @return The channel ID or null if not found
     */
    private String findChannelId(String channelName) {
        try {
            String botToken = integrationProperties.getSlack().getBotToken();
            ConversationsListResponse response = slack.methods(botToken)
                    .conversationsList(req -> req
                            .types(java.util.Arrays.asList(ConversationType.PUBLIC_CHANNEL, ConversationType.PRIVATE_CHANNEL))
                            .limit(1000)
                    );

            if (response.isOk()) {
                for (Conversation conversation : response.getChannels()) {
                    if (channelName.equals(conversation.getName())) {
                        return conversation.getId();
                    }
                }
            }
        } catch (IOException | SlackApiException e) {
            logger.error("Error finding channel ID for '{}'", channelName, e);
        }
        
        return null;
    }

    /**
     * Converts a Slack API Message to our SlackMessage model.
     * 
     * @param message The Slack API message
     * @param channelId The channel ID
     * @param channelName The channel name
     * @return SlackMessage object
     */
    private SlackMessage convertToSlackMessage(Message message, String channelId, String channelName) {
        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setMessageId(message.getTs());
        slackMessage.setChannelId(channelId);
        slackMessage.setChannelName(channelName);
        slackMessage.setUserId(message.getUser());
        slackMessage.setUsername(message.getUsername() != null ? message.getUsername() : "Unknown");
        slackMessage.setText(message.getText());
        slackMessage.setThreadTs(message.getThreadTs());
        
        // Convert timestamp
        if (message.getTs() != null) {
            try {
                long timestamp = (long) (Double.parseDouble(message.getTs()) * 1000);
                slackMessage.setTimestamp(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
            } catch (NumberFormatException e) {
                logger.warn("Invalid timestamp format: {}", message.getTs());
                slackMessage.setTimestamp(LocalDateTime.now());
            }
        } else {
            slackMessage.setTimestamp(LocalDateTime.now());
        }
        
        // Count reactions
        if (message.getReactions() != null) {
            int totalReactions = message.getReactions().stream()
                    .mapToInt(reaction -> reaction.getCount())
                    .sum();
            slackMessage.setReactionCount(totalReactions);
        }
        
        return slackMessage;
    }

    /**
     * Loads dummy messages from the sample JSON file.
     * 
     * @param channelName The channel name to filter by, or null for all messages
     * @return List of dummy SlackMessage objects
     */
    private List<SlackMessage> loadDummyMessages(String channelName) {
        try {
            ClassPathResource resource = new ClassPathResource("sample-slack-messages.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<SlackMessage> allMessages = objectMapper.readValue(inputStream, new TypeReference<List<SlackMessage>>() {});
                
                // Filter by channel name if specified
                if (channelName != null && !channelName.isEmpty()) {
                    List<SlackMessage> filteredMessages = allMessages.stream()
                            .filter(message -> channelName.equals(message.getChannelName()))
                            .collect(Collectors.toList());
                    logger.info("Loaded {} dummy messages for channel '{}'", filteredMessages.size(), channelName);
                    return filteredMessages;
                }
                
                logger.info("Loaded {} dummy messages from sample data", allMessages.size());
                return allMessages;
            }
        } catch (IOException e) {
            logger.error("Error loading dummy messages from sample-slack-messages.json", e);
            return new ArrayList<>();
        }
    }

    /**
     * Tests the Slack connection and configuration.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            String botToken = integrationProperties.getSlack().getBotToken();
            if (botToken == null || botToken.startsWith("your-slack")) {
                logger.info("Slack bot token not configured");
                return false;
            }

            var response = slack.methods(botToken).authTest(req -> req);
            if (response.isOk()) {
                logger.info("Slack connection test successful. Connected as: {}", response.getUser());
                return true;
            } else {
                logger.error("Slack connection test failed: {}", response.getError());
                return false;
            }
        } catch (IOException | SlackApiException e) {
            logger.error("Slack connection test failed", e);
            return false;
        }
    }

    /**
     * Analyzes recent Slack messages to infer team wellbeing status using simple rule-based logic.
     * @return TeamWellbeingStatus
     */
    public TeamWellbeingStatus analyzeTeamWellbeing() {
        List<SlackMessage> messages = getRecentMessages();
        int moodScore = 0;
        int stressScore = 0;
        Map<String, Integer> overloadCount = new HashMap<>();
        Map<String, String> memberFeelings = new HashMap<>();

        List<String> positiveWords = Arrays.asList("happy", "great", "awesome", "good", "excited", "love");
        List<String> negativeWords = Arrays.asList("sad", "bad", "tired", "upset", "angry", "hate");
        List<String> stressWords = Arrays.asList("stress", "overwhelmed", "busy", "deadline", "pressure");
        List<String> overloadWords = Arrays.asList("overloaded", "too much", "can't handle", "swamped");

        for (SlackMessage msg : messages) {
            String text = msg.getText().toLowerCase();
            String user = msg.getUsername();
            for (String word : positiveWords) {
                if (text.contains(word)) moodScore++;
            }
            for (String word : negativeWords) {
                if (text.contains(word)) moodScore--;
            }
            for (String word : stressWords) {
                if (text.contains(word)) stressScore++;
            }
            for (String word : overloadWords) {
                if (text.contains(word)) {
                    overloadCount.put(user, overloadCount.getOrDefault(user, 0) + 1);
                }
            }
            // Simple feeling extraction
            if (text.contains("feel")) {
                int idx = text.indexOf("feel");
                String feeling = text.substring(idx + 4).trim().split(" ")[0];
                memberFeelings.put(user, feeling);
            }
        }

        String overallMood = moodScore > 2 ? "positive" : (moodScore < -2 ? "negative" : "neutral");
        String overallStressLevel = stressScore > 2 ? "high" : (stressScore < 1 ? "low" : "moderate");
        List<String> overloadedMembers = overloadCount.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new TeamWellbeingStatus(overallMood, overallStressLevel, overloadedMembers, memberFeelings);
    }
}