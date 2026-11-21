package com.mytum.fragment.recomamandtionSubFragment


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mytum.LoginActivity
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.R
import com.mytum.adapter.RestaurantMainAdapter
import com.mytum.fragment.AdviserySubFragment.AdvisaryCheckBoxFragment
import com.mytum.fragment.AdviserySubFragment.RestaurantCheckBoxFragment
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.AdvisaryRewuest
import com.mytum.model.CheckBoxResponse.CheckBoxResponse
import com.mytum.model.NewAdvisaryResponse.AdvisaryListResponse
import com.mytum.model.NewRecomendationResponse.RecommendationResponse
import com.mytum.model.NewRecomendationResponse.RecommendationSubResponse
import com.mytum.model.RestaurantUpdateServiceAndSequencePojo
import com.mytum.model.Section
import com.mytum.model.places.Center
import com.mytum.model.places.Circle
import com.mytum.model.places.LocationRestriction
import com.mytum.model.places.PlaceCategorySearchPojo
import com.mytum.model.places.PlacesTextSearchPojo
import com.mytum.model.places.RestaurantPojo
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Locale


class RestaurantMainFragment : Fragment(), ComplaintClickPressed {
    var customDialog: Dialog? = null
    var dataList: ArrayList<RecommendationSubResponse> = ArrayList()

    var animation_view: RelativeLayout? = null
    var frameLayout_register: FrameLayout? = null
    var recyclerView: RecyclerView? = null
    var categoryAdapter: RestaurantMainAdapter? = null

    var searchData = ""
    var search: ImageView? = null
    var searchVoice: ImageView? = null
    var searchViewParent: LinearLayout? = null
    var etSearchView: EditText? = null
    var clickedCategoryType: String = ""
    var clickedCategoryTitle: String = ""
    var clickedCategoryQueryType: String = ""
    var clickedCategoryShowOpenClosed: Int = 0
    var clickedCategoryRadius: Double = 50000.0
    var clickedCategoryMaxResult: Int = 20
    var clickedCategoryRankPreference: String = "DISTANCE"

