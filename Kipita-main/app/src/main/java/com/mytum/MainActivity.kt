package com.mytum

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.database.Cursor
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.mytum.fragment.AdviserySubFragment.AdvisoryDetailsFragment
import com.mytum.fragment.AdviserySubFragment.ApplicationsFragment
import com.mytum.fragment.AdviserySubFragment.ApplicationsFragmentActivity
import com.mytum.fragment.AdviserySubFragment.ApplicationsMainFragment
import com.mytum.fragment.CreateGroupFragment
import com.mytum.fragment.GalleryFragment
import com.mytum.fragment.GroupDetailsFragment
import com.mytum.fragment.GroupListFragment
import com.mytum.fragment.InviteMembersFragment
import com.mytum.fragment.LoactionFragment
import com.mytum.fragment.LocationMapFragment
import com.mytum.fragment.MembersManageFragment
import com.mytum.fragment.PendingInvitesFragment
import com.mytum.fragment.PerksMainFragment
import com.mytum.fragment.PlanerMainFragment
import com.mytum.fragment.PlannerDetailsFragment
import com.mytum.fragment.PlannerNewFragment
import com.mytum.fragment.RecommadationFragment
import com.mytum.imagepickerlibrary.ImagePicker
import com.mytum.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.mytum.imagepickerlibrary.PickerOptions
import com.mytum.imagepickerlibrary.PickerOptionsBottomSheet
import com.mytum.imagepickerlibrary.listener.ImagePickerResultListener
import com.mytum.imagepickerlibrary.model.ImageProvider
import com.mytum.imagepickerlibrary.model.PickerType
import com.mytum.imagepickerlibrary.ui.bottomsheet.SSPickerOptionsBottomSheet
import com.mytum.model.DaysItemObject
import com.mytum.model.DeleteGroupList
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.GPSTracker
import com.mytum.proectUtil.ProjectUtil
import com.squareup.picasso.Picasso
import com.ssimagepicker.app.isAtLeast11
import com.suke.widget.SwitchButton
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    SSPickerOptionsBottomSheet.ImagePickerClickListener,
    ImagePickerResultListener, PickerOptionsBottomSheet.PickerOptionsListener,
    android.location.LocationListener {
    private var doubleBackToExitPressedOnce = false
    var customDialog: Dialog? = null
    var gsc: GoogleSignInClient? = null
    private var placesClient: PlacesClient? = null
    var gso: GoogleSignInOptions? = null
    var backpress = 0
    private var search: LinearLayout? = null
    private var changeLocation: TextView? = null
    private var closeSeacrch: LinearLayout? = null
    private var tempLayout: LinearLayout? = null
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    val MY_PERMISSIONS_REQUEST_MEDIA_LOCATION = 999
    private var googleApiClient: GoogleApiClient? = null
    private var pickerOptions = PickerOptions.default()
    private var imagePicker: ImagePicker? = null
    var locationName: TextView? = null
    var manager: LocationManager? = null
    var locationManagerWeather: LocationManager? = null
    var fragment_container: FrameLayout? = null
    var root_layout: LinearLayout? = null
    var firstLayout: LinearLayout? = null
    var secondLayout: LinearLayout? = null
    var thirdLayout: LinearLayout? = null
    var fourthLayout: LinearLayout? = null
    var firstIcon: ImageView? = null
    var secondIcon: ImageView? = null
    var thirdIcon: ImageView? = null
    var fourthIcon: ImageView? = null
    var imageAdvisoryLevel: ImageView? = null

    var countryImageView: ImageView? = null

    var firstText: TextView? = null
    var secondText: TextView? = null
    var thirdText: TextView? = null
    var fourthText: TextView? = null
    var profileNameTextTitle: TextView? = null
    var textCurrentTemp: TextView? = null
    var text_adv_type: TextView? = null
    var flag: String? = null
    var advisoryLevel: String? = null
    var advisoryDescription: String? = null
    var status: String? = null
    var groupId: String? = null
    var weatherImage: ImageView? = null

    lateinit var fab: FloatingActionButton
    lateinit var fab_gallery: FloatingActionButton
    lateinit var userName: TextView
    lateinit var closeText: TextView
    lateinit var headerLiner: View
    lateinit var rlToolbarMain: RelativeLayout
    lateinit var secondHeader: LinearLayout
    lateinit var ll_toolbar_all: LinearLayout
    lateinit var llToolbarBack: LinearLayout
    lateinit var textTitle: TextView
    lateinit var imageBack: ImageView
    private var isFirstTime = false
    val PERMISSION_REQUEST_CODE: Int = 112
    private lateinit var jsonObjectWeatherInfo: JSONObject
    lateinit var logAdapter: DaysAdapter

    companion object {
        var shouldUseCurrentLocation = true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab = findViewById(R.id.fab)
        fab_gallery = findViewById(R.id.fab_gallery)
        FirebaseApp.initializeApp(this)
        isPlayServicesAvailable()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        val intent = intent
        val msg = intent.getStringExtra("data")
        if (msg != null) {
            Log.d("notification data = ", intent.getStringExtra("jsonData")!! + msg)
            val jsonObject = JSONObject(intent.getStringExtra("jsonData")!!)
            if (jsonObject.getString("messageCode") == "REQUEST-LOCATION") {
                showDialogLocation(this, msg, jsonObject.getString("groupID"))
            } else {
                var bundles = Bundle()
                bundles.putString("list", "");
                loadFragmentInner(GroupListFragment(), bundles);
                fab.visibility = View.GONE
                fab_gallery.visibility = View.GONE
            }
            //{"messageCode":"ALL-MESSAGES","groupID":"2","notificationID":"2661"}
        } else {
            val intent = getIntent()
            val data = intent.extras
            if (data != null) {
                if (data.containsKey("messageCode")) {
                    if (intent.extras!!.getString("messageCode") == "REQUEST-LOCATION") {
                        showDialogLocation(
                            this,
                            intent.extras!!.getString("message").toString(),
                            intent.extras!!.getString("groupID")!!
                        )
                    } else {
                        var bundles = Bundle()
                        bundles.putString("list", "");
                        loadFragmentInner(GroupListFragment(), bundles);
                        fab.visibility = View.GONE
                        fab_gallery.visibility = View.GONE
                    }
                } else {
                    var bundles = Bundle()
                    bundles.putString("list", "");
                    loadFragmentInner(GroupListFragment(), bundles);
                    fab.visibility = View.GONE
                    fab_gallery.visibility = View.GONE
                }
            }
        }
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = resources.getColor(R.color.white)
        imagePicker = registerImagePicker(this@MainActivity)
        Places.initialize(applicationContext, getString(R.string.google_api_key_ishaan))
        placesClient = Places.createClient(this@MainActivity)
        googleApiClient = getAPIClientInstance()
        customDialog = Dialog(this@MainActivity)
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
        manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requestGPSSettings()
            manager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000L, 1610f, this)
        }
        locationManagerWeather = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManagerWeather!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requestGPSSettings()
            locationManagerWeather!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1800000L,
                80467f,
                this
            )
        }
        val contact_us = findViewById<LinearLayout>(R.id.refresh)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        headerLiner = findViewById<View>(R.id.headerLiner)
        val ll_advisory_level = findViewById<LinearLayout>(R.id.ll_advisory_level)
        val textlaypout = findViewById<LinearLayout>(R.id.textlaypout)
        fab.setOnClickListener(View.OnClickListener {
            if (ProjectUtil.getSession(this).equals("1", ignoreCase = true)) {
                var bundles = Bundle()
                bundles.putString("list", "");
                loadFragmentInner(GroupListFragment(), bundles);
                fab.visibility = View.GONE
            } else {
                val i = Intent(this, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
                finish()
            }

        })
        fab_gallery.setOnClickListener(View.OnClickListener {
//            if (ProjectUtil.getSession(this).equals("1", ignoreCase = true)) {
                var bundles = Bundle()
                bundles.putString("list", "");
                loadFragmentInner(GalleryFragment(), bundles);
                fab.visibility = View.GONE
                fab_gallery.visibility = View.GONE
//            } else {
//                val i = Intent(this, LoginActivity::class.java)
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(i)
//                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
//                finish()
//            }
        })


        ll_advisory_level.setOnClickListener(View.OnClickListener {
            popupAdvisoryLevel()
        })

        textlaypout.setOnClickListener(View.OnClickListener {
            popupAdvisoryLevel()
        })

        ll_toolbar_all = findViewById(R.id.ll_toolbar_all)
        secondHeader = findViewById(R.id.secondHeader)
        rlToolbarMain = toolbar.findViewById(R.id.rlToolbarMain)
        llToolbarBack = toolbar.findViewById(R.id.llToolbarBack)
        imageBack = llToolbarBack.findViewById(R.id.imageBack)
        textTitle = llToolbarBack.findViewById(R.id.textTitle)
        search = findViewById(R.id.search)
        changeLocation = findViewById(R.id.changeLocation)
        closeText = findViewById<TextView>(R.id.closeText)
        userName = findViewById<TextView>(R.id.titel)
        locationName = findViewById(R.id.placeName)
        fragment_container = findViewById(R.id.fragment_container)
        closeSeacrch = findViewById(R.id.closesearch)
        tempLayout = findViewById(R.id.tempLayout)
        text_adv_type = findViewById(R.id.text_adv_type)
        weatherImage = findViewById(R.id.weatherImage)
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")) {
                getNotificationPermission()
            }
        }
        tempLayout?.setOnClickListener(View.OnClickListener {
            popupAdvisoryWeather()
        })
        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso!!)
