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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.places.Place;
import com.mytum.proectUtil.ProjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class RestaurantNewAdapter extends RecyclerView.Adapter<RestaurantNewAdapter.ComplaintHolder> {
    private Context mContext;
    private ArrayList<Place> allPlacesList;
    private ArrayList<Place> allDataSave;
    private ComplaintClickPressed listner;
    private int showOpenClosed = 1;

    public RestaurantNewAdapter(Context contaext, ArrayList<Place> allParticipant, int showOpenClosed, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allPlacesList = allParticipant;
        this.allDataSave = new ArrayList<Place>();
        allDataSave.clear();
        allDataSave.addAll(allPlacesList);
        this.showOpenClosed = showOpenClosed;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.entertainment_list2, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        Place placeData = allPlacesList.get(position);
        holder.name.setText(Html.fromHtml(placeData.getDisplayName().getText()));
        if (showOpenClosed == 1) {
            if (placeData.getRegularOpeningHours() != null) {
                if (placeData.getRegularOpeningHours().getOpenNow() == true) {
                    holder.openOrClosed.setText("Open");
                    holder.openOrClosed.setTextColor(ContextCompat.getColor(mContext, R.color.green_bottom));
                } else {
                    holder.openOrClosed.setText("Closed");
                    holder.openOrClosed.setTextColor(ContextCompat.getColor(mContext, R.color.red_bottom));
                }
            } else {
                holder.openOrClosed.setText("Closed");
                holder.openOrClosed.setTextColor(ContextCompat.getColor(mContext, R.color.red_bottom));
            }
        } else {
            holder.openOrClosed.setVisibility(View.GONE);
        }

        holder.ratingDetails.setText(placeData.getRating() + "(" + placeData.getUserRatingCount() + ")");
        if (placeData.getAccessibilityOptions() != null && getWheelChairStatus(placeData)) {
            holder.wheelChairDash.setVisibility(View.VISIBLE);
            holder.wheelChairImage.setVisibility(View.VISIBLE);
        } else {
            holder.wheelChairDash.setVisibility(View.GONE);
            holder.wheelChairImage.setVisibility(View.GONE);
        }

        if (placeData.getAllowsDogs() == true) {
            holder.petDash.setVisibility(View.VISIBLE);
            holder.petImage.setVisibility(View.VISIBLE);
        } else {
            holder.petDash.setVisibility(View.GONE);
            holder.petImage.setVisibility(View.GONE);
        }
        String pricingLabelString = "";

        if (getPriceFromLevel(placeData.getPriceLevel()).equalsIgnoreCase("")) {
            holder.textPricing.setVisibility(View.GONE);
            holder.pricingDash.setVisibility(View.GONE);
        } else {
            holder.textPricing.setVisibility(View.VISIBLE);
            holder.pricingDash.setVisibility(View.VISIBLE);
            pricingLabelString = getPriceFromLevel(placeData.getPriceLevel());
            holder.textPricing.setText(pricingLabelString);
        }
        holder.textDistance.setText(
                ProjectUtil.calculateDistance(Double.parseDouble(ProjectUtil.getLotitude(mContext)), Double.parseDouble(ProjectUtil.getLongitude(mContext)), placeData.getLocation().getLatitude(), placeData.getLocation().getLongitude())
                        + "mi Away");

        holder.address.setText(placeData.getFormattedAddress());


        if ((placeData.getPhotos() != null) && placeData.getPhotos().size() > 0) {
            try {
                String url = "https://places.googleapis.com/v1/" + placeData.getPhotos().get(0).getName() + "/media?maxHeightPx=100&maxWidthPx=100&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4";
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.user_edit)
                        .into(holder.dog_img);
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
        allPlacesList.clear();
        if (charText.length() == 0) {
            allPlacesList.addAll(allDataSave);
        } else {
            for (Place wp : allDataSave) {
                StringBuilder category = new StringBuilder("");
                for (String cat : wp.getTypes()) {
                    category.append(cat).append("/");
                }
                if (category.length() > 0) {
                    category.deleteCharAt(category.length() - 1);
                }

                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText) || category.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    allPlacesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (allPlacesList != null) {
            return allPlacesList.size();
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
        TextView wheelChairDash;
        ImageView wheelChairImage;
        TextView petDash;
        ImageView petImage;

        TextView pricingDash;
        TextView textPricing;
        TextView distanceDash;
        TextView textDistance;
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
            wheelChairDash = itemView.findViewById(R.id.wheelChairDash);
            wheelChairImage = itemView.findViewById(R.id.wheelChairImage);
            petDash = itemView.findViewById(R.id.petDash);
            petImage = itemView.findViewById(R.id.petImage);
            pricingDash = itemView.findViewById(R.id.pricingDash);
            textPricing = itemView.findViewById(R.id.textPricing);
            distanceDash = itemView.findViewById(R.id.distanceDash);
            textDistance = itemView.findViewById(R.id.textDistance);
            llCall = itemView.findViewById(R.id.llCall);
            llDir = itemView.findViewById(R.id.llDir);
            llInfo = itemView.findViewById(R.id.llInfo);
        }
    }

    private String getPriceFromLevel(String price_level) {
        String i = "";
        if (price_level != null) {
            if (price_level.equalsIgnoreCase("PRICE_LEVEL_INEXPENSIVE")) {
                i = "$";
            } else if (price_level.equalsIgnoreCase("PRICE_LEVEL_MODERATE")) {
                i = "$$";
            } else if (price_level.equalsIgnoreCase("PRICE_LEVEL_EXPENSIVE")) {
                i = "$$$";
            } else if (price_level.equalsIgnoreCase("PRICE_LEVEL_VERY_EXPENSIVE")) {
                i = "$$$$";
            }
        }
        return i;
    }

    private boolean getWheelChairStatus(Place placeData) {
        boolean i = false;
        if (placeData != null) {
            if (placeData.getAccessibilityOptions().getWheelchairAccessibleParking() == true) {
                i = true;
            } else if (placeData.getAccessibilityOptions().getWheelchairAccessibleSeating() == true) {
                i = true;
            } else if (placeData.getAccessibilityOptions().getWheelchairAccessibleRestroom() == true) {
                i = true;
            } else if (placeData.getAccessibilityOptions().getWheelchairAccessibleEntrance() == true) {
                i = true;
            }
        }
        return i;
    }

}
