package com.hemant.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemant.popularmovies.Models.MovieDetails;
import com.hemant.popularmovies.R;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        TextView ratingsTextView = findViewById(R.id.tv_details_ratings);
        TextView releaseDateTextView = findViewById(R.id.tv_details_release_date);
        TextView synopsisTextView = findViewById(R.id.tv_details_synopsis);
        ImageView moviePosterImageView = findViewById(R.id.iv_details_movie_poster);
        MovieDetails movieDetails;
        if (getIntent().hasExtra("selectedMovieDetails")) {
            movieDetails = getIntent().getParcelableExtra("selectedMovieDetails");
            ratingsTextView.setText(String.format("%s%s", movieDetails.getRatings(), getString(R.string.hyphen10)));
            releaseDateTextView.setText(movieDetails.getReleaseDate());
            synopsisTextView.setText(movieDetails.getSynopsis());
            Picasso.with(this).load(movieDetails.getImagePath()).into(moviePosterImageView);
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(movieDetails.getTitle());
        }
    }
}

