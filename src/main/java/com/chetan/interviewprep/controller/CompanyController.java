package com.chetan.interviewprep.controller;

import com.chetan.interviewprep.dto.QuestionResponse;
import com.chetan.interviewprep.service.InterviewQuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// GET http://localhost:8080/api/companies/Amazon/questions
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final InterviewQuestionService service;

    public CompanyController(InterviewQuestionService service) {
        this.service = service;
    }

    @GetMapping("/{name}/questions")
    public ResponseEntity<List<QuestionResponse>> byCompany(@PathVariable String name) {
        return ResponseEntity.ok(service.findByCompany(name));
    }
}
