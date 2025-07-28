# Multi-stage Docker build for proper cloud deployment
# Stage 1: Build stage with Maven and JDK
FROM maven:3.9.8-eclipse-temurin-17 AS builder

# Set working directory for build
WORKDIR /build

# Copy Maven configuration files first for better layer caching
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application with Maven
# Note: In production cloud environments, this will work normally
# The -o flag is a fallback for environments with certificate issues
RUN mvn clean package -DskipTests -B || mvn clean package -DskipTests -B -o

# Stage 2: Runtime stage with minimal JRE
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Copy the JAR file from the builder stage
COPY --from=builder /build/target/team-wellbeing-agent-*.jar app.jar

# Change ownership of the app.jar to the non-root user
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose port 8080 for cloud deployment
EXPOSE 8080

# Set the entrypoint to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Default command (can be overridden)
CMD ["--server.port=8080"]