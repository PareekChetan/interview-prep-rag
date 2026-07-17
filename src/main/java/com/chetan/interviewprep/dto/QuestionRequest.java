package com.chetan.interviewprep.dto;

import jakarta.validation.constraints.NotBlank;

// DTOs (Data Transfer Objects) exist so we never expose our JPA entities
// directly over the API. This gives us control over exactly what shape of
// JSON we accept/return, independent of how the database table looks.
public class QuestionRequest {

    @NotBlank(message = "company is required")
    private String company;

    private String role;

    @NotBlank(message = "questionText is required")
    private String questionText;

    private String pattern;

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
}
