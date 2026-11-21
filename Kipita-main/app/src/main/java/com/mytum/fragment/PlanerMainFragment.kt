package com.mytum.fragment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import com.mytum.LoginActivity
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.PlannerWebViewFrgament
import com.mytum.R
import com.mytum.adapter.PlannerMainAdapter
import com.mytum.fragment.AdviserySubFragment.AdvisaryCheckBoxFragment
import com.mytum.fragment.AdviserySubFragment.PlaneerCheckBoxFragment
import com.mytum.fragment.GallerySubFragment.PrivateFragment
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.AdvisaryRewuest
import com.mytum.model.AdvisoryUpdateServiceAndSequencePojo
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.NewAdvisaryResponse.AdvisaryListResponse
import com.mytum.model.NewPalnerResponse.NewAdvisaryResponse.PlanerAdvisaryResponse
import com.mytum.model.NewPalnerResponse.NewAdvisaryResponse.PlannerListResponse
import com.mytum.model.Section
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import com.squareup.picasso.Picasso
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class PlanerMainFragment : Fragment(), ComplaintClickPressed {
    var customDialog: Dialog? = null
    var dataList: ArrayList<PlannerListResponse> = ArrayList()

    var refreshLayout: SwipeRefreshLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    var dog_img: ImageView? = null
    var text_press: TextView? = null

    //    var parentLayout: CardView? = null
    var animation_view: RelativeLayout? = null
    var categoryAdapter: PlannerMainAdapter? = null
    var image_setting: ImageView? = null

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
        var view = inflater.inflate(R.layout.fragment_travel, container, false)
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titel)
        (activity as MainActivity).showMainMenu()
        val toolbar =
            (activity as MainActivity?)!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
//        parentLayout = view.findViewById(R.id.parentLayout)
        animation_view = view.findViewById(R.id.animation_view)
        dog_img = view.findViewById(R.id.dog_img)
        customDialog = Dialog(requireActivity())
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        text_press = view.findViewById(R.id.text_press)
        image_setting = view.findViewById(R.id.image_setting)
        image_setting?.setOnClickListener {
            if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                var advisoryList: ArrayList<AdvisaryListResponse> =
                    arrayListOf<AdvisaryListResponse>()
                for (temp in dataList) {
                    var adv = AdvisaryListResponse()
                    adv.title = temp.title
                    adv.queryType = temp.queryType
                    adv.type = temp.type
                    adv.icon = ""
                    adv.order = temp.order
                    advisoryList.add(adv)
                }
                var bundle = Bundle()
                bundle.putParcelableArrayList("list", advisoryList)
                bundle.putBoolean("isFromPlanner", true)
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
        val btn_group = view.findViewById<Button>(R.id.btn_group)
        btn_group.setOnClickListener {

//                getActivity().getSupportFragmentManager().beginTransaction().hide(active).show(privateFragment).commit();
//                active = privateFragment;
            if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                var bundles = Bundle()
                bundles.putString("list", "");
                loadFragmentInner(GroupListFragment(), bundles);
            } else {
                val i = Intent(activity, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                requireActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
                requireActivity().finish()
            }

        }

        val btn_online_map = view.findViewById<Button>(R.id.btn_online_map)
        btn_online_map.setOnClickListener {

        }
        categoryAdapter = PlannerMainAdapter(activity, dataList, this@PlanerMainFragment)
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
                if ((viewHolder as PlannerMainAdapter.ComplaintHolder).titel.getTag(R.string.holdertag)
                        .toString().equals("112233")
                ) {
                    return 0
                } else {
                    return makeMovementFlags(dragFlags, swipeFlags)
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
            var data: PlanerAdvisaryResponse =
                gson.fromJson(data, PlanerAdvisaryResponse::class.java)
            if (data.data.planner_tab.size <= 0 && data.data.planner_tab == null) {
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
//                parentLayout!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            ProjectUtil.customToast(activity, e.toString())
            return
        }

        var gson = Gson()
        var data: PlanerAdvisaryResponse = gson.fromJson(data, PlanerAdvisaryResponse::class.java)

        try {
            if (data.data.planner_tab.size > 0) {
                var bundle = Bundle()
                bundle.putString("fragmentLayoutName", "")
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
//                parentLayout!!.visibility = View.VISIBLE

                dataList.addAll(data.data.planner_tab)
//                var data11 = PlannerListResponse()
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

                dog_img?.scaleType = ImageView.ScaleType.FIT_XY
                Picasso.get().load(data.data.planner_main.image).into(dog_img)

            } else if (data.data.planner_tab != null) {
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                refreshLayout!!.visibility = View.VISIBLE
//                parentLayout!!.visibility = View.VISIBLE

                dataList.addAll(data.data.planner_tab)

//                var data11 = PlannerListResponse()
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

                Picasso.get().load(data.data.planner_main.image).into(dog_img)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    override fun onClickEvent(postion: Int) {

        var data = dataList.get(postion);
        if (data.type.equals("overall")) {
//            loadFragmentInner(PlannerNewFragment(), null);
        } else if (data.type.equals("112233")) {
//            var bundles = Bundle()
//            bundles.putSerializable("list", dataList);
//            loadFragmentInner(PlaneerCheckBoxFragment(), bundles);
            if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                var advisoryList: ArrayList<AdvisaryListResponse> =
                    arrayListOf<AdvisaryListResponse>()
                for (temp in dataList) {
                    var adv = AdvisaryListResponse()
                    adv.title = temp.title
                    adv.queryType = temp.queryType
                    adv.type = temp.type
//                    adv.searchParam = temp.searchParam
                    adv.icon = ""
                    adv.order = temp.order
                    advisoryList.add(adv)
                }
                var bundle = Bundle()
                bundle.putParcelableArrayList("list", advisoryList)
                bundle.putBoolean("isFromPlanner", true)
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
            var bundles = Bundle()
            bundles.putString("list", data.url);
            loadFragmentInner(PlannerWebViewFrgament(), bundles);

        }

    }

    override fun onCallEvent(postion: Int) {
        (activity as MainActivity?)!!.textTitle.text = "Back to Advisory"
        (activity as MainActivity?)!!.rlToolbarMain.isVisible = false
        (activity as MainActivity?)!!.llToolbarBack.isVisible = true
        val bundle = Bundle()
        bundle.putString("data", Int.toString())
        val advisoryDetailsFragment = PlannerNewFragment()
        advisoryDetailsFragment.arguments = bundle
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, advisoryDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onSiteUrl(postion: Int) {
    }

    override fun onImageClick(postion: Int) {

        var data = dataList.get(postion);
        if (data.type.equals("overall")) {
            loadFragmentInner(PlannerNewFragment(), null);
        } else if (data.type.equals("112233")) {
            var bundles = Bundle()
            bundles.putSerializable("list", dataList);
            loadFragmentInner(PlaneerCheckBoxFragment(), bundles);
        } else {
            var bundles = Bundle()
            bundles.putString("list", data.url);
            loadFragmentInner(PlannerWebViewFrgament(), bundles);

        }

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
                data.timeZone = ProjectUtil.getCurrentTimeZone()
                data.country =
                    ProjectUtil.getChangedLocationCountryCode(MyApplication.getInstance().applicationContext)
                var gson = Gson();
                var jsonString = gson.toJson(data);


                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost(RetrofitClient.PRODUCTION_URL + "/plannerSections")
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
            var url = RetrofitClient.PRODUCTION_URL + "plannerSections"
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

}