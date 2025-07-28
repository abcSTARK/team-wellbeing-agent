package com.abcstark.teamwellbeing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for scheduling tasks.
 */
@Configuration
@ConfigurationProperties(prefix = "scheduling")
@Component
public class SchedulingProperties {

    private long dataCollectionInterval = 300000; // 5 minutes in milliseconds
    private long initialDelay = 30000; // 30 seconds in milliseconds

    public long getDataCollectionInterval() {
        return dataCollectionInterval;
    }

    public void setDataCollectionInterval(long dataCollectionInterval) {
        this.dataCollectionInterval = dataCollectionInterval;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }
}