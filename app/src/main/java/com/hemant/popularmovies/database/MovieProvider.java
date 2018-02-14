package com.hemant.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {
    private static final int CODE_MOVIES = 419;
    private static final int CODE_MOVIE_WITH_ID = 424;
    private static final UriMatcher URL_MATCHER = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, CODE_MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);
        return uriMatcher;
    }

    private MovieDBHelper mMovieDBHelper;

    @Override
    public boolean onCreate() {
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (URL_MATCHER.match(uri)) {
            case CODE_MOVIES:
                cursor = mMovieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        MovieContract.MovieEntry._ID
                );
                break;
            case CODE_MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                if (id == null)
                    throw new UnsupportedOperationException("unable to find Movie Id");
                cursor = mMovieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        MovieContract.MovieEntry.MOVIE_ID + " = ? ",
                        new String[]{id},
                        null,
                        null,
                        null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri Found" + uri);
        }
        assert getContext() != null;
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        switch (URL_MATCHER.match(uri)) {
            case CODE_MOVIES:
                long id = mMovieDBHelper.getWritableDatabase().insert(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Unable to insert the Movie into DB");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri Found" + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowDeleted;
        switch (URL_MATCHER.match(uri)) {
            case CODE_MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                if (id == null)
                    throw new UnsupportedOperationException("Unable to get Movie Id");
                numRowDeleted = mMovieDBHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.MOVIE_ID + " = ?",
                        new String[]{id}
                );

                break;
            default:
                throw new UnsupportedOperationException("unknown Uri Found" + uri);
        }
        return numRowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
