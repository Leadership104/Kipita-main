package com.mytum.fragment.AdviserySubFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.mytum.proectUtil.ProjectUtil
import com.mytum.R
import com.mytum.adapter.AdvisoryDetailsAdapter
import com.mytum.client.connection.YelpFusionApiFactory
import com.mytum.client.models.Business
import com.mytum.client.models.SearchResponse
import com.mytum.finestwebview.FinestWebView
import com.mytum.interfaces.ComplaintClickPressed

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AdvisoryDetailsFragment : Fragment(), ComplaintClickPressed {
    var businesses = ArrayList<Business>()
    var customDialog: Dialog? = null
    var searchData = ""
    private var mLayoutManager: LinearLayoutManager? = null
    var refreshLayoutParent: LinearLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    var animation_view: LinearLayout? = null
    var advisoryDetailsAdapter: AdvisoryDetailsAdapter? = null
    var offset = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_advisory_details, container, false)
        customDialog = Dialog(requireActivity())
        refreshLayoutParent = view.findViewById(R.id.swipeToRefresh_parent)
        animation_view = view.findViewById(R.id.animation_view)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        advisoryDetailsAdapter =
            AdvisoryDetailsAdapter(activity, businesses, this@AdvisoryDetailsFragment)
        recyclerView!!.adapter = advisoryDetailsAdapter
        businesses.clear()
        allEntertainmentData
//        refreshLayout!!.setOnRefreshListener(OnRefreshListener {
//            businesses.clear()
//            offset = 0
//            shimmerFramlayout!!.setVisibility(View.VISIBLE)
//            refreshLayout!!.setVisibility(View.GONE)
//            allEntertainmentData
//        })
//        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
////                if (!recyclerView.canScrollVertically(1)) {
////                    Log.d("aaaaaaa 00 = ", ""+(offset+50)+" businesses.size ="+businesses.size)
////                    if(businesses.size>=(offset+50)){
////                        offset += (50)
////                        allEntertainmentData
////                    }
////                }
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
//                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == businesses.size - 5) {
//                    if (businesses.size >= (offset + 30)) {
//                        offset += (30)
//                        allEntertainmentData
//                    }
//                }
//            }
//        })
        return view
    }

    private val allEntertainmentData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "")
                var category = ArrayList<String>()
                category.add("restaurants")
                category.add("foodtrucks")
                category.add("icecream")
                category.add("bakeries")
                category.add("milkshakebars")
                animation_view!!.visibility = View.GONE
                val params: MutableMap<String, String> = HashMap()
                val terms = searchData.ifEmpty {
                    "Food"
                }
                params["term"] = terms
                params["latitude"] = ProjectUtil.getLotitude(activity)
                params["longitude"] = ProjectUtil.getLongitude(activity)
                params["sort_by"] = "distance"
                params["limit"] = "30"
                params["categories"] = category.toString()
                params["offset"] = "" + offset
