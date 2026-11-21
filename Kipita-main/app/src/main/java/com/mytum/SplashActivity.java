package com.mytum;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.mytum.proectUtil.ProjectUtil;

public class SplashActivity extends AppCompatActivity {

    Bundle data = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Window window = getWindow();
        View yourView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (yourView != null) {
                yourView.setSystemUiVisibility(0);
            }
        }

        Intent intent = getIntent();
        data = intent.getExtras();
        try {
            Log.d("aaaaaaaaa noti data", intent.getStringExtra("data"));
        }catch(Exception e){
            e.printStackTrace();
        }

//        if(data != null) {
////            for (int i=0; i < data.keySet().size(); i++) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    data.keySet().forEach(key -> {
//                        Log.d("aaaaaaaaaaa11 notification data = ", key);
//                        try {
//                            Log.d("aaaaaaaaaaa22 notification data = ", intent.getExtras().getString(key));
//                        }catch(Exception e){
//                            e.printStackTrace();
//                        }
//
//                        try {
//                            Log.d("aaaaaaaaaaa221 notification data = ", ""+intent.getExtras().getLong(key));
//                        }catch(Exception e){
//                            e.printStackTrace();
//                        }
//                            });
////                    Log.d("aaaaaaaaaaaaaa11 notification data = ", data.keySet().toArray()[i].toString());
//                }
//
////            }
//            }

        new Handler().postDelayed(new Runnable() {
            public void run() {

                if(ProjectUtil.getSession(SplashActivity.this).equalsIgnoreCase("1")){
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    if(data != null) {
                        intent.putExtras(data);
                    }
                    startActivity(intent);
                }else{
                    startActivity(new Intent(SplashActivity.this, PreLoginActivity.class));
                }
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();


            }
        }, 1500);
    }
}
