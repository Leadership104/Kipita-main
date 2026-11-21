package com.mytum.fragment.GallerySubFragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mytum.ImageViewerActivity1
import com.mytum.LoginActivity
import com.mytum.MainActivity
import com.mytum.R
import com.mytum.SwipeGestureListener
import com.mytum.adapter.PublicImageAdapter
import com.mytum.client.connection.YelpFusionApi
import com.mytum.client.connection.YelpFusionApiFactory
import com.mytum.imagepickerlibrary.ImagePicker
import com.mytum.imagepickerlibrary.PickerOptions
import com.mytum.listners.ImageClickPressed
import com.mytum.model.ImageList.ImageArrayListList
import com.mytum.model.ImageList.ImageListPuclicRequest
import com.mytum.model.ImageList.ImageListResponse
import com.mytum.model.ImageList.ImageUploadRequest
import com.mytum.proectUtil.CenterZoomLayoutManager
import com.mytum.proectUtil.OnSwipeTouchListener
import com.mytum.proectUtil.ProjectUtil
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PublicFragment : Fragment(), ImageClickPressed, SwipeGestureListener {

    var onSwipeTouchListener: OnSwipeTouchListener? = null
    var businesses = ArrayList<ImageArrayListList>()
    var tagList = ArrayList<String>()
    var timeList = ArrayList<String>()
    var locationList = ArrayList<String>()
    var radiusList = ArrayList<String>()
    var customDialog: Dialog? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var fab: FloatingActionButton? = null
    var refreshLayout: SwipeRefreshLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    var frameLayout_myImg: FrameLayout? = null
    var myImg: ImageView? = null
    var animation_view: LinearLayout? = null
    var registerPetAdapter: PublicImageAdapter? = null
//    var seekbar: SeekBar? = null
//    var seekbarText: TextView? = null
    var tagSpinner: SmartMaterialSpinner<String>? = null
    var radiusSpinner: SmartMaterialSpinner<String>? = null
    var timeSpinner: SmartMaterialSpinner<String>? = null
    var locationSpinner: SmartMaterialSpinner<String>? = null

    var spinnerArrayAdapter: ArrayAdapter<String>? = null
    var spinnerArrayAdapterRadius: ArrayAdapter<String>? = null
    var selectedTag: String? = null
    var spinnerArrayAdapterTime: ArrayAdapter<String>? = null
    var selectedTime: String? = null
    var spinnerArrayAdapterLocation: ArrayAdapter<String>? = null
    var selectedLocation: String? = null

    private var pickerOptions = PickerOptions.default()
    private var imagePicker: ImagePicker? = null
    var globalPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.public_image, container, false)
        customDialog = Dialog(requireActivity())
        fab = view.findViewById(R.id.fab)
//        seekbarText = view.findViewById(R.id.seekbarText)
//        seekbar = view.findViewById(R.id.seekbar)
        refreshLayout = view.findViewById(R.id.swipeToRefresh_reg)
        refreshLayout?.isRefreshing = false
        refreshLayout?.isEnabled = false
        animation_view = view.findViewById(R.id.animation_view)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        frameLayout_myImg = view.findViewById(R.id.frameLayout_myImg)
        myImg = view.findViewById(R.id.myImg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        tagSpinner = view.findViewById(R.id.tagSpinner)
        timeSpinner = view.findViewById(R.id.timeSpinner)
        locationSpinner = view.findViewById(R.id.locationSpinner)
        radiusSpinner = view.findViewById(R.id.radiusSpinner)
        spinnerArrayAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, tagList)
        spinnerArrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerArrayAdapterTime = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, timeList)
        spinnerArrayAdapterTime?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerArrayAdapterLocation = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, locationList)
        spinnerArrayAdapterLocation?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        radiusList.add("Select Radius")
        radiusList.add("5")
        radiusList.add("15")
        radiusList.add("25")
        radiusList.add("50")
        radiusList.add("100")
        radiusList.add("250")
        radiusList.add("500")
        radiusList.add("1000")
        radiusList.add("5000")
        radiusList.add("Global")
        spinnerArrayAdapterRadius = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, radiusList)
        spinnerArrayAdapterRadius?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        radiusSpinner!!.setItem(radiusList)
        radiusSpinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if(position == 0){
                    ProjectUtil.setRadius(activity, radiusList.get(position).toString())
                }else{
                    ProjectUtil.setRadius(activity, radiusList.get(position).toString())
                    allEntertainmentData
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })


