package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<String> allParticipants;
    private ComplaintClickPressed listner;
    boolean isFromImageDetail = false;

    public TagAdapter(Context contaext, ArrayList<String> allParticipant, ComplaintClickPressed userInfo, boolean isFromImageDetail) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
        this.isFromImageDetail = isFromImageDetail;
    }


    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.tag_info, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        String getData = allParticipants.get(position);

        if (getData.startsWith("#")) {
            holder.addtitle.setText("" + getData);
        } else {
            holder.addtitle.setText("#" + getData);
        }
        if(isFromImageDetail){
            holder.close.setVisibility(View.GONE);
            holder.addtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onClickEvent(position);
                }
            });
        }else {
            holder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onClickEvent(position);
                }
            });
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

        ImageView close;
        TextView addtitle;

        public ComplaintHolder(View itemView) {
            super(itemView);
            addtitle = itemView.findViewById(R.id.addtitle);
            close = itemView.findViewById(R.id.close);


        }
    }


}
