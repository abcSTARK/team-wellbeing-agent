"""
Team Wellbeing Agent MCP Server

A FastAPI-based MCP server for the Team Wellbeing Agent REST API.
This server provides endpoints for monitoring team health and wellbeing
through Slack, GitHub, and Jira integrations.

This implementation provides mock/sample data for demonstration purposes.
Comments are included to guide future integration with real services or databases.
"""

from fastmcp import FastMCP
from pydantic import BaseModel, Field
from typing import List, Dict, Optional, Any
from datetime import datetime
from starlette.requests import Request
from starlette.responses import PlainTextResponse, JSONResponse
import json

# Initialize the FastMCP server
mcp = FastMCP("Team Wellbeing Agent MCP")

# Pydantic models for structured data
class SlackMessage(BaseModel):
    """Represents a Slack message for team wellbeing analysis."""
    message_id: str
    channel_id: str
    channel_name: str
    user_id: str
    username: str
    text: str
    timestamp: datetime
    thread_ts: Optional[str] = None
    reaction_count: int = 0
    
    class Config:
        from_attributes = True

class GitHubIssue(BaseModel):
    """Represents a GitHub issue for team wellbeing analysis."""
    issue_id: int
    number: int
    title: str
    body: Optional[str] = None
    state: str
    author: str
    assignees: List[str] = []
    labels: List[str] = []
    created_at: datetime
    updated_at: Optional[datetime] = None
    closed_at: Optional[datetime] = None
    repository: str
    comments_count: int = 0
    
    class Config:
        from_attributes = True

class JiraIssue(BaseModel):
    """Represents a Jira issue for team wellbeing analysis."""
    issue_id: str
    key: str
    summary: str
    description: Optional[str] = None
    status: str
    priority: str
    issue_type: str
    reporter: str
    assignee: Optional[str] = None
    labels: List[str] = []
    components: List[str] = []
    created: datetime
    updated: Optional[datetime] = None
    resolved: Optional[datetime] = None
    project_key: str
    story_points: Optional[float] = None
    time_spent: Optional[int] = None  # in seconds
    
    class Config:
        from_attributes = True

class TeamWellbeingStatus(BaseModel):
    """Represents team wellbeing status for orchestrator."""
    overall_mood: str
    overall_stress_level: str
    overloaded_members: List[str]
    member_feelings: Dict[str, str]
    
    class Config:
        from_attributes = True

class ConnectionStatus(BaseModel):
    """Represents connection test results."""
    slack: bool
    github: bool
    jira: bool
    overall: bool

class CollectionResponse(BaseModel):
    """Response for data collection operations."""
    status: str
    message: str

class AllDataResponse(BaseModel):
    """Response containing all collected data."""
    slack_messages: List[SlackMessage]
    github_issues: List[GitHubIssue]
    jira_issues: List[JiraIssue]
    statistics: Dict[str, Any]

