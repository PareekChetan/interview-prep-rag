package com.chetan.interviewprep.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class GapAnalysisRequest {

    @NotBlank(message = "company is required")
    private String company;

    // The patterns the user has already practiced (e.g. from their LeetCode history)
    private List<String> solvedPatterns;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<String> getSolvedPatterns() {
        return solvedPatterns;
    }

    public void setSolvedPatterns(List<String> solvedPatterns) {
        this.solvedPatterns = solvedPatterns;
    }
}
