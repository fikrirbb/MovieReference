package com.example.satyakresna.moviereference.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by satyakresna on 06-Aug-17.
 */

public class FavoriteContentProvider extends ContentProvider {
    private FavoriteDBHelper dbHelper;
    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final String TAG = FavoriteContentProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor result = null;
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                result = db.query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                // no inspection constant conditions
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case FAVORITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};
                result = db.query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                Log.w(TAG, "Unknown URI: "+uri);
        }
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri result = null;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    result = ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI, id);
                    // no inspection constant conditions
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    Log.w(TAG, "Insert data failed to: "+uri);
                }
                break;
            default:
                Log.w(TAG, "Unknown URI: "+uri);
        }
        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int result = 0;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES_WITH_ID:
                String whereClause = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                String id = uri.getPathSegments().get(1);
                result = db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, whereClause, new String[]{id});
                break;
            default:
                Log.w(TAG, "Unknown URI: "+uri);
        }

        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);
        return matcher;
    }
}
