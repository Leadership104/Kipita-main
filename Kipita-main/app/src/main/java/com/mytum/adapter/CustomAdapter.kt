package com.mytum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mytum.R
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.interfaces.ItemMoveCallback
import com.mytum.model.Application.Category
import java.util.Collections

class CustomAdapter(
    val modelList: List<Category>,
    val context: Context,
    val listner: ComplaintClickPressed
) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>(), ItemMoveCallback.ItemTouchHelperContract {

    override fun onBindViewHolder(holder: CustomAdapter.MyViewHolder, position: Int) {
        val itemsViewModel = modelList[position]
        holder.text_adv.text = itemsViewModel.name
        holder.card_adv.setOnClickListener { //                popupWindowInfo();
            listner.onCallEvent(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_acvisory, parent, false)

        return MyViewHolder(view)
    }

    class MyViewHolder(var rowView: View) : RecyclerView.ViewHolder(
        rowView
    ) {
        var img_adv: ImageView
        var card_adv: CardView
        var text_adv: TextView

        init {
            img_adv = itemView.findViewById(R.id.img_adv)
            card_adv = itemView.findViewById(R.id.card_adv)
            text_adv = itemView.findViewById(R.id.text_adv)
        }
    }

    override fun getItemCount(): Int {
        return modelList.size;
    }

    interface ClickListener {
        fun onClick(pos: Int, aView: View)
    }

//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val img_adv: ImageView = itemView.findViewById(R.id.img_adv)
//        val text_adv: TextView = itemView.findViewById(R.id.text_adv)
//        val card_adv: CardView = itemView.findViewById(R.id.card_adv)
//
//    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(modelList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(modelList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

}