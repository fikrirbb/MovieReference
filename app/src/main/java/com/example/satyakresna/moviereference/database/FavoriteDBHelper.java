package com.example.satyakresna.moviereference.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by satyakresna on 06-Aug-17.
 */

public class FavoriteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE "+ FavoriteContract.FavoriteEntry.TABLE_NAME + " (" +
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_TITLE + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_POSTER + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_RATING + " REAL, " +
                FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_BACKDROP + " TEXT " +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
