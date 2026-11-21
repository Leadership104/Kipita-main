package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.GroupListRewuest
import com.mytum.model.GroupListSequencePojo
import com.mytum.model.GroupsItemObject
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class GroupListFragment : Fragment() {
    var customDialog: Dialog? = null
    var dataList: ArrayList<GroupsItemObject> = ArrayList()
    var refreshLayout: SwipeRefreshLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    private var dogImg: ImageView? = null
    var text_press: TextView? = null
    var parentLayout: CardView? = null
    var animation_view: RelativeLayout? = null
    var categoryAdapter: GroupListAdapter? = null
    var btnInvites: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_group_list, container, false)
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titel)
        (activity as MainActivity).showMainMenu()
        val toolbar =
            (activity as MainActivity?)!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        parentLayout = view.findViewById(R.id.parentLayout)
        animation_view = view.findViewById(R.id.animation_view)
        dogImg = view.findViewById(R.id.dog_img)
        customDialog = Dialog(requireActivity())
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        text_press = view.findViewById(R.id.text_press)
        val textAdd = view.findViewById<TextView>(R.id.text_add)
        textAdd.setOnClickListener {
            var bundles = Bundle()
            bundles.putString("list", "")
            loadFragmentInner(CreateGroupFragment(), bundles)
        }
        btnInvites = view.findViewById<Button>(R.id.btn_invites)
        btnInvites?.setOnClickListener {
            var bundles = Bundle()
            bundles.putString("list", "")
            loadFragmentInner(PendingInvitesFragment(), bundles)
        }
        dataList.clear()
        refreshLayout!!.setOnRefreshListener(OnRefreshListener {
            shimmerFramlayout!!.visibility = View.VISIBLE
            refreshLayout!!.visibility = View.GONE
            dataList.clear()
            dataList.removeAll(dataList.toSet())
            allEntertainmentData1
        })
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
                        .toString() == "112233"
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
        return view
    }

    override fun onResume() {
        super.onResume()
        ProjectUtil.dismissDialog(customDialog)
        allEntertainmentData1
    }

    fun reresh(data: String) {
        dataList.removeAll(dataList.toSet())
        dataList.clear()
        try {
            val json = JSONObject(data)
            val json1 = JSONObject(json.getString("data"))
            val jsonArray = JSONArray(json1.getString("groups"))
            btnInvites?.text = "PENDING INVITES (" + json1.getString("invites") + ")"
            for (i in 0 until jsonArray.length()) {
                val jsonObject1 = jsonArray.getJSONObject(i)
                val gson = Gson()
                dataList.add(
                    gson.fromJson(
                        jsonObject1.toString(),
                        GroupsItemObject::class.java
                    )
                )
            }
            shimmerFramlayout!!.visibility = View.GONE
            animation_view!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
            refreshLayout!!.visibility = View.VISIBLE
            recyclerView!!.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            categoryAdapter = GroupListAdapter(requireActivity(), dataList)
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
                val data = GroupListRewuest()
                data.action = "mygroups"
                data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
//                data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext);
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

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        fragment!!.arguments = bundle
        if (fragment != null) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }

    fun changeOrderAndSubmitNewOrder() {
        try {
//            ProjectUtil.showDialog(customDialog, "Please wait...")
            var url = RetrofitClient.PRODUCTION_URL + "group"
            Thread {
                try {
                    val userId =
                        ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                    var requestPojo = GroupListSequencePojo(
                        userId = userId,
                        action = "mygroups",
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

    class GroupListAdapter(
        private val mContext: Context,
        private val allParticipants: java.util.ArrayList<GroupsItemObject>?,
    ) :
        RecyclerView.Adapter<GroupListAdapter.ComplaintHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ComplaintHolder {
            return ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_group_list, parent, false)
            )
        }

        override fun onBindViewHolder(
            holder: ComplaintHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {
            val getData = allParticipants!![position]
            holder.titel.text = getData.name
            holder.date.text = getData.createdDate
            holder.text_member.text = getData.totalMembers
            holder.textNoti.text = getData.totalMessages
            holder.parentLayout.setOnClickListener(View.OnClickListener {
                (mContext as MainActivity?)!!.ll_toolbar_all.isVisible = false
                val bundle = Bundle()
                bundle.putString("title", getData.name)
                bundle.putString("id", getData.id)
                bundle.putBoolean("admin", getData.admin)
                val advisoryDetailsFragment = GroupDetailsFragment()
                advisoryDetailsFragment.arguments = bundle
                val transaction: FragmentTransaction =
                    mContext.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, advisoryDetailsFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            })
        }

        override fun getItemCount(): Int {
            return allParticipants?.size ?: 0
        }

        inner class ComplaintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var titel: TextView = itemView.findViewById(R.id.titel)
            var text_member: TextView = itemView.findViewById(R.id.text_member)
            var textNoti: TextView = itemView.findViewById(R.id.textNoti)
            var date: TextView = itemView.findViewById(R.id.date)
            var parentLayout: CardView = itemView.findViewById(R.id.parentLayout)
        }
    }

}