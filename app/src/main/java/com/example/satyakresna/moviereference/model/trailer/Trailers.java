package com.example.satyakresna.moviereference.model.trailer;

import java.util.List;

/**
 * Created by satyakresna on 05-Aug-17.
 */

public class Trailers {
    private int id;
    private List<TrailerResults> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TrailerResults> getResults() {
        return results;
    }

    public void setResults(List<TrailerResults> results) {
        this.results = results;
    }
}
