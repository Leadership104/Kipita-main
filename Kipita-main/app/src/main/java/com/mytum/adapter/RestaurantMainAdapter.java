package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.NewPalnerResponse.NewAdvisaryResponse.PlannerListResponse;
import com.mytum.model.NewRecomendationResponse.RecommendationSubResponse;

import java.util.ArrayList;

public class RestaurantMainAdapter extends RecyclerView.Adapter<RestaurantMainAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<RecommendationSubResponse> allParticipants;
    private ComplaintClickPressed listner;

    public RestaurantMainAdapter(Context contaext, ArrayList<RecommendationSubResponse> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }


    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.main_planer_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        RecommendationSubResponse getData = allParticipants.get(position);

        holder.titel.setTag(R.string.holdertag, getData.getType());
        if (getData.getType().equalsIgnoreCase("overall")) {
            holder.icon.setVisibility(View.GONE);
            holder.titel.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(View.GONE);
            holder.titel.setText(getData.getTitle());
        } else if (getData.getType().equalsIgnoreCase("112233")) {
            holder.titel.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(View.VISIBLE);
            holder.titel.setText("Edit");
            holder.icon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.add_btn_red));
        } else {
            holder.icon.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(View.GONE);
            holder.titel.setVisibility(View.VISIBLE);
            holder.titel.setText(getData.getTitle());
        }



        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClickEvent(position);
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

        public TextView titel;
        ImageView icon;
        CardView parentLayout;
        LinearLayout imageLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            imageLayout = itemView.findViewById(R.id.imageLayout);
            titel = itemView.findViewById(R.id.titel);
            icon = itemView.findViewById(R.id.icon);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }


}
