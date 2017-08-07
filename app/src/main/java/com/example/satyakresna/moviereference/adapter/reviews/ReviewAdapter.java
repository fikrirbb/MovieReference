package com.example.satyakresna.moviereference.adapter.reviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.satyakresna.moviereference.R;
import com.example.satyakresna.moviereference.model.reviews.ReviewsResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by satyakresna on 06-Aug-17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private List<ReviewsResult> list = new ArrayList<>();

    public ReviewAdapter(List<ReviewsResult> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdFromListItem = R.layout.row_reviews;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachParentImmediately = false;

        View view = inflater.inflate(layoutIdFromListItem, parent, shouldAttachParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ReviewViewHolder) holder).bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_author) TextView reviewAuthorName;
        @BindView(R.id.tv_review) TextView reviewAuthorReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final ReviewsResult data) {
            reviewAuthorName.setText(data.getAuthor());
            reviewAuthorReview.setText(data.getContent());
        }
    }
}
