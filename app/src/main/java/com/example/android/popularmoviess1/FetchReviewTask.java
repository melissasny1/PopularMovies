package com.example.android.popularmoviess1;

import android.os.AsyncTask;

import java.util.List;

/**
 * Custom AsyncTask to get the review data for the selected movie from the Movie DB API
 * on a background thread and load the resulting list of Review objects into the
 * ReviewAdapter to be displayed in the reviews RecyclerView.
 */
class FetchReviewTask extends AsyncTask<Integer, Void, List<Review>>{

    private final AsyncTaskCompleteListener<List<Review>> mListener;

    FetchReviewTask(AsyncTaskCompleteListener<List<Review>> listener){
        mListener = listener;
    }

    @Override
    protected List<Review> doInBackground(Integer... movieDbApiIds) {
        if (movieDbApiIds.length == 0) {
            return null;
        }
        int movieDbApiId = movieDbApiIds[0];
        return QueryUtils.fetchReviews(movieDbApiId);
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        super.onPostExecute(reviews);
        mListener.onTaskComplete(reviews);
    }
}