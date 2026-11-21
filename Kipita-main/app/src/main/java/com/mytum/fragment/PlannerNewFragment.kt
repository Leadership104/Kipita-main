package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import com.mytum.R
import com.mytum.adapter.PlannerAdapter
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.listners.ItemMoveCallbackPlanner
import com.mytum.model.Application.ApplicationResponse
import com.mytum.model.Application.Category
import com.mytum.model.Application.CategoryList
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class PlannerNewFragment : Fragment(), ComplaintClickPressed {
    var customDialog: Dialog? = null
    var dataList: ArrayList<Category> = ArrayList()
    var refreshLayout: SwipeRefreshLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    var dog_img: ImageView? = null
    var parentLayout: CardView? = null
    var animation_view: RelativeLayout? = null
    var llTop: LinearLayout? = null
    var categoryAdapter: PlannerAdapter? = null
    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                var str = intent.getStringExtra("key")
                reresh(str!!)
            } else {
                ProjectUtil.dismissDialog(customDialog)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.application_fragment, container, false)
//        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titel)
//        textView.text = "Advisory"
        //        textView.setText("Galaxy");
        val toolbar =
            (activity as MainActivity?)!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
//        toolbar.setBackgroundColor(resources.getColor(R.color.green_bottom))
        llTop = view.findViewById(R.id.llTop)
        llTop!!.isVisible = false
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        parentLayout = view.findViewById(R.id.parentLayout)
        animation_view = view.findViewById(R.id.animation_view)
        dog_img = view.findViewById(R.id.dog_img)
        customDialog = Dialog(requireActivity())
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        categoryAdapter = PlannerAdapter(activity, dataList, this@PlannerNewFragment)
        val callback: ItemTouchHelper.Callback = ItemMoveCallbackPlanner(categoryAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
        val layoutManager = GridLayoutManager(activity, 2)
        recyclerView!!.layoutManager = layoutManager
//        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.getChildAt(View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE)
        recyclerView!!.adapter = categoryAdapter
        refreshLayout!!.isEnabled = false
        refreshLayout!!.isRefreshing = false
        refreshLayout!!.setOnRefreshListener(OnRefreshListener {
            shimmerFramlayout!!.visibility = View.VISIBLE
            refreshLayout!!.visibility = View.GONE
            dataList.clear()
            dataList.removeAll(dataList.toSet())
            allEntertainmentData
        })
        dataList.clear()
        allEntertainmentData
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null) {
//            lbm.unregisterReceiver(receiver)
        }
    }

    override fun onPause() {
        super.onPause()
        if (receiver != null) {
//            lbm.unregisterReceiver(receiver)
        }
    }


    fun reresh(data: String) {
        ProjectUtil.showDialog(customDialog, "Please wait...")
        dataList.removeAll(dataList)
        dataList.clear()
        try {
//            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+ data)
            var gson = Gson()
            var data: ApplicationResponse = gson.fromJson(data, ApplicationResponse::class.java)
            if (data.data.advisory_tab.size <= 0 && data.data.advisory_main == null) {
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                parentLayout!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            ProjectUtil.customToast(activity, e.toString())
            return
        }

        var gson = Gson()
        var data: ApplicationResponse = gson.fromJson(data, ApplicationResponse::class.java)

        try {
            if (data.data.advisory_tab.size > 0) {
                var bundle = Bundle()
                bundle.putString("fragmentLayoutName", "")
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
                parentLayout!!.visibility = View.VISIBLE

                dataList.addAll(
                    listOf(
                        Category(
                            data.data.advisory_main.title,
                            listOf(CategoryList(data.data.advisory_main.description, null))
                        )
                    )
                )

                for (sexData in data.data.advisory_tab!!) {
                    dataList.addAll(
                        listOf(
                            Category(
                                sexData.title,
                                listOf(
                                    CategoryList(
                                        sexData.content,
                                        sexData.location
                                    )
                                )
                            )
                        )
                    )
                }
//                mLayoutManager = LinearLayoutManager(activity )
//                categoryAdapter!!.setExpandCollapseListener(object : ExpandCollapseListener {
//                    override fun onListItemExpanded(position: Int) {
//                    }
//
//                    override fun onListItemCollapsed(position: Int) {
//                    }
//                })

//                recyclerView!!.layoutManager = mLayoutManager
                val layoutManager = GridLayoutManager(activity, 2)
                recyclerView!!.layoutManager = layoutManager
//                recyclerView!!.setHasFixedSize(true)
                recyclerView!!.getChildAt(View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE)
                recyclerView!!.adapter = categoryAdapter
                recyclerView!!.visibility = View.VISIBLE
                animation_view!!.visibility = View.GONE

                shimmerFramlayout!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
                if (refreshLayout!!.isRefreshing) {
                    refreshLayout!!.isRefreshing = false
                }

                dog_img?.scaleType = ImageView.ScaleType.FIT_XY
                Picasso.get().load(data.data.advisory_main.image).into(dog_img)

//                categoryAdapter!!.setExpandableParentItemList(dataList)
                //              categoryAdapter!!.onParentListItemExpanded(0)

            } else if (data.data.advisory_main != null) {
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
                parentLayout!!.visibility = View.VISIBLE

                dataList.addAll(
                    listOf(
                        Category(
                            data.data.advisory_main.title,
                            listOf(CategoryList(data.data.advisory_main.description, null))
                        )
                    )
                )

                for (sexData in data.data.advisory_tab!!) {
                    dataList.addAll(
                        listOf(
                            Category(
                                sexData.title,
                                listOf(CategoryList(sexData.content, sexData.location))
                            )
                        )
                    )
                }
//                mLayoutManager = LinearLayoutManager(activity)
//                categoryAdapter!!.setExpandCollapseListener(object : ExpandCollapseListener {
//                    override fun onListItemExpanded(position: Int) {
//                    }
//
//                    override fun onListItemCollapsed(position: Int) {
//                    }
//
//                })

//                recyclerView!!.layoutManager = mLayoutManager
                val layoutManager = GridLayoutManager(activity, 2)
                recyclerView!!.layoutManager = layoutManager
//                recyclerView!!.setHasFixedSize(true)
                recyclerView!!.getChildAt(View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE)
                recyclerView!!.adapter = categoryAdapter
                recyclerView!!.visibility = View.VISIBLE
                animation_view!!.visibility = View.GONE

                shimmerFramlayout!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
                if (refreshLayout!!.isRefreshing) {
                    refreshLayout!!.isRefreshing = false
                }
                Picasso.get().load(data.data.advisory_main.image).into(dog_img)
//                categoryAdapter!!.setExpandableParentItemList(dataList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    override fun onClickEvent(postion: Int) {}

    @SuppressLint("SetTextI18n")
    override fun onCallEvent(postion: Int) {
        (activity as MainActivity?)!!.textTitle.text = "Back to Travel"
        (activity as MainActivity?)!!.rlToolbarMain.isVisible = false
        (activity as MainActivity?)!!.llToolbarBack.isVisible = true
        val bundle = Bundle()
        bundle.putString("data", Int.toString())
        val plannerDetailsFragment = PlannerDetailsFragment()
        plannerDetailsFragment.arguments = bundle
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, plannerDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onSiteUrl(postion: Int) {
    }

    override fun onImageClick(postion: Int) {
    }

    override fun onAddressClick(postion: Int) {
    }

    private val allEntertainmentData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var client = OkHttpClient().newBuilder()
                    .build()
                var mediaType: MediaType = "application/json".toMediaTypeOrNull()!!
                var body = RequestBody.create(
                    mediaType,
                    "{\n    \"lat\":" + ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext) + ",\n    \"lng\":" + ProjectUtil.getLongitude(
                        MyApplication.getInstance().applicationContext
                    ) +
                            "\n,    \"currentLat\":" + ProjectUtil.getCurrentLotitude(MyApplication.getInstance().applicationContext) + ",\n    \"currentLng\":" + ProjectUtil.getCurrentLongitude(
                        MyApplication.getInstance().applicationContext
                    ) + ",\n    \"timeZone\":" + "\"" + ProjectUtil.getCurrentTimeZone() + "\"" +
                            "\n , \"userId\":" + ProjectUtil.getUserId(MyApplication.getInstance().applicationContext) + "\n }"
                )

                val jsonString =
                    "{\n    \"lat\":" + ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext) + ",\n    \"lng\":" + ProjectUtil.getLongitude(
                        MyApplication.getInstance().applicationContext
                    ) +
                            "\n,    \"currentLat\":" + ProjectUtil.getCurrentLotitude(MyApplication.getInstance().applicationContext) + ",\n    \"currentLng\":" + ProjectUtil.getCurrentLongitude(
                        MyApplication.getInstance().applicationContext
                    ) + ",\n    \"timeZone\":" + "\"" + ProjectUtil.getCurrentTimeZone() + "\"" +
                            "\n , \"userId\":" + ProjectUtil.getUserId(MyApplication.getInstance().applicationContext) + "\n }"

                Thread {
                    try {
                        val httpclient: HttpClient = DefaultHttpClient()
                        val post = HttpPost(RetrofitClient.PRODUCTION_URL + "restrictions")
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
                        activity?.runOnUiThread(Runnable { reresh(responseString); })
                        out.close()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
//                        ProjectUtil.showDialog(customDialog, "Please wait...")
                    }
                }.start()

//
//
//                reresh(temp);
            } catch (e: Exception) {
                e.printStackTrace()
//                ProjectUtil.showDialog(customDialog, "Please wait...")
            }
        }

//    class OkHttpHandler :
//        AsyncTask<Void?, Void?, String>() {
//        var client = OkHttpClient()
//
//        override fun doInBackground(vararg params: Void?): String {
//            try {
//                var client = OkHttpClient().newBuilder()
//                    .build()
//                var mediaType: MediaType = MediaType.parse("application/json")!!
////val tt = "{\n    \"lat\":" + ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext) +
////                ",\n    \"lng\":" + ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)+
////                "\n,    \"currentLat\":" + ProjectUtil.getCurrentLotitude(MyApplication.getInstance().applicationContext) +
////                  ",\n    \"currentLng\":" + ProjectUtil.getCurrentLongitude(MyApplication.getInstance().applicationContext) +
////        ",\n    \"timeZone\":" + "\""+ProjectUtil.getCurrentTimeZone()+ "\"" +
////                  "\n , \"userId\":"+ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)+ "\n }"
//
//                var body = RequestBody.create(
//                    mediaType,
//                    "{\n    \"lat\":" + ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext) + ",\n    \"lng\":" + ProjectUtil.getLongitude(
//                        MyApplication.getInstance().applicationContext
//                    ) +
//                    "\n,    \"currentLat\":" + ProjectUtil.getCurrentLotitude(MyApplication.getInstance().applicationContext) + ",\n    \"currentLng\":" + ProjectUtil.getCurrentLongitude(
//                        MyApplication.getInstance().applicationContext
//                    ) + ",\n    \"timeZone\":" + "\""+ProjectUtil.getCurrentTimeZone()+ "\"" +
//                            "\n , \"userId\":"+ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)+ "\n }"
//                )
//
//
////                var body = RequestBody.create(
////                    mediaType,
////                    "{\n    \"lat\":" + "40.730610" + ",\n    \"lng\":" + "-73.935242" + "\n}"
////                )
//
////                Log.d(
////                    "aaaaaaaa",
////                    "{\n    \"currentLat\":" + ProjectUtil.getCurrentLotitude(MyApplication.getInstance().applicationContext) + ",\n    \"currentLng\":" + ProjectUtil.getCurrentLongitude(
////                        MyApplication.getInstance().applicationContext
////                    ) + "\n}"
////                )
////                Log.d(
////                    "aaaaaaaa",
////                    "{\n    \"lat\":" + ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext) + ",\n    \"lng\":" + ProjectUtil.getLongitude(
////                        MyApplication.getInstance().applicationContext
////                    ) + "\n}"
////                )
//
//                var request: Request = Request.Builder()
//                    .url(RetrofitClient.PRODUCTION_URL+"/restrictions")
//                    .method("POST", body)
//                    .addHeader("Content-Type", "application/json")
//                    .build()
//                var response = client.newCall(request).execute()
//                var temp: String = response.body()!!.string()
//                var intent = Intent("filter_string")
//                intent.putExtra("key", temp)
//                LocalBroadcastManager.getInstance(MyApplication.getInstance().applicationContext)
//                    .sendBroadcast(intent)
//
//                return temp
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            return ""
//        }
//    }
override fun onMoreInfo(postion: Int) {
}
}