//        try{
//            radiusSpinner!!.selectedItem = ""+radiusList.indexOf(ProjectUtil.getRadius(activity))
//        }catch (e: Exception){
//
//        }



        registerPetAdapter = PublicImageAdapter(activity, businesses, this@PublicFragment)
        mLayoutManager =
            CenterZoomLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.layoutManager = mLayoutManager
        // Scroll to the position we want to snap to
        mLayoutManager!!.scrollToPosition(0)
        recyclerView!!.adapter = registerPetAdapter

        allEntertainmentData
//        refreshLayout!!.setOnRefreshListener(OnRefreshListener {
//            shimmerFramlayout!!.setVisibility(View.VISIBLE)
//            refreshLayout!!.setVisibility(View.GONE)
//            allEntertainmentData
//        })

        fab!!.setOnClickListener(View.OnClickListener {
            if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                requestCameraImage()
            } else {
                val i = Intent(activity, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                activity?.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
                activity?.finish()
            }

        })

        return view
    }

    protected val allEntertainmentData: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                animation_view!!.visibility = View.GONE
                val imageListRequest = ImageListPuclicRequest()
                imageListRequest.lat = ProjectUtil.getLotitude(activity)
                imageListRequest.lng = ProjectUtil.getLongitude(activity)
                imageListRequest.radius = ProjectUtil.getRadius(requireActivity())
                imageListRequest.screenWidth = ProjectUtil.getScreenWidth(requireActivity())
                imageListRequest.timeZone = ProjectUtil.getCurrentTimeZone()
                if(selectedTag != null)
                    imageListRequest.imageTag = selectedTag
                else
                    imageListRequest.imageTag = ""

                if(selectedTime != null)
                    imageListRequest.imageTime = selectedTime
                else
                    imageListRequest.imageTime = ""

                if(selectedLocation != null)
                    imageListRequest.imageLocation = selectedLocation
                else
                    imageListRequest.imageLocation = ""

                if(ProjectUtil.getRadius(requireActivity()).equals("0")){
                    ProjectUtil.setRadius(requireActivity(), "15")
                }


                val apiService = YelpFusionApiFactory.getApplication().create(
                    YelpFusionApi::class.java
                )
                val getAllComplaint = apiService.imageSearch("application/json", imageListRequest)
                getAllComplaint.enqueue(object : Callback<ImageListResponse?> {
                    override fun onResponse(
                        call: Call<ImageListResponse?>, response: Response<ImageListResponse?>
                    ) {
                        val getAllData = response.body()
                        val handler = Handler()
                        handler.postDelayed(Runnable {
                            businesses.clear()
                            tagList.clear()
                            timeList.clear()
                            locationList.clear()

                            if(getAllData == null){
                                val bundle = Bundle()
                                bundle.putString("fragmentLayoutName", "")
                                shimmerFramlayout!!.visibility = View.GONE
                                animation_view!!.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                                refreshLayout!!.visibility = View.VISIBLE
                                if (refreshLayout!!.isRefreshing) {
                                    refreshLayout!!.isRefreshing = false
                                }
                                ProjectUtil.dismissDialog(customDialog)
                                return@Runnable
                            }else {
                                businesses.addAll(getAllData.data)
                                tagList.addAll(getAllData.allTags.split(","))
                                tagList.add(0, "Filter by Tag")
                                tagSpinner?.setItem(tagList)
//                                if (selectedTag != null)
//                                    tagSpinner?.selectedIndex = tagList.indexOf(selectedTag)
                                tagSpinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        if (position == 0) {
                                            selectedTag = null
                                        } else {
                                            selectedTag = tagList.get(position)
                                            allEntertainmentData
                                        }
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                                })

                                timeList.addAll(getAllData.timeTags.split(","))
                                timeList.add(0, "Filter by Time")
                                timeSpinner?.setItem(timeList)
//                                if (selectedTime != null)
//                                    timeSpinner?.selectedIndex = timeList.indexOf(selectedTime)
                                timeSpinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        if (position == 0) {
                                            selectedTime = null
                                        } else {
                                            selectedTime = timeList.get(position)
                                            allEntertainmentData
                                        }
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                                })
                                locationList.addAll(getAllData.locationTags.split(","))
                                locationList.add(0, "Filter by Location")
                                locationSpinner?.setItem(locationList)
//                                if (selectedLocation != null)
//                                    locationSpinner?.selectedIndex = locationList.indexOf(selectedLocation)
                                locationSpinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        if (position == 0) {
                                            selectedLocation = null
                                        } else {
                                            selectedLocation = locationList.get(position)
                                            allEntertainmentData
                                        }
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                                })
                                if (getAllData.data.size <= 0) {
                                    val bundle = Bundle()
                                    bundle.putString("fragmentLayoutName", "")
                                    shimmerFramlayout!!.visibility = View.GONE
                                    animation_view!!.visibility = View.VISIBLE
                                    recyclerView!!.visibility = View.GONE
                                    refreshLayout!!.visibility = View.VISIBLE
                                    if (refreshLayout!!.isRefreshing) {
                                        refreshLayout!!.isRefreshing = false
                                    }
                                    ProjectUtil.dismissDialog(customDialog)
                                    return@Runnable
                                } else {
                                    animation_view!!.visibility = View.GONE
                                    recyclerView!!.visibility = View.VISIBLE
                                }

                                /** Tip: code modified -: umesh **/
