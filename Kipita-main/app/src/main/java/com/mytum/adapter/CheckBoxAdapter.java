package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.CheckBoxResponse.CheckBoxResponse;
import com.mytum.model.CheckBoxResponse.Datum;
import com.mytum.model.NewAdvisaryResponse.AdvisaryListResponse;
import com.mytum.model.NewPlaceGoResponse.ResultNewPlace;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class CheckBoxAdapter extends RecyclerView.Adapter<CheckBoxAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<Datum> allDataSave;
    private ArrayList<Datum> allParticipants;
    private ComplaintClickPressed listner;
    ArrayList<AdvisaryListResponse> dta1;

    public CheckBoxAdapter(Context contaext, ArrayList<Datum> allParticipant, ComplaintClickPressed userInfo,ArrayList<AdvisaryListResponse> data) {
        this.mContext = contaext;
        this.listner = userInfo;
        allParticipants = allParticipant;
        dta1 = data;

        this.allDataSave = new ArrayList<Datum>();
        allDataSave.clear();
        allDataSave.addAll(allParticipants);
    }



    @Override
    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.checkbox_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
        Datum getData =  allParticipants.get(position);
        holder.titel.setText(Html.fromHtml(getData.getTitle()));

//        if(getData.getType().equalsIgnoreCase("overall")){
//            holder.titel.setText(Html.fromHtml("Select All"));
//        }else{
//            holder.titel.setText(Html.fromHtml(getData.getTitle()));
//        }


        if(getData.isChecked() == true){
            holder.checkbox.setChecked(true); ;
        }else{
            holder.checkbox.setChecked(false); ;
        }

        if(getData.getQueryType() != null) {
            if (getData.getQueryType().equalsIgnoreCase("userinput")) {
                holder.image_edit.setVisibility(View.VISIBLE);
                holder.image_delete.setVisibility(View.VISIBLE);
            } else {
                holder.image_edit.setVisibility(View.GONE);
                holder.image_delete.setVisibility(View.GONE);
            }
        }else {
            holder.image_edit.setVisibility(View.GONE);
            holder.image_delete.setVisibility(View.GONE);
        }

        holder.image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onCallEvent(position);
            }
        });

        holder.image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onImageClick(position);
            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listner.onClickEvent(position);
            }
        });


        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    allDataSave.get(position)
                getData.setChecked(isChecked);
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
        CheckBox checkbox;
        LinearLayout parentLayout;
        TextView titel;
        ImageView image_edit;
        ImageView image_delete;

        public ComplaintHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            checkbox = itemView.findViewById(R.id.checkbox);
            titel = itemView.findViewById(R.id.titel);
            image_edit = itemView.findViewById(R.id.image_edit);
            image_delete = itemView.findViewById(R.id.image_delete);
        }
    }


}
