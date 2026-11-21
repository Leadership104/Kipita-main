package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.fragment.GroupListFragment;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.GroupsItemObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ComplaintHolder> {
    private Context mContext;
    private ArrayList<GroupsItemObject> allParticipants;
    private ComplaintClickPressed listner;

    public GroupListAdapter(Context activity, ArrayList<GroupsItemObject> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = activity;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_group_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        GroupsItemObject getData = allParticipants.get(position);
        holder.titel.setText(getData.getName());
        holder.date.setText(getData.getCreatedDate());
        holder.text_member.setText("0");
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
        public TextView text_member;
        public TextView date;
        //        ImageView icon;
        CardView parentLayout;
//        LinearLayout imageLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
//            imageLayout = itemView.findViewById(R.id.imageLayout);
            titel = itemView.findViewById(R.id.titel);
            text_member = itemView.findViewById(R.id.text_member);
            date = itemView.findViewById(R.id.date);
//            icon = itemView.findViewById(R.id.icon);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

}
