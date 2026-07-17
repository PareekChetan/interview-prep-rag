package com.chetan.interviewprep.service;

import com.chetan.interviewprep.dto.*;
import com.chetan.interviewprep.embedding.EmbeddingService;
import com.chetan.interviewprep.model.InterviewQuestion;
import com.chetan.interviewprep.repository.InterviewQuestionRepository;
import com.chetan.interviewprep.repository.VectorSearchRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// This is where the three pieces of RAG come together for each feature:
//   ingest()      -> stores a question AND its embedding (the "indexing" side)
//   search()      -> embeds a query, retrieves the closest matches (the "R" in RAG)
//   gapAnalysis() -> a simpler feature that doesn't need embeddings at all,
//                    just plain aggregation over stored data
@Service
public class InterviewQuestionService {

    private final InterviewQuestionRepository repository;
    private final VectorSearchRepository vectorSearchRepository;
    private final EmbeddingService embeddingService;

    public InterviewQuestionService(InterviewQuestionRepository repository,
                                     VectorSearchRepository vectorSearchRepository,
                                     EmbeddingService embeddingService) {
        this.repository = repository;
        this.vectorSearchRepository = vectorSearchRepository;
        this.embeddingService = embeddingService;
    }

    public QuestionResponse ingest(QuestionRequest request) {
        InterviewQuestion question = new InterviewQuestion();
        question.setCompany(request.getCompany());
        question.setRole(request.getRole());
        question.setQuestionText(request.getQuestionText());
        question.setPattern(request.getPattern());
        question.setCreatedAt(LocalDateTime.now());

        // Step 1: save the "normal" fields via JPA, which gives us a generated ID
        InterviewQuestion saved = repository.save(question);

        // Step 2: turn the question text into a vector
        float[] embedding = embeddingService.embed(request.getQuestionText());

        // Step 3: store that vector against the row we just created
        vectorSearchRepository.saveEmbedding(saved.getId(), embedding);

        return toResponse(saved);
    }

    public List<SearchResultResponse> search(SearchRequest request) {
        // Embed the user's query the exact same way we embedded the stored
        // questions - this is essential, mixing embedding models/methods
        // between storage and search silently breaks similarity search.
        float[] queryEmbedding = embeddingService.embed(request.getQuery());
        int limit = request.getLimit() != null ? request.getLimit() : 10;

        return vectorSearchRepository.findSimilar(queryEmbedding, request.getCompany(), limit)
                .stream()
                .map(r -> new SearchResultResponse(
                        r.id(), r.company(), r.role(), r.questionText(), r.pattern(), r.distance()
                ))
                .collect(Collectors.toList());
    }

    public GapAnalysisResponse gapAnalysis(GapAnalysisRequest request) {
        List<InterviewQuestion> companyQuestions = repository.findByCompanyIgnoreCase(request.getCompany());

        // Count how often each pattern (Arrays, DP, Graphs...) shows up for this company
        Map<String, Long> patternCounts = companyQuestions.stream()
                .filter(q -> q.getPattern() != null)
                .collect(Collectors.groupingBy(InterviewQuestion::getPattern, Collectors.counting()));

        List<String> solved = request.getSolvedPatterns() == null
                ? List.of()
                : request.getSolvedPatterns().stream().map(String::toLowerCase).collect(Collectors.toList());

        // The "gap" is: patterns this company asks about that the user hasn't
        // told us they've practiced, sorted by how common that pattern is.
        List<PatternGap> gaps = patternCounts.entrySet().stream()
                .filter(e -> !solved.contains(e.getKey().toLowerCase()))
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> new PatternGap(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new GapAnalysisResponse(request.getCompany(), gaps);
    }

    public List<QuestionResponse> findByCompany(String company) {
        return repository.findByCompanyIgnoreCase(company)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private QuestionResponse toResponse(InterviewQuestion q) {
        return new QuestionResponse(
                q.getId(), q.getCompany(), q.getRole(), q.getQuestionText(), q.getPattern(), q.getCreatedAt()
        );
    }
}