//                businesses.clear()
                val apiService =
                    YelpFusionApiFactory.createAPI("uWwWVaVVFSyTvOv0SMn9L03TfDNo_Zad1UKQXlFyx3ThsQYhc6C97VOIBKjks0q3UCpNGao5rdTg988pZZdvRxReZczmiiLLzIsFqBVZFt9PzyRryIkEZ3lwo1ozY3Yx")
                val getAllComplaint = apiService.getBusinessSearch(params)
                getAllComplaint.enqueue(
                    object : Callback<SearchResponse?> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(
                            call: Call<SearchResponse?>,
                            response: Response<SearchResponse?>
                        ) {
                            val getAllData = response.body()

                            val handler = Handler()
                            handler.postDelayed(Runnable {
//                                businesses.clear()
                                businesses.addAll(getAllData!!.businesses)
                                ProjectUtil.dismissDialog(customDialog)
                                if (getAllData.businesses.size <= 0) {
                                    val bundle = Bundle()
                                    bundle.putString("fragmentLayoutName", "")
                                    shimmerFramlayout!!.visibility = View.GONE
                                    animation_view!!.visibility = View.VISIBLE
                                    recyclerView!!.visibility = View.GONE
                                    refreshLayoutParent!!.visibility = View.VISIBLE
                                    return@Runnable
                                }

                                mLayoutManager = LinearLayoutManager(activity)
                                recyclerView!!.layoutManager = mLayoutManager
                                advisoryDetailsAdapter =
                                    AdvisoryDetailsAdapter(
                                        activity,
                                        businesses,
                                        this@AdvisoryDetailsFragment
                                    )
                                recyclerView!!.adapter = advisoryDetailsAdapter
                                advisoryDetailsAdapter!!.notifyDataSetChanged()
                                recyclerView!!.visibility = View.VISIBLE
                                recyclerView!!.layoutManager?.scrollToPosition(offset)
                                shimmerFramlayout!!.visibility = View.GONE
                                refreshLayoutParent!!.visibility = View.VISIBLE

                            }, 10)
//                            searchView!!.clearFocus()
                        }

                        override fun onFailure(call: Call<SearchResponse?>, t: Throwable) {
                            t.printStackTrace()
                            ProjectUtil.dismissDialog(customDialog)
                            shimmerFramlayout!!.visibility = View.GONE
                            animation_view!!.visibility = View.VISIBLE
                            recyclerView!!.visibility = View.GONE
                            refreshLayoutParent!!.visibility = View.VISIBLE
                        }
                    })
            } catch (e: IOException) {
                e.printStackTrace()
                ProjectUtil.dismissDialog(customDialog)
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                refreshLayoutParent!!.visibility = View.VISIBLE
            }
        }


    override fun onClickEvent(postion: Int) {
//        Gson gson = new Gson();
//        Bundle bundle = new Bundle();
//        bundle.putString("position", gson.toJson(businesses.get(postion)));
//        Intent result = new Intent(getActivity(), ViewRecomendDataActivity.class);
//        result.putExtra("position", gson.toJson(businesses.get(postion)));
//        startActivity(result);
    }

    override fun onImageClick(postion: Int) {
//        val result = Intent(activity, ImageViewerActivity::class.java)
//        result.putExtra(ImageViewerActivity.EXTRA_PATH, businesses.get(postion).imageUrl)
//        startActivity(result)

        FinestWebView(requireActivity()).theme(R.style.RedTheme)
            .titleDefault(requireActivity().resources.getString(R.string.app_name))
            .toolbarScrollFlags(0)
            .statusBarColorRes(R.color.red_bottom)
            .toolbarColorRes(R.color.red_bottom)
            .titleColorRes(R.color.white)
            .urlColorRes(R.color.white)
            .iconDefaultColorRes(R.color.white)
            .progressBarColorRes(R.color.white)
            .swipeRefreshColorRes(R.color.white) //                        .menuSelector(R.drawable.selector_light_theme)
            .menuTextGravity(Gravity.CENTER_VERTICAL or Gravity.RIGHT) //                        .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
            .dividerHeight(0)
            .gradientDivider(false) //                        .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold, R.anim.slide_right_out)
            .disableIconBack(false)
            .disableIconClose(false)
            .disableIconForward(false)
            .disableIconMenu(false)
            .show(businesses.get(postion).url)
    }

    override fun onSiteUrl(postion: Int) {
        FinestWebView(requireActivity()).theme(R.style.RedTheme)
            .titleDefault(requireActivity().resources.getString(R.string.app_name))
            .toolbarScrollFlags(0)
            .statusBarColorRes(R.color.red_bottom)
            .toolbarColorRes(R.color.red_bottom)
            .titleColorRes(R.color.white)
            .urlColorRes(R.color.white)
            .iconDefaultColorRes(R.color.white)
            .progressBarColorRes(R.color.white)
            .swipeRefreshColorRes(R.color.white) //                        .menuSelector(R.drawable.selector_light_theme)
            .menuTextGravity(Gravity.CENTER_VERTICAL or Gravity.RIGHT) //                        .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
            .dividerHeight(0)
            .gradientDivider(false) //                        .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold, R.anim.slide_right_out)
            .disableIconBack(false)
            .disableIconClose(false)
            .disableIconForward(false)
            .disableIconMenu(false)
            .show(businesses.get(postion).url)
    }

    override fun onCallEvent(postion: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + businesses[postion].phone)
        startActivity(intent)
    }

    override fun onAddressClick(postion: Int) {
        try {
            var listData = businesses.get(postion)

            if (listData.coordinates.longitude == null || listData.coordinates.latitude == null) {
                ProjectUtil.customToast(activity, "Camp address not found")
                return
            }


            var strUri =
                "http://maps.google.com/maps?q=loc:" + listData.coordinates.latitude.toString() + "," + listData.coordinates.longitude.toString()
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))

            intent.setClassName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"
            )
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.apps.maps")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps")
                    )
                )
            }
            e.printStackTrace()
        }
    }

    override fun onMoreInfo(postion: Int) {
    }


}