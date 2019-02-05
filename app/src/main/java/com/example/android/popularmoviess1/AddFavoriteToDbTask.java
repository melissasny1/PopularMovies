package com.example.android.popularmoviess1;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmoviess1.data.FavoritesContract;
import com.example.android.popularmoviess1.data.FavoritesContract.FavoritesEntry;

//Custom AsyncTask to add a favorite movie to the SQLite database on a background thread.
class AddFavoriteToDbTask extends AsyncTask<Void, Void, Uri> {

    private final Context mContext;
    private final Movie mMovie;
    private final AsyncTaskCompleteListener<Uri> mListener;

    AddFavoriteToDbTask(Context context, Movie movie,
                             AsyncTaskCompleteListener<Uri> listener){
        mContext = context;
        mMovie = movie;
        mListener = listener;
    }

    @Override
    protected Uri doInBackground(Void... params) {
        int favoriteFlag = 1;
        ContentValues favoriteMovieValues = new ContentValues();
        favoriteMovieValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
        favoriteMovieValues.put(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE,
                mMovie.getReleaseDate());
        favoriteMovieValues.put(FavoritesEntry.COLUMN_MOVIE_AVG_RATING,
                mMovie.getVoteAverage());
        favoriteMovieValues.put(FavoritesEntry.COLUMN_MOVIE_SUMMARY,
                mMovie.getOverview());
        favoriteMovieValues.put(FavoritesEntry.COLUMN_MOVIE_API_ID, mMovie.getMovieDbId());
        favoriteMovieValues.put(FavoritesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH,
                mMovie.getPosterPath());
        favoriteMovieValues.put(FavoritesEntry.COLUMN_MOVIE_IS_FAVORITE, favoriteFlag);

        return mContext.getContentResolver().insert(FavoritesEntry.CONTENT_URI, favoriteMovieValues);
    }

    @Override
    protected void onPostExecute(Uri newFavoriteUri) {
        super.onPostExecute(newFavoriteUri);
        mListener.onTaskComplete(newFavoriteUri);
    }
}
