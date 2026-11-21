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
import com.mytum.client.models.Category;
import com.mytum.interfaces.ComplaintClickPressed;

import java.util.ArrayList;

public class CategryAdapter extends RecyclerView.Adapter<CategryAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<Category> allParticipants;
    private ComplaintClickPressed listner;

    public CategryAdapter(Context contaext, ArrayList<Category> allParticipant) {
        this.mContext = contaext;
        allParticipants = allParticipant;
    }



    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.category_list_info, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        Category getData =  allParticipants.get(position);
        holder.title.setText(getData.getTitle());
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

        TextView title;

        CardView parentLayout;

        public ComplaintHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            parentLayout = itemView.findViewById(R.id.parentLayout);

        }
    }


}
