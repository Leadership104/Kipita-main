package com.mytum

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mytum.fragment.AdviserySubFragment.ApplicationsMainFragment
import com.mytum.fragment.PlanerMainFragment

class PlannerWebViewFrgament : Fragment() {
    var imageView: ImageView? = null
    var backarrow: ImageView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        var view = inflater.inflate(R.layout.fragment_planner_webview, container, false)

        Log.i(TAG, "onCreate: ")
        var path =
            requireArguments().getString("list")

        (activity as MainActivity).showSubMenu()
        var textView = (activity as MainActivity?)!!.findViewById<TextView>(R.id.titelSub)
        var backButton = (activity as MainActivity?)!!.findViewById<ImageView>(R.id.backarrow)
        textView.text = "Back to Travel"
        backButton.setOnClickListener(View.OnClickListener {
            loadFragmentInner(PlanerMainFragment())
        })

//        //        imageView = findViewById(R.id.viewer_imageview);
//        backarrow = view.findViewById(R.id.backarrow)
//        val planner_title = view.findViewById<TextView>(R.id.planner_title)
//        if (path.equals(PlannerFragment.FLIGHT_PATH, ignoreCase = true)) {
//            planner_title.text = "Book Flights"
//        } else if (path.equals(PlannerFragment.HOTEL_PATH, ignoreCase = true)) {
//            planner_title.text = "Book Hotels"
//        } else if (path.equals(PlannerFragment.CAR_PATH, ignoreCase = true)) {
//            planner_title.text = "Car Rental"
//        } else if (path.equals(PlannerFragment.TRANSFER_PATH, ignoreCase = true)) {
//            planner_title.text = "Transfer"
//        } else if (path.equals(PlannerFragment.BIKE_PATH, ignoreCase = true)) {
//            planner_title.text = "Bike Rental"
//        }
        var webViewPlanner = view.findViewById<WebView>(R.id.webViewPlanner)
        var webSettings = webViewPlanner.settings
        webSettings.javaScriptEnabled = true
        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
        webSettings.defaultTextEncodingName = "utf-8"
        webViewPlanner.webChromeClient = WebChromeClient()
        webViewPlanner.webViewClient = WebViewClient()
        webViewPlanner.loadUrl(path!!)
        return view;
    }

    companion object {
        private const val TAG = "WebVIewFragment"
    }

    fun loadFragmentInner(fragment: Fragment?) {
        if (fragment != null) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

}