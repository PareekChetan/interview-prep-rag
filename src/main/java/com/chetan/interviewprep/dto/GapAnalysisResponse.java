package com.chetan.interviewprep.dto;

import java.util.List;

public class GapAnalysisResponse {

    private String company;
    private List<PatternGap> gaps;

    public GapAnalysisResponse() {
    }

    public GapAnalysisResponse(String company, List<PatternGap> gaps) {
        this.company = company;
        this.gaps = gaps;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<PatternGap> getGaps() {
        return gaps;
    }

    public void setGaps(List<PatternGap> gaps) {
        this.gaps = gaps;
    }
}
