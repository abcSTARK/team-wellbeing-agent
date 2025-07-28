# Team Wellbeing Agent

A Spring Boot application that collects and analyzes team wellbeing data from Slack, GitHub, and Jira to help organizations monitor team health and prevent burnout.

## Features

### Core Capabilities
- **Multi-Platform Integration**: Connects to Slack, GitHub, and Jira APIs
- **Automated Data Collection**: Scheduled tasks collect data at configurable intervals
- **RESTful API**: Provides endpoints for testing integrations and accessing collected data
- **Modular Architecture**: Clean separation of concerns with dedicated service classes
- **Configuration-Driven**: Centralized configuration through application.yml
- **In-Memory Storage**: Demonstration persistence layer (ready for database integration)

### Supported Data Sources
- **Slack**: Channel messages, user interactions, and team communication patterns
- **GitHub**: Repository issues, pull requests, contributor activity, and project statistics
- **Jira**: Project issues, task assignments, story points, and work progress tracking

## Architecture

The application follows a modular Spring Boot architecture:

```
src/main/java/com/abcstark/teamwellbeing/
├── TeamWellbeingAgentApplication.java    # Main Spring Boot application
├── config/                               # Configuration classes
│   ├── IntegrationProperties.java        # API credentials and endpoints
│   └── SchedulingProperties.java         # Scheduling configuration
├── controller/                           # REST API endpoints
│   └── TeamWellbeingController.java      # Main API controller
├── model/                                # Data models
│   ├── SlackMessage.java                 # Slack message representation
│   ├── GitHubIssue.java                  # GitHub issue representation
│   └── JiraIssue.java                    # Jira issue representation
├── service/                              # Integration services
│   ├── SlackService.java                 # Slack API integration
│   ├── GitHubService.java                # GitHub API integration
│   └── JiraService.java                  # Jira API integration
├── scheduled/                            # Scheduled tasks
│   └── DataCollectionScheduler.java      # Automated data collection
└── persistence/                          # Data storage
    ├── PersistenceService.java           # Storage interface
    └── InMemoryPersistenceService.java   # In-memory implementation
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **API Credentials** for the platforms you want to integrate:
  - Slack Bot Token (starts with `xoxb-`)
  - GitHub Personal Access Token
  - Jira API Token and credentials

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/abcSTARK/team-wellbeing-agent.git
cd team-wellbeing-agent
```

### 2. Configure API Credentials

The application uses environment variables for API credentials. You can set them in several ways:

#### Option A: Environment Variables

```bash
export SLACK_BOT_TOKEN="xoxb-your-slack-bot-token"
export SLACK_DEFAULT_CHANNEL="general"
export GITHUB_TOKEN="your-github-personal-access-token"
export GITHUB_OWNER="your-github-username-or-org"
export GITHUB_REPOSITORY="your-repository-name"
export JIRA_URL="https://your-company.atlassian.net"
export JIRA_USERNAME="your-jira-email@company.com"
export JIRA_TOKEN="your-jira-api-token"
export JIRA_PROJECT_KEY="PROJ"
```

#### Option B: Create application-local.yml

Create `src/main/resources/application-local.yml` (this file is gitignored):

```yaml
integrations:
  slack:
    bot-token: "xoxb-your-slack-bot-token"
    default-channel: "general"
  github:
    token: "your-github-personal-access-token"
    owner: "your-github-username-or-org"
    repository: "your-repository-name"
  jira:
    url: "https://your-company.atlassian.net"
    username: "your-jira-email@company.com"
    token: "your-jira-api-token"
    project-key: "PROJ"
```

Then run with: `mvn spring-boot:run -Dspring.profiles.active=local`

### 3. Obtain API Credentials

