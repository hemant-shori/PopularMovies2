package com.hemant.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemant.popularmovies.models.MovieDetails;
import com.hemant.popularmovies.R;
import com.hemant.popularmovies.utils.DbBitmapUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AllMoviesAdapter extends RecyclerView.Adapter<AllMoviesAdapter.GridMovieViewHolder> {
    private ArrayList<MovieDetails> mMoviesDetails;
    private final OnMovieClickListener onMovieClickListener;

    public interface OnMovieClickListener {
        void onItemClicked(MovieDetails movieDetails);
    }

    public AllMoviesAdapter(OnMovieClickListener onMovieClickListener) {
        mMoviesDetails = null;
        this.onMovieClickListener = onMovieClickListener;
    }

    @Override
    public GridMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout_movies_vh, parent, false);
        return new GridMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridMovieViewHolder holder, int position) {
        holder.mMovieNameTextView.setText(mMoviesDetails.get(position).getTitle());
        holder.mRatingsTextView.setText(String.format("%s%s", mMoviesDetails.get(position).getRatings(), holder.mRatingsTextView.getContext().getString(R.string.hyphen10)));
        if (mMoviesDetails.get(position).getMoviePoster() != null && mMoviesDetails.get(position).getMoviePoster().length > 0) {
            holder.mPosterImageView.setImageBitmap(DbBitmapUtility.getImage(mMoviesDetails.get(position).getMoviePoster()));
        } else {
            Picasso.with(holder.mMovieNameTextView.getContext()).load(mMoviesDetails.get(position).getImagePath()).into(holder.mPosterImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviesDetails == null)
            return 0;
        else
            return mMoviesDetails.size();
    }

    public void setMoviesData(ArrayList<MovieDetails> mMoviesData) {
        this.mMoviesDetails = mMoviesData;
        notifyDataSetChanged();
    }

    public ArrayList<MovieDetails> getMoviesData() {
        return this.mMoviesDetails;
    }

    class GridMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mPosterImageView;
        final TextView mMovieNameTextView;
        final TextView mRatingsTextView;

        GridMovieViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_movie_poster);
            mMovieNameTextView = itemView.findViewById(R.id.tv_movie_name);
            mRatingsTextView = itemView.findViewById(R.id.tv_ratings);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMovieClickListener.onItemClicked(mMoviesDetails.get(getAdapterPosition()));
        }
    }
}
