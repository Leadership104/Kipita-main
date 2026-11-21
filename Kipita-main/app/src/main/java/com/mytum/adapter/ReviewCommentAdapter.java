package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

//import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.places.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewCommentAdapter extends RecyclerView.Adapter<ReviewCommentAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<Review> allDataSave;
    private ArrayList<Review> allParticipants;
    private ComplaintClickPressed listner;

    public ReviewCommentAdapter(Context contaext, ArrayList<Review> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
        this.allDataSave = new ArrayList<Review>();
        allDataSave.clear();
        allDataSave.addAll(allParticipants);
    }


    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.review_rating_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        Review getData = allParticipants.get(position);

        if (getData.getName() != null) {
            try {

                Picasso.get()
                        .load(getData.getAuthorAttribution().getPhotoUri())
                        .placeholder(R.drawable.user_edit)
                        .into(holder.reviewUserImg);
            } catch (Exception e) {

            }
        }
        try{
            if(getData.getAuthorAttribution() != null) {
                holder.reviewUserName.setText(getData.getAuthorAttribution().getDisplayName() + " * " + getData.getRelativePublishTimeDescription());
            }else{
            }
            if(getData.getText() != null) {
                holder.reviewComments.setVisibility(View.VISIBLE);
                holder.reviewComments.setText(getData.getText().getText());
            }else{
                holder.reviewComments.setVisibility(View.GONE);
            }
//        holder.ratingBar.setRating(getData.getRating());
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        if (allParticipants != null) {
            return allParticipants.size();
        } else {
            return 0;
        }
    }

    public class ComplaintHolder extends RecyclerView.ViewHolder {

        LinearLayout reviewCommentListLayout;
        ImageView reviewUserImg;
        TextView reviewUserName;
//        SimpleRatingBar ratingBar;
        TextView reviewComments;

        public ComplaintHolder(View itemView) {
            super(itemView);
            reviewCommentListLayout = itemView.findViewById(R.id.reviewCommentListLayout);
            reviewUserImg = itemView.findViewById(R.id.reviewUserImg);
            reviewUserName = itemView.findViewById(R.id.reviewUserName);
//            ratingBar = itemView.findViewById(R.id.rating_bar);
            reviewComments = itemView.findViewById(R.id.reviewComments);
        }
    }


}
