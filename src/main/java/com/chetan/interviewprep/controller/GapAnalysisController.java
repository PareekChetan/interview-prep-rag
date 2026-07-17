package com.chetan.interviewprep.controller;

import com.chetan.interviewprep.dto.GapAnalysisRequest;
import com.chetan.interviewprep.dto.GapAnalysisResponse;
import com.chetan.interviewprep.service.InterviewQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// POST http://localhost:8080/api/gap-analysis
// Body: { "company": "Amazon", "solvedPatterns": ["Arrays", "Strings"] }
@RestController
@RequestMapping("/api/gap-analysis")
public class GapAnalysisController {

    private final InterviewQuestionService service;

    public GapAnalysisController(InterviewQuestionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GapAnalysisResponse> analyze(@Valid @RequestBody GapAnalysisRequest request) {
        return ResponseEntity.ok(service.gapAnalysis(request));
    }
}
