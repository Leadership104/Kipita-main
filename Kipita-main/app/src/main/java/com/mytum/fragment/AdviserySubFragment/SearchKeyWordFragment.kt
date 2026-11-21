package com.mytum.fragment.AdviserySubFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.fuzzproductions.ratingbar.RatingBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mytum.ImageViewerActivity
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.adapter.EntertainmentAdapter
import com.mytum.adapter.ImageAdapter
import com.mytum.adapter.OpeningTimeAdapter
import com.mytum.adapter.RestaurantNewAdapter
import com.mytum.adapter.ReviewCommentAdapter
import com.mytum.fragment.MoreInfoAllImageFragment
import com.mytum.fragment.recomamandtionSubFragment.RestaurantsNewFragment
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.CallResponse.CallResponse
import com.mytum.model.MorInfo.MoreInfoResponse
import com.mytum.model.NewAdvisaryResponse.AdvisaryListResponse
import com.mytum.model.NewPlaceGoResponse.NewPlaceResponse
import com.mytum.model.NewPlaceGoResponse.ResultNewPlace
import com.mytum.model.places.Center
import com.mytum.model.places.Circle
import com.mytum.model.places.LocationRestriction
import com.mytum.model.places.Photo
import com.mytum.model.places.Place
import com.mytum.model.places.PlacesTextSearchPojo
import com.mytum.model.places.RestaurantPojo
import com.mytum.model.places.Review
import com.mytum.proectUtil.ProjectUtil
import com.mytum.proectUtil.RecyclerItemClickListener
import com.squareup.picasso.Picasso
//import com.thefinestartist.finestwebview.FinestWebView
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class SearchKeyWordFragment : Fragment(), ComplaintClickPressed {

    var allPlacesDataList = ArrayList<Place>()

    var customDialog: Dialog? = null
    private var mLayoutManager: LinearLayoutManager? = null
    var tital = ""
    var recyclerView: RecyclerView? = null
    var imageViewIcon: ImageView? = null
    var txtTitle: TextView? = null
    var offset = 0;
    var nextPageToken: String? = null;
    var searchData = ""
    var search: ImageView? = null
    var searchVoice: ImageView? = null
    var searchViewParent: LinearLayout? = null
    var etSearchView: EditText? = null
    var clickedCategoryRadius: Double = 50000.0
    var clickedCategoryMaxResult: Int = 20
    var clickedCategoryRankPreference: String = "DISTANCE"
    var clickedCategoryIncludeType: String = "airport"
    var showOpenClosed: Int = 1
    lateinit var clickedItemData: AdvisaryListResponse

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.search_keyword_fragment, container, false)
        (activity as MainActivity).showSubMenu()
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titelSub)
        var backButton = (activity as MainActivity?)!!.findViewById<ImageView>(R.id.backarrow)
        textView.text = "Back to Advisory"
        backButton.setOnClickListener(View.OnClickListener {
            loadFragment(ApplicationsMainFragment())
        })
        etSearchView = view.findViewById(R.id.etSearchView)
        searchViewParent = view.findViewById(R.id.searchViewParent)
        search = view.findViewById(R.id.search)
        searchVoice = view.findViewById(R.id.searchVoice)
        customDialog = Dialog(requireActivity())
        imageViewIcon = view.findViewById(R.id.imageViewIcon)
        txtTitle = view.findViewById(R.id.txtTitle)
        imageViewIcon?.visibility = View.GONE
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        tital = requireArguments().getString("list").toString()
        val iconUrl = requireArguments().getString("iconUrl").toString()
        val flagIconUrl = requireArguments().getString("flagIconUrl").toString()
        val lastUpdated = requireArguments().getString("lastUpdated").toString()

        val searchText = arguments?.getString("searchText");
        val data = arguments?.getString("placesList");
        showOpenClosed = arguments?.getInt("showOpenClosed", 1)!!;
        clickedItemData = arguments?.getParcelable("clickedItemData")!!
        arguments?.clear()
        if(clickedItemData != null){
            clickedCategoryRadius = clickedItemData.radius
            clickedCategoryMaxResult = clickedItemData.maxResults
            clickedCategoryRankPreference = clickedItemData.rankPreference
            clickedCategoryIncludeType = clickedItemData.type
        }
        txtTitle?.text = tital
        val gson = Gson()
        val mainPojo = gson.fromJson(data, RestaurantPojo::class.java)
        allPlacesDataList = mainPojo.places as ArrayList<Place>
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = mLayoutManager
        val restaurantNewAdapter =
            RestaurantNewAdapter(
                activity,
                allPlacesDataList,
                showOpenClosed!!,
                this@SearchKeyWordFragment
            )
        recyclerView!!.adapter = restaurantNewAdapter
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
//                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == businesses.size-3) {
//                    if (businesses.size >= (offset + 20) && nextPageToken != null) {
//                        offset += (20)
//                        allEntertainmentData
//                    }
//                }
            }
        })

        etSearchView?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                etSearchView?.setSelection(etSearchView?.text?.length!!)
                searchData = etSearchView?.text.toString()
                if (searchData.isEmpty()) {
                    ProjectUtil.customToast(requireActivity(), "Please type any word")
                } else {
                    hideKeyBord()
                    getPlacesTextSearchData
                }
            }
            true
        }

        searchVoice!!.setOnClickListener(View.OnClickListener {
            etSearchView?.text?.clear()
            askSpeechInput()
        })
        search!!.setOnClickListener(View.OnClickListener {
            etSearchView?.setSelection(etSearchView?.text?.length!!)
            searchData = etSearchView?.text.toString()
            if (searchData.isEmpty()) {
                ProjectUtil.customToast(requireActivity(), "Please type any word")
            } else {
                hideKeyBord()
                getPlacesTextSearchData
            }
        })

        return view
    }

    private val getPlacesTextSearchData: Unit
        @SuppressLint("LongLogTag")
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val includedTypes = "airport"//clickedCategoryIncludeType
                val center = Center(
                    ProjectUtil.getLotitude(activity as MainActivity).toDouble(),
                    ProjectUtil.getLongitude(activity as MainActivity).toDouble()
                )
                val circle = Circle(center, clickedCategoryRadius)
                var locationRestriction = LocationRestriction(circle)
                var data = PlacesTextSearchPojo(
                    includedTypes,
                    searchData,
                    clickedCategoryMaxResult,
                    clickedCategoryRankPreference,
                    locationRestriction
                );
                var gson = Gson();
                var jsonString = gson.toJson(data);
                Log.d("aaaaa text search reques", jsonString)
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
                            handlePlacesResponse(responseString, true);
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

    private fun handlePlacesResponse(responseData: String, isFromSearch: Boolean) {
        try {
            allPlacesDataList.clear()
            val gson = Gson()
            val data: RestaurantPojo =
                gson.fromJson(responseData, RestaurantPojo::class.java)
            if (data.places?.size!! <= 0) {
                showAlertDialogButtonClicked()
            } else {
                etSearchView?.text?.clear()
                allPlacesDataList = data.places as ArrayList<Place>
                mLayoutManager = LinearLayoutManager(activity)
                recyclerView!!.layoutManager = mLayoutManager
                val restaurantNewAdapter =
                    RestaurantNewAdapter(
                        activity,
                        allPlacesDataList,
                        showOpenClosed!!,
                        this@SearchKeyWordFragment
                    )
                recyclerView!!.adapter = restaurantNewAdapter
            }

        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.customToast(activity, "No Result Found")
        }
    }

    private fun askSpeechInput() {
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Language model defines the purpose, there are special models for other use cases, like search.
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        // Adding an extra language, you can use any language from the Locale class.
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        // Text that shows up on the Speech input prompt.
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")
        try {
            // Start the intent for a result, and pass in our request code.
//            startActivityForResult(sttIntent, REQUEST_CODE_STT)
            voiceResultLauncher.launch(sttIntent)
        } catch (e: ActivityNotFoundException) {
            // Handling error when the service is not available.
            e.printStackTrace()
            ProjectUtil.customToast(activity, "Speech is Not Available")
        }
    }

    private var voiceResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val result =
                        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {
                        // Recognized text is in the first position.
                        val recognizedText = result[0]
                        // Do what you want with the recognized text.
                        searchData = recognizedText
                        etSearchView?.setText(recognizedText)
                        getPlacesTextSearchData
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showSubMenu()
    }

    override fun onClickEvent(postion: Int) {
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

    override fun onImageClick(postion: Int) {

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
                Uri.parse(
                    "http://maps.google.com/maps?saddr=" + ProjectUtil.getCurrentLotitude(
                        activity
                    ) + "," + ProjectUtil.getCurrentLongitude(activity) + "&daddr=" + listData.location.latitude.toString() + "," + listData.location.longitude.toString()
                )
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

    override fun onSiteUrl(postion: Int) {
    }

    override fun onMoreInfo(postion: Int) {
        popupWindowInfo(postion)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun popupWindowInfo(position: Int) {
        var timingAdapter: OpeningTimeAdapter? = null
        var imageAdapter: ImageAdapter? = null
        try {
            val placeData = allPlacesDataList[position];

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
            val text_view_all = layout.findViewById<TextView>(R.id.text_view_all)
            val llPet = layout.findViewById<LinearLayout>(R.id.llPet)
            val petFriendly = layout.findViewById<TextView>(R.id.petFriendly)
            val llWheelChair = layout.findViewById<LinearLayout>(R.id.llWheelChair)
            val wheelChairParking = layout.findViewById<TextView>(R.id.wheelChairParking)
            val wheelChairEntrance = layout.findViewById<TextView>(R.id.wheelChairEntrance)
            val wheelChairRestroom = layout.findViewById<TextView>(R.id.wheelChairRestroom)
            val wheelChairSeating = layout.findViewById<TextView>(R.id.wheelChairSeating)

            val ratingReviewListLayout =
                layout.findViewById<LinearLayout>(R.id.ratingReviewListLayout)
            val llReviews = layout.findViewById<LinearLayout>(R.id.llReviews)

            try {
                hotelName!!.text = placeData.displayName?.text
                if (placeData.formattedAddress != null) {
                    llAddress?.visibility = View.VISIBLE
                    address!!.text = placeData.formattedAddress
                } else {
                    llAddress?.visibility = View.GONE
                }
                if (placeData.allowsDogs != null) {
                    llPet?.visibility = View.VISIBLE
                    petFriendly!!.text = if (placeData.allowsDogs == true) "Yes" else "No"
                } else {
                    llPet?.visibility = View.GONE
                }
                if (placeData.accessibilityOptions != null) {
                    llWheelChair?.visibility = View.VISIBLE
                    wheelChairParking!!.text =
                        " Wheel Chair Parking - " + if (placeData.accessibilityOptions?.wheelchairAccessibleParking == true) "Yes" else "No"
                    wheelChairEntrance!!.text =
                        " Wheel Chair Entrance - " + if (placeData.accessibilityOptions?.wheelchairAccessibleEntrance == true) "Yes" else "No"
                    wheelChairRestroom!!.text =
                        " Wheel Chair Restroom - " + if (placeData.accessibilityOptions?.wheelchairAccessibleRestroom == true) "Yes" else "No"
                    wheelChairSeating!!.text =
                        " Wheel Chair Seating - " + if (placeData.accessibilityOptions?.wheelchairAccessibleSeating == true) "Yes" else "No"
                } else {
                    llWheelChair?.visibility = View.GONE
                }


                if (placeData?.types != null) {
//                    llTags?.visibility = View.VISIBLE
//                    var allTags = ""
//                    for(temp in placeData.types){
//                        allTags = "$allTags$temp, "
//                    }
//                    allTags.removeSuffix(", ")
//                    tags!!.text = allTags
                } else {
                    llTags?.visibility = View.GONE
                }
                if (placeData?.nationalPhoneNumber != null || placeData?.internationalPhoneNumber != null) {
                    llCall?.visibility = View.VISIBLE
                    call!!.text =
                        "Ph: " + (if (placeData?.nationalPhoneNumber !== null) placeData?.nationalPhoneNumber else "--") + " Mob: " + (if (placeData?.internationalPhoneNumber !== null) placeData?.internationalPhoneNumber else "--")
                } else {
                    llCall?.visibility = View.GONE
                }
                if (placeData?.websiteUri != null) {
                    llWebsite?.visibility = View.VISIBLE
                    visitWebsite!!.movementMethod = LinkMovementMethod.getInstance()
                    visitWebsite!!.text = placeData?.websiteUri
                } else {
                    llWebsite?.visibility = View.GONE
                }

            } catch (e: Exception) {

            }
            try {
                if (placeData?.currentOpeningHours != null) {
                    if (placeData?.currentOpeningHours?.weekdayDescriptions != null) {
                        llOpeningHour?.visibility = View.VISIBLE
                        mLayoutManager = LinearLayoutManager(activity)
                        recyclerView_reg!!.layoutManager = mLayoutManager
                        timingAdapter =
                            OpeningTimeAdapter(
                                activity,
                                placeData?.currentOpeningHours?.weekdayDescriptions as ArrayList<String>?,
                                this@SearchKeyWordFragment
                            )
                        recyclerView_reg!!.adapter = timingAdapter
                        timingAdapter!!.notifyDataSetChanged()
                    } else {
                        llOpeningHour?.visibility = View.GONE
                    }
                } else {
                    llOpeningHour?.visibility = View.GONE
                }
            } catch (e: Exception) {

            }
            try {
                if (placeData?.photos != null && placeData?.photos.isNotEmpty()) {
                    llPhotos?.visibility = View.VISIBLE
                    val myPhotos: List<Photo> =
                        if (placeData.photos.size > 8) placeData.photos.take(8) else placeData.photos
                    val mLayoutManager = GridLayoutManager(context, 4)
                    recyclerView_image!!.setLayoutManager(mLayoutManager)
                    imageAdapter =
                        ImageAdapter(
                            activity,
                            myPhotos as ArrayList<Photo>,
                            this@SearchKeyWordFragment
                        )
                    recyclerView_image.adapter = imageAdapter
                    imageAdapter.notifyDataSetChanged()
                } else {
                    llPhotos?.visibility = View.GONE
                }
            } catch (e: Exception) {

            }

            text_view_all!!.setOnClickListener { v: View? ->
                val bundle = Bundle()
                bundle.putString("title", "Back to more info")
                bundle.putString("dataList", allPlacesDataList.toString())
                loadFragmentInner(MoreInfoAllImageFragment(), bundle);
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
                if (placeData.rating != null) {
                    totalReview!!.text = "" + placeData.rating + " out 5.0"
                    rating_bar!!.rating = (placeData.rating!!.toFloat())
                    rating_bar!!.setFilledDrawable(getResources().getDrawable(R.drawable.icn_rating_start_green))
                } else {
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
                            placeData?.reviews as ArrayList<Review>?, this@SearchKeyWordFragment
                        )
                    recyclerView_review!!.adapter = reviewAdapter
                    reviewAdapter!!.notifyDataSetChanged()
                } else {
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

    fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
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

    fun hideKeyBord() {
        try {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(etSearchView?.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAlertDialogButtonClicked() {
        // Create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
//        builder.setTitle("Note")

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
            etSearchView?.text?.clear()
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
    }

}