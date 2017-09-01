package com.example.satyakresna.moviereference;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.satyakresna.moviereference.adapter.reviews.ReviewAdapter;
import com.example.satyakresna.moviereference.adapter.trailer.TrailerAdapter;
import com.example.satyakresna.moviereference.database.FavoriteContract;
import com.example.satyakresna.moviereference.model.movies.MovieResults;
import com.example.satyakresna.moviereference.model.reviews.Reviews;
import com.example.satyakresna.moviereference.model.reviews.ReviewsResult;
import com.example.satyakresna.moviereference.model.trailer.TrailerResults;
import com.example.satyakresna.moviereference.model.trailer.Trailers;
import com.example.satyakresna.moviereference.utilities.Constant;
import com.example.satyakresna.moviereference.utilities.DateFormatter;
import com.example.satyakresna.moviereference.utilities.ImageUrlBuilder;
import com.example.satyakresna.moviereference.utilities.TrailerUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity
implements TrailerAdapter.TrailerItemClickListener {
    private static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.iv_backdrop_path) ImageView backdrop;
    @BindView(R.id.iv_poster) ImageView poster;
    @BindView(R.id.tv_release_date) TextView releaseDate;
    @BindView(R.id.tv_vote_average) TextView voteAverage;
    @BindView(R.id.tv_overview) TextView overview;
    @BindView(R.id.parent_detail) CoordinatorLayout parentDetail;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private String movieId;
    private String jsonData;
    private MovieResults movieResults;
    private Gson gson = new Gson();

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;

    @BindView(R.id.rv_trailers) RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;
    private List<TrailerResults> trailerResult = new ArrayList<>();

    @BindView(R.id.rv_reviews) RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;
    private List<ReviewsResult> reviewResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        jsonData = getIntent().getStringExtra(Constant.KEY_MOVIE);

        if (jsonData != null) {
            movieResults = gson.fromJson(jsonData, MovieResults.class);
            bindData();
            trailerRecyclerView();
            reviewRecyclerView();
            setupLoader(this, getContentResolver(), getMovieItem(jsonData).getId());
            initLoader(getSupportLoaderManager());

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((Integer)fab.getTag() == R.drawable.ic_star_selected) {
                        unsetAsFavorite(getContentResolver(), getMovieItem(jsonData));
                    } else {
                        saveAsFavorite(getContentResolver(), getMovieItem(jsonData));
                        restartLoader(getSupportLoaderManager());
                    }
                }
            });
        }
    }

    private void reviewRecyclerView() {
        mReviewAdapter = new ReviewAdapter(reviewResult);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setNestedScrollingEnabled(false);
        movieId = getIntent().getStringExtra(Constant.MOVIE_ID);
        getReviewFromAPI(movieId);
    }

    private void getReviewFromAPI(String movieId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Constant.URL_API + movieId + Constant.REVIEW + Constant.PARAM_API_KEY  + Constant.API_KEY;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Reviews reviews = gson.fromJson(response, Reviews.class);
                            for (ReviewsResult result : reviews.getResults()) {
                                reviewResult.add(result);
                            }
                            mReviewAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e(TAG, "Something error happened!");
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

    private void trailerRecyclerView() {
        mTrailerAdapter = new TrailerAdapter(trailerResult, this);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        movieId = getIntent().getStringExtra(Constant.MOVIE_ID);
        getTrailerFromAPI(movieId);
    }

    private void getTrailerFromAPI(String movieId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Constant.URL_API + movieId + Constant.VIDEOS + Constant.PARAM_API_KEY  + Constant.API_KEY;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Trailers trailers = gson.fromJson(response, Trailers.class);
                            for (TrailerResults result : trailers.getResults()) {
                                trailerResult.add(result);
                            }
                            mTrailerAdapter.notifyDataSetChanged();
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

    private void unsetAsFavorite(ContentResolver contentResolver, MovieResults movieItem) {
        long result = contentResolver.delete(uriWithIDBuilder(movieItem.getId()), null, null);
        if (result > 0) {
            restartLoader(getSupportLoaderManager());
        }
    }

    private void initLoader(LoaderManager supportLoaderManager) {
        supportLoaderManager.initLoader(Constant.LOADER_ID, null, loaderCallbacks);
    }

    private void setupLoader(final DetailActivity detailActivity, final ContentResolver contentResolver, final long movieID) {
        loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Cursor>(detailActivity) {
                    @Override
                    public Cursor loadInBackground() {
                        try {
                            return contentResolver.query(
                                    uriWithIDBuilder(movieID),
                                    null,
                                    null,
                                    null,
                                    null
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onStartLoading() {
                        forceLoad();
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                setFavoriteButton(data.getCount());
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

    private Uri uriWithIDBuilder(long id) {
        return ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI, id);
    }

    private void setFavoriteButton(int count) {
        if (count > 0) {
            onStatusReceived(true);
        } else {
            onStatusReceived(false);
        }
    }

    private void onStatusReceived(boolean isFavorite) {
        if (isFavorite) {
            fab.setImageResource(R.drawable.ic_star_selected);
            fab.setTag(R.drawable.ic_star_selected);
        } else {
            fab.setImageResource(R.drawable.ic_star_unselected);
            fab.setTag(R.drawable.ic_star_unselected);
        }
    }

    private MovieResults getMovieItem(String json) {
        return gson.fromJson(json, MovieResults.class);
    }

    private void saveAsFavorite(ContentResolver resolver, MovieResults item) {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, item.getId());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, item.getTitle());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP, item.getBackdrop_path());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER, item.getPoster_path());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_RATING, item.getVote_average());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, item.getRelease_date());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS, item.getOverview());
        resolver.insert(FavoriteContract.FavoriteEntry.CONTENT_URI, cv);
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
        releaseDate.setText(DateFormatter.getReadableDate(movieResults.getRelease_date()));
        voteAverage.setText(String.valueOf(movieResults.getVote_average()));
        overview.setText(movieResults.getOverview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        restartLoader(getSupportLoaderManager());
    }

    private void restartLoader(LoaderManager supportLoaderManager) {
        supportLoaderManager.restartLoader(Constant.LOADER_ID, null, loaderCallbacks);
    }

    @Override
    public void onTrailerItemClick(TrailerResults data, int position) {
        Uri webPage = Uri.parse(TrailerUtil.getYoutubeUrl(data.getKey()));
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie Reference");
            shareIntent.putExtra(Intent.EXTRA_TEXT, movieInfo());
            startActivity(Intent.createChooser(shareIntent, "Share to your friends"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String movieInfo() {
        String content = "Let's watch "+ movieResults.getTitle() + "\n"
                +"Overview: "+"\n"+ movieResults.getOverview() + "\n" +
                "Rate: \u2605"+ movieResults.getVote_average() + "\n"+
                "Release date: "+ DateFormatter.getReadableDate(movieResults.getRelease_date());
        return content;
    }
}
