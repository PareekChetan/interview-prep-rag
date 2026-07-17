package com.chetan.interviewprep.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// @Entity tells Hibernate "this class represents a database table."
// Note: we deliberately do NOT map the "embedding" column here. Hibernate
// doesn't understand pgvector's "vector" type out of the box, so we handle
// that column separately with raw JDBC (see VectorSearchRepository).
// This class only knows about the normal, everyday columns.
@Entity
@Table(name = "questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    private String role;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    private String pattern;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // JPA requires a no-args constructor
    public InterviewQuestion() {
    }

    // ----- Getters and setters -----
    // (Spring/Hibernate use these to read and populate the object's fields)

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
