package com.mytum.fragment.recomamandtionSubFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fuzzproductions.ratingbar.RatingBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mytum.ImageViewerActivity
import com.mytum.MainActivity
import com.mytum.R
import com.mytum.adapter.ImageAdapter
import com.mytum.adapter.OpeningTimeAdapter
import com.mytum.adapter.RestaurantNewAdapter
import com.mytum.adapter.ReviewCommentAdapter
import com.mytum.fragment.RecommadationFragment
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.places.Photo
import com.mytum.model.places.Place
import com.mytum.model.places.RestaurantPojo
import com.mytum.model.places.Review
import com.mytum.proectUtil.ProjectUtil
import com.mytum.proectUtil.RecyclerItemClickListener
import java.util.*

class RestaurantsNewFragment : Fragment(), ComplaintClickPressed {
    var allPlacesDataList = ArrayList<Place>()
    var customDialog: Dialog? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var llMain: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.resturant_new, container, false)
        customDialog = Dialog(requireActivity())

        llMain = view.findViewById(R.id.llMain);

        val searchText = arguments?.getString("searchText");
        val data = arguments?.getString("placesList");
        val fromWhichCategory = arguments?.getInt("fromWhichCategory", 1)
        val showOpenClosed = arguments?.getInt("showOpenClosed", 1);
        arguments?.clear()
        Log.d("aaaaaa searchText  = ", ""+searchText);
