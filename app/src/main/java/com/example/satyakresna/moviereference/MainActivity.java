package com.example.satyakresna.moviereference;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.satyakresna.moviereference.adapter.movies.MovieReferenceAdapter;
import com.example.satyakresna.moviereference.database.FavoriteContract;
import com.example.satyakresna.moviereference.model.movies.MovieResults;
import com.example.satyakresna.moviereference.model.movies.Movies;
import com.example.satyakresna.moviereference.utilities.Constant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieReferenceAdapter.ItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<MovieResults> results = new ArrayList<>();
    private Gson gson = new Gson();
    private MovieReferenceAdapter mAdapter;
    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;
    @BindView(R.id.line_network_retry) LinearLayout mLinearNetworkRetry;
    @BindView(R.id.tv_error_message) TextView mDisplayErrorMessage;

    private Parcelable layoutManagerSaveState;
    private String selectedCategory;
    private String categorySelected;
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;
    private Cursor favoriteData = null;

    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(MainActivity.this));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieReferenceAdapter(results, this);
        mRecyclerView.setAdapter(mAdapter);
        if (isWifiConnected() || isNetworkConnected()) {
            if (savedInstanceState != null) {
                selectedCategory = savedInstanceState.getString(Constant.KEY_SELECTED_CATEGORY);
                if (selectedCategory.equals(Constant.POPULAR)) {
                    getSupportActionBar().setSubtitle(R.string.action_most_popular);
                } else if (selectedCategory.equals(Constant.TOP_RATED)) {
                    getSupportActionBar().setSubtitle(R.string.action_top_rated);
                } else {
                    getSupportActionBar().setSubtitle(R.string.action_favorites);
                }
                layoutManagerSaveState = savedInstanceState.getParcelable(Constant.LAYOUT_MANAGER);
            } else {
                // Set default
                selectedCategory = Constant.POPULAR;
                getSupportActionBar().setSubtitle(R.string.action_most_popular);
            }
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mLinearNetworkRetry.setVisibility(View.VISIBLE);
        }

        if (selectedCategory.equals(Constant.FAVORITES)) {
            setCategory(selectedCategory);
            setupLoader(this, getContentResolver());
            restartLoader(getSupportLoaderManager());
        } else {
            loadData(selectedCategory);
        }

    }

    private void setupLoader(final MainActivity mainActivity, final ContentResolver contentResolver) {
        loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Cursor>(mainActivity) {
                    @Override
                    public Cursor loadInBackground() {
                        try {
                            return contentResolver.query(
                                    FavoriteContract.FavoriteEntry.CONTENT_URI,
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
                        if (categorySelected.equals(Constant.FAVORITES)) {
                            forceLoad();
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                Log.d("Favorites found: ", ""+data.getCount());
                favoriteData = data;
                generateFromCursor(favoriteData);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

    private void generateFromCursor(Cursor cursor) {
        List<MovieResults> results = new ArrayList<>();
        cursor.moveToPosition(-1);
        try {
            while (cursor.moveToNext()) {
                MovieResults movieResult = new MovieResults();
                movieResult.setPoster_path(
                        cursor.getString(
                                cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER)
                        )
                );
                movieResult.setTitle(
                        cursor.getString(
                                cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE)
                        )
                );
                movieResult.setRelease_date(
                        cursor.getString(
                                cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE)
                        )
                );
                movieResult.setOverview(
                        cursor.getString(
                                cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS)
                        )
                );
                movieResult.setVote_average(
                        cursor.getDouble(
                                cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RATING)
                        )
                );
                movieResult.setId(
                        cursor.getInt(
                                cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID)
                        )
                );
                results.add(movieResult);
            }
        } finally {
            cursor.close();
        }

        onDataReceived(results, 1);
    }

    private void onDataReceived(List<MovieResults> results, int page) {
        Log.d("received", ""+results.size());
        if (page > 1) {
            mAdapter.updateData(results);
        } else {
            mAdapter.replaceAll(results);

            // retain scroll position
            if (layoutManagerSaveState != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSaveState);
            }
        }
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
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
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
                            results.clear();
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

    @Override
    public void onItemClick(MovieResults data, int position) {
        Intent startDetailActivity = new Intent(this, DetailActivity.class);
        startDetailActivity.putExtra(Constant.KEY_MOVIE, gson.toJson(data));
        startDetailActivity.putExtra(Constant.MOVIE_ID, String.valueOf(data.getId()));
        startActivity(startDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_most_popular:
                selectedCategory = Constant.POPULAR;
                resetPage();
                loadData(selectedCategory);
                getSupportActionBar().setSubtitle(R.string.action_most_popular);
                return true;
            case R.id.action_top_rated:
                selectedCategory = Constant.TOP_RATED;
                resetPage();
                loadData(selectedCategory);
                getSupportActionBar().setSubtitle(R.string.action_top_rated);
                return true;
            case R.id.action_favorites:
                selectedCategory = Constant.FAVORITES;
                getSupportActionBar().setSubtitle(R.string.action_favorites);
                setCategory(selectedCategory);
                setupLoader(this, getContentResolver());
                restartLoader(getSupportLoaderManager());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setCategory(String selectedCategory) {
        categorySelected = selectedCategory;
    }

    private void initLoader(LoaderManager supportLoaderManager) {
        supportLoaderManager.initLoader(Constant.LOADER_MAIN_ID, null, loaderCallbacks);
    }

    private void restartLoader(LoaderManager supportLoaderManager) {
        supportLoaderManager.restartLoader(Constant.LOADER_MAIN_ID, null, loaderCallbacks);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && (ConnectivityManager.TYPE_WIFI == networkInfo.getType());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constant.KEY_SELECTED_CATEGORY, selectedCategory);
        outState.putParcelable(Constant.LAYOUT_MANAGER, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void resetPage() {
        currentPage = 1;
    }

    private void loadData(String category) {
        if (category.equals(Constant.POPULAR)) {
            mAdapter.replaceAll(results);
            getDataFromAPI(Constant.POPULAR);
        } else if (category.equals(Constant.TOP_RATED)) {
            mAdapter.replaceAll(results);
            getDataFromAPI(Constant.TOP_RATED);
        }
    }
}
