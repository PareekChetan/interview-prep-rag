package com.chetan.interviewprep.embedding;

// This interface is the whole point of good design here: our controllers and
// services only depend on "something that can embed text" - they never know
// or care whether that's a free mock, Hugging Face, or something else later.
// Swapping providers means changing application.properties, not our code.
public interface EmbeddingService {

    float[] embed(String text);

}
