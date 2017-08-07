package com.example.satyakresna.moviereference.adapter.trailer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.satyakresna.moviereference.R;
import com.example.satyakresna.moviereference.model.trailer.TrailerResults;
import com.example.satyakresna.moviereference.utilities.TrailerUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by satyakresna on 06-Aug-17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = TrailerAdapter.class.getSimpleName();
    private final TrailerItemClickListener mOnClickListener;
    private List<TrailerResults> list = new ArrayList<>();

    public interface TrailerItemClickListener {
        void onTrailerItemClick(TrailerResults data, int position);
    }

    public TrailerAdapter(List<TrailerResults> list, TrailerItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdFromListItem = R.layout.row_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdFromListItem, parent, shouldAttachToParentImmediately);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TrailerViewHolder) holder).bind(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_trailer) ImageView trailerThumbnail;
        @BindView(R.id.tv_trailer_title) TextView trailerTitle;
        @BindView(R.id.tv_trailer_type) TextView trailerType;
        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final TrailerResults data, final int position) {
            Picasso.with(itemView.getContext())
                    .load(TrailerUtil.getVideoThumbnailUrl(data.getKey()))
                    .placeholder(R.drawable.ic_local_movies)
                    .error(R.drawable.ic_error)
                    .into(trailerThumbnail);
            trailerTitle.setText(data.getName());
            trailerType.setText(data.getType());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickListener.onTrailerItemClick(data, position);
                }
            });
        }
    }
}
