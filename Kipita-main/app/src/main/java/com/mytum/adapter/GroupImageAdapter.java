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
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.ImageItemObject;
import com.mytum.model.MamberItemObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupImageAdapter extends RecyclerView.Adapter<GroupImageAdapter.ComplaintHolder> {
    private Context mContext;
    private ArrayList<ImageItemObject> allParticipants;
    private ComplaintClickPressed listner;

    public GroupImageAdapter(Context activity, ArrayList<ImageItemObject> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = activity;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_group_image_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageItemObject getData = allParticipants.get(position);
        Picasso.get().load(getData.getPath()).into(holder.image_main);
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

        public ImageView image_main;
        CardView parentLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            image_main = itemView.findViewById(R.id.image_main);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

}
