package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.listners.ItemMoveCallback;
import com.mytum.listners.ItemMoveCallbackPlanner;
import com.mytum.model.Application.Category;

import java.util.ArrayList;
import java.util.Collections;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.MyViewHolder> implements ItemMoveCallbackPlanner.ItemTouchHelperContract {
    private Context mContext;
    private ArrayList<Category> allParticipants;
    private ComplaintClickPressed listner;

    public PlannerAdapter(Context contaext, ArrayList<Category> allParticipant, ComplaintClickPressed userInfo) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_planner, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category getData = allParticipants.get(position);
        holder.text_adv.setText(Html.fromHtml(getData.getName()));
//        if ((getData.getImageUrl() != null)) {
//            try {
//                Picasso.get().load(getData.getImageUrl()).into(holder.img_adv);
//            } catch (Exception e) {
//
//            }
//        }
        holder.card_adv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onCallEvent(position);

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

    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(allParticipants, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(allParticipants, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View rowView;
        ImageView img_adv;
        CardView card_adv;
        TextView text_adv;

        public MyViewHolder(View itemView) {
            super(itemView);
            rowView = itemView;
            img_adv = itemView.findViewById(R.id.img_adv);
            card_adv = itemView.findViewById(R.id.card_adv);
            text_adv = itemView.findViewById(R.id.text_adv);
        }
    }

}
