package com.mytum.fragment.AdviserySubFragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.mytum.MyApplication
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import com.mytum.R
import com.mytum.adapter.PlanerCheckBoxAdapter
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.AdvisaryRewuest
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.CheckBoxResponse.Datum
import com.mytum.model.NewPalnerResponse.NewAdvisaryResponse.PlannerListResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class PlaneerCheckBoxFragment : Fragment(), ComplaintClickPressed {
    var businesses = ArrayList<Datum>()
    var customDialog: Dialog? = null
    var dataList: ArrayList<PlannerListResponse> = ArrayList()
    private var mLayoutManager: LinearLayoutManager? = null
    var refreshLayoutParent: LinearLayout? = null
    var shimmerFramlayout: ShimmerFrameLayout? = null
    var recyclerView: RecyclerView? = null
    var search: ImageView? = null
    var animation_view: LinearLayout? = null
    var searchVoice: ImageView? = null
    var registerPetAdapter: PlanerCheckBoxAdapter? = null
    var offset = 0;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.entertenment_planner, container, false)
        customDialog = Dialog(requireActivity())
        search = view.findViewById(R.id.search)
        searchVoice = view.findViewById(R.id.searchVoice)
        refreshLayoutParent = view.findViewById(R.id.swipeToRefresh_parent)
        animation_view = view.findViewById(R.id.animation_view)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        shimmerFramlayout = view.findViewById(R.id.shimmerFramlayout)
        registerPetAdapter =
            PlanerCheckBoxAdapter(activity, businesses, this@PlaneerCheckBoxFragment, dataList)
        recyclerView!!.setAdapter(registerPetAdapter)
        businesses.clear()


        var transactionList =
            requireArguments().getSerializable("list") as ArrayList<PlannerListResponse>?
        dataList.addAll(transactionList!!);
        allEntertainmentData
        return view
    }


    protected val allEntertainmentData: Unit
        protected get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var url = RetrofitClient.PRODUCTION_URL + "plannerSections"
                Thread {
                    try {
                        var data = AdvisaryRewuest();
                        data.action = "GetAllList";
                        data.zipCode =
                            ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                        data.timeZone = ProjectUtil.getCurrentTimeZone();
                        data.userId =
                            ProjectUtil.getUserId(MyApplication.getInstance().applicationContext);
                        data.timeZone = ProjectUtil.getCurrentTimeZone()


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
                        activity?.runOnUiThread(Runnable {

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

                            mLayoutManager = LinearLayoutManager(activity)
                            recyclerView!!.layoutManager = mLayoutManager
                            registerPetAdapter =
                                PlanerCheckBoxAdapter(
                                    activity,
                                    businesses,
                                    this@PlaneerCheckBoxFragment,
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
                        ProjectUtil.showDialog(customDialog, "Please wait...")
                    }
                }.start()


            } catch (e: Exception) {
                e.printStackTrace()
                ProjectUtil.showDialog(customDialog, "Please wait...")
            }
        }


    override fun onClickEvent(postion: Int) {
        var tempData = ArrayList<Datum>()
        tempData.clear()
        var data = businesses.get(postion)
        if (data.type.equals("overall")) {
            if (data.isChecked == true) {
                for (oldData in businesses) {
                    oldData.isChecked = false
                    tempData.add(oldData);
                }
            } else {
                for (oldData in businesses) {
                    oldData.isChecked = true
                    tempData.add(oldData);
                }
            }
            businesses.clear()
            businesses.addAll(tempData)
        } else {
            if (data.isChecked == true) {
                data.isChecked = false
            } else {
                data.isChecked = true
            }
            businesses.set(postion, data);
        }


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