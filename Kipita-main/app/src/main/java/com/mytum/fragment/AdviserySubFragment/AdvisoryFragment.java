package com.mytum.fragment.AdviserySubFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mytum.fragment.AdviserySubFragment.ApplicationsFragment;
import com.mytum.fragment.AdviserySubFragment.DestinationFragment;
import com.mytum.MainActivity;
import com.mytum.R;


public class AdvisoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_advisry, null);
        Toolbar toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        TextView textView = ((MainActivity) getActivity()).findViewById(R.id.titel);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
//        textView.setText("Advisory");

        Button btn1= (Button) view.findViewById(R.id.btn1);
        Button btn2= (Button) view.findViewById(R.id.btn2);
        TextView textView1= (TextView) view.findViewById(R.id.textView1);
        TextView textView2= (TextView) view.findViewById(R.id.textView2);

        loadFragment(new DestinationFragment());

        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new DestinationFragment());
                textView1.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ApplicationsFragment());
                textView2.setVisibility(View.VISIBLE);
                textView1.setVisibility(View.INVISIBLE);
            }
        });

        return view;

    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_advisory, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}