package com.mytum.fragment.recomamandtionSubFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.fuzzproductions.ratingbar.RatingBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mytum.ImageViewerActivity
import com.mytum.MyApplication
import com.mytum.proectUtil.ProjectUtil
import com.mytum.proectUtil.RecyclerItemClickListener
import com.mytum.R
import com.mytum.adapter.ImageAdapter
import com.mytum.adapter.OpeningTimeAdapter
import com.mytum.adapter.ShoppingAdapter
import com.mytum.finestwebview.FinestWebView
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.CallResponse.CallResponse
import com.mytum.model.MorInfo.MoreInfoResponse
import com.mytum.model.NewPlaceGoResponse.NewPlaceResponse
import com.mytum.model.NewPlaceGoResponse.ResultNewPlace
//import com.thefinestartist.finestwebview.FinestWebView
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import java.io.ByteArrayOutputStream


class ShoppingFragment : Fragment(), ComplaintClickPressed {
    var businesses = ArrayList<ResultNewPlace>()
    var customDialog: Dialog? = null
    private var mLayoutManager: LinearLayoutManager? = null
    var timingAdapter: OpeningTimeAdapter? = null
    var imageAdapter: ImageAdapter? = null
    var refreshLayoutParent: LinearLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    lateinit var etSearchView: EditText
    var searchViewParent: LinearLayout? = null
    var search: ImageView? = null
    var searchData = ""
    var animation_view: LinearLayout? = null
    var registerPetAdapter: ShoppingAdapter? = null
    var offset = 0;
    var nextPageToken: String? = null;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.shopping, container, false)
        customDialog = Dialog(requireActivity())
//        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        refreshLayoutParent = view.findViewById(R.id.swipeToRefresh_parent)
        search = view.findViewById(R.id.search)
        etSearchView = view.findViewById(R.id.etSearchView)
        searchViewParent = view.findViewById(R.id.searchViewParent)
        animation_view = view.findViewById(R.id.animation_view)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        registerPetAdapter = ShoppingAdapter(activity, businesses, this@ShoppingFragment)
        recyclerView!!.setAdapter(registerPetAdapter)
        businesses.clear()
        allEntertainmentData

        search!!.setOnClickListener(View.OnClickListener {
            etSearchView.setSelection(etSearchView.text.length)
            if (searchData.isEmpty()) {
                ProjectUtil.customToast(requireActivity(), "Please type any word")
            } else {
                businesses.clear()
                hideKeyBord()
                allEntertainmentData
            }
        })


        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == businesses.size-3) {
                    if (businesses.size >= (offset + 20) && nextPageToken != null) {
                        offset += (20)
                        allEntertainmentData
                    }
                }
            }
        })

        return view
    }

    protected val allEntertainmentData: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                ////                var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ProjectUtil.getLongitude(activity)+","+ProjectUtil.getLotitude(activity)+"&radius=1500&type=Entertainment&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4"

                var url =
                    "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ProjectUtil.getCurrentLotitude(activity)+","+ProjectUtil.getCurrentLongitude(activity)+"&radius=1500&type=shopping&" +
                            "key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4&next_page_token="+nextPageToken
