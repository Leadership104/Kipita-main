package com.mytum.fragment.GallerySubFragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mytum.*

import com.mytum.proectUtil.ProjectUtil
import com.mytum.adapter.PrivateImageAdapter
import com.mytum.client.connection.YelpFusionApi
import com.mytum.client.connection.YelpFusionApiFactory
import com.mytum.listners.ImageClickPressed
import com.mytum.model.ImageList.ImageArrayListList
import com.mytum.model.ImageList.ImageListRequest
import com.mytum.model.ImageList.ImageListResponse
import com.mytum.model.ImageList.ImageUploadRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PrivateFragment : Fragment(), ImageClickPressed {
    var businesses = ArrayList<ImageArrayListList>()
    var customDialog: Dialog? = null
    private var fab: FloatingActionButton? = null
    private var mLayoutManager: LinearLayoutManager? = null
    var refreshLayout: SwipeRefreshLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    var animation_view: LinearLayout? = null
    var registerPetAdapter: PrivateImageAdapter? = null

    var myPhoto: Button? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.private_image, container, false)

        customDialog = Dialog(requireActivity()!!)
        refreshLayout = view!!.findViewById(R.id.swipeToRefresh_reg)
        animation_view = view!!.findViewById(R.id.animation_view)
        recyclerView = view!!.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view!!.findViewById(R.id.shimmerFramlayout)
        registerPetAdapter = PrivateImageAdapter(activity, businesses, this@PrivateFragment)
        recyclerView!!.setAdapter(registerPetAdapter)
        fab = view.findViewById(R.id.fab)
        allEntertainmentData
        refreshLayout!!.setOnRefreshListener(OnRefreshListener {
            shimmerFramlayout!!.setVisibility(View.VISIBLE)
            refreshLayout!!.setVisibility(View.GONE)
            allEntertainmentData
        })


        myPhoto = requireActivity().findViewById<Button>(R.id.btn2)




        fab!!.setOnClickListener(View.OnClickListener {
            if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                requestCameraImage()
            } else {
                val i = Intent(activity, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                getActivity()?.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
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
                val imageListRequest = ImageListRequest()
                imageListRequest.userID = ProjectUtil.getUserId(activity)
                imageListRequest.screenWidth = ProjectUtil.getScreenWidth(requireActivity())


                val apiService = YelpFusionApiFactory.getApplication().create(
                    YelpFusionApi::class.java
                )
                val getAllComplaint = apiService.imageList("application/json", imageListRequest)
                getAllComplaint.enqueue(object : Callback<ImageListResponse?> {
                    override fun onResponse(
                        call: Call<ImageListResponse?>, response: Response<ImageListResponse?>
                    ) {
                        val getAllData = response.body()
                        val handler = Handler()
                        handler.postDelayed(Runnable {
                            businesses.clear()
                            businesses.addAll(getAllData!!.data)
                            myPhoto!!.text = "My Photo (" + businesses.size + ")"
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
                            }else{
                                animation_view!!.visibility = View.GONE
                                recyclerView!!.visibility = View.VISIBLE
                            }
                            mLayoutManager = LinearLayoutManager(
                                activity
                            )

                            val staggeredGridLayoutManager =
                                StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
//                            recyclerView!!.layoutManager = GridLayoutManager(activity, 3)
                            recyclerView!!.layoutManager = staggeredGridLayoutManager
                            ProjectUtil.dismissDialog(customDialog)
                            registerPetAdapter!!.notifyDataSetChanged()
                            shimmerFramlayout!!.visibility = View.GONE
                            refreshLayout!!.visibility = View.VISIBLE
                            if (refreshLayout!!.isRefreshing) {
                                refreshLayout!!.isRefreshing = false
                            }
                        }, 2000)
                    }

                    override fun onFailure(call: Call<ImageListResponse?>, t: Throwable) {
                        t.printStackTrace()
                        ProjectUtil.dismissDialog(customDialog)
                        ProjectUtil.customToast(activity, t.toString())
                        shimmerFramlayout!!.visibility = View.GONE
                        animation_view!!.visibility = View.VISIBLE
                        recyclerView!!.visibility = View.GONE
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                ProjectUtil.dismissDialog(customDialog)
                ProjectUtil.customToast(activity, e.toString())
                shimmerFramlayout!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
            }
        }


    override fun onResume() {
        super.onResume()
        if (!ProjectUtil.getPhotoPath(activity)
                .isEmpty() && ProjectUtil.getPhotoPath(activity) != null
        ) {

            var fetchProfileData = ProjectUtil.getPhotoPath(activity).split(",")
//            ProjectUtil.customToast(activity, fetchProfileData.toString())

            ProjectUtil.setPhotoPath(activity, "")
            ProjectUtil.setPhotoPath(activity, null)
            var imageList= ArrayList<String>()
            fetchProfileData.forEachIndexed { index, e ->
                if(index != fetchProfileData.size-1)
                    imageList?.add(e)
            }
            var fetchImageLocationData = ProjectUtil.getPhotoMetaData(activity).split(",")
//            ProjectUtil.customToast(activity, fetchProfileData.toString())

            var imageLatLngList= java.util.ArrayList<String>()
            fetchImageLocationData.forEachIndexed { index, e ->
                if(index != fetchProfileData.size-1)
                    imageLatLngList?.add(e)
            }

            var fetchImageDateTimeData = ProjectUtil.getPhotoDateTimeData(activity).split(",")
            var imageDateTimeList= java.util.ArrayList<String>()
            fetchImageDateTimeData.forEachIndexed { index, e ->
                if(index != fetchProfileData.size-1)
                    imageDateTimeList?.add(e)
            }

            var imageUploadRequest = ImageUploadRequest()
            imageUploadRequest.imageData =  imageList
            imageUploadRequest.location = imageLatLngList
            imageUploadRequest.createdDateTime = imageDateTimeList
            imageUploadRequest.userID = ProjectUtil.getUserId(activity)
            imageUploadRequest.imageId = "" + System.currentTimeMillis();
            imageUploadRequest.imageName = "" + System.currentTimeMillis();
            imageUploadRequest.imageTag = "private"
            imageUploadRequest.action = "add"
            imageUploadRequest.mode = "private"
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

    private fun requestCameraImage() {
        (activity as MainActivity?)!!.requestCameraImage()
    }

    fun onImageDelete(postion: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure. Do you want to delete this image?")

        builder.setPositiveButton("Yes") { dialog, which ->
            ProjectUtil.showDialog(customDialog, "Please wait...")
            try {

                var data = businesses.get(postion)
                animation_view!!.visibility = View.GONE
                val imageUploadRequest = ImageUploadRequest()

                imageUploadRequest.userID = ProjectUtil.getUserId(activity)
                imageUploadRequest.imageId = data.imgID
                imageUploadRequest.imageName = "" + System.currentTimeMillis();
                imageUploadRequest.imageTag = ""
                imageUploadRequest.action = "delete"
                imageUploadRequest.mode = "private"
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
                e.printStackTrace()
                ProjectUtil.customToast(activity, e.toString())
                ProjectUtil.dismissDialog(customDialog)
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
        bundle.putParcelableArrayList("allData", businesses)
        bundle.putInt("position", postion)

        startActivity(Intent(activity, ImageViewerActivity1::class.java).putExtra("data", bundle))

    }

    override fun onDeleteClickEvent(postion: Int) {
        onImageDelete(postion)
    }



}