package com.chetan.interviewprep.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

// Why raw JDBC instead of Spring Data JPA here? Because Hibernate doesn't have
// a built-in mapping for pgvector's "vector" type. Rather than fighting the
// framework, we just talk to Postgres directly for the two vector-specific
// operations: saving an embedding, and finding the closest ones.
//
// JdbcTemplate is Spring's thin wrapper around plain JDBC - it removes the
// usual boilerplate (opening connections, closing them, catching SQLException)
// while still letting us write real SQL.
@Repository
public class VectorSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    public VectorSearchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Stores the embedding for a question that was already saved via JPA.
    // The "::vector" cast tells Postgres "treat this text as a vector literal".
    public void saveEmbedding(Long questionId, float[] embedding) {
        String vectorLiteral = toVectorLiteral(embedding);
        jdbcTemplate.update(
                "UPDATE questions SET embedding = ?::vector WHERE id = ?",
                vectorLiteral, questionId
        );
    }

    // The actual "retrieval" in Retrieval-Augmented Generation.
    // "<=>" is pgvector's cosine distance operator: smaller = more similar.
    // We order by distance ascending, so the closest matches come first.
    public List<SimilarityResult> findSimilar(float[] queryEmbedding, String companyFilter, int limit) {
        String vectorLiteral = toVectorLiteral(queryEmbedding);

        StringBuilder sql = new StringBuilder(
                "SELECT id, company, role, question_text, pattern, embedding <=> ?::vector AS distance " +
                "FROM questions WHERE embedding IS NOT NULL "
        );

        List<Object> params = new ArrayList<>();
        params.add(vectorLiteral);

        if (companyFilter != null && !companyFilter.isBlank()) {
            sql.append("AND company ILIKE ? ");
            params.add(companyFilter);
        }

        sql.append("ORDER BY distance ASC LIMIT ?");
        params.add(limit);

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new SimilarityResult(
                rs.getLong("id"),
                rs.getString("company"),
                rs.getString("role"),
                rs.getString("question_text"),
                rs.getString("pattern"),
                rs.getDouble("distance")
        ), params.toArray());
    }

    // pgvector expects vectors written as text in the form "[0.12,-0.5,0.33,...]"
    private String toVectorLiteral(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // A small plain data holder for query results - not an entity, just a carrier.
    public record SimilarityResult(
            Long id,
            String company,
            String role,
            String questionText,
            String pattern,
            double distance
    ) {
    }
}
