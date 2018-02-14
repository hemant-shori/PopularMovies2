package com.hemant.popularmovies.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hemant.popularmovies.R;
import com.hemant.popularmovies.adapters.MovieReviewsAdapters;
import com.hemant.popularmovies.adapters.YouTubeTrailersAdapters;
import com.hemant.popularmovies.database.MovieContract;
import com.hemant.popularmovies.interfaces.AsyncTaskCallbackListener;
import com.hemant.popularmovies.interfaces.MovieReviewAsyncTaskListener;
import com.hemant.popularmovies.interfaces.YouTubeTrailersAsyncTaskListener;
import com.hemant.popularmovies.models.MovieDetails;
import com.hemant.popularmovies.models.MovieReview;
import com.hemant.popularmovies.network.FetchMovieReviewsAsyncTaskLoader;
import com.hemant.popularmovies.network.FetchYouTubeTrailersAsyncTaskLoader;
import com.hemant.popularmovies.network.MovieDetailsAsyncTasks;
import com.hemant.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks, YouTubeTrailersAsyncTaskListener, MovieReviewAsyncTaskListener, YouTubeTrailersAdapters.OnTrailerItemClickListener, MovieReviewsAdapters.OnMovieReviewItemClickListener, AsyncTaskCallbackListener, View.OnClickListener {
    public static final String SELECTED_MOVIE_DETAILS = "SELECTED_MOVIE_DETAILS";
    private ProgressBar youtubeProgressBar, movieReviewProgressBar;
    public static final String MOVIE_DETAILS_NETWORK_PATH_BUNDLE_KEY = "MOVIE_DETAILS_NETWORK_PATH_BUNDLE_KEY";
    public static final String MOVIE_ID_BUNDLE_KEY = "MOVIE_ID_BUNDLE_KEY";
    private static final int FETCH_YOUTUBE_TRAILERS_LOADER = 799;
    private static final int FETCH_MOVIE_REVIEWS_LOADER = 380;
    private String mMovieId;
    private RecyclerView recyclerViewTrailer, recyclerViewReviews;
    private TextView textViewErrorLoading, textViewNoReviewsFound, textViewNoYouTubeTrailer;
    private Button buttonTryAgain;
    private YouTubeTrailersAdapters youTubeTrailersAdapters;
    private MovieReviewsAdapters movieReviewsAdapters;
    private MovieDetails movieDetails;
    private FloatingActionButton floatingActionButtonFavorite;
    private boolean favoriteMovie = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (!getIntent().hasExtra(SELECTED_MOVIE_DETAILS)) {
            Toast.makeText(getApplicationContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        movieDetails = getIntent().getParcelableExtra(SELECTED_MOVIE_DETAILS);

        TextView ratingsTextView = findViewById(R.id.tv_details_ratings);
        TextView releaseDateTextView = findViewById(R.id.tv_details_release_date);
        TextView synopsisTextView = findViewById(R.id.tv_details_synopsis);
        TextView orgTitleTextView = findViewById(R.id.tv_details_title);
        ImageView moviePosterImageView = findViewById(R.id.iv_details_movie_poster);
        youtubeProgressBar = findViewById(R.id.progressBarYouTubeTrailer);
        movieReviewProgressBar = findViewById(R.id.progressBarMovieReviews);
        textViewErrorLoading = findViewById(R.id.textViewErrorLoading);
        buttonTryAgain = findViewById(R.id.buttonTryAgain);
        floatingActionButtonFavorite = findViewById(R.id.fabActionAddToFavorite);
        textViewNoReviewsFound = findViewById(R.id.textViewNoReviewsFound);
        textViewNoYouTubeTrailer = findViewById(R.id.textViewNoYouTubeTrailer);
        ratingsTextView.setText(String.format("%s%s", movieDetails.getRatings(), getString(R.string.hyphen10)));
        releaseDateTextView.setText(movieDetails.getReleaseDate());
        synopsisTextView.setText(movieDetails.getSynopsis());
        Picasso.with(this).load(movieDetails.getImagePath()).into(moviePosterImageView);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(movieDetails.getTitle());
        orgTitleTextView.setText(movieDetails.getTitle());
        mMovieId = String.valueOf(movieDetails.getMovieId());
        buttonTryAgain.setOnClickListener(this);
        floatingActionButtonFavorite.setOnClickListener(this);
        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        recyclerViewReviews = findViewById(R.id.recyclerViewMovieReviews);
        LinearLayoutManager linearLayoutManagerReview = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewReviews.setLayoutManager(linearLayoutManagerReview);
        recyclerViewReviews.setHasFixedSize(true);
        movieReviewsAdapters = new MovieReviewsAdapters(this);
        recyclerViewReviews.setAdapter(movieReviewsAdapters);

        recyclerViewTrailer = findViewById(R.id.recyclerViewYouTubeTrailers);
        LinearLayoutManager linearLayoutManagerYouTube = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTrailer.setLayoutManager(linearLayoutManagerYouTube);
        recyclerViewTrailer.setHasFixedSize(true);

        youTubeTrailersAdapters = new YouTubeTrailersAdapters(this);
        recyclerViewTrailer.setAdapter(youTubeTrailersAdapters);
    }

    private void restartMovieDetailsLoaders() {
        getSupportLoaderManager().restartLoader(FETCH_MOVIE_REVIEWS_LOADER, getBundleForLoader(NetworkUtils.REVIEWS_PATH), this);
        getSupportLoaderManager().restartLoader(FETCH_YOUTUBE_TRAILERS_LOADER, getBundleForLoader(NetworkUtils.VIDEOS_PATH), this);
    }

    private Bundle getBundleForLoader(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_DETAILS_NETWORK_PATH_BUNDLE_KEY, path);
        bundle.putString(MOVIE_ID_BUNDLE_KEY, mMovieId);
        return bundle;
    }

    private void initMovieDetailsLoaders() {
        getSupportLoaderManager().initLoader(FETCH_MOVIE_REVIEWS_LOADER, getBundleForLoader(NetworkUtils.REVIEWS_PATH), this);
        getSupportLoaderManager().initLoader(FETCH_YOUTUBE_TRAILERS_LOADER, getBundleForLoader(NetworkUtils.VIDEOS_PATH), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMovieDetailsLoaders();
        new MovieDetailsAsyncTasks(this, MovieDetailsAsyncTasks.TaskType.FIND_MOVIE_ID).execute();
    }

    @Override
    public void onYouTubeTrailerOnStartLoading() {
        showYouTubeLoadingIndicator();
    }

    private void showYouTubeLoadingIndicator() {
        recyclerViewTrailer.setVisibility(View.INVISIBLE);
        youtubeProgressBar.setVisibility(View.VISIBLE);
        textViewErrorLoading.setVisibility(View.GONE);
        buttonTryAgain.setVisibility(View.GONE);
        textViewNoYouTubeTrailer.setVisibility(View.GONE);
    }


    @Override
    public void onMovieReviewOnStartLoading() {
        showReviewLoadingIndicator();
    }

    private void showReviewLoadingIndicator() {
        recyclerViewReviews.setVisibility(View.INVISIBLE);
        movieReviewProgressBar.setVisibility(View.VISIBLE);
        textViewNoReviewsFound.setVisibility(View.GONE);
    }

    private boolean isConnected() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FETCH_MOVIE_REVIEWS_LOADER:
                return new FetchMovieReviewsAsyncTaskLoader(getApplicationContext(), args, this);
            case FETCH_YOUTUBE_TRAILERS_LOADER:
                return new FetchYouTubeTrailersAsyncTaskLoader(getApplicationContext(), args, this);
            default:
                throw new UnsupportedOperationException("Unknown Loader Found");
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case FETCH_MOVIE_REVIEWS_LOADER:
                if (data != null) {
                    //noinspection unchecked
                    ArrayList<MovieReview> movieReviews = (ArrayList<MovieReview>) data;
                    movieReviewsAdapters.swapReviewsData(movieReviews);
                    if (movieReviews.size() > 0) {
                        showReviewUI();
                        break;
                    }
                }
                showErrorFetchingReviews();
                break;
            case FETCH_YOUTUBE_TRAILERS_LOADER:
                if (data != null) {
                    //noinspection unchecked
                    ArrayList<String> youTubeUrlArrayList = (ArrayList<String>) data;
                    youTubeTrailersAdapters.swapTrailerData(youTubeUrlArrayList);
                    if (youTubeUrlArrayList.size() > 0) {
                        showYoutubeUI();
                        break;
                    }
                }
                showErrorFetchingTrailers();
                break;
            default:
                throw new UnsupportedOperationException("Unknown Loader Found");
        }
    }

    private void showErrorFetchingTrailers() {
        youtubeProgressBar.setVisibility(View.GONE);
        if (isConnected()) {
            textViewNoYouTubeTrailer.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "Unable to Fetch YouTube Trailers", Toast.LENGTH_SHORT).show();
            buttonTryAgain.setVisibility(View.VISIBLE);
            textViewErrorLoading.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorFetchingReviews() {
        movieReviewProgressBar.setVisibility(View.GONE);
        if (isConnected()) {
            textViewNoReviewsFound.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "unable to Fetch Movie Reviews, Check Network", Toast.LENGTH_SHORT).show();
        }
    }

    private void showReviewUI() {
        recyclerViewReviews.setVisibility(View.VISIBLE);
        movieReviewProgressBar.setVisibility(View.GONE);
    }

    private void showYoutubeUI() {
        recyclerViewTrailer.setVisibility(View.VISIBLE);
        youtubeProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onYouTubeTrailerItemClicked(String youtubeId) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.YouTubeURL) + youtubeId)));

    }

    @Override
    public void onMovieReviewItemClicked(MovieReview movieReview) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movieReview.getUrl())));
    }


    @Override
    public void onAsyncTaskPreExecute(MovieDetailsAsyncTasks.TaskType taskType) {
        switch (taskType) {
            case FIND_MOVIE_ID:
                floatingActionButtonFavorite.setClickable(false);
                floatingActionButtonFavorite.setVisibility(View.GONE);
                break;
            case INSERT_FAV_MOVIE:
                floatingActionButtonFavorite.setClickable(false);
                break;
            case DELETE_FAV_MOVIE:
                floatingActionButtonFavorite.setClickable(false);
                break;
        }
    }

    @Override
    public void onAsyncTaskPostExecute(Boolean result, MovieDetailsAsyncTasks.TaskType taskType) {
        switch (taskType) {
            case FIND_MOVIE_ID:
                floatingActionButtonFavorite.setVisibility(View.VISIBLE);
                floatingActionButtonFavorite.setClickable(true);
                favoriteMovie = result;
                if (result) {
                    floatingActionButtonFavorite.setImageResource(R.drawable.ic_action_favorite);
                } else {
                    floatingActionButtonFavorite.setImageResource(R.drawable.ic_add_to_fav);
                }
                break;
            case INSERT_FAV_MOVIE:
                floatingActionButtonFavorite.setClickable(true);
                favoriteMovie = result;
                if (result) {
                    floatingActionButtonFavorite.setImageResource(R.drawable.ic_action_favorite);
                } else {
                    floatingActionButtonFavorite.setImageResource(R.drawable.ic_add_to_fav);
                }
                break;
            case DELETE_FAV_MOVIE:
                floatingActionButtonFavorite.setClickable(true);
                setResult(RESULT_OK, getIntent().putExtra(MainActivity.MOVIE_DETAILS_RESULT_DATA, result));
                if (result) {
                    favoriteMovie = false;
                    floatingActionButtonFavorite.setImageResource(R.drawable.ic_add_to_fav);
                } else {
                    floatingActionButtonFavorite.setImageResource(R.drawable.ic_action_favorite);
                }
                break;
        }
    }

    @Override
    public boolean findMovieInDB() {
        Uri uri = MovieContract.MovieEntry.buildMovieUriWithId(mMovieId);
        Cursor cursor = getContentResolver().query(uri,
                null,
                null,
                null,
                null
        );
        if (cursor != null) {
            int count = cursor.getCount();
            cursor.close();
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean insertMovieInDB() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.MOVIE_ID, movieDetails.getMovieId());
        contentValues.put(MovieContract.MovieEntry.MOVIE, new Gson().toJson(movieDetails));
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        if (uri != null) {
            String idString = uri.getLastPathSegment();
            try {
                int id = Integer.parseInt(idString);
                if (id > 0) {
                    return true;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean deleteMovieFromDB() {
        Uri uri = MovieContract.MovieEntry.buildMovieUriWithId(mMovieId);
        int rowsDeleted = getContentResolver().delete(uri,
                null,
                null
        );
        return rowsDeleted > 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabActionAddToFavorite:
                if (favoriteMovie) {
                    new MovieDetailsAsyncTasks(this, MovieDetailsAsyncTasks.TaskType.DELETE_FAV_MOVIE).execute();
                } else {
                    new MovieDetailsAsyncTasks(this, MovieDetailsAsyncTasks.TaskType.INSERT_FAV_MOVIE).execute();
                }
                break;
            case R.id.buttonTryAgain:
                if (isConnected()) {
                    restartMovieDetailsLoaders();
                }
                break;
        }
    }
}

