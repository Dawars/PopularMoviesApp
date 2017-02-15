package me.dawars.popularmoviesapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Video;

/**
 * Created by dawars on 1/15/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();

    private List<Video> videoData;

    private ListItemClickListener clickListener;

    public Video getVideo(int position) {
        if (position < 0 || position >= videoData.size())
            throw new ArrayIndexOutOfBoundsException();

        return videoData.get(position);
    }

    public void setOnClickListener(ListItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.video_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bind(videoData.get(position));
    }

    @Override
    public int getItemCount() {
        if (videoData == null) {
            return 0;
        }
        return videoData.size();
    }

    public void setVideoData(List<Video> videoData) {
        this.videoData = videoData;
        notifyDataSetChanged();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView title;
        public final ImageView thumbnail;

        public VideoViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.tv_title);
            thumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);

            itemView.setOnClickListener(this);
        }

        public void bind(Video video) {
            title.setText(video.getName());
            Glide.with(thumbnail.getContext())
                    .load("http://img.youtube.com/vi/" + video.getKey() + "/1.jpg")
                    .into(thumbnail);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (clickListener != null) {
                clickListener.onItemClick(v, position);
            }        }
    }
}
