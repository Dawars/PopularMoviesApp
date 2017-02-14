package me.dawars.popularmoviesapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Review;

/**
 * Created by dawars on 1/15/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();

    private List<Review> reviewData;

    final private ListItemClickListener clickListener;

    public Review getReview(int position) {
        if (position < 0 || position >= reviewData.size())
            throw new ArrayIndexOutOfBoundsException();

        return reviewData.get(position);
    }

    public interface ListItemClickListener {
        void onItemClick(View v, int position);
    }

    public ReviewAdapter(ListItemClickListener clickListener) {

        this.clickListener = clickListener;

    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.review_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(reviewData.get(position));
    }

    @Override
    public int getItemCount() {
        if (reviewData == null) {
            return 0;
        }
        return reviewData.size();
    }

    public void setReviewData(List<Review> reviewData) {
        this.reviewData = reviewData;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView authorTextView;
        public final TextView contentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            authorTextView = (TextView) itemView.findViewById(R.id.tv_author);
            contentTextView = (TextView) itemView.findViewById(R.id.tv_content);

            itemView.setOnClickListener(this);
        }

        public void bind(Review review) {
            authorTextView.setText(review.getAuthor());
            contentTextView.setText(review.getContent());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onItemClick(v, position);
        }
    }
}
