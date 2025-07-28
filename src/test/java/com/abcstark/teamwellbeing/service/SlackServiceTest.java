package com.abcstark.teamwellbeing.service;

import com.abcstark.teamwellbeing.config.IntegrationProperties;
import com.abcstark.teamwellbeing.model.SlackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for SlackService dummy data functionality.
 */
@ExtendWith(MockitoExtension.class)
class SlackServiceTest {

    private SlackService slackService;
    private IntegrationProperties integrationProperties;

    @BeforeEach
    void setUp() {
        integrationProperties = new IntegrationProperties();
        // Set up Slack properties with default token (which should trigger dummy data loading)
        integrationProperties.setSlack(new IntegrationProperties.Slack());
        integrationProperties.getSlack().setBotToken("your-slack-bot-token");
        integrationProperties.getSlack().setDefaultChannel("general");
        
        slackService = new SlackService(integrationProperties);
    }

    @Test
    void testGetRecentMessages_WhenTokenNotConfigured_ReturnsDummyData() {
        // When token is not configured (starts with "your-slack"), should return dummy data
        List<SlackMessage> messages = slackService.getRecentMessages("general");

        // Verify we get dummy messages
        assertNotNull(messages);
        assertFalse(messages.isEmpty());
        assertTrue(messages.size() > 0);

        // Verify first message structure
        SlackMessage firstMessage = messages.get(0);
        assertNotNull(firstMessage.getMessageId());
        assertNotNull(firstMessage.getChannelName());
        assertNotNull(firstMessage.getUsername());
        assertNotNull(firstMessage.getText());
        assertNotNull(firstMessage.getTimestamp());
        
        // Verify channel filtering
        assertEquals("general", firstMessage.getChannelName());
    }

    @Test
    void testGetRecentMessages_WhenChannelNotExists_ReturnsEmptyList() {
        // When requesting a non-existent channel, should return empty list
        List<SlackMessage> messages = slackService.getRecentMessages("nonexistent");

        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    void testGetAvailableChannels_WhenTokenNotConfigured_ReturnsDummyChannels() {
        // When token is not configured, should return dummy channels
        List<String> channels = slackService.getAvailableChannels();

        assertNotNull(channels);
        assertFalse(channels.isEmpty());
        assertTrue(channels.contains("general"));
    }

    @Test
    void testTestConnection_WhenTokenNotConfigured_ReturnsFalse() {
        // When token is not configured, connection test should return false
        boolean connectionResult = slackService.testConnection();

        assertFalse(connectionResult);
    }
}