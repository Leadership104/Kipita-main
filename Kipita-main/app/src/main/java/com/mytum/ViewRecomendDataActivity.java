package com.mytum;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mytum.adapter.CategryAdapter;
import com.mytum.adapter.ReviewAdapter;
import com.mytum.client.connection.YelpFusionApi;
import com.mytum.client.connection.YelpFusionApiFactory;
import com.mytum.client.models.Business;
import com.mytum.client.models.Category;
import com.mytum.client.models.Review;
import com.mytum.client.models.Reviews;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRecomendDataActivity extends AppCompatActivity {
    private ArrayList<Review> reviewList = new ArrayList<Review>();
    private ArrayList<Business> petStatusList = new ArrayList<Business>();
    private ArrayList<Category> catList = new ArrayList<Category>();
    private TextView name;
    private RecyclerView recyclerView;
    private MaterialRatingBar rating;
    private ImageView allreview;
    private ImageView call;
    private TextView review;
    private CardView parentLayout;
    private TextView openOrCloserd;
    private TextView phoneno;
    private TextView address;
    private LinearLayoutManager mLayoutManager;
    private ImageView mainImage;
    private Business listResponse;
    private CategryAdapter categryAdapter;

    ImageView backarrow;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        View yourView = window.getDecorView();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.blue));
            if (yourView != null) {
                yourView.setSystemUiVisibility(0);
            }
        }
        setContentView(R.layout.fragment_recommend_status);
//        ProjectUtil.hideKeyboard(getActivity());
//        Toolbar toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
//        TextView textView = ((MainActivity) getActivity()).findViewById(R.id.titel);
//        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);
//        textView.setText("Pet Details");
//        actionBar.setDisplayHomeAsUpEnabled(true);

        rating = findViewById(R.id.rating);
        call = findViewById(R.id.call);
        allreview = findViewById(R.id.allreview);
        phoneno = findViewById(R.id.phoneno);
        openOrCloserd = findViewById(R.id.openOrCloserd);
        recyclerView = findViewById(R.id.recyclerView_reg);
        review = findViewById(R.id.review);
        name = findViewById(R.id.name);
        parentLayout = findViewById(R.id.parentLayout);
        mainImage = findViewById(R.id.mainImage);


        String path = getIntent().getExtras().getString("position");
        if (path != null) {
            Gson gson = new Gson();
            listResponse = gson.fromJson(path, Business.class);
        }

        backarrow = findViewById(R.id.backarrow);
        address = findViewById(R.id.address);
        name.setText(listResponse.getName());
        phoneno.setText(listResponse.getPhone());
        rating.setRating(Float.parseFloat("" + listResponse.getRating()));
        address.setText("" + listResponse.getLocation().getAddress1() + ", " + listResponse.getLocation().getCity() + ", "
                + listResponse.getLocation().getState() + ", " + listResponse.getLocation().getZipCode());
        review.setText("  (" + listResponse.getReviewCount() + " reviews)");
        Picasso.get().load(listResponse.getImageUrl()).into(mainImage);
        if (listResponse.getIsClosed()) {
            openOrCloserd.setText("Closed Now");
            parentLayout.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red));
        } else {
            openOrCloserd.setText("Open Now");
            parentLayout.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }

        catList.addAll(listResponse.getCategories());
        categryAdapter = new CategryAdapter(this, catList);
        recyclerView.setAdapter(categryAdapter);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        categryAdapter.notifyDataSetChanged();

        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(ViewRecomendDataActivity.this, ImageViewerActivity.class);
                result.putExtra(ImageViewerActivity.EXTRA_PATH, listResponse.getImageUrl());
                startActivity(result);
            }
        });

        allreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readReview();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_CALL);
//                intent.setData(Uri.parse("tel:" + listResponse.getPhone()));
//                startActivity(intent);
            }
        });

        allreview.setVisibility(View.INVISIBLE);
        getAllUserReview(listResponse.getId());
        backarrow.setOnClickListener(v -> finish());
    }


    protected void getAllUserReview(String Id) {
        try {
            YelpFusionApi apiService = YelpFusionApiFactory.createAPI("uWwWVaVVFSyTvOv0SMn9L03TfDNo_Zad1UKQXlFyx3ThsQYhc6C97VOIBKjks0q3UCpNGao5rdTg988pZZdvRxReZczmiiLLzIsFqBVZFt9PzyRryIkEZ3lwo1ozY3Yx");
            Call<Reviews> getAllComplaint = apiService.getBusinessReviews(Id, "en_AU");
            getAllComplaint.enqueue(
                    new Callback<Reviews>() {
                        @Override
                        public void onResponse(
                                @NotNull Call<Reviews> call,
                                @NotNull Response<Reviews> response) {
                            Reviews getAllData = response.body();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    reviewList.clear();
                                    reviewList.addAll(getAllData.getReviews());
                                    allreview.setVisibility(View.VISIBLE);
                                    if (reviewList.size() <= 0) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("fragmentLayoutName", "");
                                        return;
                                    }
                                }
                            }, 2000);
                        }

                        @Override
                        public void onFailure(@NonNull Call<Reviews> call, @NonNull Throwable t) {
                            t.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    private void readReview() {
         ReviewAdapter reviewAdapter;
         LinearLayoutManager mLayoutManager;
        try {
            BottomSheetDialog popupWindow = new BottomSheetDialog(ViewRecomendDataActivity.this, R.style.chatOptionsBottomSheetDialog);
            popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupWindow.setContentView(R.layout.popup_with_edit_text);

            popupWindow.setCancelable(true);
            popupWindow.setCanceledOnTouchOutside(true);
            RecyclerView recyclerView = popupWindow.findViewById(R.id.recyclerView_reg);

            reviewAdapter = new ReviewAdapter(this, reviewList);
            recyclerView.setAdapter(reviewAdapter);
            mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            reviewAdapter.notifyDataSetChanged();
            popupWindow.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}