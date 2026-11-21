package com.mytum;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ImageViewerMoreInforActivity extends AppCompatActivity {
    private static final String TAG = "ImageViewerActivity";

    public static final String EXTRA_PATH = "path";
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    ImageView imageView;
    ImageView backarrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        View yourView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.red_bottom));
            if (yourView != null) {
                yourView.setSystemUiVisibility(0);
            }
        }
        setContentView(R.layout.activity_image_viewer_more_info);
        Log.i(TAG, "onCreate: ");

        String path = getIntent().getExtras().getString(EXTRA_PATH);
        if (path == null) {
            finish();
        }
        imageView = findViewById(R.id.viewer_imageview);
        backarrow = findViewById(R.id.backarrow);





        backarrow.setOnClickListener(v -> finish());

        Picasso.get().load(path).into(imageView);

    }


}