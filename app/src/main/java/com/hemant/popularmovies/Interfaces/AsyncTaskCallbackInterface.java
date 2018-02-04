package com.hemant.popularmovies.Interfaces;

import com.hemant.popularmovies.Models.MovieDetails;

import java.util.ArrayList;

public interface AsyncTaskCallbackInterface {
    public void onAsyncTaskPreExecute();

    public void onAsyncTaskPostExecute(ArrayList<MovieDetails> movieDetails);
}
