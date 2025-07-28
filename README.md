# Team Wellbeing Agent

The Team Wellbeing Agent is a modular AI component of the AskManager platform, designed to monitor team health indicators and proactively surface risks of disengagement or burnout. It leverages IBM watsonx Orchestrate to integrate, analyze, and synthesize signals from multiple data sources, generating actionable wellness alerts.

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
