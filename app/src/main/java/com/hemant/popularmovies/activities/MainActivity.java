package com.hemant.popularmovies.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hemant.popularmovies.R;
import com.hemant.popularmovies.adapters.AllMoviesAdapter;
import com.hemant.popularmovies.database.MovieContract;
import com.hemant.popularmovies.interfaces.AsyncTaskLoaderCallbackInterface;
import com.hemant.popularmovies.models.MovieDetails;
import com.hemant.popularmovies.network.FetchMoviesAsyncTaskLoader;
import com.hemant.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AllMoviesAdapter.OnMovieClickListener,
        AsyncTaskLoaderCallbackInterface, LoaderManager.LoaderCallbacks<ArrayList<MovieDetails>>, View.OnClickListener {

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private AllMoviesAdapter allMoviesAdapter;

    public static final String NETWORK_PATH_BUNDLE_KEY = "NETWORK_PATH_BUNDLE_KEY";
    public static final String PERSISTENT_TYPE_BUNDLE_KEY = "PERSISTENT_TYPE_BUNDLE_KEY";
    private static final String SAVE_LOADING_PERSISTENT_TYPE_BUNDLE_KEY = "SAVE_LOADING_PERSISTENT_TYPE_BUNDLE_KEY";
    private static final String SAVE_NETWORK_PATH_TYPE_BUNDLE_KEY = "SAVE_NETWORK_PATH_TYPE_BUNDLE_KEY";
    private static final String SAVE_ACTIVITY_TITLE_BUNDLE_KEY = "SAVE_ACTIVITY_TITLE_BUNDLE_KEY";
    private static final int SYNC_MOVIE_DETAILS_LOADER = 369;
    private int mCurrentRunningPersistentTypeId = FetchMoviesAsyncTaskLoader.LOAD_MOVIE_FROM_API;
    private String mPath = NetworkUtils.POPULAR_PATH;
    private String mTitle;
    private static final int MOVIE_DETAIL_REQUEST_CODE = 384;
    public static final String MOVIE_DETAILS_RESULT_DATA = "MOVIE_DETAILS_RESULT_DATA";
    private boolean restartLoaderToUpdateFavMovies = false;
    private TextView textViewNoMovieFoundError;
    private Button buttonNoMovieFoundTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_ACTIVITY_TITLE_BUNDLE_KEY)) {
            mTitle = savedInstanceState.getString(SAVE_ACTIVITY_TITLE_BUNDLE_KEY);
            setMyActionBarCustomTitle();
        }
        mTitle = getString(R.string.PopularMovieTitle);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.rv_all_movies);
        textViewNoMovieFoundError = findViewById(R.id.textViewNoMovieFoundError);
        buttonNoMovieFoundTryAgain = findViewById(R.id.buttonNoMovieFoundTryAgain);
        buttonNoMovieFoundTryAgain.setOnClickListener(this);
        int columnCount = getColumnAccordingToScreenSize();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        allMoviesAdapter = new AllMoviesAdapter(this);
        mRecyclerView.setAdapter(allMoviesAdapter);
    }

    private int getColumnAccordingToScreenSize() {
        Configuration configuration = getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        //int smallestWidth = configuration.smallestScreenWidthDp; //The smallest screen size an application will see in normal operation, corresponding to smallest screen width resource qualifier.
        if (screenWidthDp > getResources().getInteger(R.integer.smallestWidth700)) {
            return getResources().getInteger(R.integer.columnCount4);
        } else if (screenWidthDp > getResources().getInteger(R.integer.smallestWidth600)) {
            return getResources().getInteger(R.integer.columnCount3);
        } else {
            return getResources().getInteger(R.integer.columnCount2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_by_popular:
                mCurrentRunningPersistentTypeId = FetchMoviesAsyncTaskLoader.LOAD_MOVIE_FROM_API;
                restartLoaderToUpdateFavMovies = false;
                restartMovieDetailsLoader(NetworkUtils.POPULAR_PATH);
                mTitle = getString(R.string.PopularMovieTitle);
                setMyActionBarCustomTitle();
                break;
            case R.id.sort_by_top:
                mCurrentRunningPersistentTypeId = FetchMoviesAsyncTaskLoader.LOAD_MOVIE_FROM_API;
                restartLoaderToUpdateFavMovies = false;
                restartMovieDetailsLoader(NetworkUtils.TOP_RATED_PATH);
                mTitle = getString(R.string.TopMovieTitle);
                setMyActionBarCustomTitle();
                break;
            case R.id.show_favorites:
                mCurrentRunningPersistentTypeId = FetchMoviesAsyncTaskLoader.LOAD_FAVORITE_MOVIES;
                restartMovieDetailsLoader(null);
                mTitle = getString(R.string.FavoriteMovieTitle);
                setMyActionBarCustomTitle();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDetailsView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        buttonNoMovieFoundTryAgain.setVisibility(View.GONE);
        textViewNoMovieFoundError.setVisibility(View.GONE);
    }

    private void showProgressView() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void restartMovieDetailsLoader(String path) {
        this.mPath = path;
        Bundle bundle = new Bundle();
        bundle.putString(NETWORK_PATH_BUNDLE_KEY, path);
        bundle.putInt(PERSISTENT_TYPE_BUNDLE_KEY, mCurrentRunningPersistentTypeId);
        getSupportLoaderManager().restartLoader(SYNC_MOVIE_DETAILS_LOADER, bundle, this);
    }

    private void initMovieDetailsLoader(String path) {
        this.mPath = path;
        Bundle bundle = new Bundle();
        bundle.putString(NETWORK_PATH_BUNDLE_KEY, path);
        bundle.putInt(PERSISTENT_TYPE_BUNDLE_KEY, mCurrentRunningPersistentTypeId);
        getSupportLoaderManager().initLoader(SYNC_MOVIE_DETAILS_LOADER, bundle, this);
    }

    @Override
    public void onItemClicked(MovieDetails movieDetails) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.SELECTED_MOVIE_DETAILS, movieDetails);
        startActivityForResult(intent, MOVIE_DETAIL_REQUEST_CODE);
    }

    @Override
    public Loader<ArrayList<MovieDetails>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SYNC_MOVIE_DETAILS_LOADER:
                return new FetchMoviesAsyncTaskLoader(getApplicationContext(), args, this);
            default:
                throw new UnsupportedOperationException("Unknown Loader Executed : " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieDetails>> loader, ArrayList<MovieDetails> data) {
        if (data != null) {
            allMoviesAdapter.setMoviesData(data);
            showDetailsView();
        } else {
            showNoMovieFoundUI();
        }
    }

    private void showNoMovieFoundUI() {
        mProgressBar.setVisibility(View.GONE);
        if (mCurrentRunningPersistentTypeId == FetchMoviesAsyncTaskLoader.LOAD_FAVORITE_MOVIES) {
            textViewNoMovieFoundError.setText(getText(R.string.NoFavoriteMovieFound));
            textViewNoMovieFoundError.setVisibility(View.VISIBLE);
            buttonNoMovieFoundTryAgain.setVisibility(View.GONE);
        } else {
            textViewNoMovieFoundError.setVisibility(View.VISIBLE);
            if (!isConnected()) {
                textViewNoMovieFoundError.setText(getText(R.string.UnableToConnectToInternet));
                buttonNoMovieFoundTryAgain.setVisibility(View.VISIBLE);
            } else {
                textViewNoMovieFoundError.setText(getText(R.string.NoMovieFound));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieDetails>> loader) {
    }


    @Override
    public ArrayList<MovieDetails> loadFavMoviesFromDB() {
        ArrayList<MovieDetails> movieDetails = null;
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                movieDetails = new ArrayList<>(cursor.getCount());
                cursor.moveToFirst();
                do {
                    String movieJsonData = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE));
                    try {
                        Gson gson = new Gson();
                        MovieDetails movie = gson.fromJson(movieJsonData, MovieDetails.class);
                        assert movieDetails != null;
                        movieDetails.add(movie);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        movieDetails = null;
                    }
                }
                while (cursor.moveToNext());

            }
            cursor.close();
        }
        return movieDetails;
    }

    @Override
    public void onStartLoading() {
        showProgressView();
        allMoviesAdapter.setMoviesData(null);

    }

    private boolean isConnected() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_LOADING_PERSISTENT_TYPE_BUNDLE_KEY, mCurrentRunningPersistentTypeId);
        outState.putString(SAVE_NETWORK_PATH_TYPE_BUNDLE_KEY, mPath);
        outState.putString(SAVE_ACTIVITY_TITLE_BUNDLE_KEY, mTitle);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_LOADING_PERSISTENT_TYPE_BUNDLE_KEY)) {
            mCurrentRunningPersistentTypeId = savedInstanceState.getInt(SAVE_LOADING_PERSISTENT_TYPE_BUNDLE_KEY);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_NETWORK_PATH_TYPE_BUNDLE_KEY)) {
            mPath = savedInstanceState.getString(SAVE_NETWORK_PATH_TYPE_BUNDLE_KEY);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_ACTIVITY_TITLE_BUNDLE_KEY)) {
            mTitle = savedInstanceState.getString(SAVE_ACTIVITY_TITLE_BUNDLE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (restartLoaderToUpdateFavMovies) {
            restartMovieDetailsLoader(null);
        } else {
            initMovieDetailsLoader(mPath);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MOVIE_DETAIL_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        if (data.hasExtra(MOVIE_DETAILS_RESULT_DATA)) {
                            if (mPath == null && data.getBooleanExtra(MOVIE_DETAILS_RESULT_DATA, false)) {
                                restartLoaderToUpdateFavMovies = true;
                            }
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setMyActionBarCustomTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNoMovieFoundTryAgain:
                buttonNoMovieFoundTryAgain.setVisibility(View.GONE);
                textViewNoMovieFoundError.setVisibility(View.GONE);
                restartMovieDetailsLoader(mPath);
                break;
        }
    }
}
