package com.mytum.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.mytum.MainActivity
import com.mytum.MyApplication
import com.mytum.proectUtil.LocationPermissions
import com.mytum.proectUtil.ProjectUtil
import com.mytum.R
import java.util.*

class LoactionFragment : AppCompatActivity() {

    private var currentLocationBtn: Button? = null
    private var locationCancellation: CancellationTokenSource? = null
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (LocationPermissions.isGranted(this)) fetchCurrentLocation()
    }
    private var materialSearchBar: MaterialSearchBar? = null
    private val mCountry = ""
    private var placesClient: PlacesClient? = null
    private var predictionList: List<AutocompletePrediction>? = null
//    var locationName: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val window = window
        val yourView = window.decorView
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = resources.getColor(R.color.blue)
            if (yourView != null) {
                yourView.systemUiVisibility = 0
            }
        }

        setContentView(R.layout.fragment_location)

//        var view = inflater.inflate(R.layout.fragment_location, null)

//        var search = (this as MainActivity?)!!.findViewById<LinearLayout>(R.id.search)
//        var closeSeacrch = (this as MainActivity?)!!.findViewById<LinearLayout>(R.id.closesearch)

        currentLocationBtn = findViewById<Button>(R.id.currentLocationBtn)
        materialSearchBar = findViewById<MaterialSearchBar>(R.id.searchBar)
        Places.initialize(this, getString(R.string.google_api_key_ishaan))
        placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
//        locationName = (this as MainActivity?)!!.findViewById<TextView>(R.id.placeName)
        materialSearchBar!!.setOnSearchActionListener(object :
            MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence) {
                startSearch(text.toString(), true, null, true)
            }

            override fun onButtonClicked(buttonCode: Int) {
                ProjectUtil.customToast(
                    this@LoactionFragment,
                    "1111"
                )
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar!!.disableSearch()
                    materialSearchBar!!.clearSuggestions()
                }
            }

        })




        materialSearchBar!!.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var predictionsRequest: FindAutocompletePredictionsRequest =
                    FindAutocompletePredictionsRequest.builder()
                        .setCountry(mCountry)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build()
                placesClient!!.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                var predictionsResponse: FindAutocompletePredictionsResponse? =
                                    task.result
                                if (predictionsResponse != null) {
                                    predictionList =
                                        predictionsResponse.getAutocompletePredictions()
                                    var suggestionsList: MutableList<String?> = ArrayList()
                                    for (i in predictionList!!.indices) {
                                        var prediction: AutocompletePrediction =
                                            predictionList!!.get(i)
                                        suggestionsList.add(prediction.getFullText(null).toString())
                                    }
                                    materialSearchBar!!.updateLastSuggestions(suggestionsList)
                                    Handler().postDelayed({

                                        if (!materialSearchBar!!.isSuggestionsVisible()) {
                                            materialSearchBar!!.showSuggestionsList()
                                        }
                                    }, 1000)
                                }
                            } else {
                                Log.i(
                                    "Tag",
                                    "prediction fetching task unSuccessful"
                                )
                            }
                        })
            }

            override fun afterTextChanged(s: Editable) {}
        })

        materialSearchBar!!.setSuggestionsClickListener(object :
            SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemClickListener(position: Int, v: View) {
                if (position >= predictionList!!.size) {
                    return
                }
                var selectedPrediction = predictionList!![position]
                var suggestion = materialSearchBar!!.lastSuggestions[position].toString()
                materialSearchBar!!.text = suggestion
                Handler().postDelayed({ materialSearchBar!!.clearSuggestions() }, 1000)
                var imm =
                    getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    materialSearchBar!!.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
                var placeId = selectedPrediction.placeId
                var placeFields =
                    Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
                var fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient!!.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener { fetchPlaceResponse ->
                        var place = fetchPlaceResponse.place
                        Log.i(
                            "TAG1121212",
                            "place found " + place.name + place.address
                        )
                        var latLng = place.latLng
                        if (latLng != null) {
                            MainActivity.shouldUseCurrentLocation = false
                            ProjectUtil.setLongitude(
                                MyApplication.getInstance().applicationContext,
                                "" + latLng.longitude
                            )
                            ProjectUtil.setLotitude(
                                MyApplication.getInstance().applicationContext,
                                "" + latLng.latitude
                            )
//                            var address = ProjectUtil.getAddress(
//                                ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext),
//                                ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext)
//                            )
//                            locationName?.text = address
                        }

                        setResult(Activity.RESULT_OK)
                        finish()

//                        var fm: FragmentManager = this!!.getSupportFragmentManager()
//                        fm.popBackStack()
//                        search!!.setVisibility(View.VISIBLE)
//                        closeSeacrch!!.setVisibility(View.GONE)
                    }
                    .addOnFailureListener { e ->
                        if (e is ApiException) {
                            var apiException = e as ApiException
                            apiException.printStackTrace()
                            var statusCode = apiException.statusCode
                            Log.i(
                                "TAG1112",
                                "place not found" + e.message
                            )
                            Log.i(
                                "TAG11",
                                "status code : $statusCode"
                            )
                        }
                    }
            }

            override fun OnItemDeleteListener(position: Int, v: View) {}
        })

        currentLocationBtn?.setOnClickListener {
            if (LocationPermissions.isGranted(this)) {
                fetchCurrentLocation()
            } else {
                locationPermissionRequest.launch(LocationPermissions.foregroundPermissions())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        val cancellation = CancellationTokenSource()
        locationCancellation = cancellation
        currentLocationBtn?.isEnabled = false
        LocationServices.getFusedLocationProviderClient(this)
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellation.token)
            .addOnCompleteListener(this) { result ->
                locationCancellation = null
                currentLocationBtn?.isEnabled = true
                val location = if (result.isSuccessful) result.result else null
                if (location == null) {
                    Toast.makeText(this, R.string.current_location_unavailable, Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                MainActivity.shouldUseCurrentLocation = true
                ProjectUtil.setLotitude(this, location.latitude.toString())
                ProjectUtil.setLongitude(this, location.longitude.toString())
                ProjectUtil.setCurrentLotitude(this, location.latitude.toString())
                ProjectUtil.setCurrentLongitude(this, location.longitude.toString())
                setResult(Activity.RESULT_OK)
                finish()
            }
    }

    override fun onStop() {
        locationCancellation?.cancel()
        locationCancellation = null
        currentLocationBtn?.isEnabled = true
        super.onStop()
    }
}
