package com.chetan.interviewprep.dto;

import java.time.LocalDateTime;

public class QuestionResponse {

    private Long id;
    private String company;
    private String role;
    private String questionText;
    private String pattern;
    private LocalDateTime createdAt;

    public QuestionResponse() {
    }

    public QuestionResponse(Long id, String company, String role, String questionText,
                             String pattern, LocalDateTime createdAt) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.questionText = questionText;
        this.pattern = pattern;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