//        val acct = GoogleSignIn.getLastSignedInAccount(this@MainActivity)
//        if (acct != null) {
//            ProjectUtil.setFirstName(this@MainActivity, acct.displayName)
//            ProjectUtil.setMailId(this@MainActivity, acct.email)
//            ProjectUtil.customToast(this@MainActivity, ProjectUtil.getFirstName(this@MainActivity))
//            userName.text = "Hi, " + acct.displayName?.capitalize()
//        }else{
//            userName.text = "Hi, " + ProjectUtil.getFirstName(this@MainActivity)?.capitalize() + ProjectUtil.getLastName(this@MainActivity)?.capitalize()
//        }

        if (ProjectUtil.getFirstName(this@MainActivity).toString() == "") {
            userName.text = "Hi, " + "Guest"
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } else {
            userName.text = "Hi, " + ProjectUtil.getFirstName(this@MainActivity)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
        ProjectUtil.setPhotoPath(this, "")
        root_layout = findViewById(R.id.root_layout)
        firstLayout = findViewById(R.id.first)
        secondLayout = findViewById(R.id.second)
        thirdLayout = findViewById(R.id.third)
        fourthLayout = findViewById(R.id.fourth)
        firstIcon = findViewById(R.id.iconFirst)
        secondIcon = findViewById(R.id.iconSecond)
        thirdIcon = findViewById(R.id.iconThird)
        fourthIcon = findViewById(R.id.iconFourth)
        firstText = findViewById(R.id.textFirst)
        secondText = findViewById(R.id.textSecond)
        thirdText = findViewById(R.id.textThird)
        fourthText = findViewById(R.id.textFourth)
        profileNameTextTitle = findViewById(R.id.profileNameTextTitle)
        textCurrentTemp = findViewById(R.id.textCurrentTemp)
        imageAdvisoryLevel = findViewById(R.id.imageAdvisoryLevel)
        countryImageView = findViewById(R.id.countryImageView)
        if (ProjectUtil.getFirstName(this@MainActivity).toString() == "") {
            val shortName = "Guest".first().toString()
            profileNameTextTitle!!.text = shortName
        } else {
            val shortName = ProjectUtil.getFirstName(this@MainActivity).first()
                .toString() + " " + ProjectUtil.getLastName(this@MainActivity).first().toString()
            profileNameTextTitle!!.text = shortName
        }
        profileNameTextTitle!!.setTextColor(resources.getColor(R.color.white))
        headerLiner.setBackgroundColor(resources.getColor(R.color.red_bottom))
        loadFragment(RecommadationFragment())
        firstLayout?.setOnClickListener(View.OnClickListener {
//            fab.visibility = View.VISIBLE
            fab_gallery.visibility = View.VISIBLE
            llToolbarBack.isVisible = false
            rlToolbarMain.isVisible = true
            changeSsearchIcon()
            changeBottomDesignOnClick(1)
            root_layout?.setBackgroundColor(resources.getColor(R.color.red_bottom))
            toolbar.setBackgroundColor(resources.getColor(R.color.white))
            headerLiner.setBackgroundColor(resources.getColor(R.color.red_bottom))
            profileNameTextTitle!!.setTextColor(resources.getColor(R.color.white))
            profileNameTextTitle!!.setBackgroundDrawable(resources.getDrawable(R.drawable.background_example_red))
            locationName?.setTextColor(ContextCompat.getColor(this, R.color.white))
            userName.setTextColor(ContextCompat.getColor(this, R.color.black))
            changeLocation?.setTextColor(ContextCompat.getColor(this, R.color.black))
            closeText.setTextColor(ContextCompat.getColor(this, R.color.black))
            loadFragment(RecommadationFragment())

        })
        secondLayout?.setOnClickListener(View.OnClickListener {
//            fab.visibility = View.VISIBLE
            fab_gallery.visibility = View.VISIBLE
            llToolbarBack.isVisible = false
            rlToolbarMain.isVisible = true
            changeSsearchIcon()
            changeBottomDesignOnClick(2)
            root_layout?.setBackgroundColor(resources.getColor(R.color.yellow_bottom))
            profileNameTextTitle!!.setBackgroundDrawable(resources.getDrawable(R.drawable.background_example_yellow))
            headerLiner.setBackgroundColor(resources.getColor(R.color.yellow_bottom))
            profileNameTextTitle!!.setTextColor(resources.getColor(R.color.white))
            locationName?.setTextColor(ContextCompat.getColor(this, R.color.white))
            userName?.setTextColor(ContextCompat.getColor(this, R.color.black))
            changeLocation?.setTextColor(ContextCompat.getColor(this, R.color.black))
            closeText?.setTextColor(ContextCompat.getColor(this, R.color.black))
//            loadFragment(ApplicationsFragment())
            loadFragment(ApplicationsMainFragment())

        })
        thirdLayout?.setOnClickListener(View.OnClickListener {
//            fab.visibility = View.VISIBLE
            fab_gallery.visibility = View.VISIBLE
            llToolbarBack.isVisible = false
            rlToolbarMain.isVisible = true
            changeSsearchIcon()
            changeBottomDesignOnClick(3)
            root_layout?.setBackgroundColor(resources.getColor(R.color.green_bottom))
            profileNameTextTitle!!.setBackgroundDrawable(resources.getDrawable(R.drawable.background_example_green))
            headerLiner.setBackgroundColor(resources.getColor(R.color.green_bottom))
            profileNameTextTitle!!.setTextColor(resources.getColor(R.color.white))
            locationName?.setTextColor(ContextCompat.getColor(this, R.color.white))
            userName?.setTextColor(ContextCompat.getColor(this, R.color.black))
            changeLocation?.setTextColor(ContextCompat.getColor(this, R.color.black))
            closeText?.setTextColor(ContextCompat.getColor(this, R.color.black))
            loadFragment(PlanerMainFragment())
//            loadFragment(PlannerNewFragment())

        })
        fourthLayout?.setOnClickListener(View.OnClickListener {
//            fab.visibility = View.VISIBLE
            fab_gallery.visibility = View.VISIBLE
            llToolbarBack.isVisible = false
            rlToolbarMain.isVisible = true
            changeSsearchIcon()
            changeBottomDesignOnClick(4)
            root_layout?.setBackgroundColor(resources.getColor(R.color.blue_bottom))
            profileNameTextTitle!!.setBackgroundDrawable(resources.getDrawable(R.drawable.background_example_blue))
            headerLiner.setBackgroundColor(resources.getColor(R.color.blue_bottom))
            profileNameTextTitle!!.setTextColor(resources.getColor(R.color.white))
            locationName?.setTextColor(ContextCompat.getColor(this, R.color.white))
            userName?.setTextColor(ContextCompat.getColor(this, R.color.black))
            changeLocation?.setTextColor(ContextCompat.getColor(this, R.color.black))
            closeText?.setTextColor(ContextCompat.getColor(this, R.color.black))
            loadFragment(PerksMainFragment())
        })

        contact_us.setOnClickListener { view: View? ->
            signOut()
        }

        search!!.setOnClickListener { view: View? -> searchPopupShow() }

        closeSeacrch!!.setOnClickListener { view: View? -> searchPopupHide() }

        checkLocationPermission()
        if (ProjectUtil.getSession(this@MainActivity).equals("1", ignoreCase = true)) {
            checkMediaLocationPermission()
        }
        fetchCurrentLocation()
        val token = AutocompleteSessionToken.newInstance()

        ProjectUtil.getAddress(
            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext),
            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext),
            locationName,
            this
        )

        imageBack.setOnClickListener { v: View? ->
            val currentFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is AdvisoryDetailsFragment) {
                callBackFragment(1)
            } else if (currentFragment is PlannerDetailsFragment) {
                callBackFragment(2)
            }
        }
