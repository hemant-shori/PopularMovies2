package com.hemant.popularmovies.network;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.hemant.popularmovies.interfaces.AsyncTaskLoaderCallbackInterface;
import com.hemant.popularmovies.models.MovieDetails;
import com.hemant.popularmovies.utils.MovieDetailsJsonUtils;
import com.hemant.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.hemant.popularmovies.activities.MainActivity.NETWORK_PATH_BUNDLE_KEY;
import static com.hemant.popularmovies.activities.MainActivity.PERSISTENT_TYPE_BUNDLE_KEY;

public class FetchMoviesAsyncTaskLoader extends AsyncTaskLoader<ArrayList<MovieDetails>> {
    private final AsyncTaskLoaderCallbackInterface asyncTaskLoaderCallbackInterface;
    private final URL url;
    private final Bundle args;
    private ArrayList<MovieDetails> movieDetails;
    public static final int LOAD_FAVORITE_MOVIES = 200;
    public static final int LOAD_MOVIE_FROM_API = 371;

    private int mLoadingType = LOAD_MOVIE_FROM_API;

    public FetchMoviesAsyncTaskLoader(@NonNull Context context, Bundle args, AsyncTaskLoaderCallbackInterface asyncTaskLoaderCallbackInterface) {
        super(context);
        this.asyncTaskLoaderCallbackInterface = asyncTaskLoaderCallbackInterface;
        this.args = args;
        url = getURLAndMovieTypeFromBundle(context);
    }

    private URL getURLAndMovieTypeFromBundle(Context context) {
        try {
            String path = null;
            if (args != null) {
                if (args.containsKey(NETWORK_PATH_BUNDLE_KEY)) {
                    if (!TextUtils.isEmpty(args.getString(NETWORK_PATH_BUNDLE_KEY))) {
                        path = args.getString(NETWORK_PATH_BUNDLE_KEY);
                    }
                }
                if (args.containsKey(PERSISTENT_TYPE_BUNDLE_KEY)) {
                    if (args.getInt(PERSISTENT_TYPE_BUNDLE_KEY) == LOAD_FAVORITE_MOVIES || args.getInt(PERSISTENT_TYPE_BUNDLE_KEY) == LOAD_MOVIE_FROM_API) {
                        mLoadingType = args.getInt(PERSISTENT_TYPE_BUNDLE_KEY);
                    }
                }
            }
            return NetworkUtils.buildUrl(context, path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public ArrayList<MovieDetails> loadInBackground() {
        if (url == null) return null;
        ArrayList<MovieDetails> movieDetails = null;
        try {
            if (mLoadingType == LOAD_MOVIE_FROM_API) {
                String jsonData = NetworkUtils.getResponseFromHttpUrl(url);
                movieDetails = MovieDetailsJsonUtils.getMovieDetailsFromJson(jsonData);
            } else if (mLoadingType == LOAD_FAVORITE_MOVIES) {
                movieDetails = asyncTaskLoaderCallbackInterface.loadFavMoviesFromDB();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieDetails;
    }

    @Override
    public void deliverResult(@Nullable ArrayList<MovieDetails> data) {
        super.deliverResult(data);
        movieDetails = data;
    }

    @Override
    protected void onStartLoading() {
        asyncTaskLoaderCallbackInterface.onStartLoading();
        if (args == null) {
            return;
        }
        if (movieDetails != null) {
            deliverResult(movieDetails);
        } else {
            forceLoad();
        }
    }

}
