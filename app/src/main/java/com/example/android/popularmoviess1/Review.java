package com.example.android.popularmoviess1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to create a parcelable Review object, with getter methods, to contain the review
 * detail obtained from the Movie DB API, to be displayed in the detail screen UI.
 */

class Review implements Parcelable{

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){

        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }
    };

    private final String mReviewAuthor;
    private final String mReviewContent;

    Review(String reviewAuthor, String reviewContent){
        mReviewAuthor = reviewAuthor;
        mReviewContent = reviewContent;
    }

    private Review(Parcel in){
        mReviewAuthor = in.readString();
        mReviewContent = in.readString();
    }

    String getReviewAuthor(){return mReviewAuthor;}

    String getReviewContent(){return mReviewContent;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mReviewAuthor);
        parcel.writeString(mReviewContent);
    }
}
