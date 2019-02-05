package com.example.android.popularmoviess1;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmoviess1.data.FavoritesContract.FavoritesEntry;

//Custom AsyncTask to remove a favorite movie from the SQLite database on
//a background thread.
class RemoveFavoriteFromDbTask extends AsyncTask<Void, Void, Integer> {

    private final Context mContext;
    private final int mMovieApiId;
    private final AsyncTaskCompleteListener<Integer> mListener;

    RemoveFavoriteFromDbTask(Context context, int movieApiId, AsyncTaskCompleteListener<Integer> listener){
        mContext = context;
        mListener = listener;
        mMovieApiId = movieApiId;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Uri selectedMovieContentUri = ContentUris.withAppendedId(FavoritesEntry.CONTENT_URI,
                mMovieApiId);
        return mContext.getContentResolver()
                .delete(selectedMovieContentUri, null, null);
    }

    @Override
    protected void onPostExecute(Integer rowsDeleted) {
        super.onPostExecute(rowsDeleted);
        mListener.onTaskComplete(rowsDeleted);
    }
}
