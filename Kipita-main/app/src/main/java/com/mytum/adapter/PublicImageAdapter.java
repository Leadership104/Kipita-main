package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.listners.ImageClickPressed;
import com.mytum.model.ImageList.ImageArrayListList;
import com.mytum.proectUtil.ProjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PublicImageAdapter extends RecyclerView.Adapter<PublicImageAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<ImageArrayListList> allParticipants;
    private ImageClickPressed listner;
    private int selected = 0;

    public PublicImageAdapter(Context contaext, ArrayList<ImageArrayListList> allParticipant, ImageClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }


    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.public_image_list, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageArrayListList getData = allParticipants.get(position);
        holder.imageName.setText(getData.getName());
        holder.mode.setText(getData.getMode());
        holder.date.setText(getData.getDateAdded());
        holder.lat.setText(getData.getLat());
        holder.long12.setText(getData.getLng());

        if (selected == position) {
            holder.root_layout.setBackground(mContext.getDrawable(R.drawable.rounded_cover_with_alert));
        } else {
            holder.root_layout.setBackground(mContext.getDrawable(R.drawable.rounded_cover_with_border));
        }

        if ((getData.getThumb() != null)) {
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getData.getThumbWidth(), getData.getThumbHeight());
//            holder.image.setLayoutParams(layoutParams);
            try {
                Picasso.get()
                        .load(getData.getThumb())
                        .placeholder(R.drawable.splash)
                        .into(holder.image);
//                ProjectUtil.customToast(mContext,getData.getPath());
                Log.d("PATH ADApter", getData.getThumb());
            } catch (Exception e) {
                Log.d("PATH", e.toString());
            }
        }

        holder.parentLayout.setOnClickListener(v -> {
//                holder.parentLayout.setBackgroundResource(R.drawable.selected_image_highlighter);
            Log.d("aaaaaaaaaa position", "" + position);
            listner.onImageClickEvent(position);
            selected = position;
            holder.root_layout.setBackground(mContext.getDrawable(R.drawable.rounded_cover_with_alert));
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onDeleteClickEvent(position);
            }
        });

        holder.editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onEditClickEvent(position);
            }
        });

        if (getData.getUserID().equalsIgnoreCase(ProjectUtil.getUserId(mContext))) {
            holder.editInfo.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.editInfo.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }

    }

    public void setSelection(int itemPost) {
        selected = itemPost;
        notifyDataSetChanged();
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
        TextView imageName;
        TextView mode;
        TextView date;
        CardView parentLayout;
        LinearLayout delete, root_layout;
        LinearLayout editInfo;
        TextView lat;
        TextView long12;

        public ComplaintHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            editInfo = itemView.findViewById(R.id.editInfo);
            image = itemView.findViewById(R.id.image);
            date = itemView.findViewById(R.id.date);
            imageName = itemView.findViewById(R.id.imageName);
            lat = itemView.findViewById(R.id.lat);
            mode = itemView.findViewById(R.id.mode);
            long12 = itemView.findViewById(R.id.long12);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            root_layout = itemView.findViewById(R.id.root_layout);
        }
    }


}
