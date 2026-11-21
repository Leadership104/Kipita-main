package com.mytum.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mytum.MainActivity;
import com.mytum.PlannerWebViewActivity;
import com.mytum.R;


public class PlannerFragment extends Fragment {

    public static final String FLIGHT_PATH = " https://savetraveldeals.com/book-hotels-app/";
    public static final String HOTEL_PATH = " https://savetraveldeals.com/book-hotels-app/";
    public static final String CAR_PATH = "https://savetraveldeals.com/car-rental-app/";
    public static final String TRANSFER_PATH = "https://savetraveldeals.com/airport-transfers/";
    public static final String BIKE_PATH = "https://savetraveldeals.com/bike-rental-app/";
    public static final String URL_PATH = "URL_PATH";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view =  inflater.inflate(R.layout.fragment_planner, null);
        Toolbar toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        TextView textView = ((MainActivity) getActivity()).findViewById(R.id.titel);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
//        textView.setText("Planner");
        toolbar.setBackgroundColor(getResources().getColor(R.color.green_bottom));

//        WebView webViewPlanner = view.findViewById(R.id.webViewPlanner);
//        webViewPlanner.loadUrl("https://savetraveldeals.com");

        CardView rlFlights = view.findViewById(R.id.rlFlights);
        CardView rlHotels = view.findViewById(R.id.rlHotels);
        CardView rlCarRental = view.findViewById(R.id.rlCarRental);
        CardView rlTransfer = view.findViewById(R.id.rlTransfer);
        CardView rlBikeRental = view.findViewById(R.id.rlBikeRental);
        rlFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(getActivity(), PlannerWebViewActivity.class);
                result.putExtra(PlannerFragment.URL_PATH, PlannerFragment.FLIGHT_PATH);
                startActivity(result);
            }
        });
        rlHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(getActivity(), PlannerWebViewActivity.class);
                result.putExtra(PlannerFragment.URL_PATH, PlannerFragment.HOTEL_PATH);
                startActivity(result);
            }
        });
        rlCarRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(getActivity(), PlannerWebViewActivity.class);
                result.putExtra(PlannerFragment.URL_PATH, PlannerFragment.CAR_PATH);
                startActivity(result);
            }
        });
        rlTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(getActivity(), PlannerWebViewActivity.class);
                result.putExtra(PlannerFragment.URL_PATH, PlannerFragment.TRANSFER_PATH);
                startActivity(result);
            }
        });
        rlBikeRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(getActivity(), PlannerWebViewActivity.class);
                result.putExtra(PlannerFragment.URL_PATH, PlannerFragment.BIKE_PATH);
                startActivity(result);
            }
        });

        return view;

    }
}