# Mock data storage (in production, this would be replaced with a real database)
class MockDataStore:
    """Mock data store for demonstration purposes."""
    
    def __init__(self):
        self.slack_messages = self._generate_mock_slack_messages()
        self.github_issues = self._generate_mock_github_issues()
        self.jira_issues = self._generate_mock_jira_issues()
    
    def _generate_mock_slack_messages(self) -> List[SlackMessage]:
        """Generate mock Slack messages for demonstration."""
        return [
            SlackMessage(
                message_id="msg1",
                channel_id="C1234567890",
                channel_name="general",
                user_id="U1111111111",
                username="alice.dev",
                text="Good morning team! Ready for the sprint planning today 🚀",
                timestamp=datetime(2024, 1, 15, 9, 0, 0),
                reaction_count=5
            ),
            SlackMessage(
                message_id="msg2",
                channel_id="C1234567890",
                channel_name="general",
                user_id="U2222222222",
                username="bob.eng",
                text="The deployment went smoothly yesterday, great work everyone!",
                timestamp=datetime(2024, 1, 15, 10, 30, 0),
                reaction_count=8
            ),
            SlackMessage(
                message_id="msg3",
                channel_id="C0987654321",
                channel_name="development",
                user_id="U3333333333",
                username="charlie.tech",
                text="Need help with the new API design, anyone available for a quick review?",
                timestamp=datetime(2024, 1, 15, 14, 15, 0),
                reaction_count=3
            )
        ]
    
    def _generate_mock_github_issues(self) -> List[GitHubIssue]:
        """Generate mock GitHub issues for demonstration."""
        return [
            GitHubIssue(
                issue_id=1001,
                number=101,
                title="Add user authentication to API",
                body="We need to implement JWT-based authentication for the new API endpoints.",
                state="open",
                author="alice.dev",
                assignees=["bob.eng"],
                labels=["enhancement", "security"],
                created_at=datetime(2024, 1, 10, 9, 0, 0),
                updated_at=datetime(2024, 1, 15, 10, 0, 0),
                repository="team-wellbeing-agent",
                comments_count=5
            ),
            GitHubIssue(
                issue_id=1002,
                number=102,
                title="Fix memory leak in data collector",
                body="The data collection service is consuming too much memory over time.",
                state="open",
                author="charlie.tech",
                assignees=["alice.dev", "charlie.tech"],
                labels=["bug", "high-priority"],
                created_at=datetime(2024, 1, 12, 14, 30, 0),
                updated_at=datetime(2024, 1, 15, 11, 45, 0),
                repository="team-wellbeing-agent",
                comments_count=8
            ),
            GitHubIssue(
                issue_id=1003,
                number=103,
                title="Update documentation for new endpoints",
                body="Add documentation for the new MCP server endpoints.",
                state="closed",
                author="bob.eng",
                assignees=["bob.eng"],
                labels=["documentation"],
                created_at=datetime(2024, 1, 8, 16, 0, 0),
                updated_at=datetime(2024, 1, 14, 17, 30, 0),
                closed_at=datetime(2024, 1, 14, 17, 30, 0),
                repository="team-wellbeing-agent",
                comments_count=2
            )
        ]
    
    def _generate_mock_jira_issues(self) -> List[JiraIssue]:
        """Generate mock Jira issues for demonstration."""
        return [
            JiraIssue(
                issue_id="10001",
                key="TEAM-101",
                summary="Implement team mood analytics dashboard",
                description="Create a dashboard to visualize team mood trends over time.",
                status="In Progress",
                priority="High",
                issue_type="Story",
                reporter="alice.dev",
                assignee="bob.eng",
                labels=["analytics", "dashboard"],
                components=["Frontend", "Analytics"],
                created=datetime(2024, 1, 8, 10, 0, 0),
                updated=datetime(2024, 1, 15, 9, 30, 0),
                project_key="TEAM",
                story_points=8.0,
                time_spent=14400  # 4 hours in seconds
            ),
            JiraIssue(
                issue_id="10002",
                key="TEAM-102",
                summary="Set up automated slack message analysis",
                description="Configure the system to automatically analyze slack messages for sentiment.",
                status="To Do",
                priority="Medium",
                issue_type="Task",
                reporter="charlie.tech",
                assignee="alice.dev",
                labels=["automation", "slack"],
                components=["Backend", "Integrations"],
                created=datetime(2024, 1, 12, 11, 0, 0),
                updated=datetime(2024, 1, 15, 8, 45, 0),
                project_key="TEAM",
                story_points=5.0
            ),
            JiraIssue(
                issue_id="10003",
                key="TEAM-103",
                summary="Research burnout detection algorithms",
                description="Investigate different approaches for detecting team member burnout.",
                status="Done",
                priority="Low",
                issue_type="Research",
                reporter="bob.eng",
                assignee="charlie.tech",
                labels=["research", "algorithms"],
                components=["Analytics"],
                created=datetime(2024, 1, 5, 14, 0, 0),
                updated=datetime(2024, 1, 11, 16, 0, 0),
                resolved=datetime(2024, 1, 11, 16, 0, 0),
                project_key="TEAM",
                story_points=3.0,
                time_spent=10800  # 3 hours in seconds
            )
        ]
    
    def clear_all_data(self):
        """Clear all stored data."""
        self.slack_messages = []
        self.github_issues = []
        self.jira_issues = []
    
    def get_statistics(self) -> Dict[str, Any]:
        """Get storage statistics."""
        return {
            "slack_messages_count": len(self.slack_messages),
            "github_issues_count": len(self.github_issues),
            "jira_issues_count": len(self.jira_issues),
            "last_updated": datetime.now().isoformat()
        }

