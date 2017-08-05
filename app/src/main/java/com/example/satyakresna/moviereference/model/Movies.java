package com.example.satyakresna.moviereference.model;

import java.util.List;

/**
 * Created by satyakresna on 05-Aug-17.
 */

public class Movies {
    private List<MovieResults> results;
    private int page;
    private int total_results;
    private int total_pages;

    public List<MovieResults> getResults() {
        return results;
    }

    public void setResults(List<MovieResults> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }
}
