package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.MamberItemObject;

import java.util.ArrayList;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ComplaintHolder> {
    private Context mContext;
    private ArrayList<MamberItemObject> allParticipants;
    private ComplaintClickPressed listner;

    public GroupMemberAdapter(Context activity, ArrayList<MamberItemObject> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = activity;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_member_group, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        MamberItemObject getData = allParticipants.get(position);
        holder.text_name.setText(getData.getName());
        holder.text_type.setText(getData.getRole());
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

        public TextView text_type;
        public TextView text_name;
        public TextView text_short;
        CardView parentLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            text_type = itemView.findViewById(R.id.text_type);
            text_name = itemView.findViewById(R.id.text_name);
            text_short = itemView.findViewById(R.id.text_short);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

}