# Initialize the mock data store
data_store = MockDataStore()

# MCP Tool endpoints
@mcp.tool()
def test_connections() -> ConnectionStatus:
    """
    Test all integration connections.
    Returns the status of Slack, GitHub, and Jira connections.
    
    In a real implementation, this would test actual API connections.
    """
    # Mock connection tests (in production, these would be real API calls)
    slack_ok = True  # Mock successful Slack connection
    github_ok = True  # Mock successful GitHub connection  
    jira_ok = True   # Mock successful Jira connection
    
    overall = slack_ok and github_ok and jira_ok
    
    return ConnectionStatus(
        slack=slack_ok,
        github=github_ok,
        jira=jira_ok,
        overall=overall
    )

@mcp.tool()
def collect_data() -> CollectionResponse:
    """
    Manually trigger data collection from all integrations.
    
    In a real implementation, this would trigger data collection services
    to fetch fresh data from Slack, GitHub, and Jira APIs.
    """
    try:
        # Mock data collection process
        # In production, this would:
        # 1. Call Slack API to fetch recent messages
        # 2. Call GitHub API to fetch recent issues
        # 3. Call Jira API to fetch recent tickets
        # 4. Store the collected data in the database
        
        # For demo, we just refresh our mock data
        global data_store
        data_store = MockDataStore()
        
        return CollectionResponse(
            status="success",
            message="Data collection triggered successfully"
        )
    except Exception as e:
        return CollectionResponse(
            status="error",
            message=f"Data collection failed: {str(e)}"
        )

@mcp.tool()
def get_slack_messages(channel: str = "general") -> List[SlackMessage]:
    """
    Get recent Slack messages from a specific channel.
    
    Args:
        channel: The Slack channel name (default: 'general')
    
    In a real implementation, this would query the Slack API.
    """
    # Filter messages by channel name
    filtered_messages = [
        msg for msg in data_store.slack_messages 
        if msg.channel_name == channel
    ]
    
    # If no messages found for the channel, return all messages
    if not filtered_messages:
        return data_store.slack_messages
    
    return filtered_messages

@mcp.tool()
def get_slack_channels() -> List[str]:
    """
    Get available Slack channels.
    
    In a real implementation, this would call the Slack API
    to get the list of channels the bot has access to.
    """
    # Extract unique channel names from mock data
    channels = list(set(msg.channel_name for msg in data_store.slack_messages))
    
    # Add some additional mock channels
    all_channels = channels + ["random", "engineering", "design", "product"]
    return list(set(all_channels))

@mcp.tool()
def get_github_issues() -> List[GitHubIssue]:
    """
    Get recent GitHub issues.
    
    In a real implementation, this would query the GitHub API
    for recent issues in the configured repositories.
    """
    return data_store.github_issues

@mcp.tool()
def get_github_stats() -> Dict[str, str]:
    """
    Get GitHub repository statistics.
    
    In a real implementation, this would call the GitHub API
    to get repository statistics like commits, contributors, etc.
    """
    open_issues = len([issue for issue in data_store.github_issues if issue.state == "open"])
    closed_issues = len([issue for issue in data_store.github_issues if issue.state == "closed"])
    
    stats = {
        "repository": "team-wellbeing-agent",
        "open_issues": str(open_issues),
        "closed_issues": str(closed_issues),
        "total_issues": str(len(data_store.github_issues)),
        "recent_activity": "5 commits in the last week"
    }
    
    return stats

@mcp.tool()
def get_github_issues_for_user(username: str) -> List[GitHubIssue]:
    """
    Get GitHub issues for a specific user.
    
    Args:
        username: The GitHub username to filter by
    
    In a real implementation, this would query the GitHub API
    for issues assigned to or created by the specified user.
    """
    user_issues = [
        issue for issue in data_store.github_issues 
        if issue.author == username or username in issue.assignees
    ]
    return user_issues

