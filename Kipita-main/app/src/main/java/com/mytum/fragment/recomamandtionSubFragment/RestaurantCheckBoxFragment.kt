package com.mytum.fragment.AdviserySubFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.mytum.MyApplication
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import com.mytum.R
import com.mytum.adapter.CheckBoxAdapter
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.AdvisaryRewuest
import com.mytum.model.AdvisoryUpdateServiceAndSequencePojo
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.CheckBoxResponse.Datum
import com.mytum.model.NewAdvisaryResponse.AdvisaryListResponse
import com.mytum.model.Section
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class RestaurantCheckBoxFragment : AppCompatActivity(), ComplaintClickPressed {
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
    var fromWhichCategory = 1
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
        search = findViewById(R.id.search)
        searchVoice = findViewById(R.id.searchVoice)
        refreshLayoutParent = findViewById(R.id.swipeToRefresh_parent)
        animation_view = findViewById(R.id.animation_view)
        recyclerView = findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = findViewById(R.id.shimmerFramlayout)
        registerPetAdapter =
            CheckBoxAdapter(this, businesses, this@RestaurantCheckBoxFragment, dataList)
        recyclerView!!.setAdapter(registerPetAdapter)
        businesses.clear()

        var bundle = intent.getBundleExtra("data")
        var transactionList: ArrayList<AdvisaryListResponse>? = (bundle!!.getParcelableArrayList("list"))
        fromWhichCategory = bundle.getInt("fromWhichCategory", 1)

        val backarrow = findViewById<ImageView>(R.id.backarrow)
        backarrow!!.setOnClickListener(View.OnClickListener { v: View? -> finish() })
        val imageViewDone = findViewById<ImageView>(R.id.imageViewDone)
        imageViewDone.setOnClickListener(View.OnClickListener { v: View? ->
            updateMyList
        })
        val textLabelInfo = findViewById<TextView>(R.id.textLabelInfo)
        val lineView = findViewById<View>(R.id.lineView)
        val grp_name_info = findViewById<TextView>(R.id.grp_name_info)
        if(fromWhichCategory == 1){
            textLabelInfo.text = "Choose service to show on restaurant section"
            lineView.setBackgroundColor(resources.getColor(R.color.red_bottom))
        }else if(fromWhichCategory == 2){
            textLabelInfo.text = "Choose service to show on entertainment section"
            lineView.setBackgroundColor(resources.getColor(R.color.red_bottom))
        }else if(fromWhichCategory == 3){
            textLabelInfo.text = "Choose service to show on shopping section"
            lineView.setBackgroundColor(resources.getColor(R.color.red_bottom))
        }

        dataList.addAll(transactionList!!);
        allEntertainmentData
    }


    protected val allEntertainmentData: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")

                var url = RetrofitClient.PRODUCTION_URL +"placesSections"
                var parentType = "restaurant"
                if(fromWhichCategory == 1){
                    url = RetrofitClient.PRODUCTION_URL +"placesSections"
                    parentType = "restaurant"
                }else if(fromWhichCategory == 2){
                    url = RetrofitClient.PRODUCTION_URL +"placesSections"
                    parentType = "entertainment"
                }else if(fromWhichCategory == 3){
                    url = RetrofitClient.PRODUCTION_URL +"placesSections"
                    parentType = "shopping"
                }

                Thread {
                    try {
                        var data = AdvisaryRewuest();
                        data.action = "GetAllList";
                        data.parentType = parentType;
                        data.zipCode =
                            ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                        data.timeZone = ProjectUtil.getCurrentTimeZone();
                        data.userId =
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext);

                        var gson = Gson();
                        var jsonString = gson.toJson(data);

                        Log.d("aaaaaa request all", jsonString)

                        val httpclient: HttpClient = DefaultHttpClient()
                        val post = HttpPost(url)
                        val holder = JSONObject(jsonString)
                        val se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        val response = httpclient.execute(post)
                        val out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        val responseString = out.toString()
                        Log.d("aaaaaa respo all", responseString)
                        runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            var gson = Gson()
                            var getAllData: CheckBoxResponse =
                                gson.fromJson(responseString, CheckBoxResponse::class.java)
                            var businessesMian = ArrayList<Datum>()


                            for (photoList1 in getAllData!!.data) {

                                for (oldData in dataList) {
                                    if (oldData.type != "112233") {
                                        if (oldData.title.equals(photoList1.title)) {
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
                                    this@RestaurantCheckBoxFragment,
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
                var url = RetrofitClient.PRODUCTION_URL +"placesSections"
                if(fromWhichCategory == 1){
                    url = RetrofitClient.PRODUCTION_URL +"placesSections"
                }else if(fromWhichCategory == 2){
                    url = RetrofitClient.PRODUCTION_URL +"placesSections"
                }else if(fromWhichCategory == 3){
                    url = RetrofitClient.PRODUCTION_URL +"placesSections"
                }

                Thread {
                    try {
                        val userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                        val zipCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                        val countryCode = ProjectUtil.getChangedLocationCountryCode(MyApplication.getInstance().applicationContext)
                        var sectionList: ArrayList<Section> = arrayListOf()
                        for(temp in businesses){
                            if(temp.isChecked){
                                sectionList.add(Section(order = temp.order, type = temp.type))
                            }
                        }
                        var requestPojo = AdvisoryUpdateServiceAndSequencePojo(userId = userId, action = "EditMyList", country = countryCode, zipCode = zipCode,
                            timeZone = ProjectUtil.getCurrentTimeZone(), sections = sectionList)

                        var gson = Gson();
                        var jsonString = gson.toJson(requestPojo);

                        val httpclient: HttpClient = DefaultHttpClient()
                        val post = HttpPost(url)
                        val holder = JSONObject(jsonString)
                        val se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        val response = httpclient.execute(post)
                        val out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        val responseString = out.toString()
                        runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            var gson = Gson()
                            var getAllData: CheckBoxResponse =
                                gson.fromJson(responseString, CheckBoxResponse::class.java)
                            if(getAllData.status.equals("success")){
                                ProjectUtil.customToast(this@RestaurantCheckBoxFragment,"Services are added successfully")
                                finish()
                            }else{
                                ProjectUtil.customToast(this@RestaurantCheckBoxFragment,"Error while adding the services.")
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

    override fun onClickEvent(postion: Int) {
//        var tempData = ArrayList<Datum>()
//        tempData.clear()
//        var data = businesses.get(postion)
//        if (data.type.equals("overall")) {
//            if (data.isChecked == true) {
//                for (oldData in businesses) {
//                    oldData.isChecked = false
//                    tempData.add(oldData);
//                }
//            } else {
//                for (oldData in businesses) {
//                    oldData.isChecked = true
//                    tempData.add(oldData);
//                }
//            }
//            businesses.clear()
//            businesses.addAll(tempData)
//        } else {
//            if (data.isChecked == true) {
//                data.isChecked = false
//            } else {
//                data.isChecked = true
//            }
//            businesses.set(postion, data);
//        }

        var data = businesses.get(postion)
        if (data.isChecked == true) {
            data.isChecked = false
        } else {
            data.isChecked = true
        }
        businesses.set(postion, data);
        registerPetAdapter!!.notifyDataSetChanged()
    }

    override fun onCallEvent(postion: Int) {

    }

    override fun onImageClick(postion: Int) {

    }

    override fun onAddressClick(postion: Int) {
    }

    override fun onSiteUrl(postion: Int) {

    }

    override fun onMoreInfo(postion: Int) {
    }
}