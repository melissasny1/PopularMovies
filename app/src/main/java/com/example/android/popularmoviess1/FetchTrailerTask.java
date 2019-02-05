package com.example.android.popularmoviess1;

import android.os.AsyncTask;

import java.util.List;

/**
 * Custom AsyncTask to get the trailer data for the selected movie from the Movie DB API
 * on a background thread and load the resulting list of Trailer objects into the
 * TrailerAdapter to be displayed in the trailers RecyclerView.
 */
class FetchTrailerTask extends AsyncTask<Integer, Void, List<Trailer>>{

    private final AsyncTaskCompleteListener<List<Trailer>> mListener;

    FetchTrailerTask(AsyncTaskCompleteListener<List<Trailer>> listener){
        mListener = listener;
    }

    @Override
    protected List<Trailer> doInBackground(Integer... movieDbApiIds) {
        if (movieDbApiIds.length == 0) {
            return null;
        }
        int movieDbApiId = movieDbApiIds[0];
        return QueryUtils.fetchTrailers(movieDbApiId);
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        super.onPostExecute(trailers);
        mListener.onTaskComplete(trailers);
    }
}
