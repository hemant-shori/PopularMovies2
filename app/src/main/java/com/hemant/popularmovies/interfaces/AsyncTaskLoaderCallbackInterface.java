package com.hemant.popularmovies.interfaces;


import com.hemant.popularmovies.models.MovieDetails;

import java.util.ArrayList;

public interface AsyncTaskLoaderCallbackInterface {
    ArrayList<MovieDetails> loadFavMoviesFromDB();

    void onStartLoading();
}
