package com.mytum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mytum.R;
import com.mytum.interfaces.ComplaintClickPressed;
import com.mytum.model.CheckBoxResponse.Datum;
import com.mytum.model.NewAdvisaryResponse.AdvisaryListResponse;
import com.mytum.model.NewPalnerResponse.NewAdvisaryResponse.PlannerListResponse;

import java.util.ArrayList;

public class PlanerCheckBoxAdapter extends RecyclerView.Adapter<PlanerCheckBoxAdapter.ComplaintHolder> {

    private Context mContext;
    private ArrayList<Datum> allDataSave;
    private ArrayList<Datum> allParticipants;
    private ComplaintClickPressed listner;
    ArrayList<PlannerListResponse> dta1;

    public PlanerCheckBoxAdapter(Context contaext, ArrayList<Datum> allParticipant, ComplaintClickPressed userInfo, ArrayList<PlannerListResponse> data) {
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

        if(getData.getType().equalsIgnoreCase("overall")){
            holder.titel.setText(Html.fromHtml("Select All"));
        }else{
            holder.titel.setText(Html.fromHtml(getData.getTitle()));
        }


        if(getData.isChecked() == true){
            holder.checkbox.setChecked(true); ;
        }else{
            holder.checkbox.setChecked(false); ;
        }


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClickEvent(position);
            }
        });


//        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                listner.onClickEvent(position);
//            }
//        });


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

        public ComplaintHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            checkbox = itemView.findViewById(R.id.checkbox);
            titel = itemView.findViewById(R.id.titel);
        }
    }


}
