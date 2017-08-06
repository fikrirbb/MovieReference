package com.example.satyakresna.moviereference.adapter.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.satyakresna.moviereference.R;
import com.example.satyakresna.moviereference.model.movies.MovieResults;
import com.example.satyakresna.moviereference.utilities.ImageUrlBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satyakresna on 05-Aug-17.
 */

public class MovieReferenceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MovieReferenceAdapter.class.getSimpleName();
    private List<MovieResults> list = new ArrayList<>();
    private final ItemClickListener mOnClickListener;

    public interface ItemClickListener {
        void onItemClick(MovieResults data, int position);
    }

    public MovieReferenceAdapter(List<MovieResults> list, ItemClickListener mOnClickListener) {
        this.list = list;
        this.mOnClickListener = mOnClickListener;
    }

    public void replaceAll(List<MovieResults> list) {
        this.list.clear();
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateData(List<MovieResults> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int LayoutIdFromListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(LayoutIdFromListItem, parent, shouldAttachToParentImmediately);
        MovieReferenceViewHolder viewHolder = new MovieReferenceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MovieReferenceViewHolder) holder).bind(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MovieReferenceViewHolder extends RecyclerView.ViewHolder {

        ImageView posterPath;

        public MovieReferenceViewHolder(View itemView) {
            super(itemView);
            posterPath = (ImageView) itemView.findViewById(R.id.iv_poster_path);
        }

        public void bind(final MovieResults data, final int position) {
            Picasso.with(itemView.getContext())
                    .load(ImageUrlBuilder.getPosterUrl(data.getPoster_path()))
                    .placeholder(R.drawable.ic_local_movies)
                    .error(R.drawable.ic_error).into(posterPath);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickListener.onItemClick(data, position);
                }
            });
        }
    }
}
