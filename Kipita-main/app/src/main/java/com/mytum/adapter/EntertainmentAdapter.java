package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.NewPlaceGoResponse.ResultNewPlace;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class EntertainmentAdapter extends RecyclerView.Adapter<EntertainmentAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<ResultNewPlace> allDataSave;
    private ArrayList<ResultNewPlace> allParticipants;
    private ComplaintClickPressed listner;

    public EntertainmentAdapter(Context contaext, ArrayList<ResultNewPlace> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;

        this.allDataSave = new ArrayList<ResultNewPlace>();
        allDataSave.clear();
        allDataSave.addAll(allParticipants);
    }


    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.entertainment_list2, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        ResultNewPlace getData = allParticipants.get(position);
        holder.name.setText(Html.fromHtml(getData.getName()));
//        holder.phoneNo.setText(getData.getPhone());


        StringBuilder category = new StringBuilder("");
        for (String cat : getData.getTypes()) {
            category.append(cat).append("/");
        }
        if (category.length() > 0) {
            category.deleteCharAt(category.length() - 1);
        }
        holder.category.setText(category.toString());
        if (getData.getOpeningHours() == null) {
            holder.openOrClosed.setText("Closed");
            holder.openOrClosed.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        } else {
            if (getData.getOpeningHours().getOpenNow() == true) {
                holder.openOrClosed.setText("Open");
                holder.openOrClosed.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            } else {
                holder.openOrClosed.setText("Closed");
                holder.openOrClosed.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            }
        }

//        String price  = getData.getPrice();
//        if(price == null || price.equalsIgnoreCase("null")){
//            price ="";
//        }

        if(getData.getRating() != null)
            holder.ratingDetails.setText(getData.getRating() + "  (" + getData.getUserRatingsTotal() + ")");

        holder.address.setText(getData.getVicinity());

        if ((getData.getPhotos() != null) && getData.getPhotos().size() > 0) {
            try {
//                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=%5C(" + getData.getPhotos().get(0).getPhotoReference() + ")&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4";
                 String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + getData.getPhotos().get(0).getPhotoReference() + "&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4";
                Picasso.get().load(url).into(holder.dog_img);
            } catch (Exception e) {

            }
        }
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClickEvent(position);
            }
        });
        holder.dog_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onImageClick(position);
            }
        });

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onSiteUrl(position);
            }
        });
        holder.llDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onAddressClick(position);
            }
        });

        holder.llCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onCallEvent(position);
            }
        });

        holder.llInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onMoreInfo(position);
            }
        });

    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        allParticipants.clear();
        if (charText.length() == 0) {
            allParticipants.addAll(allDataSave);
        } else {
            for (ResultNewPlace wp : allDataSave) {
                StringBuilder category = new StringBuilder("");
                for (String cat : wp.getTypes()) {
                    category.append(cat).append("/");
                }
                if (category.length() > 0) {
                    category.deleteCharAt(category.length() - 1);
                }

                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText) || category.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    allParticipants.add(wp);
                }
            }
        }
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
        ImageView dog_img;
        TextView name;
        TextView phoneNo;
        TextView status;
        CardView parentLayout;
        TextView category;
        TextView openOrClosed;
        TextView address;
        TextView ratingDetails;
        TextView validdate;
        TextView greendot;
        TextView yellowdor;
        LinearLayout llCall, llDir, llInfo;

        public ComplaintHolder(View itemView) {
            super(itemView);
            dog_img = itemView.findViewById(R.id.dog_img);
            status = itemView.findViewById(R.id.status);
            name = itemView.findViewById(R.id.name);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            phoneNo = itemView.findViewById(R.id.phoneNo);
            category = itemView.findViewById(R.id.category);
            openOrClosed = itemView.findViewById(R.id.openOrClosed);
            address = itemView.findViewById(R.id.address);
            ratingDetails = itemView.findViewById(R.id.ratingDetails);
            llCall = itemView.findViewById(R.id.llCall);
            llDir = itemView.findViewById(R.id.llDir);
            llInfo = itemView.findViewById(R.id.llInfo);
        }
    }


}
