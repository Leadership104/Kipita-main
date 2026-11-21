package com.mytum.fragment.AdviserySubFragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
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
import com.mytum.R
import com.mytum.adapter.AdvisryMainAdapter
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.AdvisaryRewuest
import com.mytum.model.AdvisoryUpdateServiceAndSequencePojo
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.NewAdvisaryResponse.AdvisaryListResponse
import com.mytum.model.NewAdvisaryResponse.NewAdvisaryResponse
import com.mytum.model.Section
import com.mytum.model.places.Center
import com.mytum.model.places.Circle
import com.mytum.model.places.LocationRestriction
import com.mytum.model.places.PlaceCategorySearchPojo
import com.mytum.model.places.PlacesTextSearchPojo
import com.mytum.model.places.RestaurantPojo
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import com.squareup.picasso.Picasso
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class ApplicationsMainFragment : Fragment(), ComplaintClickPressed {
    private var customDialog: Dialog? = null
    private var dataList: ArrayList<AdvisaryListResponse> = ArrayList()
    private var flagUrl = "";
    private var lastUpdatedString = "";

    //    private var mLayoutManager: LinearLayoutManager? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    private var shimmerFramlayout: ShimmerFrameLayout? = null
    private var recyclerView: RecyclerView? = null
    private var dog_img: ImageView? = null
    private var animation_view: RelativeLayout? = null
    private var categoryAdapter: AdvisryMainAdapter? = null
    private var clickedItemPosition: Int = 0
    var image_setting: ImageView? = null

    //    var lbm = LocalBroadcastManager.getInstance(MyApplication.getInstance().applicationContext)
    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
//                var str = intent.getStringExtra("key")
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
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.application_mian_fragment, container, false)
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titel)
//        textView.text = "Advisory"
        //        textView.setText("Galaxy");
        (activity as MainActivity).showMainMenu()
        val toolbar =
            (activity as MainActivity?)!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
//        toolbar.setBackgroundColor(resources.getColor(R.color.yellow_bottom))
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        animation_view = view.findViewById(R.id.animation_view)
        dog_img = view.findViewById(R.id.dog_img)
        customDialog = Dialog(requireActivity())
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        categoryAdapter = AdvisryMainAdapter(activity, dataList, this@ApplicationsMainFragment)
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
            var gson = Gson()
            var data: NewAdvisaryResponse = gson.fromJson(data, NewAdvisaryResponse::class.java)
            if (data.data.section.size <= 0 && data.data.general == null) {
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            ProjectUtil.customToast(activity, e.toString())
            return
        }

        var gson = Gson()
        var data: NewAdvisaryResponse = gson.fromJson(data, NewAdvisaryResponse::class.java)

        try {
            if (data.data.section.size > 0) {
                var bundle = Bundle()
                bundle.putString("fragmentLayoutName", "")
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE

                dataList.addAll(data.data.section)
//                var data11 = AdvisaryListResponse()
//                data11.type = "112233"
//                dataList.add(data11)

                recyclerView!!.layoutManager = GridLayoutManager(activity, 2)
                recyclerView!!.adapter = categoryAdapter;
                recyclerView!!.visibility = View.VISIBLE
                animation_view!!.visibility = View.GONE

                shimmerFramlayout!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
                if (refreshLayout!!.isRefreshing) {
                    refreshLayout!!.isRefreshing = false
                }

                flagUrl = data.data.general.image
                lastUpdatedString = data.data.general.lastupdated

                dog_img?.scaleType = ImageView.ScaleType.FIT_XY
                Picasso.get().load(data.data.general.image).into(dog_img)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    override fun onClickEvent(postion: Int) {
        var data = dataList.get(postion);
        if (data.type.equals("overall")) {
            loadFragmentInner(ApplicationsFragment(), null);
        } else if (data.type.equals("112233")) {
//            var bundles = Bundle()
//            bundles.putSerializable("list", dataList);
//            loadFragmentInner(AdvisaryCheckBoxFragment(),bundles);
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
        } else {
            clickedItemPosition = postion
            if (data.queryType.equals("category")) {
                getPlacesCategoryData
            } else {
                getPlacesTextSearchData
            }
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
                data.action = "GetMyList";
                data.zipCode =
                    ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                data.timeZone = ProjectUtil.getCurrentTimeZone();
                data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext);
                data.country =
                    ProjectUtil.getChangedLocationCountryCode(MyApplication.getInstance().applicationContext)

                var gson = Gson();
                var jsonString = gson.toJson(data);
                Log.d("aaaaaaa advisory req", jsonString)


                Thread {
                    try {
                        val httpclient: HttpClient = DefaultHttpClient()
                        val post = HttpPost(RetrofitClient.PRODUCTION_URL + "/advisorySections")
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
                        sectionList.add(Section(order = index, type = temp.type))
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

    private val getPlacesTextSearchData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")

                var includedTypes = ArrayList<String>()

                val center = Center(
                    ProjectUtil.getLotitude(activity as MainActivity).toDouble(),
                    ProjectUtil.getLongitude(activity as MainActivity).toDouble()
                )
//                val center = Center(37.7937, -122.3965)
                val queryText = dataList.get(clickedItemPosition).searchParam.replace("{{cityName}}", ProjectUtil.getCityName(ProjectUtil.getLotitude(activity as MainActivity).toDouble(),
                    ProjectUtil.getLongitude(activity as MainActivity).toDouble()))
                val circle = Circle(center, dataList.get(clickedItemPosition).radius)
                var locationRestriction = LocationRestriction(circle)
//                var data = PlacesTextSearchPojo(includedTypes, dataList.get(clickedItemPosition).type, ProjectUtil.PLACES_SEARCH_MAX_RESULT, ProjectUtil.PLACES_SEARCH_SORT_BY, locationRestriction);
                var data = PlacesTextSearchPojo(
                    dataList.get(clickedItemPosition).type,
                    queryText,
                    dataList.get(clickedItemPosition).maxResults,
                    dataList.get(clickedItemPosition).rankPreference,
                    locationRestriction
                );
                var gson = Gson();
                var jsonString = gson.toJson(data);

                Log.d("text search reques", jsonString)

                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost("https://places.googleapis.com/v1/places:searchText")
                        var holder = JSONObject(jsonString)
                        var se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        post.setHeader("lat", ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext))
                        post.setHeader("lng", ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext))
                        post.setHeader("userId", ProjectUtil.getUserId(MyApplication.getInstance().applicationContext))
                        post.setHeader("X-Goog-Api-Key", "AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4")
                        post.setHeader("X-Goog-FieldMask", "*")
                        var response = httpclient.execute(post)
                        var out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        var responseString = out.toString()
                        activity?.runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            handlePlacesResponse(responseString);
                        })
                        out.close()
                    } catch (e: java.lang.Exception) {
                        ProjectUtil.dismissDialog(customDialog)
                        e.printStackTrace()
                    }
                }.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private val getPlacesCategoryData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var includedTypes = ArrayList<String>()
                includedTypes.add(dataList.get(clickedItemPosition).type);
                val center = Center(
                    ProjectUtil.getLotitude(activity as MainActivity).toDouble(),
                    ProjectUtil.getLongitude(activity as MainActivity).toDouble()
                )
