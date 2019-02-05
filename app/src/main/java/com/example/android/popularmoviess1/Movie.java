package com.example.android.popularmoviess1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Melissa on 6/6/2017.
 * Create a Movie object that contains the fields from the Movie DB API that will be displayed
 * either in the main RecyclerView or the individual movie detail screen.
 */

class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
    //The unique integer which identifies the movie in the Movie DB API.
    private final int mMovieDbId;
    //The relative path for the movie poster in the Movie DB API.
    private final String mPosterPath;
    //The original title of the movie in the Movie DB API.
    private final String mOriginalTitle;
    //The plot synopsis for the movie in the Movie DB API.
    private final String mOverview;
    //The release date for the movie in the Movie DB API.
    private final String mReleaseDate;
    //The average rating for the movie in the Movie DB API.
    private final double mVoteAverage;
    //The int which indicates whether the user has selected the movie as a favorite
    //to be stored in the SQLite database.
    private final int mFavoriteFlag;

    Movie(int movieDbId, String posterPath, String originalTitle, String overview,
          String releaseDate, double voteAverage) {
        mMovieDbId = movieDbId;
        mPosterPath = posterPath;
        mOriginalTitle = originalTitle;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mVoteAverage = voteAverage;
        mFavoriteFlag = 0;
    }

    //Alternate constructor to be used when the user has marked the movie as a favorite
    //and it has already been saved to the SQLite database.  This constructor will be used
    //when the favorite movies are retrieved from the SQLite database.
    Movie(int movieDbId, String posterPath, String originalTitle, String overview,
          String releaseDate, double voteAverage, int favoriteFlag) {
        mMovieDbId = movieDbId;
        mPosterPath = posterPath;
        mOriginalTitle = originalTitle;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mVoteAverage = voteAverage;
        mFavoriteFlag = favoriteFlag;
    }

    private Movie(Parcel in) {
        mMovieDbId = in.readInt();
        mPosterPath = in.readString();
        mOriginalTitle = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mVoteAverage = in.readDouble();
        mFavoriteFlag = in.readInt();
    }

    //Getters.
    String getPosterPath() {
        return mPosterPath;
    }

    String getTitle() {
        return mOriginalTitle;
    }

    String getOverview() {
        return mOverview;
    }

    String getReleaseDate() {
        return mReleaseDate;
    }

    double getVoteAverage() {
        return mVoteAverage;
    }

    int getMovieDbId() {return mMovieDbId;}

    int getFavoriteFlag(){ return mFavoriteFlag;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mMovieDbId);
        parcel.writeString(mPosterPath);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mOverview);
        parcel.writeString(mReleaseDate);
        parcel.writeDouble(mVoteAverage);
        parcel.writeInt(mFavoriteFlag);
    }
}
