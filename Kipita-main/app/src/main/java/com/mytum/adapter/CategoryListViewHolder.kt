package com.mytum.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mytum.model.Application.CategoryList
import com.mytum.proectUtil.ProjectUtil
import com.mytum.R
import com.mytum.interfaces.ChildViewHolder
import com.squareup.picasso.Picasso


class CategoryListViewHolder(view:View, _ctx: Context) : ChildViewHolder(view){
    var ctx = _ctx;
    fun bind(categoryList : CategoryList){
        val result = Html.fromHtml(categoryList.name)
        itemView.findViewById<TextView>(R.id.nameTv).text = result
        var locationParent = itemView.findViewById<LinearLayout>(R.id.linearParentLocationMain)
        locationParent.removeAllViews()
        categoryList.locationList?.forEachIndexed { rowIndex, row ->
            val item = LayoutInflater.from(ctx).inflate(R.layout.destination_advisry_location,null)
            item.tag = row.lat+row.lng
            item.setTag(R.string.location_tagKey1, row.lat)
            item.setTag(R.string.location_tagKey2, row.lng)
            locationParent.addView(item, rowIndex)
            val image_location_icon = item.findViewById<ImageView>(R.id.image_location_icon)
            try {
                Picasso.get()
                    .load(row.status)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .into(image_location_icon)
            } catch (e: Exception) {
                Log.d("PATH", e.toString())
            }
            val txtName = item.findViewById<TextView>(R.id.location_name)
            val txtLatLng = item.findViewById<TextView>(R.id.location_details)
            txtName.text = row.name
            txtLatLng.text = row.lat +"\n" + row.lng
            txtLatLng.visibility = View.GONE
            item.setOnClickListener(View.OnClickListener {
                try {
                    if (item.tag.toString() == null) {
                        ProjectUtil.customToast(ctx, "Camp address not found")
                    }else{
                        var strUri =
                            "http://maps.google.com/maps?q=loc:" + item.getTag(R.string.location_tagKey1).toString() + "," + item.getTag(R.string.location_tagKey2).toString()
                        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))

                        intent.setClassName(
                            "com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity"
                        )
                        ctx.startActivity(intent)
                    }

                } catch (e: ActivityNotFoundException) {
                    try {
                        ctx.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=com.google.android.apps.maps")
                            )
                        )
                    } catch (anfe: ActivityNotFoundException) {
                        ctx.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps")
                            )
                        )
                    }
                    e.printStackTrace()
                }
            })
        }
    }
}