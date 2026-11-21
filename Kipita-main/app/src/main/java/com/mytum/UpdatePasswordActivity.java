package com.mytum;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mytum.model.Registration.RegistrationResponse;
import com.mytum.model.Registration.RegistratonUpdateRequest;
import com.mytum.network.APIService;
import com.mytum.network.RetrofitClient;
import com.mytum.proectUtil.ProjectUtil;

import org.jetbrains.annotations.NotNull;

//import butterknife.BindView;
//import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity {


//    @BindView(R.id.backbtn)
    ImageView backbtn;

//    @BindView(R.id.mailId)
    EditText mailId;

//    @BindView(R.id.password)
    EditText password;

//    @BindView(R.id.cpassword)
    EditText cpassword;

    Dialog dialog = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepassword);
//        ButterKnife.bind((Activity) this);
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
        dialog = new Dialog(this);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


        mailId = findViewById(R.id.mailId);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        mailId.setText(ProjectUtil.getMailId(this));


        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (password.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(UpdatePasswordActivity.this, "Please Enter Password");
                    password.setError("Please Enter Password");
                } else if (cpassword.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(UpdatePasswordActivity.this, "Please Enter confirm Password");
                    cpassword.setError("Please Enter confirm Password");
                } else if (!cpassword.getText().toString().equalsIgnoreCase(password.getText().toString())) {
                    ProjectUtil.customToast(UpdatePasswordActivity.this, "Password and Confirm password not match");
                    password.setError("Please Enter First Name");
                } else if (mailId.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(UpdatePasswordActivity.this, "Please Enter Mail Id");
                    mailId.setError("Please Enter Mail Id");
                } else {
                    RegistratonUpdateRequest registratonRequest = new RegistratonUpdateRequest();

                    registratonRequest.setEmailId(mailId.getText().toString());
                    registratonRequest.setUpdate_profile(true);
                    registratonRequest.setPassword(cpassword.getText().toString());
                    registerRequest(registratonRequest);
                }


            }
        });

    }



    protected void registerRequest(RegistratonUpdateRequest registratonRequest) {
        ProjectUtil.showDialog(
                dialog,
                "Please wait ..."
        );


        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<RegistrationResponse> getAllComplaint = apiService.update("application/json", registratonRequest);
        getAllComplaint.enqueue(
                new Callback<RegistrationResponse>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<RegistrationResponse> call,
                            @NotNull Response<RegistrationResponse> response) {
                        RegistrationResponse getAllData = response.body();

                        if (getAllData == null) {
                            ProjectUtil.dismissDialog(dialog);
                            ProjectUtil.customToast(UpdatePasswordActivity.this, "Internal Server error");
                            return;
                        }
                        int responseCode = response.code();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    ProjectUtil.customToast(UpdatePasswordActivity.this, getAllData.getMessage());
                                    ProjectUtil.dismissDialog(dialog);
                                    finish();
                                } else {
                                    ProjectUtil.customToast(UpdatePasswordActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<RegistrationResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(UpdatePasswordActivity.this, t.toString());
                        ProjectUtil.dismissDialog(dialog);

                    }
                });
    }

}
