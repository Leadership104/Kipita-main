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

public class UpdateProfileActivity extends AppCompatActivity {


//    @BindView(R.id.backbtn)
    ImageView backbtn;

//    @BindView(R.id.umobile)
    EditText umobile;


//    @BindView(R.id.zipcode)
    EditText zipcode;

//    @BindView(R.id.firstName)
    EditText firstName;

//    @BindView(R.id.lastName)
    EditText lastName;

//    @BindView(R.id.mailId)
    EditText mailId;


    Dialog dialog = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprile);
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
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        umobile = findViewById(R.id.umobile);
        zipcode = findViewById(R.id.zipcode);
        mailId.setText(ProjectUtil.getMailId(this));
        firstName.setText(ProjectUtil.getFirstName(this));
        lastName.setText(ProjectUtil.getLastName(this));
        umobile.setText(ProjectUtil.getMobileNumber(this));
        zipcode.setText(ProjectUtil.getZiCode(this));;



        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (zipcode.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(UpdateProfileActivity.this, "Please Enter ZipCode");
                    zipcode.setError("Please Enter ZipCode");
                } else if (lastName.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(UpdateProfileActivity.this, "Please Enter Last Name");
                    lastName.setError("Please Enter Last Name");
                } else if (firstName.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(UpdateProfileActivity.this, "Please Enter First Name");
                    firstName.setError("Please Enter First Name");
                } else if (mailId.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(UpdateProfileActivity.this, "Please Enter Mail Id");
                    mailId.setError("Please Enter Mail Id");
                } else if (umobile.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(UpdateProfileActivity.this, "Please Enter Mobile Number");
                    umobile.setError("Please Enter Mobile Number");
                } else {
                    RegistratonUpdateRequest registratonRequest = new RegistratonUpdateRequest();

                    registratonRequest.setEmailId(mailId.getText().toString());
                    registratonRequest.setMobileNumber(umobile.getText().toString());
                    registratonRequest.setFirstName(firstName.getText().toString());
                    registratonRequest.setLastName(lastName.getText().toString());
                    registratonRequest.setTitle("");
                    registratonRequest.setUpdate_profile(true);
                    registratonRequest.setZipCode(zipcode.getText().toString());
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
                            ProjectUtil.customToast(UpdateProfileActivity.this, "Internal Server error");
                            return;
                        }
                        int responseCode = response.code();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    ProjectUtil.customToast(UpdateProfileActivity.this, getAllData.getMessage());
                                    ProjectUtil.dismissDialog(dialog);
                                    ProjectUtil.setFirstName(UpdateProfileActivity.this, getAllData.getData().getFirstName());
                                    ProjectUtil.setLasttName(UpdateProfileActivity.this, getAllData.getData().getLastName());
                                    ProjectUtil.setSirName(UpdateProfileActivity.this, getAllData.getData().getTitle());
                                    ProjectUtil.setMobileNumber(UpdateProfileActivity.this, getAllData.getData().getMobileNumber());
                                    ProjectUtil.setZipCode(UpdateProfileActivity.this, getAllData.getData().getZipCode());

                                    finish();
                                } else {
                                    ProjectUtil.customToast(UpdateProfileActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<RegistrationResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(UpdateProfileActivity.this, t.toString());
                        ProjectUtil.dismissDialog(dialog);

                    }
                });
    }

}
