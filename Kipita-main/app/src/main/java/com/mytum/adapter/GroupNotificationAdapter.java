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
import com.mytum.model.GroupNotificationItemObject;
import com.mytum.model.GroupsItemObject;

import org.json.JSONObject;

import java.util.ArrayList;

public class GroupNotificationAdapter extends RecyclerView.Adapter<GroupNotificationAdapter.ComplaintHolder> {
    private final Context mContext;
    private final ArrayList<GroupNotificationItemObject> allParticipants;
    private final ComplaintClickPressed listner;

    public GroupNotificationAdapter(Context activity, ArrayList<GroupNotificationItemObject> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = activity;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_group_noti, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        GroupNotificationItemObject getData = allParticipants.get(position);
        try{
            JSONObject obj = new JSONObject(getData.getText());
            holder.text_name.setText(getData.getNo() + ".  " + obj.get("message"));
        }catch (Exception e){

        }

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

        public TextView text_name;
        CardView parentLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.text_name);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

}
