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

import java.util.Objects;

public class ImageViewerActivity extends AppCompatActivity {

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
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.black));
        yourView.setSystemUiVisibility(0);
        setContentView(R.layout.activity_image_viewer);
        String path = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_PATH);
        if (path == null) {
            finish();
        }
        imageView = findViewById(R.id.viewer_imageview);
        backarrow = findViewById(R.id.backarrow);
        backarrow.setOnClickListener(v -> finish());
        Picasso.get().load(path).into(imageView);
    }
}