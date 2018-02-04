package com.hemant.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.hemant.popularmovies.Adapters.AllMoviesAdapter;
import com.hemant.popularmovies.Interfaces.AsyncTaskCallbackInterface;
import com.hemant.popularmovies.Models.MovieDetails;
import com.hemant.popularmovies.Utils.LoadMovieDetailsAsyncTask;
import com.hemant.popularmovies.Utils.MovieDetailsJsonUtils;
import com.hemant.popularmovies.Utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AllMoviesAdapter.OnMovieClickListener, AsyncTaskCallbackInterface {

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private AllMoviesAdapter allMoviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.rv_all_movies);
        int columnCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        allMoviesAdapter = new AllMoviesAdapter(this);
        mRecyclerView.setAdapter(allMoviesAdapter);
        fetchMovieDetails(NetworkUtils.POPULAR_PATH);
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
                fetchMovieDetails(NetworkUtils.POPULAR_PATH);
                break;
            case R.id.sort_by_top:
                fetchMovieDetails(NetworkUtils.TOP_RATED_PATH);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDetailsView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showProgressView() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void fetchMovieDetails(String path) {
        try {
            URL url = NetworkUtils.buildUrl(getApplicationContext(), path);
            new LoadMovieDetailsAsyncTask(this).execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClicked(MovieDetails movieDetails) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("selectedMovieDetails", movieDetails);
        startActivity(intent);
    }


    @Override
    public void onAsyncTaskPreExecute() {
        showProgressView();
    }

    @Override
    public void onAsyncTaskPostExecute(ArrayList<MovieDetails> movieDetails) {
        allMoviesAdapter.setMoviesData(movieDetails);
        showDetailsView();
    }
}
