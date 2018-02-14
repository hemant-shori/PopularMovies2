package com.hemant.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemant.popularmovies.models.MovieReview;
import com.hemant.popularmovies.R;

import java.util.ArrayList;


public class MovieReviewsAdapters extends RecyclerView.Adapter<MovieReviewsAdapters.TrailerHorizontalViewHolder> {
    private final OnMovieReviewItemClickListener onMovieReviewItemClickListener;
    private ArrayList<MovieReview> movieReviewArrayList;

    public interface OnMovieReviewItemClickListener {
        void onMovieReviewItemClicked(MovieReview movieReview);
    }

    public MovieReviewsAdapters(OnMovieReviewItemClickListener onMovieReviewItemClickListener) {
        this.onMovieReviewItemClickListener = onMovieReviewItemClickListener;
        movieReviewArrayList = null;
    }

    @Override
    public TrailerHorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_reviews_layout_vh, parent, false);
        return new TrailerHorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerHorizontalViewHolder holder, int position) {
        holder.textViewAuthor.setText(movieReviewArrayList.get(position).getAuthor());
        holder.textViewReview.setText(movieReviewArrayList.get(position).getContent());

    }

    public void swapReviewsData(ArrayList<MovieReview> movieReviews) {
        this.movieReviewArrayList = movieReviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (movieReviewArrayList == null) {
            return 0;
        } else {
            return movieReviewArrayList.size();
        }
    }

    class TrailerHorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View itemView;
        final TextView textViewAuthor, textViewReview;


        TrailerHorizontalViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewReview = itemView.findViewById(R.id.textViewReview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMovieReviewItemClickListener.onMovieReviewItemClicked(movieReviewArrayList.get(getAdapterPosition()));
        }
    }
}
