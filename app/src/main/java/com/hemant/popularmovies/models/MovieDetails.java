package com.hemant.popularmovies.models;


import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class MovieDetails implements Parcelable {
    private String Title;
    private String ImagePath;
    private String Synopsis;
    private String Ratings;
    private String ReleaseDate;
    private int MovieId;
    private byte[] MoviePoster;

    public String getImagePath() {
        return ImagePath;
    }

    public String getRatings() {
        return Ratings;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public String getSynopsis() {
        return Synopsis;
    }

    public String getTitle() {
        return Title;
    }

    public int getMovieId() {
        return MovieId;
    }

    public byte[] getMoviePoster() {
        return MoviePoster;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public void setRatings(String ratings) {
        Ratings = ratings;
    }

    public void setReleaseDate(String releaseDate) {
        ReleaseDate = releaseDate;
    }

    public void setSynopsis(String synopsis) {
        Synopsis = synopsis;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setMovieId(int movieId) {
        MovieId = movieId;
    }

    public void setMoviePoster(byte[] moviePoster) {
        MoviePoster = moviePoster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getTitle());
        parcel.writeString(getImagePath());
        parcel.writeString(getSynopsis());
        parcel.writeString(getRatings());
        parcel.writeString(getReleaseDate());
        parcel.writeInt(getMovieId());
//        Alternative solution to read a byte[]
        if (getMoviePoster() == null) {
            parcel.writeInt(0);
            parcel.writeByteArray(new byte[]{});
        } else {
            parcel.writeInt(getMoviePoster().length);
            parcel.writeByteArray(getMoviePoster());
        }
    }

    @Override
    public String toString() {
        return "MovieDetails{" +
                "title='" + getTitle() + "'" +
                ", releaseDate='" + getReleaseDate() + "'" +
                ", ratings='" + getRatings() + "'" +
                ", synopsis='" + getSynopsis() + "'" +
                ",imagePath='" + getImagePath() + "'" +

                "}";

    }

    public MovieDetails() {
    }

    private MovieDetails(Parcel in) {
        setTitle(in.readString());
        setImagePath(in.readString());
        setSynopsis(in.readString());
        setRatings(in.readString());
        setReleaseDate(in.readString());
        setMovieId(in.readInt());
//        Alternative solution to read a byte[]
        byte[] _byte = new byte[in.readInt()];
        in.readByteArray(_byte);
        setMoviePoster(_byte);
//        setMoviePoster(in.createByteArray());
    }

    public static Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new MovieDetails(parcel);
        }

        @Override
        public MovieDetails[] newArray(int i) {
            return new MovieDetails[i];
        }
    };
}
