package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.CheckBoxResponse.Datum;

import java.util.ArrayList;

public class OpeningTimeAdapter extends RecyclerView.Adapter<OpeningTimeAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<String> allDataSave;
    private ArrayList<String> allParticipants;
    private ComplaintClickPressed listner;

    public OpeningTimeAdapter(Context contaext, ArrayList<String> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
        this.allDataSave = new ArrayList<String>();
        allDataSave.clear();
        allDataSave.addAll(allParticipants);
    }


    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.opening_time_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        String getData = allParticipants.get(position);
        holder.titel.setText(Html.fromHtml(getData));
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
        TextView titel;

        public ComplaintHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            titel = itemView.findViewById(R.id.titel);
        }
    }


}
