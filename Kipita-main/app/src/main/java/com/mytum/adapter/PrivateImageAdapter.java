package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.listners.ImageClickPressed;
import com.mytum.model.ImageList.ImageArrayListList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PrivateImageAdapter extends RecyclerView.Adapter<PrivateImageAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<ImageArrayListList> allParticipants;
    private ImageClickPressed listner;

    public PrivateImageAdapter(Context contaext, ArrayList<ImageArrayListList> allParticipant, ImageClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }


    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.private_image_list, parent, false));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageArrayListList getData = allParticipants.get(position);
        holder.imageName.setText(getData.getName());
        holder.mode.setText(getData.getMode());
        holder.date.setText(getData.getDateAdded());
        holder.lat.setText(getData.getLat());
        holder.long12.setText(getData.getLng());


        if ((getData.getThumb() != null)) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getData.getThumbWidth(), getData.getThumbHeight());
            holder.image.setLayoutParams(layoutParams);
            try {
                Picasso.get()
                        .load(getData.getThumb())
                        .placeholder(R.drawable.splash)
                        .into(holder.image);
            } catch (Exception e) {

            }
        }

        holder.parentLayout.setOnClickListener(v -> listner.onImageClickEvent(position));

        holder.llMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(mContext, v, Gravity.END);
            popup.setOnMenuItemClickListener(MenuItem::collapseActionView);
            popup.inflate(R.menu.popup_menu);
            popup.show();
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.view_edit:
                        listner.onEditClickEvent(position);
                        return true;
                    case R.id.view_delete:
                        listner.onDeleteClickEvent(position);
                        return true;
                    default:
                        return false;
                }
            });
        });
        /*holder.delete.setOnClickListener(new View.OnClickListener() {
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
        });*/
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
        LinearLayout delete;
        LinearLayout editInfo, llMenu;

        TextView lat;
        TextView long12;

        public ComplaintHolder(View itemView) {
            super(itemView);

            llMenu = itemView.findViewById(R.id.llMenu);
            editInfo = itemView.findViewById(R.id.editInfo);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.image);
            date = itemView.findViewById(R.id.date);
            imageName = itemView.findViewById(R.id.imageName);
            lat = itemView.findViewById(R.id.lat);
            mode = itemView.findViewById(R.id.mode);
            long12 = itemView.findViewById(R.id.long12);
            parentLayout = itemView.findViewById(R.id.parentLayout);


        }
    }


}
