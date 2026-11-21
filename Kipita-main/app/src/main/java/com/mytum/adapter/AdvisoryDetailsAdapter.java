package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.proectUtil.ProjectUtil;
import com.mytum.R;
import com.mytum.client.models.Business;
import com.mytum.client.models.Category;
import com.mytum.interfaces.ComplaintClickPressed;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class AdvisoryDetailsAdapter extends RecyclerView.Adapter<AdvisoryDetailsAdapter.ComplaintHolder> {
    private Context mContext;
    private ArrayList<Business> allParticipants;
    private ArrayList<Business> allDataSave;
    private ComplaintClickPressed listner;

    public AdvisoryDetailsAdapter(Context contaext, ArrayList<Business> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
        this.allDataSave = new ArrayList<Business>();
        allDataSave.clear();
        allDataSave.addAll(allParticipants);
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_advisory_details, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        Business getData = allParticipants.get(position);
        holder.name.setText(Html.fromHtml(getData.getName()));
        holder.textDistance.setText(ProjectUtil.getDistanceInMileFromMeter(getData.getDistance()) + "mi Away");
//        if(getData.getRating() != null)
            holder.ratingDetails.setText(String.valueOf(getData.getRating()) + String.valueOf(getData.getReviewCount()));

        holder.address.setText(getData.getLocation().getAddress1() + " ," + getData.getLocation().getCity());
        if ((getData.getImageUrl() != null)) {
            try {
                Picasso.get().load(getData.getImageUrl()).into(holder.dog_img);
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
    @SuppressLint("NotifyDataSetChanged")
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        allParticipants.clear();
        if (charText.length() == 0) {
            allParticipants.addAll(allDataSave);
        } else {
            for (Business wp : allDataSave) {
                StringBuilder category = new StringBuilder("");
                for (Category cat : wp.getCategories()) {
                    category.append(cat.getTitle()).append("/");
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
        CardView parentLayout;
        TextView address;
        TextView ratingDetails;
        TextView textDistance;
        LinearLayout llCall, llDir, llInfo;

        public ComplaintHolder(View itemView) {
            super(itemView);
            dog_img = itemView.findViewById(R.id.dog_img);
            name = itemView.findViewById(R.id.name);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            textDistance = itemView.findViewById(R.id.textDistance);
            address = itemView.findViewById(R.id.address);
            ratingDetails = itemView.findViewById(R.id.ratingDetails);
            llCall = itemView.findViewById(R.id.llCall);
            llDir = itemView.findViewById(R.id.llDir);
            llInfo = itemView.findViewById(R.id.llInfo);
        }
    }



}
