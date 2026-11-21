package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.CreateGroupeItemObject;
import com.mytum.model.GroupsItemObject;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CreateGroupAdapter extends RecyclerView.Adapter<CreateGroupAdapter.ComplaintHolder> {
    private Context mContext;
    private ArrayList<CreateGroupeItemObject> allParticipants;

    public CreateGroupAdapter(@Nullable FragmentActivity activity, ArrayList<CreateGroupeItemObject> allParticipant) {
        this.mContext = activity;
        allParticipants = allParticipant;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_create_group, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        CreateGroupeItemObject getData = allParticipants.get(position);
        holder.titel.setText(getData.getFirstName());
         holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        CardView parentLayout;
//        LinearLayout imageLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
//            imageLayout = itemView.findViewById(R.id.imageLayout);
            titel = itemView.findViewById(R.id.titel);
//            icon = itemView.findViewById(R.id.icon);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }


}
