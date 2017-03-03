package me.dawars.popularmoviesapp.ui.detail;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Cast;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

/**
 * Created by dawars on 1/15/17.
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private static final String TAG = CastAdapter.class.getSimpleName();

    private ArrayList<Cast> CastData;

    public Cast getCast(int position) {
        if (position < 0 || position >= CastData.size())
            throw new ArrayIndexOutOfBoundsException();

        return CastData.get(position);
    }

    public ArrayList<Cast> getCasts() {
        return CastData;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.cast_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {
        holder.bind(CastData.get(position));
    }

    @Override
    public int getItemCount() {
        if (CastData == null) {
            return 0;
        }
        return Math.min(5, CastData.size());
    }

    public void setCastData(ArrayList<Cast> CastData) {
        this.CastData = CastData;
        notifyDataSetChanged();
    }

    class CastViewHolder extends RecyclerView.ViewHolder {
        public final TextView characterTextView;
        public final TextView nameTextView;
        public final ImageView profileImage; // TODO: round image view

        public CastViewHolder(View itemView) {
            super(itemView);

            characterTextView = (TextView) itemView.findViewById(R.id.tv_character);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            profileImage = (ImageView) itemView.findViewById(R.id.iv_profile);
        }

        public void bind(Cast cast) {
            characterTextView.setText(cast.getCharacter());
            nameTextView.setText(cast.getName());

            Glide.with(profileImage.getContext())
                    .load(NetworkUtils.getImageUri(cast.getProfilePath(), dpToPx(40)))
                    .crossFade()
                    .into(profileImage);
        }

        public int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }

    }
}
