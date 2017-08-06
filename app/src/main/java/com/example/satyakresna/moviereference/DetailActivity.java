package com.example.satyakresna.moviereference;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.satyakresna.moviereference.database.FavoriteContract;
import com.example.satyakresna.moviereference.model.movies.MovieResults;
import com.example.satyakresna.moviereference.utilities.Constant;
import com.example.satyakresna.moviereference.utilities.DateFormatter;
import com.example.satyakresna.moviereference.utilities.ImageUrlBuilder;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private ImageView backdrop;
    private ImageView poster;
    private TextView releaseDate;
    private TextView voteAverage;
    private TextView overview;
    private CoordinatorLayout parentDetail;
    private FloatingActionButton fab;

    private String jsonData;
    private MovieResults movieResults;
    private Gson gson = new Gson();

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;
    private Cursor favoriteData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        backdrop = (ImageView) findViewById(R.id.iv_backdrop_path);
        poster = (ImageView) findViewById(R.id.iv_poster);
        releaseDate = (TextView) findViewById(R.id.tv_release_date);
        voteAverage = (TextView) findViewById(R.id.tv_vote_average);
        overview = (TextView) findViewById(R.id.tv_overview);
        parentDetail = (CoordinatorLayout) findViewById(R.id.parent_detail);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jsonData = getIntent().getStringExtra(Constant.KEY_MOVIE);

        if (jsonData != null) {
            movieResults = gson.fromJson(jsonData, MovieResults.class);
            bindData();

            setupLoader(this, getContentResolver(), getMovieItem(jsonData).getId());
            initLoader(getSupportLoaderManager());

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((Integer)fab.getTag() == R.drawable.ic_star_selected) {

                    } else {
                        saveAsFavorite(getContentResolver(), getMovieItem(jsonData));
                    }
                }
            });
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
}
