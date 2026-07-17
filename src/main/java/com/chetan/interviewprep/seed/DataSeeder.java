package com.chetan.interviewprep.seed;

import com.chetan.interviewprep.dto.QuestionRequest;
import com.chetan.interviewprep.repository.InterviewQuestionRepository;
import com.chetan.interviewprep.service.InterviewQuestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

// CommandLineRunner's run() method executes automatically once, right after
// the Spring app finishes starting up. We use it here to seed the database
// with sample questions on the very first run, so you have data to search
// against immediately instead of ingesting everything by hand first.
@Component
public class DataSeeder implements CommandLineRunner {

    private final InterviewQuestionRepository repository;
    private final InterviewQuestionService service;
    private final ObjectMapper objectMapper;

    @Value("classpath:data/sample-questions.json")
    private Resource sampleData;

    public DataSeeder(InterviewQuestionRepository repository,
                       InterviewQuestionService service,
                       ObjectMapper objectMapper) {
        this.repository = repository;
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            return; // already seeded - don't duplicate on every restart
        }

        List<QuestionRequest> seedQuestions = objectMapper.readValue(
                sampleData.getInputStream(), new TypeReference<List<QuestionRequest>>() {
                }
        );

        for (QuestionRequest q : seedQuestions) {
            service.ingest(q);
        }

        System.out.println("Seeded " + seedQuestions.size() + " sample interview questions.");
    }
}
