package com.hemant.popularmovies.network;

import android.os.AsyncTask;

import com.hemant.popularmovies.interfaces.AsyncTaskCallbackListener;


public class MovieDetailsAsyncTasks extends AsyncTask<Void, Void, Boolean> {
    private final AsyncTaskCallbackListener asyncTaskCallbackInterface;

    public enum TaskType {
        FIND_MOVIE_ID(1), INSERT_FAV_MOVIE(2), DELETE_FAV_MOVIE(3);

        final int value;

        TaskType(int value) {
            this.value = value;
        }
    }

    private final TaskType taskType;

    public MovieDetailsAsyncTasks(AsyncTaskCallbackListener asyncTaskCallbackInterface, TaskType taskType) {
        this.asyncTaskCallbackInterface = asyncTaskCallbackInterface;
        this.taskType = taskType;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        switch (taskType) {
            case FIND_MOVIE_ID:
                return asyncTaskCallbackInterface.findMovieInDB();
            case INSERT_FAV_MOVIE:
                return asyncTaskCallbackInterface.insertMovieInDB();
            case DELETE_FAV_MOVIE:
                return asyncTaskCallbackInterface.deleteMovieFromDB();

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        asyncTaskCallbackInterface.onAsyncTaskPreExecute(taskType);
    }


    @Override
    protected void onPostExecute(Boolean obj) {
        //unchecked
        super.onPostExecute(obj);
        asyncTaskCallbackInterface.onAsyncTaskPostExecute(obj, taskType);
    }
}