package com.mytum.fragment.AdviserySubFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.mytum.adapter.DestinationAdapter;
import com.mytum.proectUtil.ProjectUtil;
import com.mytum.R;
import com.mytum.client.connection.YelpFusionApi;
import com.mytum.client.connection.YelpFusionApiFactory;
import com.mytum.client.models.AdvisoryResponse;
import com.mytum.interfaces.ComplaintClickPressed;
import com.facebook.shimmer.ShimmerFrameLayout;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DestinationFragment extends Fragment implements ComplaintClickPressed {

    ArrayList<AdvisoryResponse> businesses  = new ArrayList<AdvisoryResponse>();
    Dialog customDialog;
    private LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout refreshLayout;
    ShimmerFrameLayout shimmerFramlayout;
    RecyclerView recyclerView;
    ImageView animation_view;
    DestinationAdapter registerPetAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.destination_fragment, container, false);
        refreshLayout  = view.findViewById(R.id.swipeToRefresh_reg);
        animation_view  = view.findViewById(R.id.animation_view);
        customDialog = new Dialog(getActivity());
        refreshLayout  = view.findViewById(R.id.swipeToRefresh_reg);
        recyclerView  = view.findViewById(R.id.recyclerView_reg);
        shimmerFramlayout  = view.findViewById(R.id.shimmerFramlayout);

        registerPetAdapter = new DestinationAdapter(getActivity(), businesses, DestinationFragment.this);
        recyclerView.setAdapter(registerPetAdapter);
        getAllDestinationData();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                shimmerFramlayout.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
                getAllDestinationData();
            }
        });


        return  view;
    }


    protected void getAllDestinationData() {
        animation_view.setVisibility(View.GONE);
        YelpFusionApi apiService = YelpFusionApiFactory.getClient().create(YelpFusionApi.class);
        Call<ArrayList<AdvisoryResponse>> apiServiceAllApprovePet = apiService.getAdvisoryResponse();
        apiServiceAllApprovePet.enqueue(
                new Callback<ArrayList<AdvisoryResponse>>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<ArrayList<AdvisoryResponse>> call,
                            @NotNull Response<ArrayList<AdvisoryResponse>> response) {
                        ArrayList<AdvisoryResponse> getAllData = response.body();
                        int responseCode = response.code();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    businesses.clear();
                                    businesses.addAll(getAllData);
                                    if (getAllData.size() <= 0) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("fragmentLayoutName", "");
                                        shimmerFramlayout.setVisibility(View.GONE);
                                        animation_view.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                        refreshLayout.setVisibility(View.VISIBLE);
                                        if (refreshLayout.isRefreshing()) {
                                            refreshLayout.setRefreshing(false);
                                        }
                                        return;
                                    }
                                    mLayoutManager = new LinearLayoutManager(getActivity());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    registerPetAdapter.notifyDataSetChanged();
                                    shimmerFramlayout.setVisibility(View.GONE);
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    if (refreshLayout.isRefreshing()) {
                                        refreshLayout.setRefreshing(false);
                                    }

                                }
                            }, 2000);

                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<AdvisoryResponse>> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        //progressDialog.dismiss();
                        ProjectUtil.customToast(getContext(), t + "");
                    }
                });
    }


    @Override
    public void onClickEvent(int postion) {

    }

    @Override
    public void onCallEvent(int postion) {

    }

    @Override
    public void onSiteUrl(int postion) {

    }

    @Override
    public void onImageClick(int postion) {

    }

    @Override
    public void onAddressClick(int postion) {

    }

    @Override
    public void onMoreInfo(int postion) {

    }
}