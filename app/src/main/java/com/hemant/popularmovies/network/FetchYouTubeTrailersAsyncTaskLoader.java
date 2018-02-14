package com.hemant.popularmovies.network;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.hemant.popularmovies.interfaces.YouTubeTrailersAsyncTaskListener;
import com.hemant.popularmovies.utils.MovieDetailsJsonUtils;
import com.hemant.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.hemant.popularmovies.activities.MovieDetailsActivity.MOVIE_DETAILS_NETWORK_PATH_BUNDLE_KEY;
import static com.hemant.popularmovies.activities.MovieDetailsActivity.MOVIE_ID_BUNDLE_KEY;

public class FetchYouTubeTrailersAsyncTaskLoader extends AsyncTaskLoader<ArrayList<String>> {
    private ArrayList<String> youTubeTrailersList;
    private final YouTubeTrailersAsyncTaskListener youTubeTrailersAsyncTaskListener;
    private final Bundle args;
    private final URL url;

    public FetchYouTubeTrailersAsyncTaskLoader(Context context, Bundle args, YouTubeTrailersAsyncTaskListener youTubeTrailersAsyncTaskListener) {
        super(context);
        this.youTubeTrailersAsyncTaskListener = youTubeTrailersAsyncTaskListener;
        this.args = args;
        url = getURLAndMovieIdFromBundle(context);
    }

    @Nullable
    private URL getURLAndMovieIdFromBundle(Context context) {
        try {
            String path = null;
            if (args != null) {
                if (args.containsKey(MOVIE_DETAILS_NETWORK_PATH_BUNDLE_KEY)) {
                    if (!TextUtils.isEmpty(args.getString(MOVIE_DETAILS_NETWORK_PATH_BUNDLE_KEY))) {
                        path = args.getString(MOVIE_DETAILS_NETWORK_PATH_BUNDLE_KEY);
                    }
                }
            }
            String movieId = null;
            if (args != null) {
                if (args.containsKey(MOVIE_ID_BUNDLE_KEY)) {
                    if (!TextUtils.isEmpty(args.getString(MOVIE_ID_BUNDLE_KEY))) {
                        movieId = args.getString(MOVIE_ID_BUNDLE_KEY);
                    }
                }
            }
            return NetworkUtils.buildUrlForMovieDetails(context, movieId, path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ArrayList<String> loadInBackground() {
        ArrayList<String> youTubeTrailers = new ArrayList<>();
        try {
            String youTubeTrailersJsonString = NetworkUtils.getResponseFromHttpUrl(url);
            youTubeTrailers = MovieDetailsJsonUtils.getYoutubeTrailersFromJson(youTubeTrailersJsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return youTubeTrailers;
    }


    @Override
    public void deliverResult(@Nullable ArrayList<String> data) {
        super.deliverResult(data);
        youTubeTrailersList = data;
    }

    @Override
    protected void onStartLoading() {
        youTubeTrailersAsyncTaskListener.onYouTubeTrailerOnStartLoading();
        if (args == null) {
            return;
        }
        if (youTubeTrailersList != null) {
            deliverResult(youTubeTrailersList);
        } else {
            forceLoad();
        }
    }

}