package com.hemant.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemant.popularmovies.R;
import com.hemant.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class YouTubeTrailersAdapters extends RecyclerView.Adapter<YouTubeTrailersAdapters.TrailerHorizontalViewHolder> {
    private final OnTrailerItemClickListener onTrailerItemClickListener;
    private ArrayList<String> youTubeUrlArrayList;

    public interface OnTrailerItemClickListener {
        void onYouTubeTrailerItemClicked(String youtubeId);
    }

    public YouTubeTrailersAdapters(OnTrailerItemClickListener onTrailerItemClickListener) {
        this.onTrailerItemClickListener = onTrailerItemClickListener;
        youTubeUrlArrayList = null;
    }

    @Override
    public TrailerHorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_trailer_layout_vh, parent, false);
        return new TrailerHorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerHorizontalViewHolder holder, int position) {
        String trailer = "Trailer #";
        holder.textViewTrailerName.setText(String.format("%s%s", trailer, String.valueOf(position + 1)));
        try {
            URL url = NetworkUtils.buildUrlForYouTubeTrailer(holder.itemView.getContext(), youTubeUrlArrayList.get(position), NetworkUtils.MEDIUM_QUALITY_PATH);
            Picasso.with(holder.itemView.getContext()).load(url.toString()).into(holder.imageViewTrailerPoster);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void swapTrailerData(ArrayList<String> youTubeUrlArrayList) {
        this.youTubeUrlArrayList = youTubeUrlArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (youTubeUrlArrayList == null) {
            return 0;
        } else {
            return youTubeUrlArrayList.size();
        }
    }

    class TrailerHorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View itemView;
        final TextView textViewTrailerName;
        final ImageView imageViewTrailerPoster;


        TrailerHorizontalViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewTrailerName = itemView.findViewById(R.id.tv_trailer_name);
            imageViewTrailerPoster = itemView.findViewById(R.id.iv_trailer_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTrailerItemClickListener.onYouTubeTrailerItemClicked(youTubeUrlArrayList.get(getAdapterPosition()));
        }
    }
}
