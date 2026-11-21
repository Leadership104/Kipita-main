package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.media.ExifInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk
import com.google.gson.Gson
import com.mytum.LoginActivity
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.client.connection.YelpFusionApi
import com.mytum.client.connection.YelpFusionApiFactory
import com.mytum.model.AddDeleteMember
import com.mytum.model.CreateGroupeItemObject
import com.mytum.model.DeleteGroupList
import com.mytum.model.DeleteMemberData
import com.mytum.model.GroupListRewuest
import com.mytum.model.ImageList.ImageUploadRequest
import com.mytum.model.ImageList.ImageUploadResponse
import com.mytum.model.InviteMemberData
import com.mytum.model.MamberManageItemObject
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import com.squareup.picasso.Picasso
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

private var selectDataList: ArrayList<MamberManageItemObject> = ArrayList()
var groupMemberUserId: String? = ""

class MembersManageFragment : Fragment() {
    var pw: PopupWindow? = null
    var customDialog: Dialog? = null
    var title: String? = null
    var gId: String? = null
    var gType: String? = null
    var toolbar_home: Toolbar? = null
    lateinit var btnInvites: Button
    var text_member: TextView? = null
    var dataAddList: ArrayList<CreateGroupeItemObject> = ArrayList()
    var dataList: ArrayList<MamberManageItemObject> = ArrayList()
    var recyclerView_member: RecyclerView? = null
    var categoryAdapter: MembersManageAdapter? = null
    var imageAdapter: GroupImageAdapter? = null
    var imageList1 = ArrayList<String>()
    var imageLocationList1 = ArrayList<String>()
    var imageDateTimeList1 = ArrayList<String>()
    var txtMsgValue: String? = null
    var uploadedImageIds: String? = ""
    var admin: Boolean? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_members_manage, container, false)
        title = arguments?.getString("title")
        gId = arguments?.getString("id")
        admin = arguments?.getBoolean("admin")
        toolbar_home = view.findViewById(R.id.toolbar_home)
        customDialog = activity?.let { Dialog(it) }!!
        recyclerView_member = view.findViewById(R.id.recyclerView_member)
        recyclerView_member!!.isNestedScrollingEnabled = false
        text_member = view.findViewById(R.id.text_member)
        btnInvites = view.findViewById<Button>(R.id.btn_invites)
        val btn_delete = view.findViewById<Button>(R.id.btn_delete)
        val ll_bottom = view.findViewById<LinearLayout>(R.id.ll_bottom)
        val img_chat = view.findViewById<ImageView>(R.id.img_chat)
        val img_location = view.findViewById<ImageView>(R.id.img_location)
        val img_location_send = view.findViewById<ImageView>(R.id.img_location_send)
        val img_rifresh = view.findViewById<ImageView>(R.id.img_rifresh)
        img_rifresh.setOnClickListener {
            onResume()
        }
        val lat =
            ProjectUtil.getCurrentLotitude(MyApplication.getInstance().applicationContext)
                .toDouble()
        val lng =
            ProjectUtil.getCurrentLongitude(MyApplication.getInstance().applicationContext)
                .toDouble()
        var util: PhoneNumberUtil =
            PhoneNumberUtil.createInstance(FacebookSdk.getApplicationContext());
        try {
            if (!arguments?.getString("list").equals("")) {
                val jsonArray = JSONArray(arguments?.getString("list"))
                btnInvites.text = "Invited Members (" + jsonArray.length() + ")"
                for (i in 0 until jsonArray.length()) {
                    val jsonObject1 = jsonArray.getJSONObject(i)
                    val phoneNumber = util.parse(
                        jsonObject1.getString("phone"),
                        ProjectUtil.getCountryCode(lat, lng)
                    )
                    val personItemObject = CreateGroupeItemObject()
                    personItemObject.phone =
                        util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                    personItemObject.firstName =
                        jsonObject1.getString("firstName")
                    personItemObject.email =
                        jsonObject1.getString("email")
                    personItemObject.lastName =
                        jsonObject1.getString("lastName")
                    dataAddList.add(personItemObject)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (admin == true) {
            ll_bottom.visibility = View.VISIBLE
        } else {
            ll_bottom.visibility = View.GONE
        }
        btn_delete.setOnClickListener {
            if (selectDataList.size > 0) {
                deleteData
            } else {
                ProjectUtil.customToast(
                    requireActivity(),
                    "Please select member "
                )
            }
        }
        btnInvites.setOnClickListener {
            if (dataAddList.size > 0) {
                addData
            } else {
                btnInvites.text = "Getting Contacts Please Wait..."
                btnInvites.isClickable = false
                val bundles = Bundle()
                bundles.putString("list", "")
                bundles.putString("type", "1")
                bundles.putString("id", gId)
                admin?.let { it1 -> bundles.putBoolean("admin", it1) }
                bundles.putString("title", title)
                bundles.putString("dis", "")
                loadFragmentInner(InviteMembersFragment(), bundles)
            }
        }
        val btn_map = view.findViewById<TextView>(R.id.btn_map)
        btn_map.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "Location on map")
            bundle.putString("id", gId)
            val advisoryDetailsFragment = LocationMapFragment()
            advisoryDetailsFragment.arguments = bundle
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, advisoryDetailsFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        img_location_send.setOnClickListener {
            gType = "sendLocationAll"
            sendData
        }
        img_location.setOnClickListener {
            gType = "requestLocationAll"
            sendData
        }
        img_chat.setOnClickListener {
            gType = "sendMessageAll"
            popupWindowInfo(requireActivity())
        }
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        allEntertainmentData1
        if (ProjectUtil.getPhotoPath(activity)
                .isNotEmpty() && ProjectUtil.getPhotoPath(activity) != null
        ) {
//            ProjectUtil.customToast(activity,"working")
//            onImageUpload(ProjectUtil.getPhotoPath(activity))
            var fetchProfileData = ProjectUtil.getPhotoPath(activity).split(",")
            var fetchImageLocationData = ProjectUtil.getPhotoMetaData(activity).split(",")
            var fetchImageDateTimeData = ProjectUtil.getPhotoDateTimeData(activity).split(",")
//            ProjectUtil.customToast(activity, fetchProfileData.toString())
            ProjectUtil.setPhotoPath(activity, "")
            ProjectUtil.setPhotoPath(activity, null)

            fetchProfileData.forEachIndexed { index, e ->
//                var temp = ImageArrayLocalList()
                if (index != fetchProfileData.size - 1) {
                    imageList1.add(e)
//                    temp.path = e
                }
                if (index != fetchImageLocationData.size - 1) {
//                    temp.location =fetchImageLocationData.get(index)
                    imageLocationList1.add(fetchImageLocationData.get(index))
                }
                if (index != fetchImageDateTimeData.size - 1) {
//                    temp.dateTime =fetchImageDateTimeData.get(index)
                    imageDateTimeList1.add(fetchImageDateTimeData.get(index))
                }
//                if(index != fetchProfileData.size-1) {
//                    imageList1.add(temp)
//                }

            }

            try {
                imageAdapter?.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                ProjectUtil.customToast(activity, e.toString())
            }

//            var imageUploadRequest = ImageUploadRequest()
//            imageUploadRequest.imageData = imageList
//            imageUploadRequest.location = imageLatLngList
//            imageUploadRequest.createdDateTime = imageDateTimeList
//            imageUploadRequest.userID = ProjectUtil.getUserId(activity)
//            imageUploadRequest.imageId = "" + System.currentTimeMillis()
//            imageUploadRequest.imageName = "" + System.currentTimeMillis()
//            imageUploadRequest.imageTag = "public"
//            imageUploadRequest.action = "add"
//            imageUploadRequest.mode = "public"
//            imageUploadRequest.imageLat = ProjectUtil.getLotitude(activity)
//            imageUploadRequest.imageLong = ProjectUtil.getLongitude(activity)
//            imageUploadRequest.currentLat = ProjectUtil.getCurrentLotitude(activity)
//            imageUploadRequest.currentLng = ProjectUtil.getCurrentLongitude(activity)
//            imageUploadRequest.timeZone = ProjectUtil.getCurrentTimeZone()
//            var bundle = Bundle()
//            bundle.putParcelable("data", imageUploadRequest)
//            startActivity(Intent(activity, ImageDetailsUploadFragment::class.java).putExtra("data", bundle))

        }
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
                        post.setHeader(
                            "lat",
                            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "lng",
                            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "userId",
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        )
                        var response = httpclient.execute(post)
                        var out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        var responseString = out.toString()
                        activity?.runOnUiThread(Runnable {
                            reresh(responseString)
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
            text_member!!.text = "Member (" + dataList.size + ")"
            recyclerView_member!!.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            categoryAdapter =
                MembersManageAdapter(
                    requireActivity(),
                    dataList,
                    customDialog
                ) { data ->
                    // do something with hero item when it's clicked
                    gType = data.name.toString()
                    if (gType.equals("0")) {
                        popupWindowInfo(requireActivity())
                    } else {
                        sendData
                    }
                }
            recyclerView_member!!.adapter = categoryAdapter
        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.customToast(activity, e.toString())
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    private val sendData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = DeleteGroupList()
                data.action = gType
                data.groupID = gId
                data.requesterID =
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                when (gType) {
                    "sendLocation" -> {
                        data.userId = groupMemberUserId
                        data.lat =
                            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        data.lng =
                            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        data.locationSharing = "0"
                    }

                    "requestLocation" -> {
                        data.userId = groupMemberUserId
                    }

                    "sendLocationAll" -> {
                        data.lat =
                            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        data.lng =
                            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        data.locationSharing = "0"
                    }

                    "sendMessageAll" -> {
//                        data.userId =
//                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        data.message = txtMsgValue
                        data.image = uploadedImageIds
                        data.locationSharing = "0"
                    }

                    "0" -> {
                        data.action = "sendMessage"
                        data.userId = groupMemberUserId
                        data.message = txtMsgValue
                        data.image = uploadedImageIds
                        data.locationSharing = "0"
                    }
                }
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
                        post.setHeader(
                            "lat",
                            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "lng",
                            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "userId",
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        )
                        val response = httpclient.execute(post)
                        val out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        val responseString = out.toString()
                        requireActivity().runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            val json = JSONObject(responseString)
                            if (json.getString("status").equals("success")) {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                            } else {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
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
            }
        }

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        fragment!!.arguments = bundle
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.fragment_container, fragment)
            ?.commit()
        return false
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar_home!!.setTitle(title)
        toolbar_home!!.setTitleTextColor(resources.getColor(R.color.black))
        toolbar_home!!.setNavigationIcon(R.drawable.ic_backarrow_sub_menu) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_home!!.setNavigationOnClickListener {
            // do something when click navigation
//            (activity as MainActivity?)!!.ll_toolbar_all.isVisible = true
            activity!!.onBackPressed()
        }
        toolbar_home!!.inflateMenu(R.menu.menu_permissions)
        toolbar_home!!.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.view_permissions -> {
                    // do something
                    showDialogLocation(requireActivity(), "")
                    true
                }

                else -> {
                    super.onOptionsItemSelected(it)
                }

            }
        }
    }

    private val addData: Unit
        get() = try {
            ProjectUtil.showDialog(customDialog, "Please wait...")
            val dataInviteList: ArrayList<InviteMemberData> = ArrayList()
            try {
                for (i in dataAddList.indices) {
                    val objectItem: CreateGroupeItemObject = dataAddList[i]
                    val dataItemObject =
                        InviteMemberData()
                    dataItemObject.email = objectItem.email
                    dataItemObject.phone = objectItem.phone
                    dataItemObject.firstName = objectItem.firstName
                    dataItemObject.lastName = objectItem.lastName
                    dataItemObject.source = ""
                    dataInviteList.add(dataItemObject)

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val data = AddDeleteMember()
            data.groupID = gId
            data.action = "editGroup"
            data.groupName = title
            data.groupDescription = ""
            data.createdBy =
                ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
            val jsonParam1 = JSONObject()
            val array = JSONArray()
            try {
                for (dataItemObject in dataInviteList) {
                    val jsonParam = JSONObject()
                    jsonParam.put("email", dataItemObject.email)
                    jsonParam.put("phone", dataItemObject.phone)
                    jsonParam.put("firstName", dataItemObject.firstName)
                    jsonParam.put("lastName", dataItemObject.lastName)
                    jsonParam.put("source", dataItemObject.source)
                    array.put(jsonParam)
                }
                jsonParam1.put("groupID", gId)
                jsonParam1.put("action", "editGroup")
                jsonParam1.put("groupName", title)
                jsonParam1.put("groupDescription", "")
                jsonParam1.put(
                    "createdBy",
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                )
                val ids = JSONArray()
                jsonParam1.put("DeletedMembers", ids)
                jsonParam1.put("invitedMembers", array)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            data.invitedMembers = array.toString()
            val j: String = jsonParam1.toString().replace("\\\\".toRegex(), "")
            Thread {
                try {
                    var httpclient: HttpClient = DefaultHttpClient()
                    var post = HttpPost(RetrofitClient.PRODUCTION_URL + "group")
                    var holder = JSONObject(j)
                    var se = StringEntity(holder.toString())
                    post.entity = se
                    post.setHeader("Accept", "application/json")
                    post.setHeader("Content-type", "application/json")
                    val response = httpclient.execute(post)
                    val out = ByteArrayOutputStream()
                    response.entity.writeTo(out)
                    val responseString = out.toString()
                    requireActivity().runOnUiThread(Runnable {
                        ProjectUtil.dismissDialog(customDialog)
                        if (responseString != "") {
                            val json = JSONObject(responseString)
                            if (json.getString("status").equals("success")) {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                                onResume()
                                dataAddList.clear()
                                btnInvites.text = "Invite Members"
                            } else {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                            }
                        } else {
                            ProjectUtil.customToast(
                                requireActivity(),
                                "Get status null"
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
        }

    private val deleteData: Unit
        get() = try {
            ProjectUtil.showDialog(customDialog, "Please wait...")
            var dataDeleteList: ArrayList<DeleteMemberData> = ArrayList()
            try {
                for (i in selectDataList.indices) {
                    val objectItem: MamberManageItemObject = selectDataList[i]
                    val dataItemObject1 =
                        DeleteMemberData()
                    dataItemObject1.userID = objectItem.userID
                    dataItemObject1.invitedID = objectItem.invitedID
                    dataDeleteList.add(dataItemObject1)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val data = AddDeleteMember()
            data.groupID = gId
            data.action = "editGroup"
            data.groupName = title
            data.groupDescription = ""
            data.createdBy =
                ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
            val jsonParam1 = JSONObject()
            val array = JSONArray()
            try {
                for (dataItemObject in dataDeleteList) {
                    val jsonParam = JSONObject()
                    jsonParam.put("userID", dataItemObject.userID)
                    jsonParam.put("invitedID", dataItemObject.invitedID)
                    array.put(jsonParam)
                }
                jsonParam1.put("groupID", gId)
                jsonParam1.put("action", "editGroup")
                jsonParam1.put("groupName", title)
                jsonParam1.put("groupDescription", "")
                jsonParam1.put(
                    "createdBy",
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                )
                jsonParam1.put("DeletedMembers", array)
                jsonParam1.put("invitedMembers", "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            data.deletedMembers = array.toString()
            val j: String = jsonParam1.toString().replace("\\\\".toRegex(), "")
            Thread {
                try {
                    var httpclient: HttpClient = DefaultHttpClient()
                    var post = HttpPost(RetrofitClient.PRODUCTION_URL + "group")
                    var holder = JSONObject(j)
                    var se = StringEntity(holder.toString())
                    post.entity = se
                    post.setHeader("Accept", "application/json")
                    post.setHeader("Content-type", "application/json")
                    val response = httpclient.execute(post)
                    val out = ByteArrayOutputStream()
                    response.entity.writeTo(out)
                    val responseString = out.toString()
                    requireActivity().runOnUiThread(Runnable {
                        ProjectUtil.dismissDialog(customDialog)
                        if (responseString != "") {
                            val json = JSONObject(responseString)
                            if (json.getString("status").equals("success")) {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                                onResume()
                            } else {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                            }
                        } else {
                            ProjectUtil.customToast(
                                requireActivity(),
                                "Get status null"
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
        }

    class MembersManageAdapter(
        private val mContext: Context,
        private val allParticipants: java.util.ArrayList<MamberManageItemObject>?,
        private val customDialog: Dialog?,
        val itemClickListener: (MamberManageItemObject) -> Unit
    ) :
        RecyclerView.Adapter<MembersManageAdapter.ComplaintHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintHolder {
            return ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_members_manage, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(
            holder: ComplaintHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {
            val getData = allParticipants!![position]
            holder.textName.text = getData.name
            if (!getData.lastLocationTime.equals("")) {
                holder.textSeen.visibility = View.VISIBLE
                holder.textSeen.text = "Last seen " + getData.lastLocationTime
            } else {
                holder.textSeen.visibility = View.GONE
            }
            val vehicle_location = Location("")
            val current_location = Location("")
            val mString = getData.lastLocation!!.split(",").toTypedArray()
            vehicle_location.latitude = mString[0].toDouble()
            vehicle_location.longitude = mString[1].toDouble()
            current_location.latitude =
                ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext).toDouble()
            current_location.longitude =
                ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext).toDouble()
            val distance = distanceCall(
                mString[0].toDouble(),
                mString[1].toDouble(),
                ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext).toDouble(),
                ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext).toDouble()
            )
            if (!getData.lastLocation.equals("") || getData.lastLocation != null) {
                holder.textEta.visibility = View.VISIBLE
            } else {
                holder.textEta.visibility = View.GONE
            }
            val distance1 =
                (current_location.distanceTo(vehicle_location) / 1000).toDouble() //in km
            if (ProjectUtil.getIsDistanceInMiles(MyApplication.getInstance().applicationContext)) {
                holder.textEta.text = ProjectUtil.decimalFormat.format(distance1) + "km away"
            } else {
                holder.textEta.text =
                    ProjectUtil.changeDistanceInMilesFromKm(distance1).toString() + "mi away"
            }

//            holder.textEta.text = DecimalFormat("###.##").format(distance / 10 * 60) + "mi away"

            holder.btn_request.setOnClickListener {
                getData.name = "requestLocation"
                groupMemberUserId = getData.userID
                itemClickListener(getData)
            }
            holder.btn_send.setOnClickListener {
                getData.name = "sendLocation"
                groupMemberUserId = getData.userID
                itemClickListener(getData)
            }
            holder.btn_message.setOnClickListener {
                getData.name = "0"
                groupMemberUserId = getData.userID
                itemClickListener(getData)
            }
            holder.btn_message.setOnClickListener {
                getData.name = "0"
                groupMemberUserId = getData.userID
                itemClickListener(getData)
            }
            holder.checkbox.tag = (getData)
            holder.checkbox.setOnCheckedChangeListener { compoundButton, b ->
                try {
                    if (b) {
                        selectDataList.add((compoundButton.tag) as MamberManageItemObject)
                        Log.d("aaaaaaaa tag", "" + compoundButton.tag)
                    } else {
                        selectDataList.remove((compoundButton.tag) as MamberManageItemObject)
                    }
                    Log.d("aaaaaa list", selectDataList.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun distanceCall(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val theta = lon1 - lon2
            var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(
                deg2rad(lat1)
            ) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
            dist = acos(dist)
            dist = rad2deg(dist)
            dist *= 60 * 1.1515
            return (dist)
        }

        private fun deg2rad(deg: Double): Double {
            return (deg * Math.PI / 180.0)
        }

        private fun rad2deg(rad: Double): Double {
            return (rad * 180.0 / Math.PI)
        }

        override fun getItemCount(): Int {
            return allParticipants?.size ?: 0
        }

        inner class ComplaintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textName: TextView = itemView.findViewById(R.id.textName)
            var textSeen: TextView = itemView.findViewById(R.id.textSeen)
            var textEta: TextView = itemView.findViewById(R.id.textEta)
            var btn_request: Button = itemView.findViewById(R.id.btn_request)
            var btn_send: Button = itemView.findViewById(R.id.btn_send)
            var btn_message: Button = itemView.findViewById(R.id.btn_message)
            var parentLayout: CardView = itemView.findViewById(R.id.parentLayout)
            var checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    private fun popupWindowInfo(requireActivity: FragmentActivity) {
        try {
            val inflater =
                requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.popup_send_message, null)
            pw = PopupWindow(
                layout,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                true
            )
//            pw.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogCreateLinkStyle)
//            pw?.animationStyle = R.style.DialogCreateLinkStyle//popup_window_animation
            pw?.animationStyle = R.style.pw_animation
//            pw?.setBackgroundDrawable(resources.getDrawable(R.drawable.back_round_top))
            pw?.showAtLocation(layout, Gravity.BOTTOM, 0, 0)
            val image_close = layout.findViewById<ImageView>(R.id.image_top)
            val recyclerView_image = layout.findViewById<RecyclerView>(R.id.recyclerView_image)
            val btn_select = layout.findViewById<Button>(R.id.btn_select)
            val btn_send = layout.findViewById<Button>(R.id.btn_send)
            val et_address = layout.findViewById<EditText>(R.id.address)
            val gridLayoutManager = GridLayoutManager(activity, 1)
            gridLayoutManager.orientation =
                LinearLayoutManager.HORIZONTAL // set Horizontal Orientation
            recyclerView_image!!.setLayoutManager(gridLayoutManager)
            imageAdapter = GroupImageAdapter(requireActivity, imageList1)
            recyclerView_image.adapter = imageAdapter
            btn_select.setOnClickListener { v: View? ->
                if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                    requestCameraImage()
//                    pw!!.dismiss()
                } else {
                    val i = Intent(activity, LoginActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    activity?.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
                    activity?.finish()
                }
            }

            btn_send.setOnClickListener { v: View? ->
                pw!!.dismiss()
                txtMsgValue = et_address.text.toString()
                if (imageList1.size > 0) {
                    var imageUploadRequest = ImageUploadRequest()
                    imageUploadRequest.imageData = imageList1
                    imageUploadRequest.location = imageLocationList1
                    imageUploadRequest.createdDateTime = imageDateTimeList1
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
                    onImageUpload(imageUploadRequest, et_address.text.toString())
                } else {
                    sendData
//                    sendMessage("", et_address.text.toString())
                }
            }

            image_close.setOnClickListener { v: View? -> pw?.dismiss() }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun requestCameraImage() {
        (activity as MainActivity?)!!.requestCameraImage()
    }

    private fun sendMessage(imageIds: String, textMsg: String) {

    }

    fun onImageUpload(provider: ImageUploadRequest, textMsg: String) {

        ProjectUtil.showDialog(customDialog, "Please wait...")
        try {
            var imageDataList = ArrayList<String>()
            var imageLatLngList = ArrayList<String>()
            var imageDateTimeList = ArrayList<String>()
            for (temp in provider.imageData) {
                var bitmap: Bitmap? = null
                var f = File(temp)

                var rotate = 0
                try {
                    var options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)

                    val matrix = Matrix()

                    val imageFile: File = File(temp)
                    val exif = ExifInterface(
                        imageFile.absolutePath
                    )
                    val orientation: Int = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                    }
                    matrix.postRotate(rotate.toFloat())
                    val rotatedBitmap: Bitmap = Bitmap.createBitmap(
                        bitmap!!, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true
                    )

                    imageDataList.add(ProjectUtil.getStringImage(rotatedBitmap))
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            for (temp in provider.location) {
                try {
                    imageLatLngList.add(temp)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            for (temp in provider.createdDateTime) {
                try {
                    imageDateTimeList.add(temp)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            var mode = "public"
            if (ProjectUtil.getPublicOrPrivate(activity)) {
                mode = "public"
            } else {
                mode = "private"
            }
            var imageUploadRequest = ImageUploadRequest()
            imageUploadRequest.imageData = imageDataList
            imageUploadRequest.location = imageLatLngList
            imageUploadRequest.createdDateTime = imageDateTimeList
            imageUploadRequest.userID = ProjectUtil.getUserId(activity)
            imageUploadRequest.imageId = "" + System.currentTimeMillis()
            imageUploadRequest.imageName = "" + System.currentTimeMillis()
//            imageUploadRequest.imageTag = tagList.toString().replace("]", "").replace("[", "")
            imageUploadRequest.action = "add"
            imageUploadRequest.mode = mode
            imageUploadRequest.dateAdded = "" + System.currentTimeMillis()
            imageUploadRequest.imageLat = ProjectUtil.getLotitude(activity)
            imageUploadRequest.imageLong = ProjectUtil.getLongitude(activity)
            imageUploadRequest.currentLat = ProjectUtil.getCurrentLotitude(activity)
            imageUploadRequest.currentLng = ProjectUtil.getCurrentLongitude(activity)
            imageUploadRequest.timeZone = ProjectUtil.getCurrentTimeZone()

            val gson = Gson()
            var daa = gson.toJson(imageUploadRequest)

//            uploadFile(f, imageUploadRequest)

            val apiService = YelpFusionApiFactory.getApplication().create(
                YelpFusionApi::class.java
            )
            val getAllComplaint =
                apiService.imageUploadGroup("application/json", imageUploadRequest)
            getAllComplaint.enqueue(object : Callback<ImageUploadResponse?> {
                override fun onResponse(
                    call: Call<ImageUploadResponse?>, response: Response<ImageUploadResponse?>
                ) {
                    var getAllData = response.body()
                    var handler = Handler()
                    var uploadedImagesIdList = StringBuilder("")
                    handler.postDelayed(Runnable {
                        var shouldShowFailedMsg = false
                        getAllData!!.data.forEachIndexed { index, e ->
//                            uploadedImagesIdList.append(e).append(",")
//                            tagList.get(index).status = getAllData!!.data.get(index).status
//                            tagList.get(index).message = getAllData!!.data.get(index).message
                            if (getAllData.data.get(index).status.equals("fail")) {
                                shouldShowFailedMsg = true
                            } else {
                                uploadedImagesIdList.append(getAllData.data.get(index).thumb)
                                    .append(",")

                            }
                        }

                        ProjectUtil.dismissDialog(customDialog)
                        ProjectUtil.setPhotoPath(activity, "")
                        ProjectUtil.setPhotoPath(activity, null)
                        if (!shouldShowFailedMsg) {
                            uploadedImageIds = uploadedImagesIdList.toString()
                            sendData
                        } else {
                            ProjectUtil.customToast(activity, "Failed to upload Images")
                        }
//                        finish()
                    }, 2000)
                }

                override fun onFailure(call: Call<ImageUploadResponse?>, t: Throwable) {
                    t.printStackTrace()
                    ProjectUtil.dismissDialog(customDialog)
                    ProjectUtil.customToast(activity, t.toString())
                    ProjectUtil.setPhotoPath(activity, "")

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.dismissDialog(customDialog)
            ProjectUtil.customToast(activity, e.toString())
            ProjectUtil.setPhotoPath(activity, "")
        }
    }

    class GroupImageAdapter(
        private val mContext: Context,
        private val allParticipants: java.util.ArrayList<String>?
    ) :
        RecyclerView.Adapter<GroupImageAdapter.ComplaintHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintHolder {
            return ComplaintHolder(
                LayoutInflater.from(mContext)
                    .inflate(R.layout.item_send_message_image_list, parent, false)
            )
        }

        override fun onBindViewHolder(
            holder: ComplaintHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {
            val getData = allParticipants!!.get(position)
            Picasso.get()
                .load(File(getData))
                .placeholder(R.drawable.splash)
                .into(holder.image_main)
//            Picasso.get().load(getData).into(holder.image_main)
            holder.parentLayout.setOnClickListener {
//                listner.onClickEvent(position)
            }
        }

        override fun getItemCount(): Int {
            return allParticipants?.size ?: 0
        }

        inner class ComplaintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var image_main: ImageView = itemView.findViewById(R.id.image_main)
            var parentLayout: CardView = itemView.findViewById(R.id.parentLayout)
        }
    }

    var status: String? = "0"
    private fun showDialogLocation(applicationContext: Context, s: String) {
        val alert = AlertDialog.Builder(applicationContext)
        var mView: View = layoutInflater.inflate(R.layout.dialog_noti_location, null)
        val text_top = mView.findViewById<TextView>(R.id.text_top)
        text_top.text = "Share Location"
        val text_details = mView.findViewById<TextView>(R.id.text_details)
        text_details.text = "Do you want to share your location with this Group"
        val btn_cancel = mView.findViewById<Button>(R.id.btn_cancel)
        val btn_okay = mView.findViewById<Button>(R.id.btn_okay)
        alert.setView(mView)
        val alertDialog = alert.create()
        alertDialog.setCanceledOnTouchOutside(false)
        btn_cancel.setOnClickListener { v: View? ->
            alertDialog.dismiss()
            status = "0"
            sendData2
        }
        btn_okay.setOnClickListener {
            status = "1"
            sendData2
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private val sendData2: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = DeleteGroupList()
                data.action = "locationTracking"
                data.groupID = gId
                data.userId =
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                data.status = status
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
                        post.setHeader(
                            "lat",
                            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "lng",
                            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        )
                        post.setHeader(
                            "userId",
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        )
                        val response = httpclient.execute(post)
                        val out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        val responseString = out.toString()
                        requireActivity().runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            val json = JSONObject(responseString)
                            if (json.getString("status").equals("success")) {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                            } else {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
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
            }
        }

    fun hideProgressBar() {
        ProjectUtil.dismissDialog(customDialog)
    }

}