package me.dawars.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.dawars.popularmoviesapp.utils.NetworkUtils;

/**
 * Created by dawars on 1/15/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> implements View.OnClickListener {


    private MovieRecord[] movieData;

    public MovieAdapter() {

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
        holder.bind(movieData[position]);
    }

    @Override
    public int getItemCount() {
        if (movieData == null) {
            return 0;
        }
        return movieData.length;
    }

    public void setMovieData(MovieRecord[] movieData) {
        this.movieData = movieData;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        //TODO add own onClick listener
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        public final TextView titleTextView;
        public final ImageView posterImageView;

        public MovieViewHolder(View view) {
            super(view);

            titleTextView = (TextView) view.findViewById(R.id.tv_title);
            posterImageView = (ImageView) view.findViewById(R.id.im_poster);
        }

        public void bind(MovieRecord record) {
            Uri posterUri = NetworkUtils.getPosterUri(record.getPosterUrl());
            Picasso.with(posterImageView.getContext()).load(posterUri).into(posterImageView);
            titleTextView.setText(record.getTitle());
        }
    }
}
