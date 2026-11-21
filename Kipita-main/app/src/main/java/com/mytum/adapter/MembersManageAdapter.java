package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.MamberItemObject;

import java.util.ArrayList;

public class MembersManageAdapter extends RecyclerView.Adapter<MembersManageAdapter.ComplaintHolder> {
    private Context mContext;
    private ArrayList<MamberItemObject> allParticipants;
    private ComplaintClickPressed listner;

    public MembersManageAdapter(Context activity, ArrayList<MamberItemObject> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = activity;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_members_manage, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        MamberItemObject getData = allParticipants.get(position);
        holder.textName.setText(getData.getName());
        holder.btn_request.setOnClickListener(new View.OnClickListener() {
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

        public TextView textName, textSeen, textEta;
        Button btn_request, btn_send, btn_message;
        CardView parentLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textSeen = itemView.findViewById(R.id.textSeen);
            textEta = itemView.findViewById(R.id.textEta);
            btn_request = itemView.findViewById(R.id.btn_request);
            btn_send = itemView.findViewById(R.id.btn_send);
            btn_message = itemView.findViewById(R.id.btn_message);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

}
