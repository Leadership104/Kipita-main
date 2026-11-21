package com.mytum;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class PlannerWebViewActivity extends AppCompatActivity {
    private static final String TAG = "ImageViewerActivity";
    ImageView backarrow;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        View yourView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.green_bottom));
            if (yourView != null) {
                yourView.setSystemUiVisibility(0);
            }
        }
        setContentView(R.layout.fragment_planner_webview);
        Log.i(TAG, "onCreate: ");

        String path = Objects.requireNonNull(getIntent().getExtras()).getString("URL_PATH");
        String title = getIntent().getExtras().getString("URL_TITLE");
//        if (path == null) {
//            finish();
//        }
//        imageView = findViewById(R.id.viewer_imageview);
        backarrow = findViewById(R.id.backarrow);
        backarrow.setOnClickListener(v -> finish());
        TextView textView_title = findViewById(R.id.textView_title);
        textView_title.setText(title);
//        if (path.equalsIgnoreCase(PlannerFragment.FLIGHT_PATH)) {
//            planner_title.setText("Book Flights");
//        } else if (path.equalsIgnoreCase(PlannerFragment.HOTEL_PATH)) {
//            planner_title.setText("Book Hotels");
//        } else if (path.equalsIgnoreCase(PlannerFragment.CAR_PATH)) {
//            planner_title.setText("Car Rental");
//        } else if (path.equalsIgnoreCase(PlannerFragment.TRANSFER_PATH)) {
//            planner_title.setText("Transfer");
//        } else if (path.equalsIgnoreCase(PlannerFragment.BIKE_PATH)) {
//            planner_title.setText("Bike Rental");
//        }
        Log.d("dadasd url = ", path);
        WebView webViewPlanner = findViewById(R.id.webViewPlanner);
        WebSettings webSettings = webViewPlanner.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webViewPlanner.setWebChromeClient(new WebChromeClient());
        webViewPlanner.setWebViewClient(new WebViewClient());
        webViewPlanner.loadUrl(path);
    }


}