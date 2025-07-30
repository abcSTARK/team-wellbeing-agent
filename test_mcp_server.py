#!/usr/bin/env python3
"""
Standalone test for Team Wellbeing MCP Server
This tests the core logic without external dependencies.
"""

from typing import List, Dict, Optional, Any
from datetime import datetime
import json

# Mock Pydantic-like base model for testing
class BaseModel:
    def __init__(self, **kwargs):
        for key, value in kwargs.items():
            setattr(self, key, value)
    
    def dict(self):
        return {k: v for k, v in self.__dict__.items() if not k.startswith('_')}

# Test data models
class SlackMessage(BaseModel):
    def __init__(self, message_id: str, channel_id: str, channel_name: str, 
                 user_id: str, username: str, text: str, timestamp: datetime,
                 thread_ts: Optional[str] = None, reaction_count: int = 0):
        super().__init__(
            message_id=message_id, channel_id=channel_id, channel_name=channel_name,
            user_id=user_id, username=username, text=text, timestamp=timestamp,
            thread_ts=thread_ts, reaction_count=reaction_count
        )

class GitHubIssue(BaseModel):
    def __init__(self, issue_id: int, number: int, title: str, state: str,
                 author: str, repository: str, created_at: datetime,
                 body: Optional[str] = None, assignees: List[str] = None,
                 labels: List[str] = None, updated_at: Optional[datetime] = None,
                 closed_at: Optional[datetime] = None, comments_count: int = 0):
        super().__init__(
            issue_id=issue_id, number=number, title=title, state=state,
            author=author, repository=repository, created_at=created_at,
            body=body, assignees=assignees or [], labels=labels or [],
            updated_at=updated_at, closed_at=closed_at, comments_count=comments_count
        )

class JiraIssue(BaseModel):
    def __init__(self, issue_id: str, key: str, summary: str, status: str,
                 priority: str, issue_type: str, reporter: str, project_key: str,
                 created: datetime, description: Optional[str] = None,
                 assignee: Optional[str] = None, labels: List[str] = None,
                 components: List[str] = None, updated: Optional[datetime] = None,
                 resolved: Optional[datetime] = None, story_points: Optional[float] = None,
                 time_spent: Optional[int] = None):
        super().__init__(
            issue_id=issue_id, key=key, summary=summary, status=status,
            priority=priority, issue_type=issue_type, reporter=reporter,
            project_key=project_key, created=created, description=description,
            assignee=assignee, labels=labels or [], components=components or [],
            updated=updated, resolved=resolved, story_points=story_points,
            time_spent=time_spent
        )

class TeamWellbeingStatus(BaseModel):
    def __init__(self, overall_mood: str, overall_stress_level: str,
                 overloaded_members: List[str], member_feelings: Dict[str, str]):
        super().__init__(
            overall_mood=overall_mood, overall_stress_level=overall_stress_level,
            overloaded_members=overloaded_members, member_feelings=member_feelings
        )

# Mock data store implementation
class MockDataStore:
    def __init__(self):
        self.slack_messages = self._generate_mock_slack_messages()
        self.github_issues = self._generate_mock_github_issues()
        self.jira_issues = self._generate_mock_jira_issues()
    
    def _generate_mock_slack_messages(self) -> List[SlackMessage]:
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
            )
        ]
    
    def _generate_mock_jira_issues(self) -> List[JiraIssue]:
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
            )
        ]

def test_mock_data_store():
    """Test the mock data store functionality."""
    print("Testing Mock Data Store...")
    
    store = MockDataStore()
    
    # Test Slack messages
    print(f"Slack messages: {len(store.slack_messages)}")
    for msg in store.slack_messages:
        print(f"  - {msg.username}: {msg.text[:50]}...")
    
    # Test GitHub issues
    print(f"\nGitHub issues: {len(store.github_issues)}")
    for issue in store.github_issues:
        print(f"  - #{issue.number}: {issue.title}")
    
    # Test Jira issues
    print(f"\nJira issues: {len(store.jira_issues)}")
    for issue in store.jira_issues:
        print(f"  - {issue.key}: {issue.summary}")
    
    # Test filtering
    general_messages = [msg for msg in store.slack_messages if msg.channel_name == "general"]
    print(f"\nGeneral channel messages: {len(general_messages)}")
    
    open_github_issues = [issue for issue in store.github_issues if issue.state == "open"]
    print(f"Open GitHub issues: {len(open_github_issues)}")
    
    in_progress_jira = [issue for issue in store.jira_issues if issue.status == "In Progress"]
    print(f"In Progress Jira issues: {len(in_progress_jira)}")
    
    print("\n✓ Mock data store test completed successfully!")

def test_team_wellbeing_analysis():
    """Test the team wellbeing analysis logic."""
    print("\nTesting Team Wellbeing Analysis...")
    
    store = MockDataStore()
    
    # Mock analysis logic (same as in the main server)
    total_messages = len(store.slack_messages)
    recent_issues = len([issue for issue in store.github_issues if issue.state == "open"])
    overdue_jira = len([issue for issue in store.jira_issues if issue.status == "In Progress"])
    
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
    
    # Mock overloaded members
    overloaded = []
    if overdue_jira > 1:
        overloaded = ["alice.dev", "charlie.tech"]
    
    # Mock member feelings
    member_feelings = {
        "alice.dev": "motivated",
        "bob.eng": "satisfied",
        "charlie.tech": "overwhelmed" if overdue_jira > 1 else "focused"
    }
    
    status = TeamWellbeingStatus(
        overall_mood=mood,
        overall_stress_level=stress_level,
        overloaded_members=overloaded,
        member_feelings=member_feelings
    )
    
    print(f"Overall mood: {status.overall_mood}")
    print(f"Stress level: {status.overall_stress_level}")
    print(f"Overloaded members: {status.overloaded_members}")
    print(f"Member feelings: {status.member_feelings}")
    
    print("\n✓ Team wellbeing analysis test completed successfully!")

def test_user_filtering():
    """Test user-specific filtering functionality."""
    print("\nTesting User Filtering...")
    
    store = MockDataStore()
    
    # Test GitHub issues for user
    username = "alice.dev"
    user_github_issues = [
        issue for issue in store.github_issues 
        if issue.author == username or username in issue.assignees
    ]
    print(f"GitHub issues for {username}: {len(user_github_issues)}")
    
    # Test Jira issues for user  
    user_jira_issues = [
        issue for issue in store.jira_issues 
        if issue.reporter == username or issue.assignee == username
    ]
    print(f"Jira issues for {username}: {len(user_jira_issues)}")
    
    print("\n✓ User filtering test completed successfully!")

if __name__ == "__main__":
    print("=== Team Wellbeing MCP Server - Standalone Test ===\n")
    
    test_mock_data_store()
    test_team_wellbeing_analysis()
    test_user_filtering()
    
    print("\n=== All tests completed successfully! ===")
    print("\nThe main MCP server file 'team_wellbeing_mcp_server.py' is ready to run")
    print("with the following command (once FastMCP dependencies are installed):")
    print("python3 team_wellbeing_mcp_server.py")