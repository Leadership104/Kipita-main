package com.mytum.proectUtil

import android.app.Activity
import android.app.Dialog
import com.google.android.gms.tasks.OnCompleteListener
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
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
import com.mytum.MyApplication
import com.mytum.R
import java.util.*

class ProjectUtilDilog {

    companion object{


     public  fun showDialog11(context: Activity?) {
         var predictionList: List<AutocompletePrediction>? = null
         var mCountry = ""
         var placesClient: PlacesClient? = null
         val token = AutocompleteSessionToken.newInstance()


        var dialog = Dialog(context!!)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog)

         var materialSearchBar = dialog.findViewById(R.id.searchBar) as MaterialSearchBar

         Places.initialize(context, context.getString(R.string.google_api_key_ishaan))
         placesClient = Places.createClient(context)
//         googleApiClient = getAPIClientInstance()
//         if (googleApiClient != null) {
//             googleApiClient!!.connect()
//         }
//


         materialSearchBar!!.setOnSearchActionListener(object :
             MaterialSearchBar.OnSearchActionListener {
             override fun onSearchStateChanged(enabled: Boolean) {}
             override fun onSearchConfirmed(text: CharSequence) {
                 context.startSearch(text.toString(), true, null, true)
             }

             override fun onButtonClicked(buttonCode: Int) {

                 ProjectUtil.customToast(
                     context,
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
                 var imm = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                 imm?.hideSoftInputFromWindow(
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
                             ProjectUtil.setLongitude(
                                 MyApplication.getInstance().applicationContext,
                                 "" + latLng.longitude)
                             ProjectUtil.setLotitude(
                                 MyApplication.getInstance().applicationContext,
                                 "" + latLng.latitude)
                         }

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


         dialog.show()
    }


    }

}