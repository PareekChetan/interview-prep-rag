package com.chetan.interviewprep.dto;

public class SearchResultResponse {

    private Long id;
    private String company;
    private String role;
    private String questionText;
    private String pattern;

    // Cosine distance - LOWER means more similar (0 = identical direction).
    // We expose this so the frontend can show a "match strength" indicator.
    private double distance;

    public SearchResultResponse() {
    }

    public SearchResultResponse(Long id, String company, String role, String questionText,
                                 String pattern, double distance) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.questionText = questionText;
        this.pattern = pattern;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
