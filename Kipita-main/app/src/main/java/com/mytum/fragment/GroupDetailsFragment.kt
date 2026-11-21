package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mytum.ImageViewerActivity
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.adapter.GroupImageAdapter
import com.mytum.adapter.GroupMemberAdapter
import com.mytum.adapter.GroupNotificationAdapter
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.DeleteGroupList
import com.mytum.model.GroupListRewuest
import com.mytum.model.GroupNotificationItemObject
import com.mytum.model.ImageItemObject
import com.mytum.model.MamberItemObject
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import com.squareup.picasso.Picasso
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class GroupDetailsFragment : Fragment(), ComplaintClickPressed {
    val EXTRA_PATH: String = "path"
    var type: String = ""
    var customDialog: Dialog? = null
    var title: String? = null
    var gId: String? = null
    var admin: Boolean? = null
    var toolbar_home: Toolbar? = null
    var text_notification: TextView? = null
    var text_notification_view: TextView? = null
    var text_photo: TextView? = null
    var text_photo_view: TextView? = null
    var text_member: TextView? = null
    var text_member_view: TextView? = null
    var dataList: ArrayList<MamberItemObject> = ArrayList()
    var dataListNoti: ArrayList<GroupNotificationItemObject> = ArrayList()
    var dataListImage: ArrayList<ImageItemObject> = ArrayList()
    var recyclerView_noti: RecyclerView? = null
    var recyclerView_image: RecyclerView? = null
    var recyclerView_member: RecyclerView? = null
    var categoryAdapter: GroupMemberAdapter? = null
    var imageAdapter: ImageListAdapter? = null
    var notiAdapter: GroupNotificationAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_details, container, false)
        title = arguments?.getString("title")
        gId = arguments?.getString("id")
        admin = arguments?.getBoolean("admin")
        toolbar_home = view.findViewById(R.id.toolbar_home)
        val btnLeave = view.findViewById<Button>(R.id.btn_leave)
        val text_delete = view.findViewById<TextView>(R.id.text_delete)
        if (admin == true) {
            text_delete.visibility = View.VISIBLE
        } else {
            text_delete.visibility = View.GONE
        }
        btnLeave.setOnClickListener {
            (activity as MainActivity?)!!.ll_toolbar_all.isVisible = true
            type = "leaveGroup"
            deleteGroupData
        }
        text_delete.setOnClickListener {
            (activity as MainActivity?)!!.ll_toolbar_all.isVisible = true
            type = "deleteGroup"
            deleteGroupData
        }

        customDialog = activity?.let { Dialog(it) }
        recyclerView_noti = view.findViewById(R.id.recyclerView_noti)
        recyclerView_noti!!.isNestedScrollingEnabled = true
        recyclerView_noti!!.setHasFixedSize(false)
        recyclerView_image = view.findViewById(R.id.recyclerView_image)
        recyclerView_image!!.isNestedScrollingEnabled = true
        recyclerView_member = view.findViewById(R.id.recyclerView_member)
        recyclerView_member!!.isNestedScrollingEnabled = true
        text_notification = view.findViewById(R.id.text_notification)
        text_notification_view = view.findViewById(R.id.text_notification_view)
        text_photo = view.findViewById(R.id.text_photo)
        text_photo_view = view.findViewById(R.id.text_photo_view)
        text_member = view.findViewById(R.id.text_member)
        text_member_view = view.findViewById(R.id.text_member_view)
        text_notification_view!!.setOnClickListener {
            (activity as MainActivity?)!!.ll_toolbar_all.isVisible = false
            val bundle = Bundle()
            bundle.putString("title", arguments?.getString("title"))
            bundle.putString("id", gId)
            val fragment = AllNotificationFragment()
            fragment.arguments = bundle
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        text_photo_view!!.setOnClickListener {
            (activity as MainActivity?)!!.ll_toolbar_all.isVisible = false
            val bundle = Bundle()
            bundle.putString("title", arguments?.getString("title"))
            bundle.putString("id", gId)
            val fragment = AllImageFragment()
            fragment.arguments = bundle
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        text_member_view!!.setOnClickListener {
            (activity as MainActivity?)!!.ll_toolbar_all.isVisible = false
            val bundle = Bundle()
            bundle.putString("title", arguments?.getString("title"))
            bundle.putString("id", gId)
            admin?.let { it1 -> bundle.putBoolean("admin", it1) }
            val fragment = MembersManageFragment()
            fragment.arguments = bundle
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        allEntertainmentData1
    }

    @SuppressLint("SetTextI18n")
    fun reresh(data: String) {
        dataList.removeAll(dataList.toSet())
        dataList.clear()
        dataListNoti.clear()
        dataListImage.clear()
        try {
            val json = JSONObject(data)
            val jsonArray = JSONArray(json.getString("data"))
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val json1 = JSONObject(jsonObject.toString())
                when (i) {
                    0 -> {
                        try {
                            val jsonArray1 = JSONArray(json1.getString("members"))
                            for (m in 0 until jsonArray1.length()) {
                                val jsonObject1 = jsonArray1.getJSONObject(m)
                                val gson = Gson()
                                dataList.add(
                                    gson.fromJson(
                                        jsonObject1.toString(),
                                        MamberItemObject::class.java
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ProjectUtil.customToast(activity, e.toString())
                        }
                    }

                    1 -> {
                        try {
                            val jsonArray1 = JSONArray(json1.getString("notifications"))
                            for (m in 0 until jsonArray1.length()) {
                                val jsonObject1 = jsonArray1.getString(m)
                                val personItemObject = GroupNotificationItemObject()
                                personItemObject.text = jsonObject1
                                personItemObject.no = m + 1
                                dataListNoti.add(personItemObject)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ProjectUtil.customToast(activity, e.toString())
                        }
                    }

                    2 -> {
                        try {
                            val jsonArray1 = JSONArray(json1.getString("images"))
                            for (m in 0 until jsonArray1.length()) {
                                val jsonObject1 = jsonArray1.getJSONObject(m)
                                val gson = Gson()
                                dataListImage.add(
                                    gson.fromJson(
                                        jsonObject1.toString(),
                                        ImageItemObject::class.java
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ProjectUtil.customToast(activity, e.toString())
                        }
                    }
                }
            }
            text_notification!!.text = "Notification (" + dataListNoti.size + ")"
            text_photo!!.text = "Photo (" + dataListImage.size + ")"
            text_member!!.text = "Member (" + dataList.size + ")"
            recyclerView_noti!!.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recyclerView_noti!!.setHasFixedSize(false)
            recyclerView_noti!!.isNestedScrollingEnabled = true
            notiAdapter = GroupNotificationAdapter(activity, dataListNoti, this)
            recyclerView_noti!!.adapter = notiAdapter
            val gridLayoutManager = GridLayoutManager(activity, 4)
            gridLayoutManager.orientation =
                LinearLayoutManager.VERTICAL // set Horizontal Orientation
            recyclerView_image!!.setLayoutManager(gridLayoutManager)
            imageAdapter = ImageListAdapter(requireActivity(), dataListImage)
            recyclerView_image!!.adapter = imageAdapter
            recyclerView_member!!.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recyclerView_member!!.setHasFixedSize(false)
            recyclerView_member!!.isNestedScrollingEnabled = true
            categoryAdapter = GroupMemberAdapter(activity, dataList, this)
            recyclerView_member!!.adapter = categoryAdapter

        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.customToast(activity, e.toString())
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    class ImageListAdapter(
        private val mContext: Context,
        private val allParticipants: java.util.ArrayList<ImageItemObject>?,
    ) :
        RecyclerView.Adapter<ImageListAdapter.ComplaintHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ComplaintHolder {
            return ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_group_image_list, parent, false)
            )
        }

        override fun onBindViewHolder(
            holder: ComplaintHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {
            val getData = allParticipants!![position]
            Picasso.get().load(getData.path)!!.into(holder.image_main)
            holder.parentLayout.setOnClickListener(View.OnClickListener {
                val result = Intent(
                    mContext,
                    ImageViewerActivity::class.java
                )
                result.putExtra(ImageViewerActivity.EXTRA_PATH, getData.path)
                mContext.startActivity(result)
            })
        }

        override fun getItemCount(): Int {
            return allParticipants?.size ?: 0
        }

        inner class ComplaintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var image_main: ImageView = itemView.findViewById(R.id.image_main)
            var parentLayout: CardView = itemView.findViewById(R.id.parentLayout)
        }
    }

    private val allEntertainmentData1: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = GroupListRewuest()
                data.action = "groupDetail"
                data.groupID = gId
                data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                val gson = Gson()
                val jsonString = gson.toJson(data)
                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost(RetrofitClient.PRODUCTION_URL + "group")
                        var holder = JSONObject(jsonString)
                        var se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        post.setHeader(
                            "lat",
                            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "lng",
                            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "userId",
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        )
                        var response = httpclient.execute(post)
                        var out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        var responseString = out.toString()
                        activity?.runOnUiThread(Runnable { reresh(responseString); })
                        out.close()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private val deleteGroupData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = DeleteGroupList()
                data.action = type
                data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                data.groupID = gId
                val gson = Gson()
                val jsonString = gson.toJson(data)
                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost(RetrofitClient.PRODUCTION_URL + "group")
                        var holder = JSONObject(jsonString)
                        var se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        post.setHeader(
                            "lat",
                            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "lng",
                            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "userId",
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        )
                        val response = httpclient.execute(post)
                        val out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        val responseString = out.toString()
                        requireActivity().runOnUiThread(Runnable {
                            val json = JSONObject(responseString)
                            if (json.getString("status").equals("success")) {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                                (activity as MainActivity?)!!.ll_toolbar_all.isVisible = true
                                activity?.onBackPressed()
                            } else {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                            }
                        })
                        out.close()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        //switching fragment
        fragment!!.arguments = bundle
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.commit()
        return false
    }

    override fun onClickEvent(postion: Int) {
        TODO("Not yet implemented")
    }

    override fun onCallEvent(postion: Int) {

    }

    override fun onSiteUrl(postion: Int) {
        TODO("Not yet implemented")
    }

    override fun onImageClick(postion: Int) {
        TODO("Not yet implemented")
    }

    override fun onAddressClick(postion: Int) {
        TODO("Not yet implemented")
    }

    override fun onMoreInfo(postion: Int) {
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar_home!!.setTitle(title)
        toolbar_home!!.setTitleTextColor(resources.getColor(R.color.black))
        toolbar_home!!.setNavigationIcon(R.drawable.ic_backarrow_sub_menu) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_home!!.setNavigationOnClickListener {
            // do something when click navigation
//            toolbar_home!!.isVisible = false
            (activity as MainActivity?)!!.ll_toolbar_all.isVisible = true
            activity!!.onBackPressed()
        }
//        toolbar_home!!.inflateMenu(R.menu.menu_permissions)
//        toolbar_home!!.setOnMenuItemClickListener {
//            when (it.itemId) {
////                R.id.view_add -> {
////                    // do something
////                    Toast.makeText(requireActivity(), "Click Add", Toast.LENGTH_SHORT)
////                        .show()
////                    true
////                }
////
////                R.id.view_delete -> {
////                    // do something
////                    true
////                }
//
//                R.id.view_permissions -> {
//                    // do something
////                    Toast.makeText(requireActivity(), "Click permissions", Toast.LENGTH_SHORT)
////                        .show()
////                    showDialogLocation(requireActivity(), "Click permissions")
//                    true
//                }
//
//                else -> {
//                    super.onOptionsItemSelected(it)
//                }
//            }
//        }
    }


}