@mcp.tool()
def get_jira_issues() -> List[JiraIssue]:
    """
    Get recent Jira issues.
    
    In a real implementation, this would query the Jira API
    for recent issues in the configured project(s).
    """
    return data_store.jira_issues

@mcp.tool()
def get_jira_stats() -> Dict[str, str]:
    """
    Get Jira project statistics.
    
    In a real implementation, this would call the Jira API
    to get project statistics like story points, cycle time, etc.
    """
    todo_count = len([issue for issue in data_store.jira_issues if issue.status == "To Do"])
    in_progress_count = len([issue for issue in data_store.jira_issues if issue.status == "In Progress"])
    done_count = len([issue for issue in data_store.jira_issues if issue.status == "Done"])
    
    total_story_points = sum(issue.story_points or 0 for issue in data_store.jira_issues)
    
    stats = {
        "project": "TEAM",
        "todo": str(todo_count),
        "in_progress": str(in_progress_count),
        "done": str(done_count),
        "total_story_points": str(total_story_points),
        "average_cycle_time": "3.5 days"
    }
    
    return stats

@mcp.tool()
def get_jira_issues_for_user(username: str) -> List[JiraIssue]:
    """
    Get Jira issues for a specific user.
    
    Args:
        username: The Jira username to filter by
    
    In a real implementation, this would query the Jira API
    for issues assigned to or reported by the specified user.
    """
    user_issues = [
        issue for issue in data_store.jira_issues 
        if issue.reporter == username or issue.assignee == username
    ]
    return user_issues

@mcp.tool()
def get_all_data() -> AllDataResponse:
    """
    Get all collected data from storage.
    
    In a real implementation, this would query the database
    for all stored team wellbeing data.
    """
    return AllDataResponse(
        slack_messages=data_store.slack_messages,
        github_issues=data_store.github_issues,
        jira_issues=data_store.jira_issues,
        statistics=data_store.get_statistics()
    )

@mcp.tool()
def clear_all_data() -> CollectionResponse:
    """
    Clear all collected data.
    
    In a real implementation, this would clear the database
    or mark data as deleted.
    """
    try:
        data_store.clear_all_data()
        return CollectionResponse(
            status="success",
            message="All data cleared successfully"
        )
    except Exception as e:
        return CollectionResponse(
            status="error",
            message=f"Failed to clear data: {str(e)}"
        )

@mcp.tool()
def get_team_wellbeing_status() -> TeamWellbeingStatus:
    """
    Fetch team wellbeing status for the MCP orchestrator.
    
    In a real implementation, this would analyze collected data
    to determine team mood, stress levels, and risk factors.
    """
    # Mock analysis based on the current data
    # In production, this would use ML models or sentiment analysis
    
    # Simple mock analysis
    total_messages = len(data_store.slack_messages)
    recent_issues = len([issue for issue in data_store.github_issues if issue.state == "open"])
    overdue_jira = len([issue for issue in data_store.jira_issues if issue.status == "In Progress"])
    
    # Determine overall mood based on activity
    if total_messages > 5 and recent_issues < 3:
        mood = "positive"
    elif recent_issues > 5 or overdue_jira > 2:
        mood = "stressed"
    else:
        mood = "neutral"
    
    # Determine stress level
    if recent_issues > 3 or overdue_jira > 1:
        stress_level = "high"
    elif recent_issues > 1:
        stress_level = "medium"
    else:
        stress_level = "low"
    
    # Mock overloaded members (in production, this would be based on workload analysis)
    overloaded = []
    if overdue_jira > 1:
        overloaded = ["alice.dev", "charlie.tech"]
    
    # Mock member feelings (in production, this would be from sentiment analysis)
    member_feelings = {
        "alice.dev": "motivated",
        "bob.eng": "satisfied",
        "charlie.tech": "overwhelmed" if overdue_jira > 1 else "focused"
    }
    
    return TeamWellbeingStatus(
        overall_mood=mood,
        overall_stress_level=stress_level,
        overloaded_members=overloaded,
        member_feelings=member_feelings
    )