//        Log.d("aaaaaa data  = ", ""+data);
        Log.d("aaaaaa fromWhichCategory  = ", ""+fromWhichCategory);

        val recyclerViewPlacesMain = view.findViewById<RecyclerView>(R.id.recyclerViewPlacesMain)

        (activity as MainActivity).showSubMenu()
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titelSub)
        var backButton = (activity as MainActivity?)!!.findViewById<ImageView>(R.id.backarrow)
        textView.text = "Back to Places"
        backButton.setOnClickListener(View.OnClickListener {
            if (fromWhichCategory == 1) {
                loadFragmentInner(RecommadationFragment(), 1)
            } else if (fromWhichCategory == 2) {
                loadFragmentInner(RecommadationFragment(), 2)
            } else if (fromWhichCategory == 3) {
                loadFragmentInner(RecommadationFragment(), 3)
            }

        })

        var gson = Gson()
        val mainPojo = gson.fromJson(data, RestaurantPojo::class.java)
        allPlacesDataList = mainPojo.places as ArrayList<Place>



        val textLabel = view.findViewById<TextView>(R.id.textLabel)
        textLabel.text = "Showing results for " + searchText

        mLayoutManager = LinearLayoutManager(activity)
        recyclerViewPlacesMain!!.layoutManager = mLayoutManager

        val restaurantNewAdapter =
            RestaurantNewAdapter(activity, allPlacesDataList, showOpenClosed!!, this@RestaurantsNewFragment)
        recyclerViewPlacesMain!!.adapter = restaurantNewAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showSubMenu()
    }

    override fun onClickEvent(postion: Int) {
    }

    override fun onImageClick(postion: Int) {
    }

    override fun onSiteUrl(postion: Int) {
    }

    override fun onCallEvent(postion: Int) {
        val phoneNumber = allPlacesDataList.get(postion).nationalPhoneNumber
        if (phoneNumber == null) {
            ProjectUtil.customToast(activity, "Mobile number not available")
        } else {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + phoneNumber)
                startActivity(intent)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    override fun onAddressClick(postion: Int) {
        try {
            var listData = allPlacesDataList.get(postion)

            if (listData.location.latitude == null || listData.location.longitude == null) {
                ProjectUtil.customToast(activity, "Location not found")
                return
            }

//            var strUri =
//                "http://maps.google.com/maps?q=loc:" + listData.location.latitude.toString() + "," + listData.location.longitude.toString()
//            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))
//
//            intent.setClassName(
//                "com.google.android.apps.maps",
//                "com.google.android.maps.MapsActivity"
//            )
//            startActivity(intent)

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+ProjectUtil.getCurrentLotitude(activity)+ "," + ProjectUtil.getCurrentLongitude(activity) +"&daddr=" + listData.location.latitude.toString() + "," + listData.location.longitude.toString())
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
        popupWindowInfo(postion)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun popupWindowInfo(position: Int) {
        var timingAdapter: OpeningTimeAdapter? = null
        var imageAdapter: ImageAdapter? = null
        try {
            val placeData = allPlacesDataList.get(position);

            var layout = BottomSheetDialog(requireActivity(), R.style.MaterialDialogSheet)
            layout.requestWindowFeature(Window.FEATURE_NO_TITLE)
            layout.setContentView(R.layout.popup_info)

            val image_top = layout.findViewById<ImageView>(R.id.image_top)
            val address = layout.findViewById<TextView>(R.id.address)
            val hotelName = layout.findViewById<TextView>(R.id.hotelName)
            val call = layout.findViewById<TextView>(R.id.call)
            val totalReview = layout.findViewById<TextView>(R.id.totalreview)
            val rating_bar = layout.findViewById<RatingBar>(R.id.rating_bar)
            val visitWebsite = layout.findViewById<TextView>(R.id.visitWebsite)
            val recyclerView_image = layout.findViewById<RecyclerView>(R.id.recyclerView_image)
            val recyclerView_reg = layout.findViewById<RecyclerView>(R.id.recyclerView_reg)
            val recyclerView_review = layout.findViewById<RecyclerView>(R.id.recyclerView_review)
            val llAddress = layout.findViewById<LinearLayout>(R.id.llAddress)
            val llCall = layout.findViewById<LinearLayout>(R.id.llCall)
            val llWebsite = layout.findViewById<LinearLayout>(R.id.llWebsite)
            val llOpeningHour = layout.findViewById<LinearLayout>(R.id.llOpeningHour)
            val llPhotos = layout.findViewById<LinearLayout>(R.id.llPhotos)
            val llRating = layout.findViewById<LinearLayout>(R.id.llRating)
            val llTags = layout.findViewById<LinearLayout>(R.id.llTags)
            val tags = layout.findViewById<TextView>(R.id.tags)
            val llPet = layout.findViewById<LinearLayout>(R.id.llPet)
            val petFriendly = layout.findViewById<TextView>(R.id.petFriendly)
            val llWheelChair = layout.findViewById<LinearLayout>(R.id.llWheelChair)
            val wheelChairParking = layout.findViewById<TextView>(R.id.wheelChairParking)
            val wheelChairEntrance = layout.findViewById<TextView>(R.id.wheelChairEntrance)
            val wheelChairRestroom = layout.findViewById<TextView>(R.id.wheelChairRestroom)
            val wheelChairSeating = layout.findViewById<TextView>(R.id.wheelChairSeating)

            val ratingReviewListLayout = layout.findViewById<LinearLayout>(R.id.ratingReviewListLayout)
            val llReviews = layout.findViewById<LinearLayout>(R.id.llReviews)

            try {
                hotelName!!.text = placeData?.displayName?.text
                if(placeData?.formattedAddress != null){
                    llAddress?.visibility = View.VISIBLE
                    address!!.text = placeData?.formattedAddress
                }else{
                    llAddress?.visibility = View.GONE
                }
                if(placeData?.allowsDogs != null){
                    llPet?.visibility = View.VISIBLE
                    petFriendly!!.text = if(placeData?.allowsDogs == true) "Yes" else "No"
                }else{
                    llPet?.visibility = View.GONE
                }
                if(placeData?.accessibilityOptions != null){
                    llWheelChair?.visibility = View.VISIBLE
                    wheelChairParking!!.text = " Wheel Chair Parking - "+if(placeData?.accessibilityOptions?.wheelchairAccessibleParking == true) "Yes" else "No"
                    wheelChairEntrance!!.text = " Wheel Chair Entrance - "+if(placeData?.accessibilityOptions?.wheelchairAccessibleEntrance == true) "Yes" else "No"
                    wheelChairRestroom!!.text = " Wheel Chair Restroom - "+if(placeData?.accessibilityOptions?.wheelchairAccessibleRestroom == true) "Yes" else "No"
                    wheelChairSeating!!.text = " Wheel Chair Seating - "+if(placeData?.accessibilityOptions?.wheelchairAccessibleSeating == true) "Yes" else "No"
                }else{
                    llWheelChair?.visibility = View.GONE
                }


                if(placeData?.types != null){
//                    llTags?.visibility = View.VISIBLE
//                    var allTags = ""
//                    for(temp in placeData.types){
//                        allTags = "$allTags$temp, "
//                    }
//                    allTags.removeSuffix(", ")
//                    tags!!.text = allTags
                }else{
                    llTags?.visibility = View.GONE
                }
                if(placeData?.nationalPhoneNumber != null || placeData?.internationalPhoneNumber != null){
                    llCall?.visibility = View.VISIBLE
                    call!!.text = "Ph: "  + (if(placeData?.nationalPhoneNumber !== null) placeData?.nationalPhoneNumber else "--") + " Mob: " + (if(placeData?.internationalPhoneNumber !== null) placeData?.internationalPhoneNumber else "--")
                }else{
                    llCall?.visibility = View.GONE
                }
                if(placeData?.websiteUri != null){
                    llWebsite?.visibility = View.VISIBLE
                    visitWebsite!!.movementMethod = LinkMovementMethod.getInstance()
                    visitWebsite!!.text = placeData?.websiteUri
                }else{
                    llWebsite?.visibility = View.GONE
                }

            } catch (e: Exception) {

            }
            try {
                if (placeData?.currentOpeningHours != null){
                    if (placeData?.currentOpeningHours?.weekdayDescriptions != null) {
                        llOpeningHour?.visibility = View.VISIBLE
                        mLayoutManager = LinearLayoutManager(activity)
                        recyclerView_reg!!.layoutManager = mLayoutManager
                        timingAdapter =
                            OpeningTimeAdapter(
                                activity,
                                placeData?.currentOpeningHours?.weekdayDescriptions as ArrayList<String>?,
                                this@RestaurantsNewFragment
                            )
                        recyclerView_reg!!.adapter = timingAdapter
                        timingAdapter!!.notifyDataSetChanged()
                    }else{
                        llOpeningHour?.visibility = View.GONE
                    }
                }else{
                    llOpeningHour?.visibility = View.GONE
                }
            } catch (e: Exception) {

            }
            try {
                if (placeData?.photos != null && placeData?.photos.isNotEmpty()) {
                    llPhotos?.visibility = View.VISIBLE
                    var myPhotos: List<Photo> = if(placeData?.photos.size > 8) placeData?.photos.take(8) else placeData?.photos
                    var mLayoutManager = GridLayoutManager(context, 4)
                    recyclerView_image!!.setLayoutManager(mLayoutManager)
                    imageAdapter =
                        ImageAdapter(
                            activity,
                            myPhotos as ArrayList<Photo>,
                            this@RestaurantsNewFragment
                        )
                    recyclerView_image!!.adapter = imageAdapter
                    imageAdapter!!.notifyDataSetChanged()
                }else{
                    llPhotos?.visibility = View.GONE
                }
            } catch (e: Exception) {

            }
//            try {
//                recyclerView_image!!.addOnItemTouchListener(
//                    RecyclerItemClickListener(context, recyclerView_image, object :
//                        RecyclerItemClickListener.OnItemClickListener {
//                        override fun onItemClick(view: View?, position: Int) {
//                            val result = Intent(
//                                requireActivity(),
//                                ImageViewerActivity::class.java
//                            )
////                        var photoRef = placeData?.photos?.get(position)?.photoReference
////                        result.putExtra(
////                            ImageViewerActivity.EXTRA_PATH,
////                            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + photoRef + "&key=AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4"
////                        )
//                            startActivity(result)
//                        }
//
//                        override fun onLongItemClick(view: View?, position: Int) {
//                            // do whatever
//                        }
//                    })
//                )
//            } catch (e: Exception) {
//
//            }
            try {
                if(placeData.rating != null){
                    totalReview!!.text = "" + placeData.rating + " out 5.0"
                    rating_bar!!.rating = (placeData.rating!!.toFloat())
                    rating_bar!!.setFilledDrawable(getResources().getDrawable(R.drawable.icn_rating_start_green))
                }else{
                    totalReview!!.visibility = View.GONE
                    rating_bar!!.visibility = View.GONE
                }
            } catch (e: Exception) {

            }
            try {
                if (placeData?.reviews != null && placeData?.reviews.isNotEmpty()) {
                    ratingReviewListLayout?.visibility = View.VISIBLE
                    llReviews?.visibility = View.VISIBLE
                    mLayoutManager = LinearLayoutManager(activity)
                    recyclerView_review!!.layoutManager = mLayoutManager
                    val reviewAdapter =
                        ReviewCommentAdapter(
                            activity,
                            placeData?.reviews as ArrayList<Review>?, this@RestaurantsNewFragment
                        )
                    recyclerView_review!!.adapter = reviewAdapter
                    reviewAdapter!!.notifyDataSetChanged()
                }else{
                    ratingReviewListLayout?.visibility = View.GONE
                    llReviews?.visibility = View.GONE
                }
            } catch (e: Exception) {

            }

            image_top!!.setOnClickListener { v: View? -> layout.dismiss() }

            layout.behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
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

    fun loadFragmentInner(fragment: Fragment?, fromWhichCategory: Int) {
        if (fragment != null) {
            var bundle = Bundle()
            bundle.putInt("fromWhichCategory", fromWhichCategory)
            fragment!!.arguments = bundle
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

}