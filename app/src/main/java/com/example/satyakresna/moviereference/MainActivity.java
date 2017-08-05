package com.example.satyakresna.moviereference;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.satyakresna.moviereference.adapter.MovieReferenceAdapter;
import com.example.satyakresna.moviereference.model.MovieResults;
import com.example.satyakresna.moviereference.model.Movies;
import com.example.satyakresna.moviereference.utilities.Constant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<MovieResults> results = new ArrayList<>();
    private Gson gson = new Gson();
    private MovieReferenceAdapter mAdapter;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(MainActivity.this));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieReferenceAdapter(results);
        mRecyclerView.setAdapter(mAdapter);

        getDataFromAPI(Constant.POPULAR);
    }

    /**
     * Different users of your app have different Android devices with varying screen sizes.
     *
     * Rather than hard-code the values in specific numbers i.e. 2, you can be able to calculate
     * the no. of possible columns at runtime and then use that to set your GridLayoutManager.
     *
     * Using this function will be easier and then declare GridLayoutManager:
     * GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(
     * getActivity());
     * You can replace getActivity() with YourNameActivity.this
     *
     * NB: You can vary the value held by the scalingFactor variable.
     * The smaller it is the more no. of columns you can display,
     * and the larger the value the less no. of columns will be calculated.
     * It is the scaling factor to tweak to your needs.
     * Also for dual pane divide float dpWidth by the amount of weight allocated to
     * the main screen containing the grid of movies */
    private int calculateNoOfColumns(MainActivity mainActivity) {
        DisplayMetrics displayMetrics = mainActivity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels /displayMetrics.density;
        int scallingFactor = 180;
        int noOfColumns = (int) (dpWidth / scallingFactor);
        return  noOfColumns;
    }

    private void getDataFromAPI(String selectedCategory) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Constant.URL_API + selectedCategory + Constant.PARAM_API_KEY + Constant.API_KEY;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Movies movies = gson.fromJson(response, Movies.class);
                            for (MovieResults item : movies.getResults()) {
                                results.add(item);
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            Log.e(TAG, error.getMessage());
                        } else {
                            Log.e(TAG, "Something error happened!");
                        }
                    }
                }
        );
        requestQueue.add(stringRequest);
    }
}
