package me.dawars.popularmoviesapp.ui.detail;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.utils.GenreHelper;

/**
 * Created by dawars on 1/15/17.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private static final String TAG = GenreAdapter.class.getSimpleName();

    private List<Integer> genreData;

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.genre_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        holder.bind(genreData.get(position));
    }

    @Override
    public int getItemCount() {
        if (genreData == null) {
            return 0;
        }
        return genreData.size();
    }

    public void setGenreData(List<Integer> genreData) {
        this.genreData = genreData;
        notifyDataSetChanged();
    }

    class GenreViewHolder extends RecyclerView.ViewHolder {
        public final TextView tagTextView;

        public GenreViewHolder(View itemView) {
            super(itemView);

            tagTextView = (TextView) itemView.findViewById(R.id.tv_tag);
        }

        public void bind(int genreId) {
            Resources resources = tagTextView.getContext().getResources();
            tagTextView.setText(resources.getString(GenreHelper.getGenreStringId(genreId)));
        }
    }
}
