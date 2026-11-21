package com.mytum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mytum.fragment.MembersManageFragment;
import com.mytum.model.ForgetPassword.ForgetPasswordRequest;
import com.mytum.model.ForgetPassword.ForgetResponse;
import com.mytum.network.APIService;
import com.mytum.network.RetrofitClient;
import com.mytum.proectUtil.ProjectUtil;

import org.jetbrains.annotations.NotNull;

//import butterknife.BindView;
//import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

//    @BindView(R.id.backbtn)
    ImageView backbtn;

//    @BindView(R.id.ufname)
    EditText ufname;

    Dialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
//        ButterKnife.bind((Activity) this);
        Window window = getWindow();
        View yourView = window.getDecorView();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (yourView != null) {
                yourView.setSystemUiVisibility(0);
            }
        }

        customDialog = new Dialog(this);

        ufname = findViewById(R.id.ufname);

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });

        Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ufname.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(ForgetPasswordActivity.this, "Please Enter Email Id");
                    ufname.setError("Please Enter EmailId");
                }  else {

                    loginRequest(ufname.getText().toString());
                }
            }
        });

    }

    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }


    protected void loginRequest(String useranme) {
        ProjectUtil.showDialog(
                customDialog,
                "Please wait ..."
        );
        ForgetPasswordRequest loginRequest = new ForgetPasswordRequest();
        loginRequest.setEmailId(useranme);

        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<ForgetResponse> getAllComplaint = apiService.forgotPassword("application/json", loginRequest);
        getAllComplaint.enqueue(
                new Callback<ForgetResponse>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<ForgetResponse> call,
                            @NotNull Response<ForgetResponse> response) {
                        ForgetResponse getAllData = response.body();
                        if(getAllData == null){
                            ProjectUtil.dismissDialog(customDialog);
                            ProjectUtil.customToast(ForgetPasswordActivity.this, "Internal Server error");
                            return;
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    ProjectUtil.dismissDialog(customDialog);
                                    ProjectUtil.customToast(ForgetPasswordActivity.this, getAllData.getMessage());
                                     startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                                    finish();
                                } else {
                                    ProjectUtil.dismissDialog(customDialog);
                                    ProjectUtil.customToast(ForgetPasswordActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ForgetResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(ForgetPasswordActivity.this, t.toString());
                        ProjectUtil.dismissDialog(customDialog);

                    }
                });
    }


}