#### Slack Setup
1. Go to [Slack API](https://api.slack.com/apps)
2. Create a new app or use an existing one
3. Add the following OAuth scopes:
   - `channels:history` - Read messages from public channels
   - `channels:read` - List public channels
   - `users:read` - Read user information
4. Install the app to your workspace
5. Copy the "Bot User OAuth Token" (starts with `xoxb-`)

#### GitHub Setup
1. Go to GitHub Settings → Developer settings → Personal access tokens
2. Generate a new token with the following permissions:
   - `repo` - Access to repositories (if private repos)
   - `public_repo` - Access to public repositories
   - `read:user` - Read user profile data
3. Copy the generated token

#### Jira Setup
1. Go to your Jira account settings → Security → API tokens
2. Create a new API token
3. Use your Jira email address as the username
4. Copy the generated token

### 4. Build and Run

```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run

# Or build and run as a JAR
mvn clean package
java -jar target/team-wellbeing-agent-0.1.0-SNAPSHOT.jar
```

The application will start on port 8090 by default.

## Docker Deployment

The application includes a Dockerfile for easy deployment to container platforms like IBM Cloud Code Engine.

### Building the Docker Image

First, build the application JAR:

```bash
mvn clean package
```

Then build the Docker image:

```bash
# Build with default JAR pattern
docker build -t team-wellbeing-agent .

# Or specify a specific JAR file
docker build --build-arg JAR_FILE=target/team-wellbeing-agent-0.1.0-SNAPSHOT.jar -t team-wellbeing-agent .
```

### Running the Docker Container

```bash
# Run on port 8080 (IBM Cloud compatible)
docker run -p 8080:8080 team-wellbeing-agent

# Run on custom port
docker run -p 9090:9090 -e SERVER_PORT=9090 team-wellbeing-agent

# Run with environment variables for API credentials
docker run -p 8080:8080 \
  -e SLACK_BOT_TOKEN="your-slack-token" \
  -e GITHUB_TOKEN="your-github-token" \
  -e JIRA_URL="https://your-company.atlassian.net" \
  -e JIRA_USERNAME="your-email@company.com" \
  -e JIRA_TOKEN="your-jira-token" \
  team-wellbeing-agent
```

### Deploying to IBM Cloud Code Engine

1. Build and push the image to a container registry:

```bash
# Tag for IBM Cloud Container Registry
docker tag team-wellbeing-agent us.icr.io/your-namespace/team-wellbeing-agent:latest

# Push to registry
docker push us.icr.io/your-namespace/team-wellbeing-agent:latest
```

2. Deploy using IBM Cloud CLI:

```bash
# Create a new application
ibmcloud ce application create --name team-wellbeing-agent \
  --image us.icr.io/your-namespace/team-wellbeing-agent:latest \
  --port 8080 \
  --env SLACK_BOT_TOKEN="your-slack-token" \
  --env GITHUB_TOKEN="your-github-token"
```

The Docker image includes:
- Eclipse Temurin 17 JRE (Alpine-based for smaller size)
- Non-root user for security
- Port 8080 exposed for IBM Cloud compatibility
- Configurable via environment variables

## Usage

### API Endpoints

Once the application is running, you can access the following endpoints:

#### Health Check
```bash
curl http://localhost:8090/api/wellbeing/health
```

#### Test All Integrations
```bash
curl http://localhost:8090/api/wellbeing/test-connections
```

#### Manual Data Collection
```bash
curl -X POST http://localhost:8090/api/wellbeing/collect-data
```

#### Slack Data
```bash
# Get recent messages from default channel
curl http://localhost:8090/api/wellbeing/slack/messages

# Get messages from specific channel
curl http://localhost:8090/api/wellbeing/slack/messages?channel=development

# Get available channels
curl http://localhost:8090/api/wellbeing/slack/channels
```

#### GitHub Data
```bash
# Get recent issues
curl http://localhost:8090/api/wellbeing/github/issues

# Get repository statistics
curl http://localhost:8090/api/wellbeing/github/stats

# Get issues for specific user
curl http://localhost:8090/api/wellbeing/github/issues/user/username
```

#### Jira Data
```bash
# Get recent issues
curl http://localhost:8090/api/wellbeing/jira/issues

# Get project statistics
curl http://localhost:8090/api/wellbeing/jira/stats

# Get issues for specific user
curl http://localhost:8090/api/wellbeing/jira/issues/user/username
```

#### Data Management
```bash
# Get all collected data
curl http://localhost:8090/api/wellbeing/data/all

# Clear all data
curl -X DELETE http://localhost:8090/api/wellbeing/data/clear
```

### Scheduled Data Collection

The application automatically collects data from all configured integrations every 5 minutes (configurable). You can customize the schedule in `application.yml`:

```yaml
scheduling:
  # Data collection interval in milliseconds (default: 5 minutes)
  data-collection-interval: 300000
  # Initial delay before first execution (default: 30 seconds)
  initial-delay: 30000
```

### Monitoring

- **Health Check**: `http://localhost:8090/actuator/health`
- **Application Info**: `http://localhost:8090/actuator/info`
- **Metrics**: `http://localhost:8090/actuator/metrics`

## Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
server:
  port: 8090                    # Application port

integrations:
  slack:
    bot-token: ${SLACK_BOT_TOKEN}
    default-channel: ${SLACK_DEFAULT_CHANNEL:general}
  github:
    token: ${GITHUB_TOKEN}
    owner: ${GITHUB_OWNER}
    repository: ${GITHUB_REPOSITORY}
  jira:
    url: ${JIRA_URL}
    username: ${JIRA_USERNAME}
    token: ${JIRA_TOKEN}
    project-key: ${JIRA_PROJECT_KEY}

scheduling:
  data-collection-interval: 300000  # 5 minutes
  initial-delay: 30000               # 30 seconds

logging:
  level:
    com.abcstark.teamwellbeing: INFO
```

### Data Persistence

The current implementation uses in-memory storage for demonstration purposes. For production use, you can:

1. **Replace InMemoryPersistenceService** with a database implementation
2. **Add JPA dependencies** to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>
   <dependency>
       <groupId>com.h2database</groupId>
       <artifactId>h2</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
3. **Create JPA entities** based on the existing model classes
4. **Implement repositories** extending `JpaRepository`

## Development

### Project Structure

- **Models**: Data transfer objects representing API responses
- **Services**: Integration logic for each platform
- **Controllers**: REST API endpoints for external access
- **Configuration**: Centralized configuration management
- **Scheduling**: Automated data collection tasks
- **Persistence**: Data storage abstraction layer

### Adding New Integrations

To add a new platform integration:

1. **Create a model class** in `com.abcstark.teamwellbeing.model`
2. **Add configuration properties** to `IntegrationProperties`
3. **Create a service class** in `com.abcstark.teamwellbeing.service`
4. **Update the scheduler** to include the new service
5. **Add API endpoints** to `TeamWellbeingController`

### Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify
```

## Troubleshooting

### Common Issues

1. **Authentication Errors**
   - Verify API tokens are correct and have proper permissions
   - Check that tokens haven't expired
   - Ensure environment variables are properly set

2. **Connection Timeouts**
   - Check network connectivity to API endpoints
   - Verify firewall settings allow outbound HTTPS connections
   - Consider adjusting timeout settings in service classes

3. **Rate Limiting**
   - APIs may have rate limits; check service logs for 429 errors
   - Consider implementing exponential backoff in service calls
   - Adjust collection intervals if hitting rate limits

### Debugging

Enable debug logging:

```yaml
logging:
  level:
    com.abcstark.teamwellbeing: DEBUG
    org.springframework.web: DEBUG
```

## Security Considerations

- **Never commit API tokens** to version control
- **Use environment variables** or external configuration for sensitive data
- **Rotate API tokens** regularly
- **Monitor API usage** to detect unauthorized access
- **Implement proper error handling** to avoid leaking sensitive information

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/abcSTARK/team-wellbeing-agent/issues)
- **Documentation**: This README and inline code documentation
- **Community**: [GitHub Discussions](https://github.com/abcSTARK/team-wellbeing-agent/discussions)

## Overview

The Team Wellbeing Agent serves as an intelligent monitoring system that helps organizations maintain healthy team dynamics by:

- **Proactive Risk Detection**: Identifying early signs of team disengagement and burnout
- **Multi-Source Analysis**: Integrating data from various platforms and tools
- **Actionable Insights**: Providing specific recommendations for team wellness improvement
- **Seamless Integration**: Working as part of the broader AskManager ecosystem

## Features

### Core Capabilities
- **Team Health Monitoring**: Continuous assessment of team wellness indicators
- **Burnout Risk Assessment**: Early detection algorithms for identifying at-risk team members
- **Engagement Analytics**: Analysis of team participation and interaction patterns
- **Wellness Alerts**: Automated notifications and recommendations for intervention
- **Data Integration**: Support for multiple data sources and platforms
- **Privacy-First Design**: Secure handling of sensitive team and individual data

### Supported Data Sources
- Communication platforms (Slack, Microsoft Teams, etc.)
- Project management tools (Jira, Asana, Trello, etc.)
- Calendar and meeting data
- Code repository activity (GitHub, GitLab, etc.)
- HR systems and surveys
- Custom integrations via API

### Analytics and Reporting
- Team wellness dashboards
- Individual risk assessments
- Trend analysis and historical tracking
- Customizable alert thresholds
- Export capabilities for further analysis

## Architecture

The Team Wellbeing Agent follows a modular, microservices-based architecture:

```
┌─────────────────────────────────────────────────────────┐
│                 AskManager Platform                     │
├─────────────────────────────────────────────────────────┤
│              Team Wellbeing Agent                       │
├─────────────────┬─────────────────┬─────────────────────┤
│  Data Collectors │   AI Analytics  │  Alert & Actions    │
│                 │                 │                     │
│ • Slack API     │ • watsonx       │ • Notification      │
│ • Teams API     │   Orchestrate   │   Engine           │
│ • Jira API      │ • Risk Models   │ • Action           │
│ • GitHub API    │ • ML Pipeline   │   Recommendations  │
│ • Calendar API  │ • Pattern       │ • Dashboard        │
│ • Custom APIs   │   Detection     │   Updates          │
└─────────────────┴─────────────────┴─────────────────────┘
```

### Key Components

1. **Data Collection Layer**: Secure APIs and connectors for various data sources
2. **Processing Engine**: IBM watsonx Orchestrate for data analysis and ML processing
3. **Risk Assessment Module**: Algorithms for identifying wellness risks and patterns
4. **Alert System**: Configurable notification and recommendation engine
5. **Dashboard Interface**: Real-time visualization of team wellness metrics
6. **Security Layer**: Privacy protection and data governance controls

## Installation

### Prerequisites
- Python 3.8 or higher
- IBM watsonx Orchestrate access
- Required API credentials for target platforms

### Setup

1. Clone the repository:
```bash
git clone https://github.com/abcSTARK/team-wellbeing-agent.git
cd team-wellbeing-agent
```

2. Create and activate a virtual environment:
```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

3. Install dependencies:
```bash
pip install -r requirements.txt
```

4. Configure environment variables:
```bash
cp .env.example .env
# Edit .env with your API credentials and configuration
```

5. Initialize the agent:
```bash
python -m team_wellbeing_agent.cli init
```

## Usage

### Basic Configuration

1. **Set up data sources**:
```bash
python -m team_wellbeing_agent.cli add-source --type slack --token YOUR_SLACK_TOKEN
python -m team_wellbeing_agent.cli add-source --type jira --url YOUR_JIRA_URL --credentials YOUR_CREDS
```

2. **Configure team monitoring**:
```bash
python -m team_wellbeing_agent.cli create-team --name "Engineering Team" --members "@eng-team"
```

3. **Start monitoring**:
```bash
python -m team_wellbeing_agent.cli start-monitoring --team "Engineering Team"
```

### API Usage

```python
from team_wellbeing_agent import WellbeingAgent

# Initialize the agent
agent = WellbeingAgent(config_file='config.yaml')

# Add a team for monitoring
team = agent.add_team(
    name="Engineering Team",
    members=["user1", "user2", "user3"],
    data_sources=["slack", "jira", "github"]
)

# Get current wellness assessment
assessment = agent.assess_team_wellness(team.id)
print(f"Team wellness score: {assessment.score}")
print(f"Risk factors: {assessment.risk_factors}")

# Generate recommendations
recommendations = agent.get_recommendations(team.id)
for rec in recommendations:
    print(f"Action: {rec.action}, Priority: {rec.priority}")
```

### Dashboard Access

Access the web dashboard at `http://localhost:8080` after starting the service:

```bash
python -m team_wellbeing_agent.server start
```

## Configuration

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `WATSONX_API_KEY` | IBM watsonx Orchestrate API key | Yes |
| `WATSONX_URL` | IBM watsonx service URL | Yes |
| `DATABASE_URL` | Database connection string | Yes |
| `SLACK_BOT_TOKEN` | Slack bot token for data collection | Optional |
| `JIRA_API_TOKEN` | Jira API token | Optional |
| `GITHUB_TOKEN` | GitHub personal access token | Optional |

### Alert Configuration

Customize alert thresholds in `config/alerts.yaml`:

```yaml
risk_thresholds:
  burnout_risk: 0.7
  engagement_drop: 0.3
  communication_decline: 0.5

notification_channels:
  - type: email
    recipients: ["manager@company.com"]
  - type: slack
    channel: "#hr-alerts"
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Security and Privacy

The Team Wellbeing Agent is designed with privacy and security as core principles:

- **Data Minimization**: Only collects necessary data for wellness assessment
- **Encryption**: All data encrypted in transit and at rest
- **Access Controls**: Role-based access to sensitive information
- **Anonymization**: Individual data aggregated and anonymized where possible
- **Compliance**: Designed to support GDPR, CCPA, and other privacy regulations

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Documentation**: [Full documentation](https://docs.askmanager.com/team-wellbeing-agent)
- **Issues**: [GitHub Issues](https://github.com/abcSTARK/team-wellbeing-agent/issues)
- **Community**: [Discussions](https://github.com/abcSTARK/team-wellbeing-agent/discussions)
- **Email**: support@askmanager.com

## Acknowledgments

- IBM watsonx Orchestrate for AI processing capabilities
- Open source community contributors
- Research partners in workplace wellness
