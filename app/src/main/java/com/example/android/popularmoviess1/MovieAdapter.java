package com.example.android.popularmoviess1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    //Store a reference to the MovieClickListener passed into the Adapter.
    private final MovieClickListener mMovieClickListener;
    private final Context mContext;
    private List<Movie> mMovies;

    MovieAdapter(Context context, MovieClickListener listener) {
        mContext = context;
        mMovieClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder movieViewHolder, int position) {
        //Retrieve the Movie object for the current position.
        Movie currentMovie = mMovies.get(position);

        //Get the relative path for the movie poster for the current movie.
        String currentPosterPath = currentMovie.getPosterPath();

        //Create the String for the complete URL for the current movie poster.
        String moviePosterStringUri = QueryUtils.MOVIE_DB_POSTER_BASE_URL + currentPosterPath;

        //Load the current movie poster into the movie poster ImageView.
        Picasso.get().load(moviePosterStringUri)
                .into(movieViewHolder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        int numberOfItems = 0;
        if (mMovies != null) {
            numberOfItems = mMovies.size();
        }
        return numberOfItems;
    }

    /**
     * Set the movie data on a MovieAdapter if one has already been created.
     *
     * @param movies The new movie data to be displayed.
     */
    void setMovieData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    //Create an interface to define the listener.
    interface MovieClickListener {
        void onMovieClick(Movie movie);
    }

    /**
     * Cache of the child views for a list item.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Create a member variable for each view in the item view.
        final ImageView mMoviePosterImageView;

        /**
         * Constructor for the ViewHolder.  Get a reference to the ImageView and set an
         * onClickListener to listen for clicks. Those will be handled in the onClick
         * method below.
         *
         * @param itemView The View that was inflated in MovieAdapter#onCreateViewHolder.
         */
        MovieViewHolder(View itemView) {
            super(itemView);

            //Set the click listener on the item view.
            itemView.setOnClickListener(this);

            //Use findViewById to get a reference to each member variable.
            mMoviePosterImageView = (ImageView) itemView.findViewById(R.id.movie_poster_img);
        }

        @Override
        public void onClick(View view) {
            //Get the position of the movie poster clicked.
            int clickedPosition = getAdapterPosition();
            Movie clickedMovie = mMovies.get(clickedPosition);
            //Invoke the onClickListener of the Adapter class.
            mMovieClickListener.onMovieClick(clickedMovie);
        }
    }
}
