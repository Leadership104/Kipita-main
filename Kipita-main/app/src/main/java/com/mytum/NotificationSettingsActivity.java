package com.mytum;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class NotificationSettingsActivity extends AppCompatActivity {


//    @BindView(R.id.backbtn)
    ImageView backbtn;
//    @BindView(R.id.switch_notif_pref)
    Switch switcNotifPref;
//    @BindView(R.id.switch_notif_email)
    Switch switchNotifEmail;
//    @BindView(R.id.switch_notif_lvl1)
    Switch switchNotifLvl1;
//    @BindView(R.id.switch_notif_lvl2)
    Switch switchNotifLvl2;
//    @BindView(R.id.switch_notif_lvl3)
    Switch switchNotifLvl3;
//    @BindView(R.id.switch_notif_lvl4)
    Switch switchNotifLvl4;
//    @BindView(R.id.switch_notif_lvl5)
    Switch switchNotifLvl5;
//    @BindView(R.id.submit)
    Button submit;

    Dialog dialog = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_notif_settings);
//        ButterKnife.bind((Activity) this);

        /// Tip: Change status bar colour - Umesh
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        /// Tip: Add notification code to activate notifications - Umesh
        dialog = new Dialog(this);
        backbtn = findViewById(R.id.backbtn);
        submit = findViewById(R.id.submit);
        backbtn.setOnClickListener(v -> finish());

        submit.setOnClickListener(v -> {
            // add code for save on click
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
                new Callback<>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<RegistrationResponse> call,
                            @NotNull Response<RegistrationResponse> response) {
                        RegistrationResponse getAllData = response.body();

                        if (getAllData == null) {
                            ProjectUtil.customToast(NotificationSettingsActivity.this, "Internal Server error");
                            return;
                        }
                        int responseCode = response.code();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    ProjectUtil.customToast(NotificationSettingsActivity.this, getAllData.getMessage());
                                    ProjectUtil.dismissDialog(dialog);
                                    finish();
                                } else {
                                    ProjectUtil.customToast(NotificationSettingsActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<RegistrationResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(NotificationSettingsActivity.this, t.toString());
                        ProjectUtil.dismissDialog(dialog);

                    }
                });
    }

}
