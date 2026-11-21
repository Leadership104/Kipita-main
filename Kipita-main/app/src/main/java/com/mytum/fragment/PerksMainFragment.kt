package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.mytum.LoginActivity
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.PlannerWebViewActivity
import com.mytum.R
import com.mytum.adapter.AdvisryMainAdapter
import com.mytum.adapter.PerksMainAdapter
import com.mytum.fragment.AdviserySubFragment.AdvisaryCheckBoxFragment
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.AdvisaryRewuest
import com.mytum.model.AdvisoryUpdateServiceAndSequencePojo
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.PerksListResponse
import com.mytum.model.Section
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class PerksMainFragment : Fragment(), ComplaintClickPressed {
    private var customDialog: Dialog? = null
    private var dataList: ArrayList<PerksListResponse> = ArrayList()
    private var flagUrl = "";
    private var lastUpdatedString = "";

    //    private var mLayoutManager: LinearLayoutManager? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    private var shimmerFramlayout: ShimmerFrameLayout? = null
    private var recyclerView: RecyclerView? = null
    private var dog_img: ImageView? = null
    private var animation_view: RelativeLayout? = null
    private var categoryAdapter: PerksMainAdapter? = null
    private var clickedItemPosition: Int = 0
    var image_setting: ImageView? = null

    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                allEntertainmentData1
            } else {
                ProjectUtil.dismissDialog(customDialog)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.perks_mian_fragment, container, false)
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titel)
        (activity as MainActivity).showMainMenu()
        val toolbar =
            (activity as MainActivity?)!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        animation_view = view.findViewById(R.id.animation_view)
        dog_img = view.findViewById(R.id.dog_img)
        customDialog = Dialog(requireActivity())
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        categoryAdapter = PerksMainAdapter(activity, dataList, this@PerksMainFragment)
        image_setting = view.findViewById(R.id.image_setting)
        image_setting?.setOnClickListener {
            if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                var bundle = Bundle()
                bundle.putParcelableArrayList("list", dataList)
                bundle.putBoolean("isFromPlanner", false)
                startActivity(
                    Intent(
                        activity,
                        AdvisaryCheckBoxFragment::class.java
                    ).putExtra("data", bundle)
                )
            } else {
                val i = Intent(activity, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                (activity as MainActivity).overridePendingTransition(
                    R.anim.in_from_left,
                    R.anim.out_to_right
                )
                (activity as MainActivity).finish()
            }
        }
        recyclerView!!.layoutManager = GridLayoutManager(activity, 2)
        recyclerView!!.adapter = categoryAdapter;
        refreshLayout!!.isEnabled = false
        refreshLayout!!.isRefreshing = false
        refreshLayout!!.setOnRefreshListener(OnRefreshListener {
            shimmerFramlayout!!.visibility = View.VISIBLE
            refreshLayout!!.visibility = View.GONE
            dataList.clear()
            dataList.removeAll(dataList.toSet())
            allEntertainmentData1
        })
        dataList.clear()
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
                return if ((viewHolder as AdvisryMainAdapter.ComplaintHolder).titel.getTag(R.string.holdertag)
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


        return view
    }

    override fun onResume() {
        super.onResume()
        allEntertainmentData1
    }

    fun reresh(data: String) {
        ProjectUtil.showDialog(customDialog, "Please wait...")
        dataList.removeAll(dataList)
        dataList.clear()
        try {
            val jsonObject = JSONObject(data)
            val jsonArray = JSONArray(jsonObject.getString("data"))
            for (i in 0 until jsonArray.length()) {
                val jsonObject1 = jsonArray.getJSONObject(i)
                val gson = Gson()
                dataList.add(
                    gson.fromJson<PerksListResponse>(
                        jsonObject1.toString(),
                        PerksListResponse::class.java
                    )
                )
            }
            var bundle = Bundle()
            bundle.putString("fragmentLayoutName", "")
            shimmerFramlayout!!.visibility = View.GONE
            animation_view!!.visibility = View.VISIBLE
            recyclerView!!.visibility = View.GONE
            refreshLayout!!.visibility = View.VISIBLE
            recyclerView!!.layoutManager = GridLayoutManager(activity, 1)
            recyclerView!!.adapter = categoryAdapter;
            recyclerView!!.visibility = View.VISIBLE
            animation_view!!.visibility = View.GONE
            shimmerFramlayout!!.visibility = View.GONE
            refreshLayout!!.visibility = View.VISIBLE
            if (refreshLayout!!.isRefreshing) {
                refreshLayout!!.isRefreshing = false
            }
        } catch (e: Exception) {
            ProjectUtil.customToast(activity, e.toString())
            return
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    override fun onClickEvent(postion: Int) {
        var data = dataList[postion];
        Log.d("asassaasassaassa ", data.link)
        popupDetails(data.name, data.description, data.link)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    private fun popupDetails(name: String, description: String, link: String) {
        try {
            val inflater =
                requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.popup_perks_details, null)
            val pw = PopupWindow(
                layout,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )

            pw.animationStyle = R.style.popup_window_animation
            pw.setBackgroundDrawable(resources.getDrawable(R.drawable.back_round_top))
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.BOTTOM, 0, 0)
            val titleTextView = layout.findViewById<TextView>(R.id.text_title)
            val messageTextView = layout.findViewById<TextView>(R.id.txt_message)
            val text_cancel = layout.findViewById<TextView>(R.id.text_cancel)
            val text_goTo = layout.findViewById<TextView>(R.id.text_goTo)
            text_goTo.text = "Go to "+name
            titleTextView.text = name
            messageTextView.text = description
            text_cancel.setOnClickListener { v: View? ->
                pw.dismiss()
            }
            text_goTo.setOnClickListener { v: View? ->
                val result = Intent(activity, PlannerWebViewActivity::class.java)
                result.putExtra("URL_PATH", link)
                result.putExtra("URL_TITLE", name)
                startActivity(result)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onCallEvent(postion: Int) {
    }

    override fun onSiteUrl(postion: Int) {
    }

    override fun onImageClick(postion: Int) {
    }

    override fun onAddressClick(postion: Int) {
    }

    private val allEntertainmentData1: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")

                var data = AdvisaryRewuest();
                data.action = "GetAllList";
                var gson = Gson();
                var jsonString = gson.toJson(data);
                Log.d("aaaaaaa advisory req", jsonString)
                Thread {
                    try {
                        val httpclient: HttpClient = DefaultHttpClient()
                        val post = HttpPost(RetrofitClient.PRODUCTION_URL + "/affiliates")
                        val holder = JSONObject(jsonString)
                        val se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        val response = httpclient.execute(post)
                        val out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        val responseString = out.toString()
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
        //switching fragment
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

    override fun onMoreInfo(postion: Int) {
    }

    fun changeOrderAndSubmitNewOrder() {
        try {
            ProjectUtil.showDialog(customDialog, "Please wait...")
            var url = RetrofitClient.PRODUCTION_URL + "advisorySections"
            Thread {
                try {
                    val userId =
                        ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                    val zipCode =
                        ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                    val countryCode =
                        ProjectUtil.getChangedLocationCountryCode(MyApplication.getInstance().applicationContext)
                    var sectionList: ArrayList<Section> = arrayListOf()
                    var index = 0
                    for (temp in dataList) {
                        index += 1;
                        sectionList.add(Section(order = index, type = temp.image))
                    }
                    var requestPojo = AdvisoryUpdateServiceAndSequencePojo(
                        userId = userId,
                        action = "EditMyList",
                        country = countryCode,
                        zipCode = zipCode,
                        timeZone = ProjectUtil.getCurrentTimeZone(),
                        sections = sectionList
                    )

                    var gson = Gson();
                    var jsonString = gson.toJson(requestPojo);
                    Log.d("aaaa change order", jsonString)

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

}