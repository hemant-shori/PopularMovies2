package com.hemant.popularmovies.utils;

import com.hemant.popularmovies.models.MovieDetails;
import com.hemant.popularmovies.models.MovieReview;

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
        final String MOVIE_ID = "id";

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
                movieDetails.setMovieId(movieJsonObject.getInt(MOVIE_ID));
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

    public static ArrayList<String> getYoutubeTrailersFromJson(String trailerJsonString) throws JSONException {
        final String STATUS_CODE = "status_code";
        final String STATUS_MESSAGE = "status_message";
        final String SITE = "site";
        final String YOUTUBE = "YouTube";
        final String KEY = "key";
        final String RESULTS = "results";

        ArrayList<String> trailerArrayList = new ArrayList<>();
        JSONObject trailerJsonResult = new JSONObject(trailerJsonString);
        if (trailerJsonResult.has(STATUS_CODE)) {
            //unable to Fetch Youtube URL
            return trailerArrayList;
        } else if (trailerJsonResult.has(RESULTS)) {
            JSONArray jsonArrayResult = trailerJsonResult.getJSONArray(RESULTS);
            for (int i = 0; i < jsonArrayResult.length(); i++) {
                JSONObject trailerObj = jsonArrayResult.getJSONObject(i);
                if (trailerObj.has(SITE)) {
                    if (trailerObj.getString(SITE).equals(YOUTUBE)) {
                        if (trailerObj.has(KEY)) {
                            trailerArrayList.add(trailerObj.getString(KEY));
                        }
                    }
                }
            }
        }
        return trailerArrayList;
    }


    public static ArrayList<MovieReview> getMovieReviewsFromJson(String trailerJsonString) throws JSONException {
        final String STATUS_CODE = "status_code";
        final String STATUS_MESSAGE = "status_message";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";
        final String ID = "id";
        final String RESULTS = "results";

        ArrayList<MovieReview> reviewsArrayList = new ArrayList<>();
        JSONObject reviewsJsonResult = new JSONObject(trailerJsonString);
        if (reviewsJsonResult.has(STATUS_CODE)) {
            //unable to Fetch MovieReview URL
            return reviewsArrayList;
        } else if (reviewsJsonResult.has(RESULTS)) {
            JSONArray jsonArrayResult = reviewsJsonResult.getJSONArray(RESULTS);
            for (int i = 0; i < jsonArrayResult.length(); i++) {
                JSONObject reviewsObj = jsonArrayResult.getJSONObject(i);
                MovieReview movieReview = new MovieReview();
                if (reviewsObj.has(AUTHOR)) {
                    movieReview.setAuthor(reviewsObj.getString(AUTHOR));
                }
                if (reviewsObj.has(CONTENT)) {
                    movieReview.setContent(reviewsObj.getString(CONTENT));
                }
                if (reviewsObj.has(URL)) {
                    movieReview.setUrl(reviewsObj.getString(URL));
                }
                reviewsArrayList.add(movieReview);
            }
        }
        return reviewsArrayList;
    }
}