package com.hemant.popularmovies.Models;


import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class MovieDetails implements Parcelable {
    private String Title;
    private String ImagePath;
    private String Synopsis;
    private String Ratings;
    private String ReleaseDate;

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
    }

    public static Parcelable.Creator CREATOR = new Parcelable.Creator() {
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
