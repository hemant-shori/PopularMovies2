package com.hemant.popularmovies.interfaces;

import com.hemant.popularmovies.network.MovieDetailsAsyncTasks;

public interface AsyncTaskCallbackListener {
    void onAsyncTaskPreExecute(MovieDetailsAsyncTasks.TaskType taskType);

    void onAsyncTaskPostExecute(Boolean result, MovieDetailsAsyncTasks.TaskType taskType);

    boolean findMovieInDB();

    boolean insertMovieInDB();

    boolean deleteMovieFromDB();
}