//                            mLayoutManager = LinearLayoutManager(
//                                activity
//                            )


                                // Tip: horizontal list -: umesh
                                // Wait until the RecyclerView is laid out.
                                recyclerView!!.post { // Shift the view to snap  near the center of the screen.
                                    // This does not have to be precise.
                                    val dx =
                                        (recyclerView!!.width - recyclerView!!.getChildAt(0).width) / 2
                                    recyclerView!!.scrollBy(-dx, 0)
                                    // Assign the LinearSnapHelper that will initially snap the near-center view.
                                    val snapHelper = LinearSnapHelper()
                                    recyclerView!!.onFlingListener = null
                                    snapHelper.attachToRecyclerView(recyclerView)
                                }
                                // initial image setup if available
                                globalPosition = if (businesses.size > 6) 2 else 0
                                selectMiddleItem(globalPosition)

//                            mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//                             app:smsp_hint="Filter by Location" = mLayoutManager

//                            val staggeredGridLayoutManager = mLayoutManager
//                                HorizontalScrollView(requireActivity())
//                            recyclerView!!.layoutManager = GridLayoutManager(activity, 3)
//                            recyclerView!!.layoutManager = staggeredGridLayoutManager

                                /** End of modification **/

                                registerPetAdapter!!.notifyDataSetChanged()
                                shimmerFramlayout!!.visibility = View.GONE
                                refreshLayout!!.visibility = View.VISIBLE
                                if (refreshLayout!!.isRefreshing) {
                                    refreshLayout!!.isRefreshing = false
                                }

                                spinnerArrayAdapter?.notifyDataSetChanged()
                                spinnerArrayAdapterTime?.notifyDataSetChanged()
                                spinnerArrayAdapterLocation?.notifyDataSetChanged()

                                ProjectUtil.dismissDialog(customDialog)
                            }
                        }, 2000)
                    }

                    override fun onFailure(call: Call<ImageListResponse?>, t: Throwable) {
                        t.printStackTrace()
                        if(t == null){
                            ProjectUtil.customToast(activity, t.toString())
                        }

                        ProjectUtil.dismissDialog(customDialog)
                        shimmerFramlayout!!.visibility = View.GONE
                        animation_view!!.visibility = View.VISIBLE
                        recyclerView!!.visibility = View.GONE
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                ProjectUtil.customToast(activity, e.toString())
                ProjectUtil.dismissDialog(customDialog)
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
            }
        }


    override fun onResume() {
        super.onResume()
        if (ProjectUtil.getPhotoPath(activity).isNotEmpty() && ProjectUtil.getPhotoPath(activity) != null
        ) {
//            ProjectUtil.customToast(activity,"working")
//            onImageUpload(ProjectUtil.getPhotoPath(activity))
            var fetchProfileData = ProjectUtil.getPhotoPath(activity).split(",")
//            ProjectUtil.customToast(activity, fetchProfileData.toString())
            ProjectUtil.setPhotoPath(activity, "")
            ProjectUtil.setPhotoPath(activity, null)
            var imageList= ArrayList<String>()
            fetchProfileData.forEachIndexed { index, e ->
                if(index != fetchProfileData.size-1)
                    imageList.add(e)
            }
            var fetchImageLocationData = ProjectUtil.getPhotoMetaData(activity).split(",")
//            ProjectUtil.customToast(activity, fetchProfileData.toString())
            var imageLatLngList= ArrayList<String>()
            fetchImageLocationData.forEachIndexed { index, e ->
                if(index != fetchProfileData.size-1)
                    imageLatLngList.add(e)
            }
            var fetchImageDateTimeData = ProjectUtil.getPhotoDateTimeData(activity).split(",")
            var imageDateTimeList= java.util.ArrayList<String>()
            fetchImageDateTimeData.forEachIndexed { index, e ->
                if(index != fetchProfileData.size-1)
                    imageDateTimeList.add(e)
            }
            var imageUploadRequest = ImageUploadRequest()
            imageUploadRequest.imageData = imageList
            imageUploadRequest.location = imageLatLngList
            imageUploadRequest.createdDateTime = imageDateTimeList
            imageUploadRequest.userID = ProjectUtil.getUserId(activity)
            imageUploadRequest.imageId = "" + System.currentTimeMillis()
            imageUploadRequest.imageName = "" + System.currentTimeMillis()
            imageUploadRequest.imageTag = "public"
            imageUploadRequest.action = "add"
            imageUploadRequest.mode = "public"
            imageUploadRequest.imageLat = ProjectUtil.getLotitude(activity)
            imageUploadRequest.imageLong = ProjectUtil.getLongitude(activity)
            imageUploadRequest.currentLat = ProjectUtil.getCurrentLotitude(activity)
            imageUploadRequest.currentLng = ProjectUtil.getCurrentLongitude(activity)
            imageUploadRequest.timeZone = ProjectUtil.getCurrentTimeZone()
            var bundle = Bundle()
            bundle.putParcelable("data", imageUploadRequest)
            startActivity(Intent(activity, ImageDetailsUploadFragment::class.java).putExtra("data", bundle))

        } else {
            allEntertainmentData
        }
    }

    fun openPrivate() {
        loadFragmentGallery()
        var textView1 = requireActivity().findViewById<View>(R.id.textView1)
        var textView2 = requireActivity().findViewById<View>(R.id.textView2)
        textView2.visibility = View.VISIBLE
        textView1.visibility = View.INVISIBLE
    }

    private fun loadFragmentGallery(): Boolean {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_gallery, PrivateFragment())
            .commit()
        return true
    }

    private fun requestCameraImage() {
        (activity as MainActivity?)!!.requestCameraImage()
    }

    fun onImageDelete(postion: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure. Do you want to delete this image?")

        builder.setPositiveButton("Yes") { dialog, which ->
            try {

                var data = businesses.get(postion)
                animation_view!!.visibility = View.GONE
                val imageUploadRequest = ImageUploadRequest()

                imageUploadRequest.userID = ProjectUtil.getUserId(activity)
                imageUploadRequest.imageId = data.imgID
                imageUploadRequest.imageName = "" + System.currentTimeMillis()
                imageUploadRequest.imageTag = "public"
                imageUploadRequest.action = "delete"
                imageUploadRequest.mode = "public"
                imageUploadRequest.imageLat = ProjectUtil.getLotitude(activity)
                imageUploadRequest.imageLong = ProjectUtil.getLongitude(activity)
                imageUploadRequest.currentLat = ProjectUtil.getCurrentLotitude(activity)
                imageUploadRequest.currentLng = ProjectUtil.getCurrentLongitude(activity)
                imageUploadRequest.timeZone = ProjectUtil.getCurrentTimeZone()

                val apiService = YelpFusionApiFactory.getApplication().create(
                    YelpFusionApi::class.java
                )
                val getAllComplaint = apiService.imageDelete("application/json", imageUploadRequest)
                getAllComplaint.enqueue(object : Callback<ImageListResponse?> {
                    override fun onResponse(
                        call: Call<ImageListResponse?>, response: Response<ImageListResponse?>
                    ) {
                        val getAllData = response.body()
                        val handler = Handler()
                        handler.postDelayed(Runnable {
                            ProjectUtil.setPhotoPath(activity, "")
                            allEntertainmentData

                        }, 2000)
                    }

                    override fun onFailure(call: Call<ImageListResponse?>, t: Throwable) {
                        t.printStackTrace()
                        ProjectUtil.customToast(activity, t.toString())
                        ProjectUtil.dismissDialog(customDialog)
                        ProjectUtil.setPhotoPath(activity, "")
                        shimmerFramlayout!!.visibility = View.INVISIBLE
                    }
                })
            } catch (e: Exception) {
                ProjectUtil.customToast(activity, e.toString())
                ProjectUtil.dismissDialog(customDialog)
                e.printStackTrace()
                ProjectUtil.setPhotoPath(activity, "")
                shimmerFramlayout!!.visibility = View.INVISIBLE
            }
        }

        builder.setNegativeButton("No") { dialog, which ->

        }

        builder.show()

    }

    override fun onEditClickEvent(postion: Int) {
        var data = businesses.get(postion)
        var bundle = Bundle()
        bundle.putParcelable("data", data)

        startActivity(Intent(activity, ImageDetailsEditFragment::class.java).putExtra("data", bundle))
    }

    override fun onImageClickEvent(postion: Int) {
        var data = businesses.get(postion)
        var bundle = Bundle()
        bundle.putParcelable("data", data)

//        try {
//            recyclerView!!.post { // Shift the view to snap  near the center of the screen.
//                // This does not have to be precise.
//                val dx =
//                    (recyclerView!!.width -
//                            recyclerView!!.getChildAt(0).width) / 2
//                recyclerView!!.smoothScrollBy(dx, 0)
//                // Assign the LinearSnapHelper that will initially snap the near-center view.
//                val snapHelper = LinearSnapHelper()
//                recyclerView!!.setOnFlingListener(null)
//                snapHelper.attachToRecyclerView(recyclerView)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace();
//        }
        globalPosition = postion
        selectMiddleItem(postion)


//        startActivityForResult(Intent(activity, ImageViewerActivity1::class.java).putExtra("data", bundle), 10000)

    }

    // implementation of method that is called from OnScrollListener
    private fun selectMiddleItem(position: Int) {
        val firstVisibleIndex = mLayoutManager!!.findFirstVisibleItemPosition()
        val lastVisibleIndex = mLayoutManager!!.findLastVisibleItemPosition()
        val visibleIndexes = listOf(firstVisibleIndex..lastVisibleIndex).flatten()

//                holder.parentLayout.setBackgroundResource(R.drawable.selected_image_highlighter);

        if (businesses.get(position).thumb != null) {
//            val layoutParams: FrameLayout.LayoutParams =
//                FrameLayout.LayoutParams(businesses.get(position).getThumbWidth(),
//                    businesses.get(position).getThumbHeight())
//            myImg?.setLayoutParams(layoutParams)
            try {
                Picasso.get()
                    .load(businesses.get(position).path)
                    .placeholder(R.drawable.splash)
                    .into(myImg)

                onSwipeTouchListener = OnSwipeTouchListener(activity, myImg as View, this)

                myImg?.setOnClickListener {
                    var data = businesses.get(position)
                    var bundle = Bundle()
                    bundle.putParcelableArrayList("allData", businesses)
                    bundle.putInt("position", position)
                    startActivity(Intent(activity, ImageViewerActivity1::class.java).putExtra("data", bundle))
                }
                registerPetAdapter?.setSelection(position)
//                frameLayout_myImg?.setOnClickListener({
//                    var data = businesses.get(position)
//                    var bundle = Bundle()
//                    bundle.putParcelableArrayList("allData", businesses)
//                    bundle.putInt("position", position)
//                    startActivity(Intent(activity, ImageViewerActivity1::class.java).putExtra("data", bundle))
//                })

                //                ProjectUtil.customToast(mContext,getData.getPath());
                Log.d("PATH", businesses.get(position).path)
                Log.d("PATH2", businesses.get(position).thumb)
            } catch (e: java.lang.Exception) {
                Log.d("PATH", e.toString())
            }
        }

        var j = 0
        for (i in visibleIndexes) {
            val vh = recyclerView!!.findViewHolderForLayoutPosition(i)
            if (vh?.itemView == null) {
                continue
            }
            val location = IntArray(2)
            vh.itemView.getLocationOnScreen(location)
            val x = location[0]
            val halfWidth = vh.itemView.width * .5
            val rightSide = x + halfWidth
            val leftSide = x - halfWidth
//            val isInMiddle = requireActivity().resources.displayMetrics.widthPixels * .5 in leftSide..rightSide

            if (position == i) {
                if (j == 0) {
                    // move to left
                    recyclerView!!.smoothScrollBy(if (leftSide.toInt() < 0) leftSide.toInt() * 2 else -leftSide.toInt() * 2 , 0)
                } else if (j == 1) {
                    // move to right
                    recyclerView!!.smoothScrollBy(if (rightSide.toInt() < 0) rightSide.toInt() else -rightSide.toInt(), 0)
                } else if (j == 3) {
                    // move to left
                    recyclerView!!.smoothScrollBy(if (leftSide.toInt() > 0) leftSide.toInt() / 2 else -leftSide.toInt() / 2 , 0)
                } else if (j == 4) {
                    // move to right
                    recyclerView!!.smoothScrollBy(if (rightSide.toInt() > 0) rightSide.toInt() / 2 else -rightSide.toInt() / 2 , 0)
                }
            }
            j += 1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activity
        if (requestCode == 10000 && resultCode == Activity.RESULT_OK) {
            Log.d("aaaaaaaaaaaaaa", data!!.getStringExtra("result").toString())
            if(data != null){
                try {
//                    tagSpinner?.selectedIndex = tagList.indexOf(data.getStringExtra("result").toString())
                    selectedTag = data.getStringExtra("result").toString()
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDeleteClickEvent(postion: Int) {
        onImageDelete(postion)
    }

    private fun loadFragment(fragment: Fragment?, bundle: Bundle?): Boolean {
        //switching fragment
        if (fragment != null) {
            fragment.arguments = bundle

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_gallery, fragment)
                .commit()
            return true
        }
        return false
    }

    override fun swipeRight() {
        globalPosition  -= 1
        selectMiddleItem(globalPosition)
//        Toast.makeText(this@ImageViewerActivity1, "Swiped Right 1", Toast.LENGTH_SHORT).show()
    }

    override fun swipeLeft() {
        globalPosition  += 1
        selectMiddleItem(globalPosition)
//        Toast.makeText(this@ImageViewerActivity1, "Swiped Left 2", Toast.LENGTH_SHORT).show()
    }

}