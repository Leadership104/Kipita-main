package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mytum.model.ImageList.ImageUploadData;
import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<ImageUploadData> allParticipants;
    private ComplaintClickPressed listner;

    public UploadImageAdapter(Context contaext, ArrayList<ImageUploadData> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.image_upload_item, parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageUploadData getData = allParticipants.get(position);

        try {
            if (getData != null) {
                Picasso.get()
                        .load(new File(getData.getThumb()))
                        .placeholder(R.drawable.splash)
                        .into(holder.image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.textStatus.setText(getData.getStatus());
        if(getData.getStatus().trim().equalsIgnoreCase("Pending")){
            holder.textStatus.setBackgroundResource(R.color.grey);
        }else if(getData.getStatus().trim().equalsIgnoreCase("success")){
            holder.textStatus.setBackgroundResource(R.color.green);
        }else{
            holder.textStatus.setBackgroundResource(R.color.red);
            holder.textStatus.setText(getData.getStatus()+"\n"+getData.getMessage());
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

        ImageView image;
        TextView textStatus;

        public ComplaintHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            textStatus = itemView.findViewById(R.id.textStatus);


        }
    }


}
