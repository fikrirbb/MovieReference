package com.example.satyakresna.moviereference;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.satyakresna.moviereference.model.movies.MovieResults;
import com.example.satyakresna.moviereference.utilities.DateFormatter;
import com.example.satyakresna.moviereference.utilities.ImageUrlBuilder;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private ImageView backdrop;
    private ImageView poster;
    private TextView relaseDate;
    private TextView voteAverage;
    private TextView overview;
    private CoordinatorLayout parentDetail;

    private String jsonData;
    private MovieResults movieResults;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        backdrop = (ImageView) findViewById(R.id.iv_backdrop_path);
        poster = (ImageView) findViewById(R.id.iv_poster);
        relaseDate = (TextView) findViewById(R.id.tv_release_date);
        voteAverage = (TextView) findViewById(R.id.tv_vote_average);
        overview = (TextView) findViewById(R.id.tv_overview);
        parentDetail = (CoordinatorLayout) findViewById(R.id.parent_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jsonData = getIntent().getStringExtra(Constant.KEY_MOVIE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (jsonData != null) {
            movieResults = gson.fromJson(jsonData, MovieResults.class);
            bindData();
        }
    }

    private void bindData() {
        parentDetail.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle(movieResults.getTitle());
        Picasso.with(this)
                .load(ImageUrlBuilder.getBackdropUrl(movieResults.getBackdrop_path()))
                .placeholder(R.drawable.ic_local_movies)
                .error(R.drawable.ic_error)
                .into(backdrop);
        Picasso.with(this)
                .load(ImageUrlBuilder.getPosterUrl(movieResults.getPoster_path()))
                .placeholder(R.drawable.ic_local_movies)
                .error(R.drawable.ic_error)
                .into(poster);
        relaseDate.setText(DateFormatter.getReadableDate(movieResults.getRelease_date()));
        voteAverage.setText(String.valueOf(movieResults.getVote_average()));
        overview.setText(movieResults.getOverview());
    }
}