//        locationName?.text = address
    }

    private fun showDialogLocation(applicationContext: Context, s: String, _groupId: String) {
        Log.d("dsjhbfsdkjbfsdjssd", "sjow dialog")
        val alert = AlertDialog.Builder(applicationContext)
        var mView: View = layoutInflater.inflate(R.layout.dialog_noti_location, null)
        val text_top = mView.findViewById<TextView>(R.id.text_top)
        text_top.text = "Share Location"
        val text_details = mView.findViewById<TextView>(R.id.text_details)
        text_details.text = s
        val btn_cancel = mView.findViewById<Button>(R.id.btn_cancel)
        val btn_okay = mView.findViewById<Button>(R.id.btn_okay)
        alert.setView(mView)
        val alertDialog = alert.create()
        alertDialog.setCanceledOnTouchOutside(false)
        btn_cancel.setOnClickListener { v: View? -> alertDialog.dismiss() }
        btn_okay.setOnClickListener {
            alertDialog.dismiss()
            status = "1"
            groupId = _groupId
            sendData
        }
        alertDialog.show()
    }

    private val sendData: Unit
        get() {
            try {
                ProjectUtil.showDialog(customDialog, "Please wait...")
                val data = DeleteGroupList()
                data.action = "locationTracking"
                data.groupID = groupId
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
                        runOnUiThread(Runnable {
                            ProjectUtil.dismissDialog(customDialog)
                            val json = JSONObject(responseString)
                            if (json.getString("status").equals("success")) {
                                ProjectUtil.customToast(
                                    this,
                                    json.getString("message")
                                )
                            } else {
                                ProjectUtil.customToast(
                                    this,
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

    fun loadFragment(fragment: Fragment): Boolean {
        if (fragment != null) {
            if (fragment is RecommadationFragment) {
                var bundle = Bundle()
                bundle.putInt("fromWhichCategory", 1)
                fragment.arguments = bundle
            }
            supportFragmentManager
                .beginTransaction()
                .addToBackStack("ABC")
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        fragment!!.arguments = bundle
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }

    fun signOut() {
        if (ProjectUtil.getSession(this@MainActivity).equals("1", ignoreCase = true)) {
            val i = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(i)
        } else {
            val i = Intent(this@MainActivity, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
            finish()
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10011 && resultCode == Activity.RESULT_OK) {
            try {
                val currentFragment: Fragment? =
                    supportFragmentManager.findFragmentById(R.id.fragment_container)
                when (currentFragment) {
                    null -> {
                        loadFragment(RecommadationFragment())
                        //                    autoClick(firstLayout!!)
                    }

                    is RecommadationFragment -> {
                        loadFragment(RecommadationFragment())
                        //                    autoClick(firstLayout!!)
                    }

                    is ApplicationsFragment -> {
                        loadFragment(ApplicationsFragment())
                        //                    autoClick(secondLayout!!)
                    }

                    is PlannerNewFragment -> {
                        loadFragment(PlannerNewFragment())
                        //                    autoClick(thirdLayout!!)
                    }

                    is GalleryFragment -> {
                        loadFragment(GalleryFragment())
                        //                    autoClick(fourthLayout!!)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun autoClick(linearLayout: LinearLayout) {
        Handler().postDelayed(Runnable { linearLayout.performClick() }, 5000)
    }

    fun searchPopupShow() {
        startActivityForResult(Intent(this@MainActivity, LoactionFragment::class.java), 10011)
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
        closeSeacrch!!.visibility = View.VISIBLE
        tempLayout!!.visibility = View.GONE
        search!!.visibility = View.GONE
        changeLocation!!.visibility = View.GONE
    }

    fun searchPopupHide() {

        ProjectUtil.hideKeyboard(this)
        search!!.visibility = View.VISIBLE
        changeLocation!!.visibility = View.VISIBLE
        closeSeacrch!!.visibility = View.GONE
        tempLayout!!.visibility = View.VISIBLE
    }

    fun changeSsearchIcon() {
        search!!.visibility = View.VISIBLE
        changeLocation!!.visibility = View.VISIBLE
        closeSeacrch!!.visibility = View.GONE
        tempLayout!!.visibility = View.VISIBLE
    }

    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("title_location_permission")
                    .setMessage("title_location_permission")
                    .setPositiveButton(
                        "ok"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    fun checkMediaLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Media Location")
                    .setMessage("Kapita would like to access your media location for fetching the media information based on your selected media.")
                    .setPositiveButton(
                        "ok"
                    ) { dialogInterface, i ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION),
                            PreLoginActivity.MY_PERMISSIONS_REQUEST_MEDIA_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION),
                    PreLoginActivity.MY_PERMISSIONS_REQUEST_MEDIA_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        var mGPS = GPSTracker(this)
                        if (mGPS.canGetLocation()) {
                            mGPS.location
//                            ProjectUtil.customToast(
//                                this,
//                                "Lat" + mGPS.latitude + "Lon" + mGPS.longitude
//                            )
//                            locationName?.text = mGPS.location.provider
                        } else {
                            ProjectUtil.customToast(this, "Unabletofind")
                            println("Unable")
                        }


                        //Request location updates:
                        if (manager != null) {
                            manager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                300000L,
                                1610f,
                                this
                            )
                        }
                        if (locationManagerWeather != null) {
                            locationManagerWeather!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1800000L,
                                80467f,
                                this
                            )
                        }
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            MY_PERMISSIONS_REQUEST_MEDIA_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun fetchCurrentLocation() {
        var mGPS = GPSTracker(this)
        if (mGPS.canGetLocation()) {
            mGPS.location
            ProjectUtil.setLongitude(this@MainActivity, "" + mGPS.longitude)
            ProjectUtil.setLotitude(this@MainActivity, "" + mGPS.latitude)
            ProjectUtil.setCurrentLongitude(this@MainActivity, "" + mGPS.longitude)
            ProjectUtil.setCurrentLotitude(this@MainActivity, "" + mGPS.latitude)
            ProjectUtil.getAddress(
                ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext),
                ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext),
                locationName,
                this
            )
//            locationName?.text = address
        } else {
            println("Unable")
        }
    }

    override fun onLocationChanged(p0: Location) {
        if (shouldUseCurrentLocation) {
            ProjectUtil.setLongitude(this@MainActivity, "" + p0.longitude)
            ProjectUtil.setLotitude(this@MainActivity, "" + p0.latitude)
            ProjectUtil.getAddress(
                ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext),
                ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext),
                locationName,
                this
            )
            if (isFirstTime) {
                reloadCurrentFragment()
                getAdvisoryCrimeData
                getAdvisoryWeatherData
            } else {
                isFirstTime = true
            }

        }

    }

    private fun reloadCurrentFragment() {
        var currentFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (currentFragment) {
            null -> {
                loadFragment(RecommadationFragment())
            }

            is RecommadationFragment -> {
                loadFragment(RecommadationFragment())
            }

            is ApplicationsFragment -> {
                loadFragment(ApplicationsFragment())
            }

            is PlannerNewFragment -> {
                loadFragment(PlannerNewFragment())
            }

            is GalleryFragment -> {
                loadFragment(GalleryFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showMainMenu()
        searchPopupHide()
        ProjectUtil.getCurrentTimeZone()
        if (shouldUseCurrentLocation) {
            fetchCurrentLocation()
//            reloadCurrentFragment()
        }
        ProjectUtil.getAddress(
            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext),
            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext),
            locationName,
            this
        )
//        locationName?.text = address

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (manager != null) {
                manager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000L, 1610f, this)
            }
            if (locationManagerWeather != null) {
                locationManagerWeather!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1800000L,
                    80467f,
                    this
                )
            }
//            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
        getAdvisoryCrimeData
        getAdvisoryWeatherData
    }

    private fun requestGPSSettings() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 500
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient!!, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i("", "All location settings are satisfied.")
//                    Toast.makeText(application, "GPS is already enable", Toast.LENGTH_SHORT).show()
                }

                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "",
                        "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings "
                    )
                    try {
                        status.startResolutionForResult(
                            this@MainActivity,
                            LoginActivity.REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: SendIntentException) {
                        Log.e("Applicationsett", e.toString())
                    }
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Log.i(
                        "",
                        "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created."
                    )
                    Toast.makeText(
                        application,
                        "Location settings are inadequate, and cannot be fixed here",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getAPIClientInstance(): GoogleApiClient {
        return GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()
    }

    fun requestCameraImage() {
        var fragment =
            SSPickerOptionsBottomSheet.newInstance(R.style.CustomPickerBottomSheet)
        fragment.show(supportFragmentManager, SSPickerOptionsBottomSheet.BOTTOM_SHEET_TAG)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onPickerOptions(pickerOptions: PickerOptions) {
        this.pickerOptions = pickerOptions
        openImagePicker()
    }

    override fun onImagePick(uri: Uri?) {
        uri?.let { updateImageList(listOf(it)) }
    }

    private fun updateImageList(list: List<Uri>) {
        setPhotoToImageViewList(list)
    }

    override fun onMultiImagePick(uris: List<Uri>?) {
        setPhotoToImageViewList(uris!!)
    }

    override fun onImageProvider(provider: ImageProvider) {
        when (provider) {
            ImageProvider.GALLERY -> {
                pickerOptions = pickerOptions.copy(pickerType = PickerType.GALLERY)
                openImagePicker()
            }

            ImageProvider.CAMERA -> {
                pickerOptions = pickerOptions.copy(pickerType = PickerType.CAMERA)
                openImagePicker()
            }

            ImageProvider.NONE -> {
                //User has pressed cancel show anything or just leave it blank.
            }
        }
    }

    private fun openImagePicker() {
        imagePicker!!.title("Kipita Image Picker")
            .multipleSelection(pickerOptions.allowMultipleSelection, 5)
            .showCountInToolBar(pickerOptions.showCountInToolBar)
            .showFolder(pickerOptions.showFolders)
            .cameraIcon(pickerOptions.showCameraIconInGallery)
            .doneIcon(pickerOptions.isDoneIcon)
            .allowCropping(pickerOptions.openCropOptions)
            .compressImage(pickerOptions.compressImage)
            .maxImageSize(pickerOptions.maxPickSizeMB)
            .extension(pickerOptions.pickExtension)
        if (isAtLeast11()) {
            imagePicker!!.systemPicker(pickerOptions.openSystemPicker)
        }
        imagePicker!!.open(pickerOptions.pickerType)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            if (doubleBackToExitPressedOnce) {
                finish()
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                doubleBackToExitPressedOnce = false
            }, 3000)
        } else {
            if (currentFragment is AdvisoryDetailsFragment) {
                callBackFragment(1)
            } else if (currentFragment is PlannerDetailsFragment) {
                callBackFragment(2)
            } else if (currentFragment is GroupDetailsFragment) {
                callBackFragment(3)
            } else if (currentFragment is GroupListFragment) {
                callBackFragment(4)
            } else if (currentFragment is InviteMembersFragment) {
                callBackFragment(5)
            } else if (currentFragment is CreateGroupFragment) {
                callBackFragment(6)
            } else if (currentFragment is PendingInvitesFragment) {
                callBackFragment(7)
            } else if (currentFragment is GalleryFragment) {
                callBackFragment(8)
            } else if (currentFragment is LocationMapFragment) {
                callBackFragment(9)
            } else {
                super.onBackPressed()
                backpress += 1
                Toast.makeText(applicationContext, " Press Back again to Exit ", Toast.LENGTH_SHORT)
                    .show()
                if (backpress > 1) {
                    finish()
                }
            }
//            super.onBackPressed()
            return
        }

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun callBackFragment(i: Int) {
        llToolbarBack.isVisible = false
        rlToolbarMain.isVisible = true
        ll_toolbar_all.isVisible = true
        when (i) {
            1 -> {
                val applicationsFragment = ApplicationsFragment()
                val transaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, applicationsFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            2 -> {
                val plannerNewFragment = PlannerNewFragment()
                val transaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, plannerNewFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            3 -> {
                val plannerNewFragment = GroupListFragment()
                val transaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, plannerNewFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            4 -> {
//                fab.visibility = View.VISIBLE
                fab_gallery.visibility = View.VISIBLE
                llToolbarBack.isVisible = false
                rlToolbarMain.isVisible = true
                changeSsearchIcon()
                changeBottomDesignOnClick(3)
                root_layout?.setBackgroundColor(resources.getColor(R.color.green_bottom))
                profileNameTextTitle!!.setBackgroundDrawable(resources.getDrawable(R.drawable.background_example_green))
                headerLiner.setBackgroundColor(resources.getColor(R.color.green_bottom))
                profileNameTextTitle!!.setTextColor(resources.getColor(R.color.white))
                locationName?.setTextColor(ContextCompat.getColor(this, R.color.white))
                userName?.setTextColor(ContextCompat.getColor(this, R.color.black))
                changeLocation?.setTextColor(ContextCompat.getColor(this, R.color.black))
                closeText?.setTextColor(ContextCompat.getColor(this, R.color.black))
                val plannerNewFragment = PlanerMainFragment()
                val transaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, plannerNewFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            5 -> {
                val plannerNewFragment = CreateGroupFragment()
                val transaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, plannerNewFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            6, 7 -> {
                val plannerNewFragment = GroupListFragment()
                val transaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, plannerNewFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            8 -> {
                fab_gallery.visibility = View.VISIBLE
                llToolbarBack.isVisible = false
                rlToolbarMain.isVisible = true
                changeSsearchIcon()
                changeBottomDesignOnClick(1)
                root_layout?.setBackgroundColor(resources.getColor(R.color.red_bottom))
                headerLiner.setBackgroundColor(resources.getColor(R.color.red_bottom))
                profileNameTextTitle!!.setTextColor(resources.getColor(R.color.white))
                profileNameTextTitle!!.setBackgroundDrawable(resources.getDrawable(R.drawable.background_example_red))
                locationName?.setTextColor(ContextCompat.getColor(this, R.color.white))
                userName.setTextColor(ContextCompat.getColor(this, R.color.black))
                changeLocation?.setTextColor(ContextCompat.getColor(this, R.color.black))
                closeText.setTextColor(ContextCompat.getColor(this, R.color.black))
                val recommadationFragment = RecommadationFragment()
                val transaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, recommadationFragment)
                transaction.commit()
            }
            9 -> {
                val plannerNewFragment = MembersManageFragment()
                val transaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, plannerNewFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

    fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/" + fileName
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        try {
            return try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                try {
                    cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
                } catch (e: Exception) {
                    return ""
                }
                val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(columnIndex)
            } finally {
                cursor?.close()
            }
        } catch (e: Exception) {
        }
        return ""
    }

    @SuppressLint("Range")
    private fun setPhotoToImageViewList(list: List<Uri>) {

        var allFilePath = StringBuilder("")
        var allFileMetaInfo = StringBuilder("")
        var allFileDateTimeInfo = StringBuilder("")

        for (photoList1 in list) {
            val photoUri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && checkMediaLocationPermission()) {
                    MediaStore.setRequireOriginal(photoList1)
                } else {
                    photoList1
                }
            var photoListData1 = ""
            val fileData: File = File(getFileName(photoUri))
            val fileData1: File = File(getRealPathFromURI(this@MainActivity, photoUri))
            if (fileData.exists()) {
                photoListData1 = (fileData.absolutePath)
            }
            if (fileData1.exists()) {
                photoListData1 = (fileData1.absolutePath)
            }
            allFilePath.append(photoListData1).append(",")

            val exif = ExifInterface(photoListData1)

            if (exif.getAttribute(ExifInterface.TAG_DATETIME) != null)
                allFileDateTimeInfo.append(
                    ProjectUtil.dateTimeToMillis(
                        exif.getAttribute(
                            ExifInterface.TAG_DATETIME
                        )
                    )
                ).append(",")
            else
                allFileDateTimeInfo.append("" + System.currentTimeMillis()).append(",")

            if (exif.latLong?.size == 2) {
                allFileMetaInfo.append(exif.latLong!![0].toString() + "/" + exif.latLong!![1])
                    .append(",")
            } else {
                if (exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null) {
                    var geoDegree = GeoDegree(exif)
                    if (geoDegree.isValid && !geoDegree.latitudeAndLongitude.equals("0.0/0.0")) {
                        allFileMetaInfo.append(geoDegree.latitudeAndLongitude).append(",")
                    } else {
                        allFileMetaInfo.append(
                            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                                .toString() + "/" + ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        ).append(",")
                    }
                } else {
                    allFileMetaInfo.append(
                        ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                            .toString() + "/" + ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                    ).append(",")
                }
            }

        }
        ProjectUtil.setPhotoMetaData(this@MainActivity, allFileMetaInfo.toString())
        ProjectUtil.setPhotoPath(this@MainActivity, allFilePath.toString())
        ProjectUtil.setPhotoDateTimeData(this@MainActivity, allFileDateTimeInfo.toString())

    }

    fun showMainMenu() {
        var linearLayoutMainMenu = findViewById<LinearLayout>(R.id.linearLayoutMainMenu)
        var linearLayoutSubMenu = findViewById<LinearLayout>(R.id.linearLayoutSubMenu)
        linearLayoutMainMenu.visibility = View.VISIBLE
        linearLayoutSubMenu.visibility = View.GONE
    }

    fun showSubMenu() {
        var linearLayoutMainMenu = findViewById<LinearLayout>(R.id.linearLayoutMainMenu)
        var linearLayoutSubMenu = findViewById<LinearLayout>(R.id.linearLayoutSubMenu)
        linearLayoutMainMenu.visibility = View.GONE
        linearLayoutSubMenu.visibility = View.VISIBLE
    }

    fun changeBottomDesignOnClick(index: Int) {
        if (index == 1) {
            firstLayout?.setBackgroundColor(resources.getColor(R.color.red_bottom))
            secondLayout?.setBackgroundColor(resources.getColor(R.color.white))
            thirdLayout?.setBackgroundColor(resources.getColor(R.color.white))
            fourthLayout?.setBackgroundColor(resources.getColor(R.color.white))
            firstIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.white))
            secondIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            thirdIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            fourthIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            firstText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            secondText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            thirdText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            fourthText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
        } else if (index == 2) {
            firstLayout?.setBackgroundColor(resources.getColor(R.color.white))
            secondLayout?.setBackgroundColor(resources.getColor(R.color.yellow_bottom))
            thirdLayout?.setBackgroundColor(resources.getColor(R.color.white))
            fourthLayout?.setBackgroundColor(resources.getColor(R.color.white))
            firstIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            secondIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.white))
            thirdIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            fourthIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            firstText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            secondText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            thirdText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            fourthText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
        } else if (index == 3) {
            firstLayout?.setBackgroundColor(resources.getColor(R.color.white))
            secondLayout?.setBackgroundColor(resources.getColor(R.color.white))
            thirdLayout?.setBackgroundColor(resources.getColor(R.color.green_bottom))
            fourthLayout?.setBackgroundColor(resources.getColor(R.color.white))
            firstIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            secondIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            thirdIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.white))
            fourthIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            firstText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            secondText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            thirdText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            fourthText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
        } else if (index == 4) {
            firstLayout?.setBackgroundColor(resources.getColor(R.color.white))
            secondLayout?.setBackgroundColor(resources.getColor(R.color.white))
            thirdLayout?.setBackgroundColor(resources.getColor(R.color.white))
            fourthLayout?.setBackgroundColor(resources.getColor(R.color.blue_bottom))
            firstIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            secondIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            thirdIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            fourthIcon?.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.white))
            firstText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            secondText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            thirdText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            fourthText?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
        }
    }

    private val getAdvisoryCrimeData: Unit
        get() {
            try {
//                ProjectUtil.showDialog(customDialog, "Please wait...")
                val lat =
                    ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        .toDouble()
                val lng =
                    ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        .toDouble()
                val rootObject = JSONObject()
                rootObject.put(
                    "userId",
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                )
                rootObject.put("country", ProjectUtil.getCountryCode(lat, lng))
                rootObject.put(
                    "zipCode", ProjectUtil.getZipCode(lat, lng)
                )
                rootObject.put(
                    "timeZone",
                    ProjectUtil.getCurrentTimeZone()
                )
                rootObject.put(
                    "lat",
                    lat
                )
                rootObject.put(
                    "long",
                    lng
                )
                Log.d("jsonString", rootObject.toString())
                Thread {
                    try {
                        val httpclient: HttpClient = DefaultHttpClient()
                        val post = HttpPost(RetrofitClient.PRODUCTION_URL + "/getWeatherAdvisory")
                        val holder = JSONObject(rootObject.toString())
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
                        val j: String = responseString.replace("\\\\".toRegex(), "")
                        Log.d("getWeatherAdvisory", j)
                        val jsonObject = JSONObject(j)
                        var advisoryIcon = ""
                        if (jsonObject.getString("status") == "success") {
                            val jsonObject1 = JSONObject(jsonObject.getString("data"))
                            flag = jsonObject1.getString("flag")
                            advisoryLevel = jsonObject1.getString("advisory_level")
                            advisoryDescription =
                                jsonObject1.getString("advisory_short_description")
                                    .replace(">n<", "><")

                            advisoryIcon = jsonObject1.getString("advisory_icon")


                        }
                        runOnUiThread(Runnable {
                            try {
                                Picasso.get().load(flag).into(countryImageView)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            text_adv_type!!.text = advisoryLevel
                            try {
                                Picasso.get().load(advisoryIcon).into(imageAdvisoryLevel)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }

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

    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    private fun popupAdvisoryLevel() {
        try {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.popup_advisory_level, null)
            val pw = PopupWindow(
                layout,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )
            val lat = ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                .toDouble()
            val lng = ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                .toDouble()
//            val lat =32.083333;
//            val lng =34.7999968;
            pw.animationStyle = R.style.popup_window_animation
            pw.setBackgroundDrawable(resources.getDrawable(R.drawable.back_round_top))
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.BOTTOM, 0, 0)
            val image_close = layout.findViewById<ImageView>(R.id.image_close)
            val text_city = layout.findViewById<TextView>(R.id.text_city)
            val text_level = layout.findViewById<TextView>(R.id.text_level)
            val text_level_details = layout.findViewById<TextView>(R.id.text_level_details)
            val text_country = layout.findViewById<TextView>(R.id.text_country)
            val text_level1 = layout.findViewById<TextView>(R.id.text_level1)
            val btn_read_more = layout.findViewById<Button>(R.id.btn_read_more)
            val rl_country = layout.findViewById<RelativeLayout>(R.id.rl_country)

            text_city.text = ProjectUtil.getCityName(lat, lng)
            text_country.text = ProjectUtil.getCountryName(lat, lng)
            text_level.text = advisoryLevel
            text_level_details.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(advisoryDescription, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(advisoryDescription)
            }
            text_level1.text = advisoryLevel
            image_close.setOnClickListener { v: View? -> pw.dismiss() }
            if (ProjectUtil.getCountryName(lat, lng) == "United States") {
                btn_read_more.visibility = View.GONE
                rl_country.visibility = View.GONE
            } else {
                btn_read_more.visibility = View.VISIBLE
                rl_country.visibility = View.VISIBLE
            }
            btn_read_more.setOnClickListener { v: View? ->
                pw.dismiss()
//                var bundle = Bundle()
//                bundle.putParcelable("data", imageUploadRequest)
                startActivity(
                    Intent(
                        this,
                        ApplicationsFragmentActivity::class.java
                    )
                )//.putExtra("data", bundle))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val getAdvisoryWeatherData: Unit
        get() {
            try {
//                ProjectUtil.showDialog(customDialog, "Please wait...")
                val lat =
                    ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext)
                        .toDouble()
                val lng =
                    ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
                        .toDouble()
                val rootObject = JSONObject()
                rootObject.put(
                    "userId",
                    ProjectUtil.getUserId(MyApplication.getInstance().applicationContext)
                )
                rootObject.put("country", ProjectUtil.getCountryCode(lat, lng))
                rootObject.put(
                    "zipCode", ProjectUtil.getZipCode(lat, lng)
                )
                rootObject.put(
                    "timeZone",
                    ProjectUtil.getCurrentTimeZone()
                )
                rootObject.put(
                    "lat",
                    lat
                )
                rootObject.put(
                    "long",
                    lng
                )
                Log.d("jsonString", rootObject.toString())
                Thread {
                    try {
                        val httpclient: HttpClient = DefaultHttpClient()
                        val post =
                            HttpGet("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + lat + "%2C" + lng + "/next7days?unitGroup=metric&elements=datetime%2CdatetimeEpoch%2Clatitude%2Clongitude%2Ctempmax%2Ctempmin%2Ctemp%2Cfeelslikemax%2Cfeelslikemin%2Cfeelslike%2Chumidity%2Cprecipprob%2Cpreciptype%2Csnow%2Csnowdepth%2Cwindspeed%2Cwindspeedmax%2Cwindspeedmean%2Cwindspeedmin%2Cwinddir%2Cpressure%2Ccloudcover%2Cvisibility%2Csevererisk%2Csunrise%2Csunset%2Cconditions%2Cdescription%2Cicon%2Caqius%2Caqieur&include=current%2Cdays&key=PRQ3752M3HA6UBNYSQSGUVUKY&contentType=json")
//
//                        val holder = JSONObject(rootObject.toString())
//                        val se = StringEntity(holder.toString())
//                        post.entity = se
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
                        val j: String = responseString.replace("\\\\".toRegex(), "")
                        Log.d("getWeatherAdvisory", j)
                        jsonObjectWeatherInfo = JSONObject(j)
//                        if (jsonObject.getString("status") == "success") {
                        val jsonObject1 =
                            JSONObject(jsonObjectWeatherInfo.getString("currentConditions"))
                        val weatherIconName = jsonObject1.getString("icon")
                        val currentTemp = jsonObject1.getString("temp")

//                        }
                        runOnUiThread(Runnable {
                            try {
                                Picasso.get()
                                    .load("https://traversedata.nyc3.cdn.digitaloceanspaces.com/images/icons/weather/" + weatherIconName + ".png")
                                    .into(weatherImage)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            if (ProjectUtil.getIsTempInFahrenheit(this@MainActivity)) {
                                textCurrentTemp?.text = ProjectUtil.changeTempInFrehFromCelsius(
                                    currentTemp.toString().toDouble()
                                ) + getString(R.string.f_label)
                            } else {
                                textCurrentTemp?.text =
                                    currentTemp + getString(R.string.celcius_label)
                            }
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

    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams", "SetTextI18n")
    private fun popupAdvisoryWeather() {
        try {
            val layout = BottomSheetDialog(this, R.style.popup_window_animation)
            layout.requestWindowFeature(Window.FEATURE_NO_TITLE)
            layout.setContentView(R.layout.popup_weather_info)
            val textWeatherView = layout.findViewById<TextView>(R.id.textWeatherView)
            val image_close = layout.findViewById<ImageView>(R.id.image_close)
            val text_temp_main = layout.findViewById<TextView>(R.id.text_temp_main)
            val text_feel_like = layout.findViewById<TextView>(R.id.text_feel_like)
            val text_min = layout.findViewById<TextView>(R.id.text_min)
            val text_max = layout.findViewById<TextView>(R.id.text_max)
            val image_main = layout.findViewById<ImageView>(R.id.image_main)
            val textWeatherStatusMain = layout.findViewById<TextView>(R.id.textWeatherStatusMain)
            val text_time_sunrise_am = layout.findViewById<TextView>(R.id.text_time_am)
            val text_time_sunset_pm = layout.findViewById<TextView>(R.id.text_time_pm)
            val text_precipitation = layout.findViewById<TextView>(R.id.text_precipitation)
            val text_humidity = layout.findViewById<TextView>(R.id.text_humidity)
            val dgreeOrfrenit = layout.findViewById<SwitchButton>(R.id.dgreeOrfrenit)
            val kilometerOrmiles = layout.findViewById<SwitchButton>(R.id.kilometerOrmiles)
            val text_wind = layout.findViewById<TextView>(R.id.text_wind)
            val text_speed_type = layout.findViewById<TextView>(R.id.text_speed_type)
            val text_visibility = layout.findViewById<TextView>(R.id.text_visibility)
            val text_pressure = layout.findViewById<TextView>(R.id.text_pressure)
//            val text_chance = layout.findViewById<TextView>(R.id.text_chance)
            val text_air_type = layout.findViewById<TextView>(R.id.text_air_type)
            val text_show_aqi = layout.findViewById<TextView>(R.id.text_show_aqi)
            val recyclerView_days = layout.findViewById<RecyclerView>(R.id.recyclerView_days)
            val currentWeather = jsonObjectWeatherInfo.getJSONObject("currentConditions")
            val jsonArray = jsonObjectWeatherInfo.getJSONArray("days")
            val daysList: ArrayList<DaysItemObject> = ArrayList<DaysItemObject>()
            val daysWeatherToday = jsonArray.getJSONObject(0)
            textWeatherView?.text =
                "Today's Forecast - " + daysWeatherToday.getString("description")

            dgreeOrfrenit!!.isChecked = ProjectUtil.getIsTempInFahrenheit(this@MainActivity)
            kilometerOrmiles!!.isChecked = ProjectUtil.getIsDistanceInMiles(this@MainActivity)

            logAdapter = DaysAdapter(this@MainActivity, daysList, true)
            recyclerView_days?.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView_days?.adapter = logAdapter
            logAdapter.notifyDataSetChanged()

            if (ProjectUtil.getIsTempInFahrenheit(this@MainActivity)) {
                text_temp_main?.text = ProjectUtil.changeTempInFrehFromCelsius(
                    currentWeather.getString("temp").toString().toDouble()
                ) + getString(R.string.f_label)
                text_feel_like?.text = "Feel Like " + ProjectUtil.changeTempInFrehFromCelsius(
                    currentWeather.getString("feelslike").toString().toDouble()
                ) + getString(R.string.f_label)
                text_min?.text = "" + ProjectUtil.changeTempInFrehFromCelsius(
                    daysWeatherToday.getString("tempmin").toString().toDouble()
                ) + getString(R.string.f_label)
                text_max?.text = "" + ProjectUtil.changeTempInFrehFromCelsius(
                    daysWeatherToday.getString("tempmax").toString().toDouble()
                ) + getString(R.string.f_label)

            } else {
                text_temp_main?.text =
                    currentWeather.getString("temp") + getString(R.string.celcius_label)
                text_feel_like?.text =
                    "Feel Like " + currentWeather.getString("feelslike") + getString(R.string.celcius_label)
                text_min?.text =
                    "" + daysWeatherToday.getString("tempmin") + getString(R.string.celcius_label)
                text_max?.text =
                    "" + daysWeatherToday.getString("tempmax") + getString(R.string.celcius_label)
            }

            if (ProjectUtil.getIsDistanceInMiles(this@MainActivity)) {
                text_wind?.text = ProjectUtil.changeDistanceInMilesFromKm(
                    currentWeather.getString("windspeed").toString().toDouble()
                )
                text_speed_type?.text = " Miles/H"
                text_visibility?.text = ProjectUtil.changeDistanceInMilesFromKm(
                    currentWeather.getString("visibility").toString().toDouble()
                ) + " Miles"
            } else {
                text_wind?.text = ProjectUtil.changeDistanceInKmFromMiles(
                    currentWeather.getString("windspeed").toString().toDouble()
                )
                text_speed_type?.text = " KM/H"
                text_visibility?.text = ProjectUtil.changeDistanceInKmFromMiles(
                    currentWeather.getString("visibility").toString().toDouble()
                ) + " KM"
            }



            dgreeOrfrenit.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
                ProjectUtil.setIsTempInFahrenheit(this@MainActivity, isChecked)
                logAdapter.notifyDataSetChanged()
                if (isChecked) {
                    textCurrentTemp?.text = ProjectUtil.changeTempInFrehFromCelsius(
                        textCurrentTemp?.text.toString().split("°")
                            .toTypedArray()[0].toDouble()
                    ) + getString(R.string.f_label)
                    text_temp_main?.text = ProjectUtil.changeTempInFrehFromCelsius(
                        text_temp_main?.text.toString().split("°")
                            .toTypedArray()[0].toDouble()
                    ) + getString(R.string.f_label)
                    val flc = text_feel_like?.text.toString().split(" ").toTypedArray()[2]
                    text_feel_like?.text =
                        "Feel Like " + ProjectUtil.changeTempInFrehFromCelsius(
                            flc.split("°")
                                .toTypedArray()[0].toDouble()
                        ) + getString(R.string.f_label)
                    text_min?.text = ProjectUtil.changeTempInFrehFromCelsius(
                        text_min?.text.toString().split("°")
                            .toTypedArray()[0].toDouble()
                    ) + getString(R.string.f_label)
                    text_max?.text = ProjectUtil.changeTempInFrehFromCelsius(
                        text_max?.text.toString().split("°")
                            .toTypedArray()[0].toDouble()
                    ) + getString(R.string.f_label)
                } else {
                    textCurrentTemp?.text = ProjectUtil.changeTempInCelsiusFromFreh(
                        textCurrentTemp?.text.toString().split("°")
                            .toTypedArray()[0].toDouble()
                    ) + getString(R.string.celcius_label)
                    text_temp_main?.text = ProjectUtil.changeTempInCelsiusFromFreh(
                        text_temp_main?.text.toString().split("°")
                            .toTypedArray()[0].toDouble()
                    ) + getString(R.string.celcius_label)
                    val flc = text_feel_like?.text.toString().split(" ").toTypedArray()[2]
                    text_feel_like?.text =
                        "Feel Like " + ProjectUtil.changeTempInCelsiusFromFreh(
                            flc.split("°")
                                .toTypedArray()[0].toDouble()
                        ) + getString(R.string.celcius_label)
                    text_min?.text = ProjectUtil.changeTempInCelsiusFromFreh(
                        text_min?.text.toString().split("°")
                            .toTypedArray()[0].toDouble()
                    ) + getString(R.string.celcius_label)
                    text_max?.text = ProjectUtil.changeTempInCelsiusFromFreh(
                        text_max?.text.toString().split("°")
                            .toTypedArray()[0].toDouble()
                    ) + getString(R.string.celcius_label)
                }

            })

            kilometerOrmiles.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
                ProjectUtil.setIsDistanceInMiles(this@MainActivity, isChecked)
                logAdapter.notifyDataSetChanged()
                if (isChecked) {
                    text_wind?.text = ProjectUtil.changeDistanceInMilesFromKm(
                        text_wind?.text.toString().toDouble()
                    )
                    text_speed_type?.text = " Miles/H"
                    text_visibility?.text = ProjectUtil.changeDistanceInMilesFromKm(
                        text_visibility?.text.toString().split(" ")
                            .toTypedArray()[0].toDouble()
                    ) + " Miles"
                } else {
                    text_wind?.text = ProjectUtil.changeDistanceInKmFromMiles(
                        text_wind?.text.toString().toDouble()
                    )
                    text_speed_type?.text = " KM/H"
                    text_visibility?.text = ProjectUtil.changeDistanceInKmFromMiles(
                        text_visibility?.text.toString().split(" ")
                            .toTypedArray()[0].toDouble()
                    ) + " KM"
                }
            })

            try {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject1 = jsonArray.getJSONObject(i)
                    val gson = Gson()
                    daysList.add(
                        gson.fromJson(
                            jsonObject1.toString(),
                            DaysItemObject::class.java
                        )
                    )
                }
                logAdapter = DaysAdapter(this@MainActivity, daysList, false)
                recyclerView_days?.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                recyclerView_days?.adapter = logAdapter
            } catch (e: Exception) {
                e.printStackTrace()
            }
            image_close!!.setOnClickListener { v: View? -> layout.dismiss() }

            val sdf = SimpleDateFormat("HH:mm:ss")
            val outputSdf = SimpleDateFormat("hh:mm a")
            text_time_sunrise_am?.text =
                outputSdf.format(sdf.parse(currentWeather.getString("sunrise").toString()))
            text_time_sunset_pm?.text =
                outputSdf.format(sdf.parse(currentWeather.getString("sunset").toString()))
            text_precipitation?.text = currentWeather.getString("precipprob") + "%"
            text_humidity?.text = currentWeather.getString("humidity") + "%"

            text_pressure?.text = "" + currentWeather.getString("pressure") + "HPA"
            text_show_aqi?.text = "" + daysWeatherToday.getString("aqius")
            textWeatherStatusMain?.text = "" + currentWeather.getString("conditions")
            try {
                val imageName = currentWeather.getString("icon")
                Picasso.get()
                    .load("https://traversedata.nyc3.cdn.digitaloceanspaces.com/images/icons/weather/$imageName.png")
                    .into(image_main)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val new = currentWeather.getString("aqius").split(".").toTypedArray()[0]
            val airType = new.toInt()
            text_show_aqi?.text = "($new)"
            when (airType) {
                in 1..50 -> {
                    text_air_type?.text = "Good"
                }

                in 51..100 -> {
                    text_air_type?.text = "Satisfactory"
                }

                in 101..200 -> {
                    text_air_type?.text = "Moderate"
                }

                in 201..300 -> {
                    text_air_type?.text = "Poor"
                }

                in 301..400 -> {
                    text_air_type?.text = "Very Poor"
                }

                in 401..500 -> {
                    text_air_type?.text = "Severe"
                }
            }

            layout.behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        layout.dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (BottomSheetBehavior.STATE_HALF_EXPANDED == 1) {
                        BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            })

            val wmlp: WindowManager.LayoutParams = layout.window!!.attributes
            wmlp.flags = wmlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            layout.setCancelable(false)
            layout.setCanceledOnTouchOutside(false)
            layout.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    class DaysAdapter(
        private val activity: Activity?,
        mItemList: ArrayList<DaysItemObject>?,
        isDgreeOrFarenit: Boolean
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
        var mItemList = mItemList
        var isDgreeOrFarenit = isDgreeOrFarenit
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_weather_days, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            if (viewHolder is ItemViewHolder) {
                populateItemRows(viewHolder, position, isDgreeOrFarenit)
            }
        }

        override fun getItemCount(): Int {
            return mItemList!!.size
        }

        @SuppressLint("NonConstantResourceId", "SetTextI18n", "InflateParams")
        override fun onClick(v: View) {
            when (v.id) {
//                R.id.btn_accept -> {
//
//                }
            }
        }

        private inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var text_day: TextView
            var second_dgree_celcus: TextView
            var first_dgree_celcus: TextView
            var img_type: ImageView
            var text_temp_max: TextView
            var text_temp_min: TextView
            var textWeatherCondition: TextView

            init {
                text_day = view.findViewById(R.id.text_day)
                second_dgree_celcus = view.findViewById(R.id.second_dgree_celcus)
                first_dgree_celcus = view.findViewById(R.id.first_dgree_celcus)
                img_type = view.findViewById(R.id.img_type)
                text_temp_max = view.findViewById(R.id.text_temp_max)
                text_temp_min = view.findViewById(R.id.text_temp_min)
                textWeatherCondition = view.findViewById(R.id.textWeatherCondition)
            }
        }

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        private fun populateItemRows(
            viewHolder: ItemViewHolder,
            position: Int,
            isDgreeOrFarenit: Boolean
        ) {
            val activeIncidentItemObject = mItemList!![position]
//            viewHolder.btn_accept.setOnClickListener(this)
            with(viewHolder) {
                val dateFormat_yyyyMMdd = SimpleDateFormat(
                    "yyyy-MM-dd", Locale.ENGLISH
                )
                val date =
                    dateFormat_yyyyMMdd.parse(activeIncidentItemObject.getDateTime.toString())
                val dateFormated = SimpleDateFormat("dd/MM").format(date)
                text_day.text = dateFormated.toString()
                textWeatherCondition.text = activeIncidentItemObject.getCondition.toString()
                if (ProjectUtil.getIsTempInFahrenheit(activity)) {
                    text_temp_max.text = ProjectUtil.changeTempInFrehFromCelsius(
                        activeIncidentItemObject.getTempMex.toString().toDouble()
                    )
                    text_temp_min.text = ProjectUtil.changeTempInFrehFromCelsius(
                        activeIncidentItemObject.getTempMin.toString().toDouble()
                    )
                    second_dgree_celcus.text = activity?.getString(R.string.f_label)
                    first_dgree_celcus.text = activity?.getString(R.string.f_label)
                } else {
                    text_temp_max.text = activeIncidentItemObject.getTempMex.toString()
                    text_temp_min.text = activeIncidentItemObject.getTempMin.toString()
                    second_dgree_celcus.text = activity?.getString(R.string.celcius_label)
                    first_dgree_celcus.text = activity?.getString(R.string.celcius_label)
                }

                try {
                    val imageName = activeIncidentItemObject.getIcon.toString()
                    Picasso.get()
                        .load("https://traversedata.nyc3.cdn.digitaloceanspaces.com/images/icons/weather/$imageName.png")
                        .into(img_type)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }
        }

        init {
            this.mItemList = mItemList
        }

    }

    fun isPlayServicesAvailable() {
        val resultCode = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(this@MainActivity)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                val s =
                    GoogleApiAvailability.getInstance().isUserResolvableError(resultCode).toString()
                Toast.makeText(this@MainActivity, s, Toast.LENGTH_SHORT).show()
            } else {
                //This device is not supported
                finish() // Kill the activity if you want.
            }
        } else {
            //Google Play Services is available.
        }
    }
}