//                Log.d("aaaaaaaaaaaa get data url shop", url)
                ////                var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ProjectUtil.getLongitude(activity)+","+ProjectUtil.getLotitude(activity)+"&radius=1500&type=shopping&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4"


                Thread {
                    try {

                        val httpclient: HttpClient = DefaultHttpClient()
                        val post = HttpPost(url)
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

                            var gson = Gson()
                            var getAllData: NewPlaceResponse =
                                gson.fromJson(responseString, NewPlaceResponse::class.java)

                            nextPageToken = getAllData.nextPageToken
                            businesses.addAll(getAllData!!.results)
                            ProjectUtil.dismissDialog(customDialog)
                            if (getAllData.results.size <= 0) {
                                val bundle = Bundle()
                                bundle.putString("fragmentLayoutName", "")
                                shimmerFramlayout!!.visibility = View.GONE
                                animation_view!!.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                                refreshLayoutParent!!.visibility = View.VISIBLE
                                ProjectUtil.dismissDialog(customDialog)
                                return@Runnable
                            }

//                                ProjectUtil.customToast(activity,businesses.size.toString())

                            mLayoutManager = LinearLayoutManager(activity)
                            recyclerView!!.layoutManager = mLayoutManager
                            registerPetAdapter =
                                ShoppingAdapter(activity, businesses, this@ShoppingFragment)
                            recyclerView!!.adapter = registerPetAdapter
                            registerPetAdapter!!.notifyDataSetChanged()
                            recyclerView!!.visibility = View.VISIBLE
                            recyclerView!!.layoutManager?.scrollToPosition(offset)
                            shimmerFramlayout!!.visibility = View.GONE
                            animation_view!!.visibility = View.GONE
                            refreshLayoutParent!!.visibility = View.VISIBLE


                        })
                        ProjectUtil.dismissDialog(customDialog)
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


    override fun onClickEvent(postion: Int) {
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
            .show("www.google.com")
//            .show(businesses.get(postion).url)
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
            .show("www.google.com")
//            .show(businesses.get(postion).url)
    }


    override fun onCallEvent(postion: Int) {
        getMobileNumber(businesses.get(postion).placeId)
    }

    override fun onAddressClick(postion: Int) {
        try {
            var listData = businesses!!.get(postion)

            if (listData.geometry.location.lat == null || listData.geometry.location.lng == null) {
                ProjectUtil.customToast(activity, "Camp address not found")
                return
            }


            var strUri =
                "http://maps.google.com/maps?q=loc:" + listData.geometry.location.lat.toString() + "," + listData.geometry.location.lng.toString()
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

    private fun getMobileNumber(placeId: String) {


        try {
            ProjectUtil.showDialog(customDialog, "Please wait...")
            var url =
                "https://maps.googleapis.com/maps/api/place/details/json?fields=name%2Crating%2Cformatted_phone_number&place_id=" + placeId + "&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4"
//            var url =
//                "https://maps.googleapis.com/maps/api/place/details/json?fields=name%2Crating%2Cformatted_phone_number&place_id=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4"

            Thread {
                try {

                    val httpclient: HttpClient = DefaultHttpClient()
                    val post = HttpPost(url)
                    post.setHeader("Accept", "application/json")
                    post.setHeader("Content-type", "application/json")
                    val response = httpclient.execute(post)
                    val out = ByteArrayOutputStream()
                    response.entity.writeTo(out)
                    val responseString = out.toString()
                    activity?.runOnUiThread(Runnable {

                        var gson = Gson()
                        var getAllData: CallResponse =
                            gson.fromJson(responseString, CallResponse::class.java)

                        ProjectUtil.dismissDialog(customDialog)

                        if (getAllData.result == null || getAllData.result.formattedPhoneNumber == null) {
                            ProjectUtil.customToast(activity, "Mobile number not available")
                        } else {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data =
                                Uri.parse("tel:" + getAllData!!.result.formattedPhoneNumber)
                            startActivity(intent)
                        }

                    })
                    ProjectUtil.dismissDialog(customDialog)
                    out.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    ProjectUtil.dismissDialog(customDialog)
                }
            }.start()


        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.dismissDialog(customDialog)
        } catch (e: Exception) {
            ProjectUtil.customToast(activity, "Mobile number not available")
            ProjectUtil.dismissDialog(customDialog)
        }
    }

    fun hideKeyBord() {
        try {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(etSearchView.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMoreInfo(postion: Int) {
        getMoreInfo(businesses.get(postion).placeId)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun popupWindowInfo(getData: MoreInfoResponse) {
        try {

            var layout = BottomSheetDialog(requireActivity(), R.style.popup_window_animation)
            layout.requestWindowFeature(Window.FEATURE_NO_TITLE)
            layout.setContentView(R.layout.popup_info)

            var image_top = layout.findViewById<ImageView>(R.id.image_top)
            var address = layout.findViewById<TextView>(R.id.address)
            var hotelName = layout.findViewById<TextView>(R.id.hotelName)
            var call = layout.findViewById<TextView>(R.id.call)
            var totalReview = layout.findViewById<TextView>(R.id.totalreview)
            var rating_bar = layout.findViewById<RatingBar>(R.id.rating_bar)
            var visitWebsite = layout.findViewById<TextView>(R.id.visitWebsite)
            var recyclerView_image = layout.findViewById<RecyclerView>(R.id.recyclerView_image)
            var recyclerView_reg = layout.findViewById<RecyclerView>(R.id.recyclerView_reg)
            hotelName!!.text = getData.result.vicinity
            address!!.text = getData.result.formattedAddress
            call!!.text = getData.result.formattedPhoneNumber
            visitWebsite!!.text = getData.result.website

            if(getData?.result?.openingHours != null) {
                mLayoutManager = LinearLayoutManager(activity)
                recyclerView_reg!!.layoutManager = mLayoutManager
                timingAdapter =
                    OpeningTimeAdapter(
                        activity, getData.result.openingHours.weekdayText, this@ShoppingFragment
                    )
                recyclerView_reg!!.adapter = timingAdapter
                registerPetAdapter!!.notifyDataSetChanged()
            }

            if(getData?.result?.photos != null) {
                var mLayoutManager = GridLayoutManager(context, 4)
                recyclerView_image!!.setLayoutManager(mLayoutManager)
//                imageAdapter =
//                    ImageAdapter(
//                        activity, getData.result.photos, this@ShoppingFragment
//                    )
//                recyclerView_image!!.adapter = imageAdapter
//                imageAdapter!!.notifyDataSetChanged()
            }

            recyclerView_image!!.addOnItemTouchListener(
                RecyclerItemClickListener(context, recyclerView, object :
                    RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val result = Intent(
                            requireActivity(),
                            ImageViewerActivity::class.java
                        )
                        var photoRef = getData.result.photos.get(position).photoReference
                        result.putExtra(
                            ImageViewerActivity.EXTRA_PATH,
                            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + photoRef + "&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4"
                        )
                        startActivity(result)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        // do whatever
                    }
                })
            )

            if(getData?.result?.rating != null && getData?.result?.userRatingsTotal != null) {
                totalReview!!.text =
                    "" + getData.result.rating + " out " + getData.result.userRatingsTotal
            }
            if(getData?.result?.rating != null) {
                rating_bar!!.rating = (getData.result.rating.toFloat())
                rating_bar!!.setFilledDrawable(getResources().getDrawable(R.drawable.icn_rating_start_green))
            }
            image_top!!.setOnClickListener { v: View? -> layout.dismiss() }

            layout.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        layout.dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (BottomSheetBehavior.STATE_HALF_EXPANDED == 1) {
                        BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            })


            var wmlp: WindowManager.LayoutParams = layout.getWindow()!!.getAttributes()
            wmlp.flags = wmlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            layout.setCancelable(false)
            layout.setCanceledOnTouchOutside(false)
            layout.show()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun getMoreInfo(placeId: String) {


        try {
            ProjectUtil.showDialog(customDialog, "Please wait...")
//            var url = "https://maps.googleapis.com/maps/api/place/details/json?fields=address_components,adr_address,business_status,formatted_address,formatted_phone_number,geometry,icon,icon_background_color,icon_mask_base_uri,international_phone_number,name,opening_hours,photos,place_id,plus_code,rating,reviews,types,url,user_ratings_total,vicinity,website&place_id="+placeId+"&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4"
            var url =
                "https://maps.googleapis.com/maps/api/place/details/json?fields=address_components,adr_address,business_status,formatted_address,formatted_phone_number,geometry,icon,icon_background_color,icon_mask_base_uri,international_phone_number,name,opening_hours,photos,place_id,plus_code,rating,reviews,types,url,user_ratings_total,vicinity,website&place_id=ChIJHV83VIIlv0cREnRD3tZnZvE&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4"

            Thread {
                try {

                    val httpclient: HttpClient = DefaultHttpClient()
                    val post = HttpPost(url)
                    post.setHeader("Accept", "application/json")
                    post.setHeader("Content-type", "application/json")
                    val response = httpclient.execute(post)
                    val out = ByteArrayOutputStream()
                    response.entity.writeTo(out)
                    val responseString = out.toString()
                    activity?.runOnUiThread(Runnable {

                        var gson = Gson()
                        var getAllData: MoreInfoResponse =
                            gson.fromJson(responseString, MoreInfoResponse::class.java)

                        ProjectUtil.dismissDialog(customDialog)
                        popupWindowInfo(getAllData)

                    })
                    ProjectUtil.dismissDialog(customDialog)
                    out.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    ProjectUtil.dismissDialog(customDialog)
                }
            }.start()


        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.showDialog(customDialog, "Please wait...")
        } catch (e: Exception) {
            ProjectUtil.customToast(activity, "Mobile number not available")
            ProjectUtil.dismissDialog(customDialog)
        }


    }


}