# Custom routes for HTTP endpoints
@mcp.custom_route("/", methods=["GET"])
async def health_check(request: Request) -> JSONResponse:
    """Health check endpoint."""
    response_data = {
        "status": "UP",
        "timestamp": datetime.now().isoformat(),
        "service": "Team Wellbeing Agent MCP Server",
        "version": "1.0.0"
    }
    return JSONResponse(content=response_data)

# Additional custom routes for REST API compatibility
@mcp.custom_route("/test-connections", methods=["GET"])
async def rest_test_connections(request: Request) -> JSONResponse:
    """REST endpoint for testing connections."""
    result = test_connections()
    return JSONResponse(content=result.dict())

@mcp.custom_route("/collect-data", methods=["POST"])
async def rest_collect_data(request: Request) -> JSONResponse:
    """REST endpoint for triggering data collection."""
    result = collect_data()
    return JSONResponse(content=result.dict())

@mcp.custom_route("/slack/messages", methods=["GET"])
async def rest_slack_messages(request: Request) -> JSONResponse:
    """REST endpoint for getting Slack messages."""
    channel = request.query_params.get("channel", "general")
    messages = get_slack_messages(channel)
    return JSONResponse(content=[msg.dict() for msg in messages])

@mcp.custom_route("/slack/channels", methods=["GET"])
async def rest_slack_channels(request: Request) -> JSONResponse:
    """REST endpoint for getting Slack channels."""
    channels = get_slack_channels()
    return JSONResponse(content=channels)

@mcp.custom_route("/github/issues", methods=["GET"])
async def rest_github_issues(request: Request) -> JSONResponse:
    """REST endpoint for getting GitHub issues."""
    issues = get_github_issues()
    return JSONResponse(content=[issue.dict() for issue in issues])

@mcp.custom_route("/github/stats", methods=["GET"])
async def rest_github_stats(request: Request) -> JSONResponse:
    """REST endpoint for getting GitHub statistics."""
    stats = get_github_stats()
    return JSONResponse(content={"statistics": stats})

@mcp.custom_route("/github/issues/user/{username}", methods=["GET"])
async def rest_github_user_issues(request: Request) -> JSONResponse:
    """REST endpoint for getting GitHub issues for a specific user."""
    username = request.path_params["username"]
    issues = get_github_issues_for_user(username)
    return JSONResponse(content=[issue.dict() for issue in issues])

@mcp.custom_route("/jira/issues", methods=["GET"])
async def rest_jira_issues(request: Request) -> JSONResponse:
    """REST endpoint for getting Jira issues."""
    issues = get_jira_issues()
    return JSONResponse(content=[issue.dict() for issue in issues])

@mcp.custom_route("/jira/stats", methods=["GET"])
async def rest_jira_stats(request: Request) -> JSONResponse:
    """REST endpoint for getting Jira statistics."""
    stats = get_jira_stats()
    return JSONResponse(content={"statistics": stats})

@mcp.custom_route("/jira/issues/user/{username}", methods=["GET"])
async def rest_jira_user_issues(request: Request) -> JSONResponse:
    """REST endpoint for getting Jira issues for a specific user."""
    username = request.path_params["username"]
    issues = get_jira_issues_for_user(username)
    return JSONResponse(content=[issue.dict() for issue in issues])

@mcp.custom_route("/data/all", methods=["GET"])
async def rest_all_data(request: Request) -> JSONResponse:
    """REST endpoint for getting all collected data."""
    data = get_all_data()
    return JSONResponse(content=data.dict())

@mcp.custom_route("/data/clear", methods=["DELETE"])
async def rest_clear_data(request: Request) -> JSONResponse:
    """REST endpoint for clearing all data."""
    result = clear_all_data()
    return JSONResponse(content=result.dict())

@mcp.custom_route("/mcp/status", methods=["GET"])
async def rest_wellbeing_status(request: Request) -> JSONResponse:
    """REST endpoint for getting team wellbeing status."""
    status = get_team_wellbeing_status()
    return JSONResponse(content=status.dict())

if __name__ == "__main__":
    # Run the MCP server
    mcp.run(transport="streamable-http", host="0.0.0.0", port=8080)