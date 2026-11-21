package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.adapter.GroupImageAdapter
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.GroupListRewuest
import com.mytum.model.GroupNotificationItemObject
import com.mytum.model.ImageItemObject
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class AllImageFragment : Fragment() {

    var customDialog: Dialog? = null
    var title: String? = null
    var gId: String? = null
    var toolbar_home: Toolbar? = null
    var text_notification: TextView? = null
    var dataListImage: ArrayList<ImageItemObject> = ArrayList()
    var recyclerView_image: RecyclerView? = null
    var imageAdapter: GroupDetailsFragment.ImageListAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_image, container, false)
        title = arguments?.getString("title")
        gId = arguments?.getString("id")
        toolbar_home = view.findViewById(R.id.toolbar_home)
        customDialog = activity?.let { Dialog(it) }
        recyclerView_image = view.findViewById(R.id.recyclerView_image)
        recyclerView_image!!.isNestedScrollingEnabled = false
        text_notification = view.findViewById(R.id.text_notification)

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        allEntertainmentData1
    }


    private val allEntertainmentData1: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = GroupListRewuest()
                data.action = "groupDetail"
                data.groupID = gId
                data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
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
                        activity?.runOnUiThread(Runnable { reresh(responseString); })
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
        dataListImage.removeAll(dataListImage.toSet())
        dataListImage.clear()
        try {
            val json = JSONObject(data)
            val jsonArray = JSONArray(json.getString("data"))
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val json1 = JSONObject(jsonObject.toString())
                when (i) {
                    2 -> {
                        try {
                            val jsonArray1 = JSONArray(json1.getString("images"))
                            for (m in 0 until jsonArray1.length()) {
                                val jsonObject1 = jsonArray1.getJSONObject(m)
                                val gson = Gson()
                                dataListImage.add(
                                    gson.fromJson(
                                        jsonObject1.toString(),
                                        ImageItemObject::class.java
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ProjectUtil.customToast(activity, e.toString())
                        }
                    }

                }
            }
            text_notification!!.text = "Image (" + dataListImage.size + ")"
            val gridLayoutManager = GridLayoutManager(activity, 4)
            gridLayoutManager.orientation =
                LinearLayoutManager.VERTICAL // set Horizontal Orientation
            recyclerView_image!!.setLayoutManager(gridLayoutManager)
            imageAdapter = GroupDetailsFragment.ImageListAdapter(requireActivity(), dataListImage)
            recyclerView_image!!.adapter = imageAdapter
        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.customToast(activity, e.toString())
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        fragment!!.arguments = bundle
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
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
//        toolbar_home!!.inflateMenu(R.menu.menu_group)
//        toolbar_home!!.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.view_add -> {
//                    // do something
//                    true
//                }
//
//                R.id.view_delete -> {
//                    // do something
//                    true
//                }
//
//                R.id.view_permissions -> {
//                    // do something
//                    true
//                }
//
//                else -> {
//                    super.onOptionsItemSelected(it)
//                }
//            }
//        }
    }

}