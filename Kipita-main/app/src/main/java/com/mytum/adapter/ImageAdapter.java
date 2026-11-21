package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.mytum.ImageViewerActivity;
import com.mytum.ImageViewerMoreInforActivity;
import com.mytum.ViewRecomendDataActivity;
import com.mytum.model.places.Photo;
import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<Photo> allDataSave;
    private ArrayList<Photo> allParticipants;
    private ComplaintClickPressed listner;

    public ImageAdapter(Context contaext, ArrayList<Photo> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
        this.allDataSave = new ArrayList<Photo>();
        allDataSave.clear();
        allDataSave.addAll(allParticipants);
    }


    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.image_time_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        Photo getData = allParticipants.get(position);
        if ((getData.getName() != null)) {
            try {
                String url = "https://places.googleapis.com/v1/"+ getData.getName()+"/media?maxHeightPx=400&maxWidthPx=400&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4";
                Picasso.get().load(url).into(holder.icon);
                holder.icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent result = new Intent(mContext, ImageViewerMoreInforActivity.class);
                        result.putExtra(ImageViewerActivity.EXTRA_PATH, url);
                        mContext.startActivity(result);
                    }
                });
            } catch (Exception e) {

            }
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

        LinearLayout parentLayout;
        ImageView icon;

        public ComplaintHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            icon = itemView.findViewById(R.id.icon);
        }
    }


}
