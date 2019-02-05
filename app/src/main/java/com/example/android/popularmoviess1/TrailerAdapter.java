package com.example.android.popularmoviess1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter to create list item views for the TrailerRecyclerView that
 * contain the trailer title.  Also creates an interface that defines a listener,
 * listening for user clicks on the trailer title, which will then play the video.
 */

class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{

    //Store a reference to the TrailerClickListener passed into the Adapter.
    private final TrailerClickListener mTrailerClickListener;
    private List<Trailer> mTrailers;

    TrailerAdapter(TrailerClickListener listener){
        mTrailerClickListener = listener;
    }

    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder trailerViewHolder, int position) {
        Trailer currentTrailer = mTrailers.get(position);
        trailerViewHolder.trailerName.setText(currentTrailer.getTrailerName());
    }

    @Override
    public int getItemCount() {
        int numberOfItems = 0;
        if (mTrailers != null) {
            numberOfItems = mTrailers.size();
        }
        return numberOfItems;
    }

    /**
     * Set the trailer data on a TrailerAdapter if one has already been created.
     *
     * @param trailers The new trailer data to be displayed.
     */
    void setTrailerData(List<Trailer> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    //Create an interface to define the listener.
    interface TrailerClickListener {
        void onTrailerClick(Trailer trailer);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView trailerName;
        TrailerViewHolder(View itemView){
            super(itemView);

            //Set the click listener on the item view.
            itemView.setOnClickListener(this);

            //Use findViewById to get a reference to each member variable.
            trailerName = (TextView) itemView.findViewById(R.id.tv_trailer_name);
        }

        @Override
        public void onClick(View view) {
            //Get the position of the trailer clicked.
            int clickedPosition = getAdapterPosition();
            Trailer clickedTrailer = mTrailers.get(clickedPosition);
            //Invoke the onClickListener of the Adapter class.
            mTrailerClickListener.onTrailerClick(clickedTrailer);
        }
    }
}
