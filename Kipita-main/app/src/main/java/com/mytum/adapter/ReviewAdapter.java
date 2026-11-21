package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.client.models.Review;
import com.mytum.interfaces.ComplaintClickPressed;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<Review> allParticipants;
    private ComplaintClickPressed listner;

    public ReviewAdapter(Context contaext, ArrayList<Review> allParticipant) {
        this.mContext = contaext;
        allParticipants = allParticipant;
    }



    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.review_info, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        Review getData =  allParticipants.get(position);
        holder.name.setText(getData.getUser().getName());
        holder.time.setText(getData.getTimeCreated());
        holder.descriptiom.setText(getData.getText());
        holder.userRating.setRating(Float.parseFloat(""+getData.getRating()));
        Picasso.get().load(getData.getUser().getImageUrl()).into(holder.imageView);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listner.onClickEvent(position);
            }
        });

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

        TextView name;
        TextView time;
        TextView descriptiom;
        ImageView imageView;
        ScaleRatingBar userRating;
        CardView parentLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            descriptiom = itemView.findViewById(R.id.descriptiom);
            imageView = itemView.findViewById(R.id.imageview);
            userRating = itemView.findViewById(R.id.userRating);
            name = itemView.findViewById(R.id.name);
            parentLayout = itemView.findViewById(R.id.parentLayout);



        }
    }


}
