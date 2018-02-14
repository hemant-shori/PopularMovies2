package com.hemant.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.hemant.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();
        public static final String  TABLE_NAME = "movies";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE = "movie";

        public static Uri buildMovieUriWithId(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
    }
}
