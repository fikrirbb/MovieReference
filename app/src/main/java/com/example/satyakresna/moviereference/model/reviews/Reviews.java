package com.example.satyakresna.moviereference.model.reviews;

import java.util.List;

/**
 * Created by satyakresna on 05-Aug-17.
 */

public class Reviews {
    private int id;
    private int page;
    private List<ReviewsResult> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<ReviewsResult> getResults() {
        return results;
    }

    public void setResults(List<ReviewsResult> results) {
        this.results = results;
    }
}
