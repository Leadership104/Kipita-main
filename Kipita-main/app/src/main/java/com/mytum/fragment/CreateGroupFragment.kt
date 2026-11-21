package com.mytum.fragment

import android.R.attr.data
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.adapter.CreateGroupAdapter
import com.mytum.model.CreateGroupList
import com.mytum.model.CreateGroupeItemObject
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection


class CreateGroupFragment : Fragment() {
    var dataList: ArrayList<CreateGroupeItemObject> = ArrayList()
    var recyclerView: RecyclerView? = null
    private var dogImg: ImageView? = null
    var parentLayout: CardView? = null
    var categoryAdapter: CreateGroupAdapter? = null
    var listData: String? = null
    lateinit var progressDialog: ProgressDialog
    lateinit var et_group_name: EditText
    lateinit var et_group_des: EditText

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_group, container, false)
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titel)
        (activity as MainActivity).showMainMenu()
        val toolbar =
            (activity as MainActivity?)!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        parentLayout = view.findViewById(R.id.parentLayout)
        et_group_name = view.findViewById(R.id.et_group_name)
        et_group_des = view.findViewById(R.id.et_group_des)
        et_group_name.setText(arguments?.getString("name"))
        et_group_des.setText(arguments?.getString("dis"))
        dogImg = view.findViewById(R.id.dog_img)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        listData = arguments?.getString("list")
        val btnInvites = view.findViewById<Button>(R.id.btn_invites)
        val btn_create = view.findViewById<Button>(R.id.btn_create)
        val member_count = view.findViewById<TextView>(R.id.member_count)
        btnInvites.setOnClickListener {
            btnInvites.text = "Getting Contacts Please Wait..."
            btnInvites.isClickable = false
            val bundles = Bundle()
            bundles.putString("list", listData)
            bundles.putString("type", "0")
            bundles.putString("id", "")
            bundles.putBoolean("admin", true)
            bundles.putString("name", et_group_name.text.toString())
            bundles.putString("dis", et_group_des.text.toString())
            loadFragmentInner(InviteMembersFragment(), bundles)
        }
        val lat =
            ProjectUtil.getCurrentLotitude(MyApplication.getInstance().applicationContext)
                .toDouble()
        val lng =
            ProjectUtil.getCurrentLongitude(MyApplication.getInstance().applicationContext)
                .toDouble()
        var util: PhoneNumberUtil = PhoneNumberUtil.createInstance(getApplicationContext());

        try {
            if (!arguments?.getString("list").equals("")) {
                val jsonArray = JSONArray(arguments?.getString("list"))
                member_count.text = "Invited Members (" + jsonArray.length() + ")"
                for (i in 0 until jsonArray.length()) {
                    val jsonObject1 = jsonArray.getJSONObject(i)
                    val phoneNumber = util.parse(
                        jsonObject1.getString("phone"),
                        ProjectUtil.getCountryCode(lat, lng)
                    )
                    val personItemObject = CreateGroupeItemObject()
//                    personItemObject.phone =
//                        jsonObject1.getString("phone")
                    personItemObject.phone =
                        util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                    personItemObject.firstName =
                        jsonObject1.getString("firstName")
                    personItemObject.email =
                        jsonObject1.getString("email")
                    personItemObject.lastName =
                        jsonObject1.getString("lastName")
                    dataList.add(personItemObject)
                    Log.d(
                        "aaaaaaaaaaaaaaa",
                        util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                    )
                }
                recyclerView!!.visibility = View.VISIBLE
                recyclerView!!.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                categoryAdapter = CreateGroupAdapter(requireActivity(), dataList)
                recyclerView!!.adapter = categoryAdapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        btn_create.setOnClickListener {
            if (dataList.size > 0) {
                if (et_group_name.text.toString().isNotEmpty()) {
                    if (et_group_des.text.toString().isNotEmpty()) {
                        progressDialog = ProgressDialog(requireActivity())
                        progressDialog.setTitle("Please Wait")
                        progressDialog.setMessage("Loading ...")
                        progressDialog.setCancelable(false) // blocks UI interaction
                        progressDialog.show()
                        btn_create.text = "Create Group Please Wait..."
//                        createGroupData
                        groupData(
                            et_group_name.text.toString(),
                            et_group_des.text.toString(),
                            dataList
                        )

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please enter group name descriptions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter group name", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "No member for create group", Toast.LENGTH_SHORT)
                    .show()

            }
        }
        return view
    }

    private fun groupData(name: String, dec: String, dataList1: ArrayList<CreateGroupeItemObject>) {
        var getResult: String? = null
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val urlConnection: HttpURLConnection
            val url = "https://api.dwaat.com/group"
            try {
                urlConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.addRequestProperty("Accept-Charset", "UTF-8")
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.setRequestProperty("Accept", "application/json")
                urlConnection.setRequestProperty(
                    "lat",
                    ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                )
                urlConnection.setRequestProperty(
                    "lng",
                    ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                )
                urlConnection.setRequestProperty(
                    "userId",
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                )
                urlConnection.requestMethod = "POST"
                val jsonArray = JSONArray()
                for (data in dataList) {
                    val jsonParamTemp = JSONObject()
                    jsonParamTemp.put("email", data.email)
                    jsonParamTemp.put("phone", data.phone)
                    jsonParamTemp.put("firstName", data.firstName)
                    jsonParamTemp.put("lastName", data.lastName)
                    jsonParamTemp.put("source", "")
                    jsonArray.put(jsonParamTemp)
                }
                val jsonParam = JSONObject()
//                val jsonElements = Gson().toJsonTree(dataList) as JsonArray
                jsonParam.put("action", "create")
                jsonParam.put("groupName", name)
                jsonParam.put("groupDescription", dec)
                jsonParam.put(
                    "createdBy",
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                )
                jsonParam.put("invitedMembers", jsonArray)
                val jsonString = jsonParam.toString().replace("\\\"", "\"")
                Log.d("jsonParam", jsonString)
                val outputStream = urlConnection.outputStream
                outputStream.write(jsonString.toByteArray())
                val responseCode = urlConnection.responseCode
                Log.d("url", url)
                println("stcode = $responseCode")
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    val response = StringBuilder()
                    val ins = urlConnection.inputStream
                    val isr = InputStreamReader(ins)
                    var ch: Int
                    while ((isr.read().also { ch = it }) >= 0) {
                        response.append(ch.toChar())
                    }
                    val s = response.toString()
                    println("response = $s")
                    getResult = s
                } else {
                    println("Response = $responseCode")
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            handler.post {
                println("getResult = $getResult")
                val json = JSONObject(getResult)
                if (json.getString("status").equals("success")) {
                    ProjectUtil.customToast(
                        requireActivity(),
                        json.getString("message")
                    )
                    dataList1.clear()
                    dataList.clear()
                    val bundles = Bundle()
                    bundles.putString("list", "")
                    loadFragmentInner(GroupListFragment(), bundles)
                } else {
                    ProjectUtil.customToast(
                        requireActivity(),
                        json.getString("message")
                    )
                }
                progressDialog.dismiss()
            }
        }
    }

    private val createGroupData: Unit
        get() {
            try {
                val data = CreateGroupList()
                data.action = "create"
                data.groupName = et_group_name.text.toString()
                data.groupDescription = et_group_des.text.toString()
                data.createdBy =
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                val jsonElements = Gson().toJsonTree(dataList) as JsonArray
                data.invitedMembers = jsonElements
                val gson = Gson()
                val jsonString = gson.toJson(data)
                Thread {
                    try {
                        val httpclient: HttpClient = DefaultHttpClient()
                        val post = HttpPost(RetrofitClient.PRODUCTION_URL + "group")
                        val holder = JSONObject(jsonString)
                        val se = StringEntity(holder.toString())
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
                        activity?.runOnUiThread(Runnable {
                            val json = JSONObject(responseString)
                            if (json.getString("status").equals("success")) {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                                val bundles = Bundle()
                                bundles.putString("list", "")
                                loadFragmentInner(GroupListFragment(), bundles)
                            } else {
                                ProjectUtil.customToast(
                                    requireActivity(),
                                    json.getString("message")
                                )
                            }
                            progressDialog.dismiss()
                        })
                        out.close()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        progressDialog.dismiss()
                    }
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
                progressDialog.dismiss()
            }
            progressDialog.dismiss()
        }

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        //switching fragment
        fragment!!.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }

}