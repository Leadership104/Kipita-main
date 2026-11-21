package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.adapter.GroupListAdapter
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.DeleteGroupList
import com.mytum.model.GroupListSequencePojo
import com.mytum.model.PendingInvitesGroupItem
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class PendingInvitesFragment : Fragment() {
    var customDialog: Dialog? = null
    var dataList: ArrayList<PendingInvitesGroupItem> = ArrayList()
    var refreshLayout: SwipeRefreshLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    var text_press: TextView? = null
    var text_invites: TextView? = null
    private lateinit var dog_img: ImageView
    var animation_view: RelativeLayout? = null
    var categoryAdapter: PendingInvitesAdapter? = null
    var gId: String? = null
    var gType: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_pending_invites, container, false)
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titel)
        (activity as MainActivity).showMainMenu()
        val toolbar =
            (activity as MainActivity?)!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (activity as MainActivity).secondHeader.visibility = View.GONE
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        animation_view = view.findViewById(R.id.animation_view)
        text_invites = view.findViewById(R.id.text_invites)
        customDialog = Dialog(requireActivity())
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        text_press = view.findViewById(R.id.text_press)
        dog_img = view.findViewById(R.id.dog_img)
        dog_img.setOnClickListener{
            activity?.onBackPressed();
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        allEntertainmentData1
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled() = true
            override fun isItemViewSwipeEnabled() = false
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags =
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags =
                    if (isItemViewSwipeEnabled) ItemTouchHelper.START or ItemTouchHelper.END else 0
                return if ((viewHolder as GroupListAdapter.ComplaintHolder).titel.getTag(R.string.holdertag)
                        .toString().equals("112233")
                ) {
                    0
                } else {
                    makeMovementFlags(dragFlags, swipeFlags)
                }
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if (viewHolder.itemViewType != target.itemViewType)
                    return false
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                val item = dataList.removeAt(fromPosition)
                dataList.add(toPosition, item)
                recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
                changeOrderAndSubmitNewOrder()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                dataList.removeAt(position)
                recyclerView!!.adapter!!.notifyItemRemoved(position)
            }

        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun reresh(data: String) {
        recyclerView!!.removeAllViews()
        dataList.removeAll(dataList.toSet())
        dataList.clear()
        try {
            val json = JSONObject(data)
            text_invites?.text = "Invites " + json.getString("message")
            val json1 = JSONObject(json.getString("data"))
            val jsonArray = JSONArray(json1.getString("groups"))
            for (i in 0 until jsonArray.length()) {
                val jsonObject1 = jsonArray.getJSONObject(i)
                val gson = Gson()
                dataList.add(
                    gson.fromJson(
                        jsonObject1.toString(),
                        PendingInvitesGroupItem::class.java
                    )
                )
            }
            shimmerFramlayout!!.visibility = View.GONE
            animation_view!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
            refreshLayout!!.visibility = View.VISIBLE
            recyclerView!!.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            categoryAdapter =
                PendingInvitesAdapter(requireActivity(), dataList, customDialog) { data ->
                    // do something with hero item when it's clicked
                    gId = data.id.toString()
                    gType = data.name.toString()
                    joinDeclineGroupData
                }
            recyclerView!!.adapter = categoryAdapter
            refreshLayout!!.isEnabled = false
            refreshLayout!!.isRefreshing = false
        } catch (e: Exception) {
            ProjectUtil.customToast(activity, e.toString())
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    private val allEntertainmentData1: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = DeleteGroupList()
                data.action = "myinvites"
                data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
//                data.groupID = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)

                var gson = Gson()
                var jsonString = gson.toJson(data)
                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost(RetrofitClient.PRODUCTION_URL + "group")
                        var holder = JSONObject(jsonString)
                        var se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        post.setHeader("lat", ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext))
                        post.setHeader("lng", ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext))
                        post.setHeader("userId", ProjectUtil.getUserId(MyApplication.getInstance().applicationContext))
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

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        fragment!!.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
        return false
    }

    fun changeOrderAndSubmitNewOrder() {
        try {
            ProjectUtil.showDialog(customDialog, "Please wait...")
            var url = RetrofitClient.PRODUCTION_URL + "group"
            Thread {
                try {
                    var requestPojo = GroupListSequencePojo(
                        userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext),
                        action = "myinvites",
                    )
                    var gson = Gson()
                    var jsonString = gson.toJson(requestPojo)
                    val httpclient: HttpClient = DefaultHttpClient()
                    val post = HttpPost(url)
                    val holder = JSONObject(jsonString)
                    val se = StringEntity(holder.toString())
                    post.entity = se
                    post.setHeader("Accept", "application/json")
                    post.setHeader("Content-type", "application/json")
                    post.setHeader("lat", ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext))
                    post.setHeader("lng", ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext))
                    post.setHeader("userId", ProjectUtil.getUserId(MyApplication.getInstance().applicationContext))
                    val response = httpclient.execute(post)
                    val out = ByteArrayOutputStream()
                    response.entity.writeTo(out)
                    val responseString = out.toString()
                    activity?.runOnUiThread(Runnable {
                        ProjectUtil.dismissDialog(customDialog)
                        var gson = Gson()
                        var getAllData: CheckBoxResponse =
                            gson.fromJson(responseString, CheckBoxResponse::class.java)
                        if (getAllData.status.equals("success")) {
                            ProjectUtil.customToast(activity, "Services order changed successfully")
                        } else {
                            ProjectUtil.customToast(
                                activity,
                                "Error while changing the order of services."
                            )
                        }

                    })

                    out.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    ProjectUtil.dismissDialog(customDialog)
                }
            }.start()


        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.dismissDialog(customDialog)
        }

    }

    private val joinDeclineGroupData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = DeleteGroupList()
                data.action = gType
                data.userId =
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext);
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
                        post.setHeader("lat", ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext))
                        post.setHeader("lng", ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext))
                        post.setHeader("userId", ProjectUtil.getUserId(MyApplication.getInstance().applicationContext))
                        val response = httpclient.execute(post)
                        val out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        val responseString = out.toString()
                        activity?.runOnUiThread(Runnable {
                            val json = JSONObject(responseString)
                            if (json.getString("status").equals("success")) {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                                val bundles = Bundle()
                                bundles.putString("list", "")
                                loadFragmentInner(GroupListFragment(), bundles)
                            } else {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                            }
                            ProjectUtil.dismissDialog(customDialog)
                        })
                        Handler(Looper.getMainLooper()).post {


                        }

                        out.close()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        ProjectUtil.dismissDialog(customDialog)
                    }
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
                ProjectUtil.dismissDialog(customDialog)
            }
            ProjectUtil.dismissDialog(customDialog)
        }

    class PendingInvitesAdapter(
        private val mContext: Context,
        private val allParticipants: java.util.ArrayList<PendingInvitesGroupItem>?,
        private val customDialog: Dialog?,
        val itemClickListener: (PendingInvitesGroupItem) -> Unit

    ) :
        RecyclerView.Adapter<PendingInvitesAdapter.ComplaintHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintHolder {
            return ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_pending_invites, parent, false)
            )
        }

        override fun onBindViewHolder(
            holder: ComplaintHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {
            val getData = allParticipants!![position]
            holder.textName.text = getData.name
            holder.textInvited.text = getData.invitedText
            holder.textDate.text = getData.createdDate
            holder.llJoin.setOnClickListener {
                getData.name = "joinGroup"
                itemClickListener(getData)
            }
            holder.llDec.setOnClickListener {
                getData.name = "leaveGroup"
                itemClickListener(getData)
            }
        }

        override fun getItemCount(): Int {
            return allParticipants?.size ?: 0
        }

        inner class ComplaintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var text_title: TextView = itemView.findViewById(R.id.text_title)
            var textName: TextView = itemView.findViewById(R.id.textName)
            var textInvited: TextView = itemView.findViewById(R.id.textInvited)
            var textDate: TextView = itemView.findViewById(R.id.textDate)
            var parentLayout: CardView = itemView.findViewById(R.id.parentLayout)
            var llJoin: LinearLayout = itemView.findViewById(R.id.llJoin)
            var llDec: LinearLayout = itemView.findViewById(R.id.llDec)
        }
    }

}