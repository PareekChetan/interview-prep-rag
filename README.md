# Interview Prep RAG

A RAG-based system that retrieves company-specific interview questions and
tells you which question patterns you haven't practiced yet.

---

## 1. What this project actually does

You give it a company name (or a description of a topic), and it finds the
most *semantically similar* interview questions from a stored collection —
not just keyword matches, but questions that mean something similar even if
worded differently. Then a separate feature compares which "patterns" (Arrays,
Graphs, DP, etc.) that company tends to ask about against what you've already
practiced, and tells you the gap.

That "find semantically similar text" part is RAG's **retrieval** half. We're
not using the **generation** half (an LLM writing a summary) in this version —
we're keeping it to retrieval + your own logic first, since that's the harder
and more valuable part to get right. You can bolt on an LLM call later to turn
the raw results into a natural-language paragraph.

---

## 2. How RAG works here, concretely

```
"Graphs question at Amazon"  →  [0.12, -0.45, 0.88, ...]   (a 384-number vector)
                                          |
                                          v
                          Find stored questions whose vectors
                          are closest to this one (cosine distance)
                                          |
                                          v
                    Return: "Find shortest path between two
                             warehouses on a delivery grid" (Amazon, Graphs)
```

Every piece of text — questions you store, and queries you search with — gets
converted into a vector (a list of numbers) that represents its *meaning*.
Texts with similar meaning end up as vectors that are close together in that
number-space. Postgres, with the `pgvector` extension, can efficiently search
for "which stored vectors are closest to this one."

---

## 3. Project structure

```
src/main/java/com/chetan/interviewprep/
├── InterviewPrepApplication.java   # entry point
├── model/InterviewQuestion.java    # JPA entity (maps to the "questions" table)
├── repository/
│   ├── InterviewQuestionRepository.java   # normal CRUD via Spring Data JPA
│   └── VectorSearchRepository.java        # raw JDBC for the pgvector column
├── embedding/
│   ├── EmbeddingService.java              # interface
│   ├── MockEmbeddingService.java          # free, local, fake-but-consistent vectors
│   └── HuggingFaceEmbeddingService.java   # real embeddings via HF's free API
├── service/InterviewQuestionService.java  # business logic: ingest, search, gap analysis
├── controller/                            # REST endpoints
├── dto/                                   # request/response shapes
├── exception/GlobalExceptionHandler.java  # turns errors into clean JSON
└── seed/DataSeeder.java                   # loads sample data on first run
```

**Why is the embedding stored differently from everything else?** Hibernate
(the JPA library Spring uses) doesn't understand pgvector's `vector` column
type out of the box. Rather than fighting the framework, `InterviewQuestion`
only maps the normal columns (company, role, questionText...), and
`VectorSearchRepository` talks to the `embedding` column directly with SQL.
This is a common, legitimate pattern — use the ORM where it helps, drop to raw
SQL where it doesn't.

---

## 4. Setup

### 4.1 Install prerequisites
- **JDK 21**: https://adoptium.net
- **Maven**: https://maven.apache.org/download.cgi
- **VS Code** with the "Extension Pack for Java" and "Spring Boot Extension Pack" extensions

Verify:
```
java -version
mvn -version
```

### 4.2 Set up Postgres with pgvector

This is the one tricky part: pgvector is a Postgres **extension**, and it's
not included in a plain Windows Postgres install. You have two realistic options —
pick whichever is easier for you:

**Option A — Supabase (recommended, easiest)**
1. Create a free project at https://supabase.com
2. pgvector is already enabled by default
3. Copy the connection details (host, port, database, user, password) into
   `application.properties`

**Option B — Docker (if you have Docker Desktop installed)**
```bash
docker run --name interviewprep-pg -e POSTGRES_PASSWORD=yourpassword -p 5432:5432 -d pgvector/pgvector:pg16
```
This gives you a local Postgres with pgvector pre-installed.

Either way, once connected, `schema.sql` runs automatically on app startup and
creates the `vector` extension + `questions` table for you — no manual SQL needed.

### 4.3 Configure the app

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://<your-host>:5432/<your-db>
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
```

### 4.4 Run it

```bash
mvn spring-boot:run
```

On first run, you'll see `Seeded 15 sample interview questions.` in the logs —
the app auto-loads the sample dataset so you have something to search immediately.

---

## 5. Trying it out (with the free mock embeddings)

By default, `embedding.provider=mock` — no API key needed, works immediately.

**Search:**
```bash
curl -X POST http://localhost:8080/api/search \
  -H "Content-Type: application/json" \
  -d '{"query": "shortest path in a grid", "limit": 5}'
```

**Add a new question:**
```bash
curl -X POST http://localhost:8080/api/questions/ingest \
  -H "Content-Type: application/json" \
  -d '{"company": "Amazon", "role": "SDE-1", "questionText": "Find all pairs summing to target", "pattern": "Arrays"}'
```

**Gap analysis:**
```bash
curl -X POST http://localhost:8080/api/gap-analysis \
  -H "Content-Type: application/json" \
  -d '{"company": "Amazon", "solvedPatterns": ["Arrays"]}'
```

**Browse by company:**
```bash
curl http://localhost:8080/api/companies/Amazon/questions
```

Remember: with mock embeddings, "similarity" is fake — it won't actually
understand meaning. It's there so you can confirm your whole pipeline (API →
service → embedding → storage → retrieval) works end-to-end before depending
on a real, rate-limited API.

---

## 6. Switching to real embeddings

1. Get a free API token from https://huggingface.co/settings/tokens
2. In `application.properties`:
   ```properties
   embedding.provider=huggingface
   embedding.huggingface.api-key=your_token_here
   ```
3. Restart the app. Now search results are based on actual semantic meaning.

Note: the free Hugging Face inference API can be slow to "wake up" a model on
the first call (hence `wait_for_model: true` in the request) and has rate
limits — fine for a portfolio demo, not for production traffic.

---

## 7. What to build next (in order)

1. **Expand the dataset.** 15 seed questions is a demo, not a product. Manually
   collect 150-300 more from GeeksforGeeks / LeetCode Discuss threads, tagged
   with company + pattern, and ingest them via `/api/questions/ingest`.
2. **React frontend.** A simple form (company or free-text query) that calls
   `/api/search` and `/api/gap-analysis` and renders the results.
3. **(Optional) Add an LLM generation step.** Once retrieval works well, send
   the top search results + the user's query to an LLM API to generate a
   natural-language summary instead of a raw list. This completes the "G" in RAG.
4. **Deploy.** Backend to Render/Railway free tier, frontend to Vercel.

---

## 8. Key concepts glossary (for your resume/interview talking points)

- **Embedding**: converting text into a fixed-length list of numbers that
  captures its meaning, so meaning can be compared mathematically.
- **Vector database**: a database optimized for storing embeddings and finding
  the closest ones to a given query vector (here: Postgres + pgvector).
- **Cosine distance**: a measure of how different two vectors' directions are;
  lower = more similar. This is what `<=>` computes in the search query.
- **RAG (Retrieval-Augmented Generation)**: retrieving relevant stored
  information first, then (optionally) having an LLM generate an answer
  grounded in what was retrieved, rather than answering from memory alone.
- **DTO (Data Transfer Object)**: a plain class defining the exact shape of
  data going in/out of your API, kept separate from your database entities.