    var image_setting: ImageView? = null

    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                var str = intent.getStringExtra("key")
                getMyCategoryList
            } else {
                ProjectUtil.dismissDialog(customDialog)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.restaurant_mian_fragment, container, false)
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titel)

        (activity as MainActivity).showMainMenu()

        val toolbar =
            (activity as MainActivity?)!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        customDialog = Dialog(requireActivity())

        animation_view = view.findViewById(R.id.animation_view)
        frameLayout_register = view.findViewById(R.id.frameLayout_register)
        recyclerView = view.findViewById(R.id.recyclerView_reg)

        etSearchView = view.findViewById(R.id.etSearchView)
        searchViewParent = view.findViewById(R.id.searchViewParent)
        search = view.findViewById(R.id.search)
        searchVoice = view.findViewById(R.id.searchVoice)
        image_setting = view.findViewById(R.id.image_setting)
        image_setting?.setOnClickListener {
            if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                var advisoryList: ArrayList<AdvisaryListResponse> = arrayListOf<AdvisaryListResponse>()
                for(temp in dataList){
                    var adv = AdvisaryListResponse()
                    adv.title = temp.title
                    adv.queryType = temp.parentType
                    adv.type = temp.type
                    adv.icon = ""
                    adv.order = temp.order
                    advisoryList.add(adv)
                }
                var bundle = Bundle()
                bundle.putParcelableArrayList("list", advisoryList)
                bundle.putInt("fromWhichCategory", 1)
                startActivity(Intent(activity, RestaurantCheckBoxFragment::class.java).putExtra("data", bundle))
            }else{
                val i = Intent(activity, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                (activity as MainActivity).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
                (activity as MainActivity).finish()
            }
        }

        categoryAdapter = RestaurantMainAdapter(activity, dataList, this@RestaurantMainFragment)

        recyclerView!!.layoutManager = GridLayoutManager(activity, 2)
        recyclerView!!.adapter = categoryAdapter;

        dataList.clear()
        getMyCategoryList

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

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled() = true
            override fun isItemViewSwipeEnabled() = false

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = if (isItemViewSwipeEnabled) ItemTouchHelper.START or ItemTouchHelper.END else 0
                if((viewHolder as RestaurantMainAdapter.ComplaintHolder).titel.getTag(R.string.holdertag).toString().equals("112233")){
                    return 0
                }else{
                    return makeMovementFlags(dragFlags, swipeFlags)
                }
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                if (viewHolder.itemViewType != target.itemViewType)
                    return false
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                val item = dataList.removeAt(fromPosition)
                dataList.add(toPosition, item)
                recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
                changeOrderAndSubmitNewOrder()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                dataList.removeAt(position)
                recyclerView!!.adapter!!.notifyItemRemoved(position)
            }

        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    override fun onResume() {
        super.onResume()
        getMyCategoryList
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

    fun reresh(data: String) {
        ProjectUtil.showDialog(customDialog, "Please wait...")
        dataList.removeAll(dataList)
        dataList.clear()
        try {
            var gson = Gson()
            var data: RecommendationResponse =
                gson.fromJson(data, RecommendationResponse::class.java)
            if (data.data.section.size <= 0 && data.data.section == null) {
                frameLayout_register!!.visibility = View.GONE
                recyclerView!!.visibility = View.GONE
                animation_view!!.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            ProjectUtil.customToast(activity, e.toString())
            return
        }

        var gson = Gson()
        var data: RecommendationResponse = gson.fromJson(data, RecommendationResponse::class.java)

        try {
            if (data.data.section.size > 0) {
                frameLayout_register!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.VISIBLE
                animation_view!!.visibility = View.GONE

                dataList.addAll(data.data.section)

//                var data11 = RecommendationSubResponse()
//                data11.type = "112233"
//                dataList.add(data11)


                recyclerView!!.layoutManager = GridLayoutManager(activity, 2)
                recyclerView!!.adapter = categoryAdapter;
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ProjectUtil.dismissDialog(customDialog)
    }

    override fun onClickEvent(postion: Int) {

        var data = dataList.get(postion);
        if (data.type.equals("overall")) {
//            loadFragmentInner(PlannerNewFragment(), null);
        } else if (data.type.equals("112233")) {

            if (ProjectUtil.getSession(activity).equals("1", ignoreCase = true)) {
                var advisoryList: ArrayList<AdvisaryListResponse> = arrayListOf<AdvisaryListResponse>()
                for(temp in dataList){
                    var adv = AdvisaryListResponse()
                    adv.title = temp.title
                    adv.queryType = temp.parentType
                    adv.type = temp.type
                    adv.icon = ""
                    adv.order = temp.order
                    advisoryList.add(adv)
                }
                var bundle = Bundle()
                bundle.putParcelableArrayList("list", advisoryList)
                bundle.putInt("fromWhichCategory", 1)
                startActivity(Intent(activity, RestaurantCheckBoxFragment::class.java).putExtra("data", bundle))
            }else{
                val i = Intent(activity, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                (activity as MainActivity).overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
                (activity as MainActivity).finish()
            }
        } else {
            clickedCategoryType = data.type
            clickedCategoryTitle = data.title
            clickedCategoryRadius = data.radius
            clickedCategoryQueryType = data.queryType
            clickedCategoryMaxResult = data.maxResults
            clickedCategoryRankPreference = data.rankPreference
            clickedCategoryShowOpenClosed = data.showOpenClosed
            if(data.queryType.equals("category")) {
                getPlacesCategoryData
            }else{
                searchData = data.parentType
                getPlacesTextSearchData
            }
        }
    }

    override fun onCallEvent(postion: Int) {

    }

    override fun onSiteUrl(postion: Int) {
    }

    override fun onImageClick(postion: Int) {
    }

    override fun onAddressClick(postion: Int) {
    }

    private val getMyCategoryList: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var data = AdvisaryRewuest();
                data.action = "GetMyList";
                data.parentType = "restaurant";
//                data.zipCode =
//                    ProjectUtil.getZipCode(
//                        ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext),
//                        ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
//                    );
//                data.timeZone = ProjectUtil.getCurrentTimeZone();
                data.userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext);

                var gson = Gson();
                var jsonString = gson.toJson(data);


                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost(RetrofitClient.PRODUCTION_URL + "/placesSections")
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

    override fun onMoreInfo(postion: Int) {
    }

    fun changeOrderAndSubmitNewOrder(){
        try {
            ProjectUtil.showDialog(customDialog, "Please wait...")
            var url = RetrofitClient.PRODUCTION_URL +"placesSections"
            Thread {
                try {
                    val userId = ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                    val zipCode = ProjectUtil.getChangedLocationZipCode(MyApplication.getInstance().applicationContext)
                    val countryCode = ProjectUtil.getChangedLocationCountryCode(MyApplication.getInstance().applicationContext)
                    var sectionList: ArrayList<Section> = arrayListOf()
                    var index = 0
                    for(temp in dataList){
                        if(!temp.type.equals("112233")) {
                            index += 1;
                            sectionList.add(Section(order = index, type = temp.type))
                        }
                    }
                    var requestPojo = RestaurantUpdateServiceAndSequencePojo(userId = userId, action = "EditMyList", country = countryCode, zipCode = zipCode,
                        timeZone = ProjectUtil.getCurrentTimeZone(), sections = sectionList, parentType = "restaurant")

                    var gson = Gson();
                    var jsonString = gson.toJson(requestPojo);
                    Log.d("aaaaaaaaaaa req", jsonString)

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
                    Log.d("aaaaaaaaaaa res", responseString)
                    activity?.runOnUiThread(Runnable {
                        ProjectUtil.dismissDialog(customDialog)
                        var gson = Gson()
                        var getAllData: CheckBoxResponse =
                            gson.fromJson(responseString, CheckBoxResponse::class.java)
                        if(getAllData.status.equals("success")){
                            ProjectUtil.customToast(activity,"Services order changed successfully")
                        }else{
                            ProjectUtil.customToast(activity,"Error while changing the order of services.")
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

    private val getPlacesCategoryData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                var includedTypes = ArrayList<String>()
                includedTypes.add(clickedCategoryType);

                val center = Center(ProjectUtil.getLotitude(activity as MainActivity).toDouble(), ProjectUtil.getLongitude(activity as MainActivity).toDouble())
//                val center = Center(37.7937, -122.3965)
                val circle = Circle(center, clickedCategoryRadius)
                var locationRestriction = LocationRestriction(circle)
                var data = PlaceCategorySearchPojo(includedTypes, clickedCategoryMaxResult, clickedCategoryRankPreference, locationRestriction);

                var gson = Gson();
                var jsonString = gson.toJson(data);
                Log.d("aaaaa category reques", jsonString)

                Thread {
                    try {
                        var httpclient: HttpClient = DefaultHttpClient()
                        var post = HttpPost("https://places.googleapis.com/v1/places:searchNearby")
                        var holder = JSONObject(jsonString)
                        var se = StringEntity(holder.toString())
                        post.entity = se
                        post.setHeader("Accept", "application/json")
                        post.setHeader("Content-type", "application/json")
                        post.setHeader("X-Goog-Api-Key", "AIzaSyAf6hMvn49Zl4gx4Q7d97u-atwqEBXnJa4")
                        post.setHeader("X-Goog-FieldMask", "*")
                        var response = httpclient.execute(post)
                        var out = ByteArrayOutputStream()
                        response.entity.writeTo(out)
                        var responseString = out.toString()
                        activity?.runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            handlePlacesResponse(responseString, false);
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

    private val getPlacesTextSearchData: Unit
        @SuppressLint("LongLogTag")
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
//                for(temp in dataList){
//                    includedTypes.add(temp.type);
//                }
                val includedTypes = "restaurant";
                val center = Center(ProjectUtil.getLotitude(activity as MainActivity).toDouble(), ProjectUtil.getLongitude(activity as MainActivity).toDouble())
//                val center = Center(37.7937, -122.3965)
                val circle = Circle(center, clickedCategoryRadius)
                var locationRestriction = LocationRestriction(circle)
                var data = PlacesTextSearchPojo(includedTypes, searchData, clickedCategoryMaxResult, clickedCategoryRankPreference, locationRestriction);
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

    private fun handlePlacesResponse(responseData: String, isFromSearch: Boolean){

        try {
            var gson = Gson()
            var data: RestaurantPojo =
                gson.fromJson(responseData, RestaurantPojo::class.java)
            if (data.places == null || data?.places?.size!! <= 0) {
                //show dialog for no result
                showAlertDialogButtonClicked()
            }else{
                etSearchView?.text?.clear()
                var bundle = Bundle()
                bundle.putInt("fromWhichCategory", 1)
                bundle.putString("placesList", responseData)
                bundle.putString("searchText", if(isFromSearch) searchData else clickedCategoryTitle)
                bundle.putInt("showOpenClosed", clickedCategoryShowOpenClosed)
                loadFragmentInner(RestaurantsNewFragment(), bundle)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.customToast(activity, e.toString())
        }
    }

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
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