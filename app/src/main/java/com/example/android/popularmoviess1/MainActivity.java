package com.example.android.popularmoviess1;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviess1.data.FavoritesContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieClickListener {

    static final String LOG_TAG = MainActivity.class.getSimpleName();
    static final String INTENT_EXTRA_KEY = "Movie";
    //Number of columns to display in the movie poster grid.
    private static final int GRID_COLUMNS_PORTRAIT = 3;
    private static final int GRID_COLUMNS_LAND = 4;
    private static final int FAVORITES_LOADER = 1000;
    private static final String MOVIE_LIST_KEY = "movies";
    private static final String TITLE_KEY = "title";
    private static final String ERROR_MSG_KEY = "error msg displayed";
    private static final String RECYCLERVIEW_STATE_KEY = "rv state";

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    //The user's most recent menu item selection.
    private MenuItem mPreviousMenuSelection;
    //The list of movie objects created from the movie info returned from the Movie DB API.
    private List<Movie> mMovies;

    private int mErrorMsgDisplayed;

    private Parcelable mRecyclerviewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a new MovieAdapter.
        mAdapter = new MovieAdapter(this, this);
        //Get a reference to the RecyclerView.
        mRecyclerView = findViewById(R.id.rv_movies);
        //Create new GridLayoutManager and use the RecyclerView
        //reference to assign the layout manager.
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(this, GRID_COLUMNS_PORTRAIT);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        else{
            mLayoutManager = new GridLayoutManager(this, GRID_COLUMNS_LAND);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        mRecyclerView.setHasFixedSize(true);
        //Connect the MovieAdapter to the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        //Display error messages when there's no internet connection or no movies.
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        //The ProgressBar will indicate to the user that data is loading and will be hidden
        //when it isn't.
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState != null) {
            setTitle(String.valueOf(savedInstanceState.get(TITLE_KEY)));

            if (savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
                mAdapter.setMovieData(mMovies);
            } else {
                if (savedInstanceState.containsKey(ERROR_MSG_KEY)) {
                    int errorMessage = savedInstanceState.getInt(ERROR_MSG_KEY);
                    switch (errorMessage) {
                        case (1):
                            SharedHelper.showErrorMessage(getString(R.string.text_no_internet),
                                    mRecyclerView, mErrorMessageDisplay);
                            mErrorMsgDisplayed = 1;
                            break;
                        case (2):
                            SharedHelper.showErrorMessage(getString(R.string.text_no_movies),
                                    mRecyclerView, mErrorMessageDisplay);
                            mErrorMsgDisplayed = 2;
                            break;
                        case (3):
                            SharedHelper.showErrorMessage(getString(R.string.text_no_favorites),
                                    mRecyclerView, mErrorMessageDisplay);
                            mErrorMsgDisplayed = 3;
                            break;
                        default:
                            mErrorMsgDisplayed = 0;
                    }
                }
            }
        }
            else {
            //Confirm that there is an internet connection and display movie posters from
            //the Movie DB API in the default sort order, by most popular movies.
            //Display an error message if there is no connection.
            setTitle(R.string.title_most_popular);
            displayMovies(QueryUtils.MOVIE_DB_PATH_POPULAR);
        }
    }

    /**
     * This applies when the user returns from the Detail Activity after deleting a
     * favorite movie.  This will query the favorite movies SQLite database to update mMovies,
     * to ensure that mMovies is updated in the event of a state change.
     */
    @Override
    protected void onResume(){
        super.onResume();
        if(String.valueOf(getTitle()).equals(getString(R.string.title_favorites))){
            getSupportLoaderManager().initLoader(FAVORITES_LOADER, null, this);
        }
        if(mRecyclerviewState != null){
            mLayoutManager.onRestoreInstanceState(mRecyclerviewState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Retrieve RecyclerView position.
        mRecyclerviewState = savedInstanceState.getParcelable(RECYCLERVIEW_STATE_KEY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //If there are movies displayed, save them.
        if(mMovies != null && mMovies.size() > 0){
            outState.putParcelableArrayList(MOVIE_LIST_KEY, new ArrayList<>(mMovies));
        }
        //If there is an applicable error message, save it.
        if(mErrorMsgDisplayed > 0){
            outState.putInt(ERROR_MSG_KEY, mErrorMsgDisplayed);
        }
        outState.putString(TITLE_KEY, String.valueOf(getTitle()));
        mRecyclerviewState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECYCLERVIEW_STATE_KEY, mRecyclerviewState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Clear the list of movies and the Adapter every time the user selects a menu option.
        mMovies = null;
        mAdapter.setMovieData(null);

        int idSelected = item.getItemId();

        if (mPreviousMenuSelection != null) {
            mPreviousMenuSelection.setChecked(false);
        }

        //Mark the user's current menu item selection as checked.
        item.setChecked(true);
        //Store the user's current menu item selection.
        mPreviousMenuSelection = item;

        switch (idSelected) {
            case R.id.action_display_most_popular:
                setTitle(R.string.title_most_popular);
                //Display the most popular movies from the Movie DB API.
                displayMovies(QueryUtils.MOVIE_DB_PATH_POPULAR);
                return true;
            case R.id.action_display_highest_rated:
                setTitle(R.string.title_top_rated);
                //Display the top-rated movies from the Movie DB API.
                displayMovies(QueryUtils.MOVIE_DB_PATH_TOP_RATED);
                return true;
            case R.id.action_display_favorites:
                setTitle(R.string.title_favorites);
                //Display the favorite movies saved to the local SQLiteDatabase.
                LoaderManager lm = getSupportLoaderManager();
                Loader<Cursor> faveLoader = lm.getLoader(FAVORITES_LOADER);
                if(faveLoader == null) {
                    lm.initLoader(FAVORITES_LOADER, null, this);
                }
                else{
                   lm.restartLoader(FAVORITES_LOADER, null, this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method that checks the internet connection, fetches and displays movies
     * if there is a connection and displays an error message if there is no connection.
     *
     * @param moviePath The Movie DB API path for either most-popular or top-rated movies.
     */
    private void displayMovies(String moviePath) {
        if (SharedHelper.hasInternetConnection(this)) {
            //Display the loading indicator while the data request to the Movie DB API is executed.
            mLoadingIndicator.setVisibility(View.VISIBLE);
            new FetchMovieTask(new FetchMovieTaskCompleteListener()).execute(moviePath);
        } else {
            SharedHelper.showErrorMessage(getString(R.string.text_no_internet), mRecyclerView, mErrorMessageDisplay);
            mErrorMsgDisplayed = 1;
        }
    }

    /**
     * Create an intent to start the DetailActivity, pass the parcel containing the
     * clicked movie data to DetailActivity with the intent, and start the activity.
     *
     * @param clickedMovie The Movie object for the movie clicked by the user.
     */
    @Override
    public void onMovieClick(Movie clickedMovie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_EXTRA_KEY, clickedMovie);
        startActivity(intent);
    }


    class FetchMovieTaskCompleteListener implements AsyncTaskCompleteListener<List<Movie>>
    {
        @Override
        public void onTaskComplete(List<Movie> movies)
        {
            //Hide the loading indicator when the data request to the Movie DB API is complete.
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            //If the data request has returned a list of movies, make the RecyclerView visible
            //and load the list of Movie objects into the adapter to be displayed in the
            //RecyclerView. If no list of movies is returned, display an explanatory message.
            if (movies != null) {
                mMovies = movies;
                SharedHelper.showMovieInfo(mRecyclerView, mErrorMessageDisplay);
                mAdapter.setMovieData(mMovies);
            } else {
                SharedHelper.showErrorMessage(getString(R.string.text_no_movies), mRecyclerView, mErrorMessageDisplay);
                mErrorMsgDisplayed = 2;
            }
        }
    }

    //CursorLoader to query and then display all of the favorite movies stored in the SQLite
    //database.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Define a projection that specifies which columns from the database
        //will be used.
        String[] projection = {
                FavoritesContract.FavoritesEntry._ID,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_AVG_RATING,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_SUMMARY,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_API_ID,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_IS_FAVORITE
        };

        return new CursorLoader(
                this,
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor favoriteMoviesData) {

        if(favoriteMoviesData.getCount() < 1){
            String noFavoritesText = getString(R.string.text_no_favorites);
            SharedHelper.showErrorMessage(noFavoritesText, mRecyclerView, mErrorMessageDisplay);
            mErrorMsgDisplayed = 3;
        } else{
            //If the SQLite database query has returned a list of favorite movies, make the
            //RecyclerView visible and load the list of Movie objects into the adapter to be displayed
            //in the RecyclerView. If no list of favorites is returned, display an explanatory message.
            List<Movie> movies = createListOfFavoriteMovies(favoriteMoviesData);
            if(movies != null){
                mMovies = movies;
                SharedHelper.showMovieInfo(mRecyclerView, mErrorMessageDisplay);
                mAdapter.setMovieData(mMovies);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setMovieData(null);
    }

    /**
     * Helper method that takes the cursor of favorite movie data from the SQLite database
     * and creates a list of Movie objects for the MovieAdapter, to be displayed in the UI.
     *
     * @param favoriteMoviesData The Cursor of favorite movie data stored in the SQLite database.
     * @return The list of favorite Movie objects to be displayed in the UI via the adapter.
     */
    private List<Movie> createListOfFavoriteMovies(Cursor favoriteMoviesData) {

        List<Movie> movies = new ArrayList<>();
        int movieId;
        String posterPath;
        String movieTitle;
        String overview;
        String releaseDate;
        double voteAverage;
        int favoriteFlag;

        //If the user has saved movies to favorites and there is data in the cursor,
        //retrieve all of the saved data fields and create a new movie object for each favorite saved.
        if(favoriteMoviesData.moveToFirst()){
            do{
                movieId = favoriteMoviesData.getInt(favoriteMoviesData
                        .getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_API_ID));
                posterPath = favoriteMoviesData.getString(favoriteMoviesData
                        .getColumnIndex(FavoritesContract.FavoritesEntry
                                .COLUMN_MOVIE_POSTER_IMAGE_PATH));
                movieTitle = favoriteMoviesData.getString(favoriteMoviesData
                        .getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE));
                overview = favoriteMoviesData.getString(favoriteMoviesData
                        .getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_SUMMARY));
                releaseDate = favoriteMoviesData.getString(favoriteMoviesData
                        .getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE));
                voteAverage = favoriteMoviesData.getDouble(favoriteMoviesData
                        .getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_AVG_RATING));
                favoriteFlag = favoriteMoviesData.getInt(favoriteMoviesData
                        .getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_IS_FAVORITE));

                movies.add(new Movie(movieId, posterPath, movieTitle, overview, releaseDate,
                        voteAverage, favoriteFlag));
            } while(favoriteMoviesData.moveToNext());
        }
        return movies;
    }
}
