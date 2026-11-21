package com.mytum.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mytum.fragment.GallerySubFragment.PrivateFragment;
import com.mytum.fragment.GallerySubFragment.PublicFragment;
import com.mytum.LoginActivity;
import com.mytum.MainActivity;
import com.mytum.proectUtil.ProjectUtil;
import com.mytum.R;


public class GalleryFragment extends Fragment {
    Fragment active = null;
    Button myPhoto;
    Button galaxy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, null);
        ((MainActivity) getActivity()).showMainMenu();

        galaxy = view.findViewById(R.id.btn1);
        myPhoto = view.findViewById(R.id.btn2);
        loadFragmentGallery(new PublicFragment());
        galaxy.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_btn_blue));
        myPhoto.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_btn_grey));
        galaxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragmentGallery(new PublicFragment());
                galaxy.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_btn_blue));
                myPhoto.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_btn_grey));

            }
        });


        myPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getActivity().getSupportFragmentManager().beginTransaction().hide(active).show(privateFragment).commit();
//                active = privateFragment;
                if (ProjectUtil.getSession(getActivity()).equalsIgnoreCase("1")) {
                    loadFragmentGallery(new PrivateFragment());
                    galaxy.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_btn_grey));
                    myPhoto.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_btn_blue));

                } else {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    getActivity().finish();
                }

            }
        });


        return view;

    }

    private boolean loadFragmentGallery(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_gallery, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}