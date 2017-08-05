package com.example.satyakresna.moviereference.database;

import android.provider.BaseColumns;

/**
 * Created by satyakresna on 06-Aug-17.
 */

public class FavoriteContract {
    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
