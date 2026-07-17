package com.chetan.interviewprep.controller;

import com.chetan.interviewprep.dto.QuestionRequest;
import com.chetan.interviewprep.dto.QuestionResponse;
import com.chetan.interviewprep.service.InterviewQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// POST http://localhost:8080/api/questions/ingest
// Body: { "company": "Amazon", "role": "SDE-1", "questionText": "...", "pattern": "Arrays" }
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final InterviewQuestionService service;

    public QuestionController(InterviewQuestionService service) {
        this.service = service;
    }

    @PostMapping("/ingest")
    public ResponseEntity<QuestionResponse> ingest(@Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(service.ingest(request));
    }
}
