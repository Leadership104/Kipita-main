package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.adapter.GroupMemberAdapter
import com.mytum.model.GroupListRewuest
import com.mytum.model.MamberManageItemObject
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import com.mytum.utils.MapWrapperLayout
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Objects


class LocationMapFragment : Fragment(), OnMapReadyCallback {
    var mapWrapperLayout: MapWrapperLayout? = null
    var customDialog: Dialog? = null
    var title: String? = null
    var gId: String? = null
    var toolbar_home: Toolbar? = null
    var dataList: ArrayList<MamberManageItemObject> = ArrayList()
    var categoryAdapter: GroupMemberAdapter? = null
    private var mMap: GoogleMap? = null
    var mPointMarker: Marker? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location_map, container, false)
        title = arguments?.getString("title")
        gId = arguments?.getString("id")
        toolbar_home = view.findViewById(R.id.toolbar_home)
        mapWrapperLayout = view.findViewById(R.id.rl_map)
        customDialog = activity?.let { Dialog(it) }
        setUpMap()
        return view
    }

    override fun onResume() {
        super.onResume()
        allEntertainmentData1
    }

    @SuppressLint("SetTextI18n")
    fun reresh(data: String) {
        dataList.removeAll(dataList.toSet())
        dataList.clear()
        try {
            val j = data.replace("\\\\".toRegex(), "")
            val json = JSONObject(j)
            val jsonArray = JSONArray(json.getString("data"))
            for (m in 0 until jsonArray.length()) {
                val jsonObject1 = jsonArray.getJSONObject(m)
                val gson = Gson()
                dataList.add(
                    gson.fromJson(
                        jsonObject1.toString(),
                        MamberManageItemObject::class.java
                    )
                )
            }
            displayVehicleNo()
        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.customToast(activity, e.toString())
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    private fun displayVehicleNo() {
        try {
            for (i in dataList.indices) {
                val objectItem: MamberManageItemObject = dataList[i]
                try {
                    val mString = objectItem.lastLocation!!.split(",").toTypedArray()
                    val  name = objectItem.name;
                    if (mString.isNotEmpty()) {
                        val lat: Double = mString[0].toDouble()
                        val lng: Double = mString[1].toDouble()
                        addMarkerNo(lat, lng, name,i)
                    }
                } catch (ignored: NumberFormatException) {
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun addMarkerNo(
        lat: Double,
        lng: Double,
        name: String,
        i: Int
    ) {
        val latLng = LatLng(lat, lng)
//        mMap?.addMarker(
//            MarkerOptions()
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                .position(latLng)
//                .draggable(false)
//        )
        if (i == 0) {
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 8f))
        }

        val bitmapdraw = ContextCompat.getDrawable(
            requireActivity(),
            R.drawable.icon_map_pin_everythingcivic
        ) as BitmapDrawable?
        val b = bitmapdraw!!.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, 70, 70, false)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        mPointMarker = mMap!!.addMarker(
            MarkerOptions().position(latLng)
                .title(name)
                .visible(true) //                .snippet(addressNew)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .anchor(0.5f, 1.0f)
        )
        mPointMarker!!.showInfoWindow()

        //        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LocationOnMapActivity.this));
        mMap!!.setOnMarkerClickListener(onMarkerClickedListener)

    }

    private val onMarkerClickedListener =
        OnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            true
        }


    private val allEntertainmentData1: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = GroupListRewuest()
                data.action = "allMembers"
                data.groupID = gId
                val gson = Gson()
                val jsonString = gson.toJson(data)
                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost(RetrofitClient.PRODUCTION_URL + "group")
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
                        activity?.runOnUiThread(Runnable {
                            reresh(responseString);
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

    private fun setUpMap() {
        if (mMap == null) {
            (Objects.requireNonNull(
                childFragmentManager.findFragmentById(R.id.bin_location_map)
            ) as SupportMapFragment).getMapAsync(
                this
            )
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar_home!!.setTitle(title)
        toolbar_home!!.setTitleTextColor(resources.getColor(R.color.black))
        toolbar_home!!.setNavigationIcon(R.drawable.ic_backarrow_sub_menu) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_home!!.setNavigationOnClickListener {
            // do something when click navigation
            activity!!.onBackPressed()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        if (mMap != null) {
            return
        }
        mMap = map
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    ProjectUtil.getLotitude(
                        MyApplication.getInstance().applicationContext
                    ).toDouble(),
                    ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        .toDouble()
                ), 13f
            )
        )
        mapWrapperLayout?.init(
            this.mMap,
            getPixelsFromDp(
                requireActivity(),
                20f
            )
        )
        startMap()
    }

    @SuppressLint("MissingPermission")
    private fun startMap() {
        mMap!!.isMyLocationEnabled = false
        mMap!!.uiSettings.isCompassEnabled = true
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
    }

    fun getPixelsFromDp(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}