//                val center = Center(37.7937, -122.3965)
                val circle = Circle(center, dataList[clickedItemPosition].radius)
                val locationRestriction = LocationRestriction(circle)
                val data = PlaceCategorySearchPojo(
                    includedTypes,
                    dataList.get(clickedItemPosition).maxResults,
                    dataList.get(clickedItemPosition).rankPreference,
                    locationRestriction
                );

                val gson = Gson();
                val jsonString = gson.toJson(data);
                Log.d("aaaaa category reques", jsonString)
                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost("https://places.googleapis.com/v1/places:searchNearby")
                        var holder = JSONObject(jsonString)
                        var se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        post.setHeader("lat", ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext))
                        post.setHeader("lng", ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext))
                        post.setHeader("userId", ProjectUtil.getUserId(MyApplication.getInstance().applicationContext))
                        post.setHeader("X-Goog-Api-Key", "AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4")
                        post.setHeader("X-Goog-FieldMask", "*")
                        var response = httpclient.execute(post)
                        var out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        var responseString = out.toString()
                        activity?.runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            handlePlacesResponse(responseString);
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

    private fun handlePlacesResponse(responseData: String) {

        try {
            val gson = Gson()
            val data: RestaurantPojo =
                gson.fromJson(responseData, RestaurantPojo::class.java)
            if (data.places.size <= 0) {
                //show dialog for no result
                showAlertDialogButtonClicked()
            } else {

                val bundles = Bundle()
                bundles.putString("list", dataList[clickedItemPosition].title);
                bundles.putString("iconUrl", dataList[clickedItemPosition].icon);
                bundles.putString("flagIconUrl", flagUrl);
                bundles.putString("lastUpdated", lastUpdatedString);
                bundles.putString("placesList", responseData)
                bundles.putString("searchText", dataList[clickedItemPosition].type)
                bundles.putInt("showOpenClosed", dataList.get(clickedItemPosition).showOpenClosed)
                bundles.putParcelable("clickedItemData", dataList.get(clickedItemPosition))
                loadFragmentInner(SearchKeyWordFragment(), bundles);
            }

        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.customToast(activity, e.toString())
        }
    }

    fun showAlertDialogButtonClicked() {
        // Create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.alert_dialog, null)
        val title = customLayout.findViewById<TextView>(R.id.dialogTitle)
        val okButton = customLayout.findViewById<Button>(R.id.okButton)
        title.text = "No result found for given search criteria."
        builder.setView(customLayout)

//        okButton.setOnClickListener(View.OnClickListener {
//            dialog.dismiss()
//        })

        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
    }

}