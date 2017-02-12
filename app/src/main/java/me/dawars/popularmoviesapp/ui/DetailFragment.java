package me.dawars.popularmoviesapp.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.adapter.ReviewAdapter;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.data.loader.ReviewLoader;
import me.dawars.popularmoviesapp.utils.DisplayUtils;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements ReviewAdapter.ListItemClickListener {
    private static final int LOADER_MOVIE_DETAIL_ID = 2;
    private Movie movie;

    private ReviewLoader reviewLoader;
    private ReviewAdapter reviewAdapter;
    private LinearLayoutManager layoutManager;


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
        bundle.putString(ReviewLoader.MOVIE_ID_KEY, String.valueOf(movie.getId()));
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


        reviewAdapter = new ReviewAdapter(this);

        reviewLoader = new ReviewLoader(getContext(), reviewAdapter);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        reviewsList.setAdapter(reviewAdapter);
        reviewsList.setLayoutManager(layoutManager);

        bindData(movie);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // TODO implement onsaveinstance
    }

    private void bindData(Movie movie) {
        int width = DisplayUtils.getScreenMetrics(getActivity()).widthPixels / 2;
        Uri posterUri = NetworkUtils.getImageUri(movie.getPosterPath(), width);
        Glide.with(this).load(posterUri).into(posterImageView);

        titleTextView.setText(movie.getOrigTitle());
        overviewTextView.setText(movie.getOverview());
        ratingTextView.setRating(movie.getVoteAvg());
        releaseDateTextView.setText(movie.getReleaseDate());
    }

    @Override
    public void onItemClick(View v, int position) {

    }
}
