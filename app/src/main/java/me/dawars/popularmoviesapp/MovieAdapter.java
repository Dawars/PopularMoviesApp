package me.dawars.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

/**
 * Created by dawars on 1/15/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> movieData;

    final private ListItemClickListener clickListener;

    public Movie getMovie(int position) {
        if (position < 0 || position >= movieData.size())
            throw new ArrayIndexOutOfBoundsException();

        return movieData.get(position);
    }

    public interface ListItemClickListener {
        void onItemClick(int position);
    }

    public MovieAdapter(ListItemClickListener clickListener) {

        this.clickListener = clickListener;

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.movie_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(movieData.get(position));
    }

    @Override
    public int getItemCount() {
        if (movieData == null) {
            return 0;
        }
        return movieData.size();
    }

    public void setMovieData(List<Movie> movieData) {
        this.movieData = movieData;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView titleTextView;
        public final ImageView posterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            posterImageView = (ImageView) itemView.findViewById(R.id.im_poster);

            itemView.setOnClickListener(this);
        }

        public void bind(Movie record) {
            Uri posterUri = NetworkUtils.getImageUri(record.getPosterPath(), 185/* hard coded */);
            Glide.with(posterImageView.getContext()).load(posterUri).into(posterImageView);
            // TODO: add error and loading image
            titleTextView.setText(record.getTitle());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onItemClick(position);
        }
    }
}
