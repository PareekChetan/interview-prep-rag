package com.chetan.interviewprep.embedding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

// Active when embedding.provider=huggingface. This calls a real sentence-embedding
// model over Hugging Face's free inference API, so search results are based on
// actual meaning instead of the mock's random-but-consistent vectors.
@Service
@ConditionalOnProperty(name = "embedding.provider", havingValue = "huggingface")
public class HuggingFaceEmbeddingService implements EmbeddingService {

    // all-MiniLM-L6-v2 is a small, fast, well-regarded sentence embedding model.
    // It outputs 384-dimensional vectors, which is why schema.sql uses vector(384).
    private static final String MODEL_URL =
        "https://router.huggingface.co/hf-inference/models/sentence-transformers/all-MiniLM-L6-v2/pipeline/feature-extraction";

    private final RestClient restClient;

    @Value("${embedding.huggingface.api-key}")
    private String apiKey;

    public HuggingFaceEmbeddingService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public float[] embed(String text) {
        Map<String, Object> body = Map.of(
                "inputs", text,
                "options", Map.of("wait_for_model", true)
        );

        // RestClient is Spring's modern HTTP client - a simpler alternative to
        // RestTemplate for making outbound API calls like this one.
        Object response = restClient.post()
                .uri(MODEL_URL)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Object.class);

        return parseEmbedding(response);
    }

    @SuppressWarnings("unchecked")
    private float[] parseEmbedding(Object response) {
        // Hugging Face's response shape can vary: sometimes it's a single flat
        // vector for the sentence, sometimes it's one vector per token (which
        // we then need to average into a single sentence vector ourselves -
        // this is called "mean pooling").
        if (response instanceof List<?> outer && !outer.isEmpty()) {
            if (outer.get(0) instanceof List) {
                List<List<Double>> tokenVectors = (List<List<Double>>) response;
                int dim = tokenVectors.get(0).size();
                float[] pooled = new float[dim];

                for (List<Double> tokenVector : tokenVectors) {
                    for (int i = 0; i < dim; i++) {
                        pooled[i] += tokenVector.get(i).floatValue();
                    }
                }
                for (int i = 0; i < dim; i++) {
                    pooled[i] /= tokenVectors.size();
                }
                return pooled;
            } else {
                List<Double> flat = (List<Double>) response;
                float[] vector = new float[flat.size()];
                for (int i = 0; i < flat.size(); i++) {
                    vector[i] = flat.get(i).floatValue();
                }
                return vector;
            }
        }
        throw new IllegalStateException("Unexpected embedding response format from Hugging Face");
    }
}
