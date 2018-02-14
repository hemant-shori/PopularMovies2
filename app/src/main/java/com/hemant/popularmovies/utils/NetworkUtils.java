package com.hemant.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.hemant.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_DB_URL = "https://api.themoviedb.org/3/movie";
    private static final String YOUTUBE_TRAILER_URL = "https://img.youtube.com/vi";
    static final String IMAGE_URL = "http://image.tmdb.org/t/p/w342";
//    static final String IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    private final static String API_KEY_PARAM = "api_key";
    public final static String POPULAR_PATH = "popular";
    public final static String TOP_RATED_PATH = "top_rated";
    public final static String VIDEOS_PATH = "videos";
    public final static String REVIEWS_PATH = "reviews";
    public static final String MEDIUM_QUALITY_PATH = "mqdefault.jpg";
    public static final String HIGH_QUALITY_PATH = "sddefault.jpg";

    public static URL buildUrl(Context context, String selectedPath) throws MalformedURLException {
        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(selectedPath)
                .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.api_key))
                .build();

        URL url;
        url = new URL(builtUri.toString());

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrlForMovieDetails(Context context, String movieId, String selectedPath) throws MalformedURLException {
        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(selectedPath)
                .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.api_key))
                .build();

        URL url;
        url = new URL(builtUri.toString());

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrlForYouTubeTrailer(Context context, String youTubeId,String thumbnailQualityPath) throws MalformedURLException {
        Uri builtUri = Uri.parse(YOUTUBE_TRAILER_URL).buildUpon()
                .appendPath(youTubeId)
                .appendPath(thumbnailQualityPath)
                .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.api_key))
                .build();

        URL url;
        url = new URL(builtUri.toString());

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}