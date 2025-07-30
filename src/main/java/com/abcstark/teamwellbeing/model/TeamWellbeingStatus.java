package com.abcstark.teamwellbeing.model;

import java.util.List;
import java.util.Map;

public class TeamWellbeingStatus {
    private String overallMood;
    private String overallStressLevel;
    private List<String> overloadedMembers;
    private Map<String, String> memberFeelings;

    public TeamWellbeingStatus() {}

    public TeamWellbeingStatus(String overallMood, String overallStressLevel, List<String> overloadedMembers, Map<String, String> memberFeelings) {
        this.overallMood = overallMood;
        this.overallStressLevel = overallStressLevel;
        this.overloadedMembers = overloadedMembers;
        this.memberFeelings = memberFeelings;
    }

    public String getOverallMood() {
        return overallMood;
    }

    public void setOverallMood(String overallMood) {
        this.overallMood = overallMood;
    }

    public String getOverallStressLevel() {
        return overallStressLevel;
    }

    public void setOverallStressLevel(String overallStressLevel) {
        this.overallStressLevel = overallStressLevel;
    }

    public List<String> getOverloadedMembers() {
        return overloadedMembers;
    }

    public void setOverloadedMembers(List<String> overloadedMembers) {
        this.overloadedMembers = overloadedMembers;
    }

    public Map<String, String> getMemberFeelings() {
        return memberFeelings;
    }

    public void setMemberFeelings(Map<String, String> memberFeelings) {
        this.memberFeelings = memberFeelings;
    }
}

