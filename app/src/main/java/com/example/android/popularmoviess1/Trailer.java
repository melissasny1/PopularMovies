package com.example.android.popularmoviess1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to create a parcelable Trailer object, with getter methods, to contain the trailer
 * detail obtained from the Movie DB API, to be displayed in the detail screen UI.
 */

class Trailer implements Parcelable{

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>(){

        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }
    };

    private final String mTrailerKey;
    private final String mTrailerName;

    Trailer(String trailerKey, String trailerName){
        mTrailerKey = trailerKey;
        mTrailerName = trailerName;
    }

    private Trailer(Parcel in){
        mTrailerKey = in.readString();
        mTrailerName = in.readString();
    }

    String getTrailerKey(){return mTrailerKey;}

    String getTrailerName(){return mTrailerName;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTrailerKey);
        parcel.writeString(mTrailerName);

    }
}
