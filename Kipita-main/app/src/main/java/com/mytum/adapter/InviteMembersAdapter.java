//package com.mytum.adapter;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.mytum.R;
//import com.mytum.interfaces.ComplaintClickPressed;
//import com.mytum.model.GroupsItemObject;
//import com.mytum.model.SelectGroupeMemberItemObject;
//
//import org.jetbrains.annotations.Nullable;
//
//import java.util.ArrayList;
//
//public class InviteMembersAdapter extends RecyclerView.Adapter<InviteMembersAdapter.ComplaintHolder> {
//    private final Context mContext;
//    private final ArrayList<SelectGroupeMemberItemObject> allParticipants;
//    ArrayList<String> appsList = new ArrayList<>();
//    boolean isSelectedAll;
//
//    public InviteMembersAdapter(@Nullable FragmentActivity activity, ArrayList<SelectGroupeMemberItemObject> allParticipant) {
//        this.mContext = activity;
//        allParticipants = allParticipant;
//    }
//
//    @NonNull
//    @Override
//    public ComplaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new ComplaintHolder(
//                LayoutInflater.from(mContext).inflate(R.layout.item_invite_members, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(ComplaintHolder holder, @SuppressLint("RecyclerView") int position) {
//        SelectGroupeMemberItemObject getData = allParticipants.get(position);
//        holder.text_name.setText(getData.getText());
//        holder.checkbox.setChecked(isSelectedAll);
//        holder.checkbox.setTag(getData.getNo());
//        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                try {
//                    if (b) {
//                        appsList.add((String) compoundButton.getTag());
//                    } else {
//                        appsList.remove(compoundButton.getTag());
//                    }
//                    Log.d("list", appsList.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    public void selectAll() {
//        isSelectedAll = true;
//        notifyDataSetChanged();
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    public void unSelectAll() {
//        isSelectedAll = false;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getItemCount() {
//        if (allParticipants != null) {
//            return allParticipants.size();
//        } else {
//            return 0;
//        }
//    }
//
//    public class ComplaintHolder extends RecyclerView.ViewHolder {
//        private final TextView text_name;
//        private final CheckBox checkbox;
//
//        public ComplaintHolder(View itemView) {
//            super(itemView);
//            text_name = itemView.findViewById(R.id.text_name);
//            checkbox = itemView.findViewById(R.id.checkbox);
//        }
//    }
//
//
//}
