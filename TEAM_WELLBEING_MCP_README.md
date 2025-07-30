# Team Wellbeing MCP Server

A FastAPI-based MCP (Model Context Protocol) server for the Team Wellbeing Agent REST API.

## Overview

This MCP server provides endpoints for monitoring team health and wellbeing through Slack, GitHub, and Jira integrations. It's modeled after the `booking_system_mcp/mcp_server.py` style from the galaxium-travels-infrastructure repository.

## Features

- **Health Check**: Basic server health monitoring
- **Integration Testing**: Test connections to Slack, GitHub, and Jira APIs
- **Data Collection**: Manual and automated data collection from all platforms
- **Slack Integration**: Access to messages and channel information
- **GitHub Integration**: Issues, statistics, and user-specific data
- **Jira Integration**: Issues, project statistics, and user assignments
- **Team Wellbeing Analysis**: Overall mood and stress level assessment
- **Data Management**: View and clear collected data

## Installation

1. Install the required Python dependencies:

```bash
pip install fastapi>=0.104.1 uvicorn>=0.24.0 pydantic>=2.5.3 fastmcp>=0.2.0 python-dateutil>=2.8.2
```

2. The server is ready to run:

```bash
python3 team_wellbeing_mcp_server.py
```

The server will start on `http://0.0.0.0:8080` by default.

## Available Endpoints

### Health and Testing
- `GET /` - Health check
- `GET /test-connections` - Test all integration connections

### Data Collection
- `POST /collect-data` - Manual data collection trigger

### Slack Endpoints
- `GET /slack/messages?channel=<channel_name>` - Get recent Slack messages
- `GET /slack/channels` - Get available Slack channels

### GitHub Endpoints
- `GET /github/issues` - Get recent GitHub issues
- `GET /github/stats` - Get GitHub repository statistics
- `GET /github/issues/user/{username}` - Get GitHub issues for specific user

### Jira Endpoints
- `GET /jira/issues` - Get recent Jira issues
- `GET /jira/stats` - Get Jira project statistics
- `GET /jira/issues/user/{username}` - Get Jira issues for specific user

### Data Management
- `GET /data/all` - Get all collected data
- `DELETE /data/clear` - Clear all collected data

### Team Wellbeing
- `GET /mcp/status` - Get team wellbeing status

## Data Models

The server uses Pydantic models for structured data:

### SlackMessage
- `message_id`: Unique message identifier
- `channel_id`, `channel_name`: Channel information
- `user_id`, `username`: User information
- `text`: Message content
- `timestamp`: When the message was posted
- `reaction_count`: Number of reactions

### GitHubIssue
- `issue_id`, `number`: Issue identifiers
- `title`, `body`: Issue content
- `state`: open/closed status
- `author`: Issue creator
- `assignees`: List of assigned users
- `labels`: Issue labels
- `created_at`, `updated_at`, `closed_at`: Timestamps
- `repository`: Repository name
- `comments_count`: Number of comments

### JiraIssue
- `issue_id`, `key`: Issue identifiers
- `summary`, `description`: Issue content
- `status`: Current status (To Do, In Progress, Done)
- `priority`: Issue priority
- `issue_type`: Type of issue (Story, Task, Bug, etc.)
- `reporter`, `assignee`: User assignments
- `labels`, `components`: Classification
- `created`, `updated`, `resolved`: Timestamps
- `project_key`: Jira project identifier
- `story_points`: Estimated effort
- `time_spent`: Time logged (in seconds)

### TeamWellbeingStatus
- `overall_mood`: Team mood assessment (positive, neutral, stressed)
- `overall_stress_level`: Stress level (low, medium, high)
- `overloaded_members`: List of potentially overloaded team members
- `member_feelings`: Individual team member sentiment

## Mock Data

The current implementation provides comprehensive mock/sample data for demonstration purposes. This includes:

- **Slack Messages**: Sample team communication from different channels
- **GitHub Issues**: Mock development issues with various states and assignees
- **Jira Issues**: Sample project management tickets with different priorities and statuses

## Testing

A standalone test script is provided to verify the core logic:

```bash
python3 test_mcp_server.py
```

This tests:
- Mock data generation and storage
- Team wellbeing analysis algorithms
- User-specific data filtering
- Core business logic

## Integration with Real Services

The server is designed for easy integration with real APIs. To integrate with actual services:

1. **Slack Integration**: Replace mock data generation with Slack API calls
   - Use the Slack Web API with bot tokens
   - Implement `conversations.history` for messages
   - Implement `conversations.list` for channels

2. **GitHub Integration**: Replace mock data with GitHub API calls
   - Use GitHub REST API or GraphQL API
   - Implement repository issue fetching
   - Add repository statistics collection

3. **Jira Integration**: Replace mock data with Jira REST API calls
   - Use Jira REST API with authentication
   - Implement JQL queries for issue collection
   - Add project statistics gathering

4. **Database Integration**: Replace in-memory storage
   - Add database models (SQLAlchemy recommended)
   - Implement persistent storage
   - Add data retention policies

## Security Considerations

When integrating with real services:

- Store API credentials as environment variables
- Implement proper authentication and authorization
- Add rate limiting to prevent API quota exhaustion
- Implement data privacy controls
- Add logging and monitoring

## Example Usage

```bash
# Health check
curl http://localhost:8080/

# Test all connections
curl http://localhost:8080/test-connections

# Get Slack messages from general channel
curl http://localhost:8080/slack/messages?channel=general

# Get GitHub issues for a specific user
curl http://localhost:8080/github/issues/user/alice.dev

# Get team wellbeing status
curl http://localhost:8080/mcp/status

# Trigger data collection
curl -X POST http://localhost:8080/collect-data

# Clear all data
curl -X DELETE http://localhost:8080/data/clear
```

## Development

The server follows FastMCP patterns and can be extended with additional endpoints, data sources, or analysis algorithms. The modular design makes it easy to add new integrations or modify existing functionality.

## License

This implementation is part of the Team Wellbeing Agent project and follows the same licensing terms.