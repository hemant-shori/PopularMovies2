package com.hemant.popularmovies.Utils;

import android.content.Context;
import android.widget.Toast;

import com.hemant.popularmovies.Models.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public final class MovieDetailsJsonUtils {


    public static ArrayList<MovieDetails> getMovieDetailsFromJson(String movieDetailsString)
            throws JSONException {

        final String ORIGINAL_TITLE = "original_title";
        final String BACKDROP_PATH = "backdrop_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        final String STATUS_CODE = "status_code";

        final String PAGE = "page";
        final String RESULTS = "results";

        ArrayList<MovieDetails> movieDetailsList = null;

        JSONObject movieDetailsJsonObject = new JSONObject(movieDetailsString);

        if (movieDetailsJsonObject.has(STATUS_CODE)) {
            return null;
        } else if (movieDetailsJsonObject.has(PAGE)) {
            JSONArray movieDetailsArray = movieDetailsJsonObject.getJSONArray(RESULTS);
            movieDetailsList = new ArrayList<>();

            for (int i = 0; i < movieDetailsArray.length(); i++) {
                JSONObject movieJsonObject = movieDetailsArray.getJSONObject(i);
                MovieDetails movieDetails = new MovieDetails();

                movieDetails.setRatings(movieJsonObject.getString(VOTE_AVERAGE));
                movieDetails.setReleaseDate(movieJsonObject.getString(RELEASE_DATE));
                movieDetails.setSynopsis(movieJsonObject.getString(OVERVIEW));
                movieDetails.setTitle(movieJsonObject.getString(ORIGINAL_TITLE));
                String imagePath;
                try {
                    imagePath = NetworkUtils.IMAGE_URL + URLDecoder.decode(movieJsonObject.getString(BACKDROP_PATH), "UTF-8");
                    movieDetails.setImagePath(imagePath);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                movieDetailsList.add(movieDetails);
            }
        }
        return movieDetailsList;
    }
}