-- Turns on pgvector, the Postgres extension that adds a "vector" data type
-- and similarity operators (like <=> for cosine distance).
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS questions (
    id            BIGSERIAL PRIMARY KEY,
    company       VARCHAR(255) NOT NULL,
    role          VARCHAR(255),
    question_text TEXT NOT NULL,
    pattern       VARCHAR(255),
    created_at    TIMESTAMP,
    embedding     vector(384)  -- 384 = the size of the embedding vectors we use
);

-- Speeds up "find all questions for company X" queries
CREATE INDEX IF NOT EXISTS idx_questions_company ON questions (company);
