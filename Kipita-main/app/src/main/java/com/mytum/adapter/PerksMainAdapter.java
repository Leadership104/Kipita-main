package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.PerksListResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PerksMainAdapter extends RecyclerView.Adapter<PerksMainAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<PerksListResponse> allParticipants;
    private ComplaintClickPressed listner;

    public PerksMainAdapter(Context contaext, ArrayList<PerksListResponse> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }

    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.perks_mian_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        PerksListResponse getData = allParticipants.get(position);
        holder.titel.setText("Details");
        if ((getData.getImage() != null)) {
            try {
                Picasso.get().load(getData.getImage()).into(holder.icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        ImageView icon;
        CardView parentLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            titel = itemView.findViewById(R.id.titel);
            icon = itemView.findViewById(R.id.icon);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }


}
