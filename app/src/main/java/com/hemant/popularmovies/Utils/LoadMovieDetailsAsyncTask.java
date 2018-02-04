package com.hemant.popularmovies.Utils;

import android.os.AsyncTask;

import com.hemant.popularmovies.Interfaces.AsyncTaskCallbackInterface;
import com.hemant.popularmovies.Models.MovieDetails;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class LoadMovieDetailsAsyncTask extends AsyncTask<URL, Void, ArrayList<MovieDetails>> {
    private AsyncTaskCallbackInterface asyncTaskCallbackInterface;

    public LoadMovieDetailsAsyncTask(AsyncTaskCallbackInterface asyncTaskCallbackInterface) {
        this.asyncTaskCallbackInterface = asyncTaskCallbackInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        asyncTaskCallbackInterface.onAsyncTaskPreExecute();
    }

    @Override
    protected ArrayList<MovieDetails> doInBackground(URL... urls) {
        if (urls[0] == null)
            return null;
        ArrayList<MovieDetails> movieDetails = null;
        try {
            String jsonData = NetworkUtils.getResponseFromHttpUrl(urls[0]);
            movieDetails = MovieDetailsJsonUtils.getMovieDetailsFromJson(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieDetails;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieDetails> movieDetails) {
        super.onPostExecute(movieDetails);
        asyncTaskCallbackInterface.onAsyncTaskPostExecute(movieDetails);
    }
}