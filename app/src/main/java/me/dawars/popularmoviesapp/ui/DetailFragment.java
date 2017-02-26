package me.dawars.popularmoviesapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.adapter.ListItemClickListener;
import me.dawars.popularmoviesapp.ui.detail.ReviewAdapter;
import me.dawars.popularmoviesapp.ui.detail.VideoAdapter;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.data.MovieDetail;
import me.dawars.popularmoviesapp.data.Review;
import me.dawars.popularmoviesapp.data.Video;
import me.dawars.popularmoviesapp.ui.detail.MovieDetailLoader;
import me.dawars.popularmoviesapp.ui.grid.MainActivity;
import me.dawars.popularmoviesapp.utils.DisplayUtils;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    private static final String TAG = DetailFragment.class.getSimpleName();

    private static final int LOADER_MOVIE_DETAIL_ID = 2;
    private static final String MOVIE_VIDEOS_KEY = "VIDEOS_KEY";
    private static final String MOVIE_REVIEWS_KEY = "REVIEWS_KEY";

    private Movie movie;
    private MovieDetail movieDetail;

    private MovieDetailLoader reviewLoader;
    private ReviewAdapter reviewAdapter;
    private VideoAdapter videoAdapter;
    private LinearLayoutManager reviewsLayoutManager;
    private LinearLayoutManager videosLayoutManager;


    @BindView(R.id.tv_title)
    TextView titleTextView;
    @BindView(R.id.im_poster)
    ImageView posterImageView;
    @BindView(R.id.tv_overview)
    TextView overviewTextView;
    @BindView(R.id.rb_rating)
    RatingBar ratingTextView;
    @BindView(R.id.tv_release_date)
    TextView releaseDateTextView;

    @BindView(R.id.reviews_list)
    RecyclerView reviewsList;

    @BindView(R.id.videos_list)
    RecyclerView videosList;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(Movie movie) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString(MovieDetailLoader.MOVIE_ID_KEY, String.valueOf(movie.getId()));
        loaderManager.initLoader(LOADER_MOVIE_DETAIL_ID, bundle, reviewLoader);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(MainActivity.ARG_MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);


        reviewAdapter = new ReviewAdapter();
        videoAdapter = new VideoAdapter();

        reviewLoader = new MovieDetailLoader(getContext(), reviewAdapter, videoAdapter);
        reviewsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        videosLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        reviewsList.setAdapter(reviewAdapter);
        reviewsList.setLayoutManager(reviewsLayoutManager);

        videosList.setAdapter(videoAdapter);
        videosList.setLayoutManager(videosLayoutManager);
        videoAdapter.setOnClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Video video = videoAdapter.getVideo(position);
                if (video.getSite().equals("YouTube")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + video.getKey()));
                    startActivity(intent);
                }
                Log.i(TAG, "Not a youtube video");
            }
        });

        bindData(movie);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (videoAdapter.getItemCount() != 0) {
            outState.putParcelableArrayList(MOVIE_VIDEOS_KEY, videoAdapter.getVideos());
        }
        if (reviewAdapter.getItemCount() != 0) {
            outState.putParcelableArrayList(MOVIE_REVIEWS_KEY, reviewAdapter.getReviews());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            videoAdapter.setVideoData(savedInstanceState.<Video>getParcelableArrayList(MOVIE_VIDEOS_KEY));
            reviewAdapter.setReviewData(savedInstanceState.<Review>getParcelableArrayList(MOVIE_REVIEWS_KEY));
        }
    }

    private void bindData(Movie movie) {
        int width = DisplayUtils.getScreenMetrics(getActivity()).widthPixels / 2; //FIXME 100dp or so
        Uri posterUri = NetworkUtils.getImageUri(movie.getPosterPath(), width);
        Glide.with(this).load(posterUri).diskCacheStrategy(DiskCacheStrategy.ALL).into(posterImageView);

        posterImageView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        posterImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                        getActivity().supportStartPostponedEnterTransition();
                        return true;
                    }
                }
        );

        titleTextView.setText(movie.getTitle());
        overviewTextView.setText(movie.getOverview());
        ratingTextView.setRating(movie.getVoteAvg());
        releaseDateTextView.setText(movie.getReleaseDate());
    }
}
