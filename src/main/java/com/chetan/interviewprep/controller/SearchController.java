package com.chetan.interviewprep.controller;

import com.chetan.interviewprep.dto.SearchRequest;
import com.chetan.interviewprep.dto.SearchResultResponse;
import com.chetan.interviewprep.service.InterviewQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// POST http://localhost:8080/api/search
// Body: { "query": "graph traversal shortest path problem", "company": "Amazon", "limit": 5 }
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final InterviewQuestionService service;

    public SearchController(InterviewQuestionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<List<SearchResultResponse>> search(@Valid @RequestBody SearchRequest request) {
        return ResponseEntity.ok(service.search(request));
    }
}
