package com.mytum.fragment.AdviserySubFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.adapter.CheckBoxAdapter
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.AdvisaryRewuest
import com.mytum.model.AdvisoryUpdateServiceAndSequencePojo
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.CheckBoxResponse.Datum
import com.mytum.model.NewAdvisaryResponse.AdvisaryListResponse
import com.mytum.model.PlannerAddServicePojo
import com.mytum.model.Section
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class AdvisaryCheckBoxFragment : AppCompatActivity(), ComplaintClickPressed {
    var businesses = ArrayList<Datum>()
    var customDialog: Dialog? = null
    var dataList: ArrayList<AdvisaryListResponse> = ArrayList()
    private var mLayoutManager: LinearLayoutManager? = null
    var refreshLayoutParent: LinearLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    var search: ImageView? = null
    var animation_view: LinearLayout? = null
    var searchVoice: ImageView? = null
    var registerPetAdapter: CheckBoxAdapter? = null
    var offset = 0;
    var isFromPlanner = false
    var serviceName: EditText? = null
    var serviceUrl: EditText? = null
    var pw: PopupWindow? = null

    var selectedEditDeleteItemPosition: Int? = null

    //    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.entertenment_advisary, container, false)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        val yourView = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = resources.getColor(R.color.blue_bottom)
            if (yourView != null) {
                yourView.systemUiVisibility = 0
            }
        }
        setContentView(R.layout.entertenment_advisary)

        customDialog = Dialog(this)
        val btn_create_link = findViewById<Button>(R.id.btn_create_link)

        search = findViewById(R.id.search)
        searchVoice = findViewById(R.id.searchVoice)
        refreshLayoutParent = findViewById(R.id.swipeToRefresh_parent)
        animation_view = findViewById(R.id.animation_view)
        recyclerView = findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = findViewById(R.id.shimmerFramlayout)
        registerPetAdapter =
            CheckBoxAdapter(this, businesses, this@AdvisaryCheckBoxFragment, dataList)
        recyclerView!!.setAdapter(registerPetAdapter)
        businesses.clear()

        var bundle = intent.getBundleExtra("data")
        var transactionList: ArrayList<AdvisaryListResponse>? =
            (bundle!!.getParcelableArrayList("list"))
        isFromPlanner = bundle.getBoolean("isFromPlanner")

        val backarrow = findViewById<ImageView>(R.id.backarrow)
        backarrow!!.setOnClickListener(View.OnClickListener { v: View? -> finish() })
        val imageViewDone = findViewById<ImageView>(R.id.imageViewDone)
        imageViewDone.setOnClickListener(View.OnClickListener { v: View? ->
            updateMyList
        })
        val textLabelInfo = findViewById<TextView>(R.id.textLabelInfo)
        val lineView = findViewById<View>(R.id.lineView)
        val grp_name_info = findViewById<TextView>(R.id.grp_name_info)
        if (isFromPlanner) textLabelInfo.text =
            "Choose service to show on Travel section" else textLabelInfo.text =
            "Choose service to show on advisory section"
        if (isFromPlanner) lineView.setBackgroundColor(resources.getColor(R.color.green_bottom)) else lineView.setBackgroundColor(
            resources.getColor(R.color.yellow_bottom)
        )
        if(isFromPlanner) btn_create_link.visibility = View.VISIBLE  else btn_create_link.visibility = View.GONE
        btn_create_link.setOnClickListener(View.OnClickListener { v: View? ->
            popupCreateLink(null)
        })
        dataList.addAll(transactionList!!);
        allEntertainmentData
    }

    protected val allEntertainmentData: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var url =
                    if (isFromPlanner) RetrofitClient.PRODUCTION_URL + "plannerSections" else RetrofitClient.PRODUCTION_URL + "advisorySections"
                Thread {
                    try {
                        var data = AdvisaryRewuest();
                        data.action = "GetAllList";
                        data.zipCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                        data.timeZone = ProjectUtil.getCurrentTimeZone();
                        data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext);

                        var gson = Gson();
                        var jsonString = gson.toJson(data);

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
                        runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            var gson = Gson()
                            var getAllData: CheckBoxResponse =
                                gson.fromJson(responseString, CheckBoxResponse::class.java)
                            var businessesMian = ArrayList<Datum>()


                            for (photoList1 in getAllData!!.data) {

                                for (oldData in dataList) {
                                    if (oldData.type != "112233") {
                                        if (oldData.type.equals(photoList1.type)) {
                                            photoList1.isChecked = true;
                                            break
                                        } else {
                                            photoList1.isChecked = false;
                                        }
                                    }
                                }
                                businessesMian.add(photoList1);
                            }

                            businesses.addAll(businessesMian)
//                            businesses.addAll(getAllData.data)
                            ProjectUtil.dismissDialog(customDialog)
                            if (getAllData.data.size <= 0) {
                                val bundle = Bundle()
                                bundle.putString("fragmentLayoutName", "")
                                shimmerFramlayout!!.visibility = View.GONE
                                animation_view!!.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                                refreshLayoutParent!!.visibility = View.VISIBLE
                                return@Runnable
                            }

                            mLayoutManager = LinearLayoutManager(this)
                            recyclerView!!.layoutManager = mLayoutManager
                            registerPetAdapter =
                                CheckBoxAdapter(
                                    this,
                                    businesses,
                                    this@AdvisaryCheckBoxFragment,
                                    dataList
                                )
                            recyclerView!!.adapter = registerPetAdapter
                            registerPetAdapter!!.notifyDataSetChanged()
                            recyclerView!!.visibility = View.VISIBLE
                            recyclerView!!.layoutManager?.scrollToPosition(offset)
                            shimmerFramlayout!!.visibility = View.GONE
                            animation_view!!.visibility = View.GONE
                            refreshLayoutParent!!.visibility = View.VISIBLE

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

    protected val updateMyList: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var url =
                    if (isFromPlanner) RetrofitClient.PRODUCTION_URL + "plannerSections" else RetrofitClient.PRODUCTION_URL + "advisorySections"
                Thread {
                    try {
                        val userId =
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        val zipCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                        val countryCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)

                        var sectionList: ArrayList<Section> = arrayListOf()
                        for (temp in businesses) {
                            if (temp.isChecked) {
                                sectionList.add(Section(order = temp.order, type = temp.type))
                            }
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
                        runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            var gson = Gson()
                            var getAllData: CheckBoxResponse =
                                gson.fromJson(responseString, CheckBoxResponse::class.java)
                            if (getAllData.status.equals("success")) {
                                ProjectUtil.customToast(
                                    this@AdvisaryCheckBoxFragment,
                                    "Services are added successfully"
                                )
                                finish()
                            } else {
                                ProjectUtil.customToast(
                                    this@AdvisaryCheckBoxFragment,
                                    "Error while adding the services."
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



    //For planner create own link section
    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    private fun popupCreateLink(selectedData: Datum?) {
        try {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.popup_create_own_link, null)
            pw = PopupWindow(
                layout,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )
//            pw.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogCreateLinkStyle)
            pw?.animationStyle = R.style.DialogCreateLinkStyle//popup_window_animation
            pw?.setBackgroundDrawable(resources.getDrawable(R.drawable.back_round_top))
            // display the popup in the center
            pw?.showAtLocation(layout, Gravity.BOTTOM, 0, 0)
            serviceUrl = layout.findViewById(R.id.serviceUrl)
            serviceName = layout.findViewById(R.id.serviceName)
            val image_close = layout.findViewById<ImageView>(R.id.image_close)
            val btn_create_link = layout.findViewById<Button>(R.id.btn_create_link)

            if(selectedData != null) {
                serviceUrl?.setText(selectedData?.url)
                serviceName?.setText(selectedData?.title)
            }

            image_close.setOnClickListener { v: View? -> pw?.dismiss() }
            btn_create_link.setOnClickListener { v: View? ->
                if (serviceName?.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(MyApplication.getInstance().applicationContext, "Please Enter First Name")
                    serviceName?.setError("Please Enter First Name")
                } else if (serviceUrl?.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(MyApplication.getInstance().applicationContext, "Please Enter Mail Id")
                    serviceUrl?.setError("Please Enter Mail Id")
                }else{
                    if(selectedData != null) {
                        editService
                    }else{
                        saveService
                    }
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    protected val saveService: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var url = RetrofitClient.PRODUCTION_URL + "plannerSections"
                Thread {
                    try {
                        val userId =
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        val zipCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                        val countryCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)

                        var sectionList: ArrayList<Section> = arrayListOf()
                        for (temp in businesses) {
                            if (temp.isChecked) {
                                sectionList.add(Section(order = temp.order, type = temp.type))
                            }
                        }
                        var requestPojo = PlannerAddServicePojo(
                            userId = userId,
                            action = "AddService",
                            country = countryCode,
                            zipCode = zipCode,
                            timeZone = ProjectUtil.getCurrentTimeZone(),
                            serviceName = serviceName?.getText().toString(),
                            serviceURL= serviceUrl?.getText().toString(),
                            id = 0
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
                        runOnUiThread(Runnable {
                            businesses.removeAll(businesses)
                            businesses.clear()
                            ProjectUtil.dismissDialog(customDialog)
                            var gson = Gson()
                            var getAllData: CheckBoxResponse =
                                gson.fromJson(responseString, CheckBoxResponse::class.java)
                            var businessesMian = ArrayList<Datum>()


                            for (photoList1 in getAllData!!.data) {

                                for (oldData in dataList) {
                                    if (oldData.type != "112233") {
                                        if (oldData.type.equals(photoList1.type)) {
                                            photoList1.isChecked = true;
                                            break
                                        } else {
                                            photoList1.isChecked = false;
                                        }
                                    }
                                }
                                businessesMian.add(photoList1);
                            }

                            businesses.addAll(businessesMian)
                            pw?.dismiss()
//                            businesses.addAll(getAllData.data)
                            ProjectUtil.dismissDialog(customDialog)
                            if (getAllData.data.size <= 0) {
                                val bundle = Bundle()
                                bundle.putString("fragmentLayoutName", "")
                                shimmerFramlayout!!.visibility = View.GONE
                                animation_view!!.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                                refreshLayoutParent!!.visibility = View.VISIBLE
                                return@Runnable
                            }

                            mLayoutManager = LinearLayoutManager(this)
                            recyclerView!!.layoutManager = mLayoutManager
                            registerPetAdapter =
                                CheckBoxAdapter(
                                    this,
                                    businesses,
                                    this@AdvisaryCheckBoxFragment,
                                    dataList
                                )
                            recyclerView!!.adapter = registerPetAdapter
                            registerPetAdapter!!.notifyDataSetChanged()
                            recyclerView!!.visibility = View.VISIBLE
                            recyclerView!!.layoutManager?.scrollToPosition(offset)
                            shimmerFramlayout!!.visibility = View.GONE
                            animation_view!!.visibility = View.GONE
                            refreshLayoutParent!!.visibility = View.VISIBLE

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

    protected val editService: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var url = RetrofitClient.PRODUCTION_URL + "plannerSections"
                Thread {
                    try {
                        val userId =
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        val zipCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                        val countryCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)

                        var sectionList: ArrayList<Section> = arrayListOf()
                        for (temp in businesses) {
                            if (temp.isChecked) {
                                sectionList.add(Section(order = temp.order, type = temp.type))
                            }
                        }
                        var requestPojo = PlannerAddServicePojo(
                            userId = userId,
                            action = "EditService",
                            country = countryCode,
                            zipCode = zipCode,
                            timeZone = ProjectUtil.getCurrentTimeZone(),
                            serviceName = serviceName?.getText().toString(),
                            serviceURL= serviceUrl?.getText().toString(),
                            id = businesses.get(selectedEditDeleteItemPosition!!).id
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
                        runOnUiThread(Runnable {
                            businesses.removeAll(businesses)
                            businesses.clear()
                            ProjectUtil.dismissDialog(customDialog)
                            var gson = Gson()
                            var getAllData: CheckBoxResponse =
                                gson.fromJson(responseString, CheckBoxResponse::class.java)
                            var businessesMian = ArrayList<Datum>()


                            for (photoList1 in getAllData!!.data) {

                                for (oldData in dataList) {
                                    if (oldData.type != "112233") {
                                        if (oldData.type.equals(photoList1.type)) {
                                            photoList1.isChecked = true;
                                            break
                                        } else {
                                            photoList1.isChecked = false;
                                        }
                                    }
                                }
                                businessesMian.add(photoList1);
                            }

                            businesses.addAll(businessesMian)
                            pw?.dismiss()
//                            businesses.addAll(getAllData.data)
                            ProjectUtil.dismissDialog(customDialog)
                            if (getAllData.data.size <= 0) {
                                val bundle = Bundle()
                                bundle.putString("fragmentLayoutName", "")
                                shimmerFramlayout!!.visibility = View.GONE
                                animation_view!!.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                                refreshLayoutParent!!.visibility = View.VISIBLE
                                return@Runnable
                            }

                            mLayoutManager = LinearLayoutManager(this)
                            recyclerView!!.layoutManager = mLayoutManager
                            registerPetAdapter =
                                CheckBoxAdapter(
                                    this,
                                    businesses,
                                    this@AdvisaryCheckBoxFragment,
                                    dataList
                                )
                            recyclerView!!.adapter = registerPetAdapter
                            registerPetAdapter!!.notifyDataSetChanged()
                            recyclerView!!.visibility = View.VISIBLE
                            recyclerView!!.layoutManager?.scrollToPosition(offset)
                            shimmerFramlayout!!.visibility = View.GONE
                            animation_view!!.visibility = View.GONE
                            refreshLayoutParent!!.visibility = View.VISIBLE

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

    protected val deleteService: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var url = RetrofitClient.PRODUCTION_URL + "plannerSections"
                Thread {
                    try {
                        val userId =
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        val zipCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                        val countryCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)

                        var sectionList: ArrayList<Section> = arrayListOf()
                        for (temp in businesses) {
                            if (temp.isChecked) {
                                sectionList.add(Section(order = temp.order, type = temp.type))
                            }
                        }
                        var requestPojo = PlannerAddServicePojo(
                            userId = userId,
                            action = "DeleteService",
                            country = countryCode,
                            zipCode = zipCode,
                            timeZone = ProjectUtil.getCurrentTimeZone(),
                            serviceName = serviceName?.getText().toString(),
                            serviceURL= serviceUrl?.getText().toString(),
                            id = businesses.get(selectedEditDeleteItemPosition!!).id
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
                        runOnUiThread(Runnable {
                            businesses.removeAll(businesses)
                            businesses.clear()
                            ProjectUtil.dismissDialog(customDialog)
                            var gson = Gson()
                            var getAllData: CheckBoxResponse =
                                gson.fromJson(responseString, CheckBoxResponse::class.java)
                            var businessesMian = ArrayList<Datum>()


                            for (photoList1 in getAllData!!.data) {

                                for (oldData in dataList) {
                                    if (oldData.type != "112233") {
                                        if (oldData.type.equals(photoList1.type)) {
                                            photoList1.isChecked = true;
                                            break
                                        } else {
                                            photoList1.isChecked = false;
                                        }
                                    }
                                }
                                businessesMian.add(photoList1);
                            }

                            businesses.addAll(businessesMian)
                            pw?.dismiss()
//                            businesses.addAll(getAllData.data)
                            ProjectUtil.dismissDialog(customDialog)
                            if (getAllData.data.size <= 0) {
                                val bundle = Bundle()
                                bundle.putString("fragmentLayoutName", "")
                                shimmerFramlayout!!.visibility = View.GONE
                                animation_view!!.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                                refreshLayoutParent!!.visibility = View.VISIBLE
                                return@Runnable
                            }

                            mLayoutManager = LinearLayoutManager(this)
                            recyclerView!!.layoutManager = mLayoutManager
                            registerPetAdapter =
                                CheckBoxAdapter(
                                    this,
                                    businesses,
                                    this@AdvisaryCheckBoxFragment,
                                    dataList
                                )
                            recyclerView!!.adapter = registerPetAdapter
                            registerPetAdapter!!.notifyDataSetChanged()
                            recyclerView!!.visibility = View.VISIBLE
                            recyclerView!!.layoutManager?.scrollToPosition(offset)
                            shimmerFramlayout!!.visibility = View.GONE
                            animation_view!!.visibility = View.GONE
                            refreshLayoutParent!!.visibility = View.VISIBLE

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

    override fun onClickEvent(postion: Int) {
        var data = businesses.get(postion)
        if (data.isChecked == true) {
            data.isChecked = false
        } else {
            data.isChecked = true
        }
        businesses.set(postion, data);
        registerPetAdapter!!.notifyDataSetChanged()
    }

    //For the purpose of edit service
    override fun onCallEvent(postion: Int) {
        selectedEditDeleteItemPosition = postion
        popupCreateLink(businesses?.get(postion)!!)
    }

    //For the purpose of delete service
    override fun onImageClick(postion: Int) {
        selectedEditDeleteItemPosition = postion
        deleteService
    }

    override fun onAddressClick(postion: Int) {
    }

    override fun onSiteUrl(postion: Int) {

    }

    override fun onMoreInfo(postion: Int) {
    }
}