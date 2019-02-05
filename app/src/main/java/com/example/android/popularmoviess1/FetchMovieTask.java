package com.example.android.popularmoviess1;

import android.os.AsyncTask;

import java.util.List;

/**
 * Create a custom AsyncTask to get the movie data from the Movie DB API on a background thread
 * and load the resulting list of Movie objects into the MovieAdapter to be displayed in the
 * RecyclerView.
 */
class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

    private final AsyncTaskCompleteListener<List<Movie>> mListener;

    FetchMovieTask(AsyncTaskCompleteListener<List<Movie>> listener){
        mListener = listener;
    }

    @Override
    protected List<Movie> doInBackground(String... dbMoviePathStringUrls) {
        if (dbMoviePathStringUrls.length == 0) {
            return null;
        }
        String dbMoviePathStringUrl = dbMoviePathStringUrls[0];
        return QueryUtils.fetchMovies(dbMoviePathStringUrl);
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        mListener.onTaskComplete(movies);
    }
}