package com.example.android.popularmoviess1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.popularmoviess1.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickListener{

    //Variable for the Movie object passed into the DetailActivity via Intent.
    private Movie mMovie;
    //Variable for the movies ID# from the Movie DB API.
    private int mMovieApiId;
    //Variable for the favorite flag that indicates whether a movie is a favorite.
    private int mFavoriteFlag;

    //Toggle button to favorite/unfavorite a movie.
    private ToggleButton mFavoriteButton;
    //Toggle button to display/hide reviews from the Movie DB API.
    private ToggleButton mReviewsButton;
    //Toggle button to display/hide trailers from the Movie DB API.
    private ToggleButton mTrailersButton;

    private RecyclerView mReviewsRecyclerView;
    private TextView mReviewsErrorMessageDisplay;
    private ProgressBar mReviewsLoadingIndicator;
    private ReviewAdapter mReviewAdapter;

    private RecyclerView mTrailersRecyclerView;
    private TextView mTrailersErrorMessageDisplay;
    private ProgressBar mTrailersLoadingIndicator;
    private TrailerAdapter mTrailerAdapter;

    private static final String YOUTUBE_APP_URI = "vnd.youtube:";
    private static final String YOUTUBE_VIA_WEB_BROWSER_URI = "http://www.youtube.com/watch?v=";

    //String keys to save state across configuration changes.
    private static final String REVIEWS_KEY = "reviews";
    private static final String TRAILERS_KEY = "trailers";
    private static final String REVIEW_BUTTON_KEY = "review button";
    private static final String TRAILER_BUTTON_KEY = "trailer button";
    private static final String REVIEWS_ERROR_MSG_KEY = "reviews error msg displayed";
    private static final String TRAILERS_ERROR_MSG_KEY = "trailers error msg displayed";

    //Variables to store which error message is displayed, to save
    //state across configuration changes.
    private int mReviewsErrorMsgDisplayed;
    private int mTrailersErrorMsgDisplayed;

    //The list of review objects created from the review info returned from the Movie DB API.
    private List<Review> mReviews;
    //The list of trailer objects created from the trailer info returned from the Movie DB API.
    private List<Trailer> mTrailers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Use the DataBindingUtil class to set the Content View
        ActivityDetailBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        //Create a new ReviewAdapter.
        mReviewAdapter = new ReviewAdapter(this);
        //Create a new TrailerAdapter.
        mTrailerAdapter = new TrailerAdapter(this);

        //Get references to the toggle buttons to be displayed.
        mFavoriteButton = findViewById(R.id.toggle_favorite_unfavorite);
        mReviewsButton = findViewById(R.id.toggle_reviews);
        mTrailersButton = findViewById(R.id.toggle_trailers);

        //Get a reference to the reviews RecyclerView.
        mReviewsRecyclerView = findViewById(R.id.rv_reviews);
        //Create new LinearLayoutManager for the reviews RecyclerView.
        LinearLayoutManager rLayoutManager = new LinearLayoutManager(this);
        //Use the RecyclerView reference to assign the layout manager.
        mReviewsRecyclerView.setLayoutManager(rLayoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);
        //Connect the ReviewAdapter to the reviews RecyclerView.
        mReviewsRecyclerView.setAdapter(mReviewAdapter);
        //Display error messages when there's no internet connection or no reviews.
        mReviewsErrorMessageDisplay = findViewById
                (R.id.tv_reviews_error_message_display);
        //The ProgressBar will indicate to the user that reviews are loading and will be hidden
        //when it isn't.
        mReviewsLoadingIndicator = findViewById(R.id.pb_reviews_loading_indicator);

        //Get a reference to the trailers RecyclerView.
        mTrailersRecyclerView = findViewById(R.id.rv_trailers);
        //Create new LinearLayoutManager for the trailers RecyclerView.
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(this);
        //Use the RecyclerView reference to assign the layout manager.
        mTrailersRecyclerView.setLayoutManager(tLayoutManager);
        mTrailersRecyclerView.setHasFixedSize(true);
        //Connect the TrailerAdapter to the trailers RecyclerView.
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);
        //Display error messages when there's no internet connection or no trailers.
        mTrailersErrorMessageDisplay = findViewById(R.id.tv_trailers_error_message_display);
        //The ProgressBar will indicate to the user that trailers are loading and will be hidden
        //when it isn't.
        mTrailersLoadingIndicator = findViewById(R.id.pb_trailers_loading_indicator);

        mReviewsErrorMessageDisplay.setVisibility(View.GONE);
        mReviewsLoadingIndicator.setVisibility(View.GONE);
        mTrailersErrorMessageDisplay.setVisibility(View.GONE);
        mTrailersLoadingIndicator.setVisibility(View.GONE);

        //Add horizontal dividers between the items in the reviews and trailers RecyclerViews.
        DividerItemDecoration reviewDividerItemDecoration = new DividerItemDecoration
                (mReviewsRecyclerView.getContext(),
                rLayoutManager.getOrientation());
        mReviewsRecyclerView.addItemDecoration(reviewDividerItemDecoration);

        DividerItemDecoration trailerDividerItemDecoration = new DividerItemDecoration
                (mTrailersRecyclerView.getContext(),
                tLayoutManager.getOrientation());
        mTrailersRecyclerView.addItemDecoration(trailerDividerItemDecoration);

        if(savedInstanceState != null){

            //Retrieve the indicators of whether the review button and trailer button
            //were checked and set the button status appropriately.
            boolean reviewButtonStatus = savedInstanceState.getBoolean(REVIEW_BUTTON_KEY);
            boolean trailerButtonStatus = savedInstanceState.getBoolean(TRAILER_BUTTON_KEY);

            mReviewsButton.setChecked(reviewButtonStatus);
            mTrailersButton.setChecked(trailerButtonStatus);

            //If Reviews were saved, retrieve them and store them in the global variable mReviews.
            //If the "Reviews" toggle button is checked, reset the data in the ReviewAdapter
            //and display the reviews.
            if(savedInstanceState.containsKey(REVIEWS_KEY)){
                mReviews = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
                if(mReviewsButton.isChecked()){
                    mReviewAdapter.setReviewData(mReviews);
                    SharedHelper.showMovieInfo(mReviewsRecyclerView, mReviewsErrorMessageDisplay);
                }
            } else{
                //If there was an error retrieving reviews, display the error message.
                if(savedInstanceState.containsKey(REVIEWS_ERROR_MSG_KEY)
                        && mReviewsButton.isChecked()){
                    int reviewsErrorMessage = savedInstanceState.getInt(REVIEWS_ERROR_MSG_KEY);
                    switch(reviewsErrorMessage) {
                        case (1):
                            SharedHelper.showErrorMessage(getString(R.string.text_no_internet),
                                    mReviewsRecyclerView, mReviewsErrorMessageDisplay);
                            mReviewsErrorMsgDisplayed = 1;
                            break;
                        case (2):
                            SharedHelper.showErrorMessage(getString(R.string.text_no_reviews),
                                    mReviewsRecyclerView, mReviewsErrorMessageDisplay);
                            mReviewsErrorMsgDisplayed = 2;
                            break;
                        default:
                            mReviewsErrorMsgDisplayed = 0;
                    }
                }
            }

            //If Trailers were saved, retrieve them and store them in the global variable mTrailers.
            //If the "Trailers" toggle button is checked, reset the data in the TrailerAdapter
            //and display the reviews.
            if(savedInstanceState.containsKey(TRAILERS_KEY)){
                mTrailers = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
                if(mTrailersButton.isChecked()){
                    mTrailerAdapter.setTrailerData(mTrailers);
                    SharedHelper.showMovieInfo(mTrailersRecyclerView, mTrailersErrorMessageDisplay);
                }
            } else{
                //If there was an error retrieving trailers, display the error message.
                if(savedInstanceState.containsKey(TRAILERS_ERROR_MSG_KEY)
                        && mTrailersButton.isChecked()){
                    int trailersErrorMessage = savedInstanceState.getInt(TRAILERS_ERROR_MSG_KEY);
                    switch(trailersErrorMessage) {
                        case (1):
                            SharedHelper.showErrorMessage(getString(R.string.text_no_internet),
                                    mTrailersRecyclerView, mTrailersErrorMessageDisplay);
                            mTrailersErrorMsgDisplayed = 1;
                            break;
                        case (2):
                            SharedHelper.showErrorMessage(getString(R.string.text_no_trailers),
                                    mTrailersRecyclerView, mTrailersErrorMessageDisplay);
                            mTrailersErrorMsgDisplayed = 2;
                            break;
                        default:
                            mTrailersErrorMsgDisplayed = 0;
                    }
                }
            }
        } else{
            //If savedInstanceState is null, set the visibility for both
            //RecyclerViews to GONE, so they do not take up any space in the UI.
            mReviewsRecyclerView.setVisibility(View.GONE);
            mTrailersRecyclerView.setVisibility(View.GONE);
        }

        //Get the intent used to start DetailActivity.
        Intent intentUsedToStartThisActivity = getIntent();

        //If the intent used to start DetailActivity contains the Movie parcel, "unpack"
        //the properties contained in the parcel to be displayed in the movie detail.
        if (intentUsedToStartThisActivity.hasExtra(MainActivity.INTENT_EXTRA_KEY)) {
            mMovie = intentUsedToStartThisActivity
                    .getParcelableExtra(MainActivity.INTENT_EXTRA_KEY);
            //The relative path for the movie poster to be displayed using Picasso.
            String posterPath = mMovie.getPosterPath();

            //Create the String for the complete URL for the current movie poster.
            String moviePosterStringUri = QueryUtils.MOVIE_DB_POSTER_BASE_URL + posterPath;

            Picasso.get().load(moviePosterStringUri).into(mBinding.primaryDetail
                    .posterThumbnailImg);

            //Bind the movie title, release date, average rating and summary,
            //contained in the Movie object, to the appropriate Views.
            mBinding.primaryDetail.tvOriginalTitle.setText(mMovie.getTitle());
            mBinding.primaryDetail.tvReleaseDate.setText(mMovie.getReleaseDate().substring(0,4));
            mBinding.primaryDetail.tvVoteAverage.setText(String
                    .format(getString(R.string.vote_average_label),
                    new DecimalFormat("##.#").format(mMovie.getVoteAverage())));
            mBinding.primaryDetail.tvOverview.setText(mMovie.getOverview());

            //Get the movie's ID# for the Movie DB API; needed to access reviews and trailers.
            mMovieApiId = mMovie.getMovieDbId();

            //Get the movie's favorite flag to determine whether the movie is currently
            //saved as a user favorite.
            mFavoriteFlag = mMovie.getFavoriteFlag();

            //Use the value of the favorite flag to set the favorite button
            //to checked or unchecked.
            if (mFavoriteFlag == 1) {
                mFavoriteButton.setChecked(true);
            } else {
                mFavoriteButton.setChecked(false);
            }
        }

        //Set a click listener that will (1)add the selected movie to the
        //favorites SQLite database when the favorites button is clicked, if the
        //movie is not already a favorite; and (2)remove the selected movie from the
        //favorites SQLite database when the favorites button is unclicked, if the
        //favorite flag indicates the movie is a favorite.
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mFavoriteButton.isChecked() && mFavoriteFlag == 0) {
                    mFavoriteFlag = 1;
                    new AddFavoriteToDbTask(DetailActivity.this, mMovie,
                            new AddFavoriteToDbTaskCompleteListener()).execute();
                } else if (!mFavoriteButton.isChecked() && mFavoriteFlag == 1) {
                    new RemoveFavoriteFromDbTask(DetailActivity.this, mMovieApiId,
                            new RemoveFavoriteFromDbTaskCompleteListener()).execute();
                }
            }
        });

        //Set a click listener that, when clicked, will either initiate a
        //network request for reviews to the Movie DB API and display the resulting reviews,
        //or display reviews that have been previously retrieved during the same Activity lifecycle.
        //When unclicked, this will set the visibility for the appropriate Views to GONE,
        //so they don't take up space in the UI.
        mReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mReviewsButton.isChecked()){
                    if(mReviews == null){
                        displayReviews(mMovieApiId);
                    } else{
                        mReviewAdapter.setReviewData(mReviews);
                        SharedHelper.showMovieInfo(mReviewsRecyclerView,
                                mReviewsErrorMessageDisplay);
                    }
                } else{
                    mReviewsLoadingIndicator.setVisibility(View.GONE);
                    mReviewsErrorMessageDisplay.setVisibility(View.GONE);
                    mReviewsRecyclerView.setVisibility(View.GONE);
                }
            }
        });

        //Set a click listener that, when clicked, will either initiate a
        //network request for videos to the Movie DB API and display the resulting videos,
        //or display videos that have been previously retrieved during the same Activity lifecycle.
        //When unclicked, this will set the visibility for the appropriate Views to GONE,
        //so they don't take up space in the UI.
        mTrailersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTrailersButton.isChecked()){
                    if(mTrailers == null){
                        displayTrailers(mMovieApiId);
                    } else{
                        mTrailerAdapter.setTrailerData(mTrailers);
                        SharedHelper.showMovieInfo(mTrailersRecyclerView,
                                mTrailersErrorMessageDisplay);
                    }
                } else{
                    mTrailersLoadingIndicator.setVisibility(View.GONE);
                    mTrailersErrorMessageDisplay.setVisibility(View.GONE);
                    mTrailersRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    //Save state across configuration changes for reviews and trailers that have been
    //retrieved from the MovieDB API, for error messages displayed, and for indicators
    //that store whether the "Reviews" and "Trailers" toggle buttons are checked.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mReviews != null){
            outState.putParcelableArrayList(REVIEWS_KEY, new ArrayList<>(mReviews));
        }
        if(mTrailers != null){
            outState.putParcelableArrayList(TRAILERS_KEY, new ArrayList<>(mTrailers));
        }
        if(mReviewsErrorMsgDisplayed > 0){
            outState.putInt(REVIEWS_ERROR_MSG_KEY, mReviewsErrorMsgDisplayed);
        }
        if(mTrailersErrorMsgDisplayed > 0){
            outState.putInt(TRAILERS_ERROR_MSG_KEY, mTrailersErrorMsgDisplayed);
        }
        outState.putBoolean(REVIEW_BUTTON_KEY, mReviewsButton.isChecked());
        outState.putBoolean(TRAILER_BUTTON_KEY, mTrailersButton.isChecked());
        super.onSaveInstanceState(outState);
    }

    class AddFavoriteToDbTaskCompleteListener implements AsyncTaskCompleteListener<Uri>{

        @Override
        public void onTaskComplete(Uri newFavoriteUri) {
            //Display a success toast if the movie was inserted into the database and a Uri for the new
            //favorite was returned and an error toast if the favorite was not saved.
            if (newFavoriteUri != null) {
                Toast.makeText(DetailActivity.this, getString(R.string.toast_favorite_saved),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DetailActivity.this, getString(R.string.toast_favorite_not_saved),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    class RemoveFavoriteFromDbTaskCompleteListener implements AsyncTaskCompleteListener<Integer>{

        @Override
        public void onTaskComplete(Integer rowsDeleted) {
            if (rowsDeleted > 0) {
                Toast.makeText(DetailActivity.this, getString(R.string.toast_favorite_deleted),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DetailActivity.this, getString(R.string.toast_favorite_not_deleted),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    /**
     * Helper method that checks the internet connection, fetches and displays reviews
     * for the selected movie if there is a connection and displays an error message if
     * there is no connection.
     *
     * @param movieDbApiId The Movie DB ID# for the selected movie.
     */
    private void displayReviews(int movieDbApiId) {
        if (SharedHelper.hasInternetConnection(this)) {
            //Display the loading indicator while the data request to the Movie DB API is executed.
            mReviewsLoadingIndicator.setVisibility(View.VISIBLE);
            new FetchReviewTask(new FetchReviewTaskCompleteListener() {})
                    .execute(movieDbApiId);
        } else {
            SharedHelper.showErrorMessage(getString(R.string.text_no_internet), mReviewsRecyclerView,
                    mReviewsErrorMessageDisplay);
            mReviewsErrorMsgDisplayed = 1;
        }
    }

    /**
     * Helper method that checks the internet connection, fetches and displays trailers
     * for the selected movie if there is a connection and displays an error message if
     * there is no connection.
     *
     * @param movieDbApiId The Movie DB ID# for the selected movie.
     */
    private void displayTrailers(int movieDbApiId) {
        if (SharedHelper.hasInternetConnection(this)) {
            //Display the loading indicator while the data request to the Movie DB API is executed.
            mTrailersLoadingIndicator.setVisibility(View.VISIBLE);
            new FetchTrailerTask(new FetchTrailerTaskCompleteListener())
                    .execute(movieDbApiId);
        } else {
            SharedHelper.showErrorMessage(getString(R.string.text_no_internet), mTrailersRecyclerView,
                    mTrailersErrorMessageDisplay);
            mTrailersErrorMsgDisplayed = 1;
        }
    }

    class FetchReviewTaskCompleteListener implements AsyncTaskCompleteListener<List<Review>>{

        @Override
        public void onTaskComplete(List<Review> reviews) {
            //Hide the loading indicator when the data request to the Movie DB API is complete.
            mReviewsLoadingIndicator.setVisibility(View.INVISIBLE);

            //If the data request has returned a list of movies, make the RecyclerView visible
            //and load the list of Movie objects into the adapter to be displayed in the
            //RecyclerView. If no list of movies is returned, display an explanatory message.
            if (reviews.size() > 0) {
                mReviews = reviews;
                mReviewAdapter.setReviewData(mReviews);
                SharedHelper.showMovieInfo(mReviewsRecyclerView, mReviewsErrorMessageDisplay);
            } else {
                SharedHelper.showErrorMessage(getString(R.string.text_no_reviews),
                        mReviewsRecyclerView, mReviewsErrorMessageDisplay);
                mReviewsErrorMsgDisplayed = 2;
            }
        }
    }

    class FetchTrailerTaskCompleteListener implements AsyncTaskCompleteListener<List<Trailer>>{

        @Override
        public void onTaskComplete(List<Trailer> trailers) {
            //Hide the loading indicator when the data request to the Movie DB API is complete.
            mTrailersLoadingIndicator.setVisibility(View.INVISIBLE);

            //If the data request has returned a list of movies, make the RecyclerView visible
            //and load the list of Movie objects into the adapter to be displayed in the
            //RecyclerView. If no list of movies is returned, display an explanatory message.
            if (trailers.size() > 0) {
                mTrailers = trailers;
                mTrailerAdapter.setTrailerData(mTrailers);
                SharedHelper.showMovieInfo(mTrailersRecyclerView, mTrailersErrorMessageDisplay);
            } else {
                SharedHelper.showErrorMessage(getString(R.string.text_no_trailers),
                        mTrailersRecyclerView, mTrailersErrorMessageDisplay);
                mTrailersErrorMsgDisplayed = 2;
            }
        }
    }

    /**
     * Get the YouTube video key from the clicked Trailer object and play the Youtube video.
     *
     * @param trailer   The Trailer object for the trailer clicked by the user.
     */
    @Override
    public void onTrailerClick(Trailer trailer) {
        playYouTubeVideo(trailer.getTrailerKey());
    }


    /**
     * Play the movie trailer the user has clicked.  First try the YouTube app, if installed,
     * then try the webpage if the YouTube app is not installed.
     *
     * @param videoId       The YouTube video key for the trailer, from the Movie DB API.
     */
    private void playYouTubeVideo(String videoId){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_URI + videoId));
        appIntent.putExtra("VIDEO_ID", videoId);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_VIA_WEB_BROWSER_URI + videoId));

        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            if(webIntent.resolveActivity(getPackageManager()) != null){
                this.startActivity(webIntent);
            } else{
               (Toast.makeText(this, getString(R.string.toast_video_can_not_be_viewed),
                       Toast.LENGTH_LONG)).show();
            }
        }
    }
}
