# Use Eclipse Temurin 17 as the base image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Define build argument for JAR file
ARG JAR_FILE=target/team-wellbeing-agent-*.jar

# Copy the JAR file to the container as app.jar
COPY ${JAR_FILE} app.jar

# Change ownership of the app.jar to the non-root user
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose port 8080 for IBM Cloud deployment
EXPOSE 8080

# Set the entrypoint to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Default command (can be overridden)
CMD ["--server.port=8080"]