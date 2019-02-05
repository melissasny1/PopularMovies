package com.example.android.popularmoviess1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter to create list item views for the ReviewRecyclerView that
 * contain the review author and content.
 */

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private List<Review> mReviews;
    private final Context mContext;

    ReviewAdapter(Context context){
        mContext = context;
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder reviewViewHolder, int position) {
        Review currentReview = mReviews.get(position);
        reviewViewHolder.reviewAuthor.setText(String.format(mContext.getResources()
                .getString(R.string.reviews_author_text), currentReview.getReviewAuthor()));
        reviewViewHolder.reviewContent.setText(currentReview.getReviewContent());
    }

    @Override
    public int getItemCount() {
        int numberOfItems = 0;
        if (mReviews != null) {
            numberOfItems = mReviews.size();
        }
        return numberOfItems;
    }

    /**
     * Set the review data on a ReviewAdapter if one has already been created.
     *
     * @param reviews The new review data to be displayed.
     */
    void setReviewData(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        final TextView reviewAuthor;
        final TextView reviewContent;

        ReviewViewHolder(View itemView){
            super(itemView);

            //Use findViewById to get a reference to each member variable.
            reviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            reviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);
        }
    }
}
