package com.chetan.interviewprep.embedding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Random;

// Active when embedding.provider=mock (or if the property is missing entirely).
// @ConditionalOnProperty means Spring only creates this bean when that
// condition is true - this is how we swap implementations via config alone.
@Service
@ConditionalOnProperty(name = "embedding.provider", havingValue = "mock", matchIfMissing = true)
public class MockEmbeddingService implements EmbeddingService {

    @Value("${embedding.dimension:384}")
    private int dimension;

    @Override
    public float[] embed(String text) {
        // IMPORTANT: this is NOT a real semantic embedding. It does not
        // understand meaning - "dog" and "puppy" will get unrelated vectors.
        // What it DOES give you is a deterministic vector (same text always
        // produces the same vector), which is enough to build and test the
        // full pipeline - storage, similarity search, API wiring - for free,
        // before you touch a real embedding API.
        Random random = new Random(text.hashCode());
        float[] vector = new float[dimension];
        float sumSquares = 0f;

        for (int i = 0; i < dimension; i++) {
            vector[i] = (random.nextFloat() * 2) - 1; // range: -1 to 1
            sumSquares += vector[i] * vector[i];
        }

        // Normalize to unit length so distance comparisons behave sensibly,
        // the same way real embedding models normalize their output.
        float norm = (float) Math.sqrt(sumSquares);
        for (int i = 0; i < dimension; i++) {
            vector[i] /= norm;
        }

        return vector;
    }
}
