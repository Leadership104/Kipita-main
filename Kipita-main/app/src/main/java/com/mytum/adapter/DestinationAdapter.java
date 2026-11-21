package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.client.models.AdvisoryResponse;
import com.mytum.interfaces.ComplaintClickPressed;

import java.util.ArrayList;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<AdvisoryResponse> allParticipants;
    private ComplaintClickPressed listner;

    public DestinationAdapter(Context contaext, ArrayList<AdvisoryResponse> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }



    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.destination_advisry_info, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        AdvisoryResponse getData =  allParticipants.get(position);
        holder.name.setText(getData.getTitle());
        holder.pubdate.setText(getData.getPubDate());
        holder.categry.setText(getData.getCategory().toString());
//        holder.category.setText(getData.getCategory().toString());


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listner.onClickEvent(position);
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

        TextView name;
        TextView categry;
        TextView pubdate;
        TextView category;
        CardView parentLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            pubdate = itemView.findViewById(R.id.pubdate);
            categry = itemView.findViewById(R.id.categry);
            category = itemView.findViewById(R.id.category);
            name = itemView.findViewById(R.id.name);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }


}
