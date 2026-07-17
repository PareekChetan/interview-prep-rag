package com.chetan.interviewprep.repository;

import com.chetan.interviewprep.model.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// This is the magic of Spring Data JPA: by extending JpaRepository, we get
// save(), findById(), findAll(), delete(), etc. for free — no SQL written.
//
// findByCompanyIgnoreCase is a "derived query method" - Spring reads the
// method name and generates the SQL automatically:
//   SELECT * FROM questions WHERE LOWER(company) = LOWER(?)
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findByCompanyIgnoreCase(String company);

}
