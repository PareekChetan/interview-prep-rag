package com.chetan.interviewprep.dto;

import jakarta.validation.constraints.NotBlank;

public class SearchRequest {

    @NotBlank(message = "query is required")
    private String query;

    // Optional - narrows the search to one company
    private String company;

    // Optional - how many results to return (defaults to 10 in the service)
    private Integer limit;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
