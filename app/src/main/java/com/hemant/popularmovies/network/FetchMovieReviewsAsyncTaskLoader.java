package com.hemant.popularmovies.network;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.hemant.popularmovies.interfaces.MovieReviewAsyncTaskListener;
import com.hemant.popularmovies.models.MovieReview;
import com.hemant.popularmovies.utils.MovieDetailsJsonUtils;
import com.hemant.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.hemant.popularmovies.activities.MovieDetailsActivity.MOVIE_DETAILS_NETWORK_PATH_BUNDLE_KEY;
import static com.hemant.popularmovies.activities.MovieDetailsActivity.MOVIE_ID_BUNDLE_KEY;

public class FetchMovieReviewsAsyncTaskLoader extends AsyncTaskLoader<ArrayList<MovieReview>> {
    private ArrayList<MovieReview> movieReviewsArrayList;
    private final MovieReviewAsyncTaskListener movieReviewAsyncTaskListener;
    private final Bundle args;
    private final URL url;

    public FetchMovieReviewsAsyncTaskLoader(Context context, Bundle args, MovieReviewAsyncTaskListener movieReviewAsyncTaskListener) {
        super(context);
        this.movieReviewAsyncTaskListener = movieReviewAsyncTaskListener;
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
    public ArrayList<MovieReview> loadInBackground() {
        ArrayList<MovieReview> movieReviews = new ArrayList<>();
        try {
            String movieReviewJsonString = NetworkUtils.getResponseFromHttpUrl(url);
            movieReviews = MovieDetailsJsonUtils.getMovieReviewsFromJson(movieReviewJsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieReviews;
    }


    @Override
    public void deliverResult(@Nullable ArrayList<MovieReview> data) {
        super.deliverResult(data);
        movieReviewsArrayList = data;
    }

    @Override
    protected void onStartLoading() {
        movieReviewAsyncTaskListener.onMovieReviewOnStartLoading();
        if (args == null) {
            return;
        }
        if (movieReviewsArrayList != null) {
            deliverResult(movieReviewsArrayList);
        } else {
            forceLoad();
        }
    }

}