package com.mytum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.CallbackManager;
import com.mytum.model.Login.LoginRequest;
import com.mytum.model.Login.LoginResponse;
import com.mytum.model.Registration.RegistrationResponse;
import com.mytum.model.Registration.RegistratonRequest;
import com.mytum.network.APIService;
import com.mytum.network.RetrofitClient;
import com.mytum.proectUtil.LocationPermissions;
import com.mytum.proectUtil.ProjectUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreLoginActivity extends AppCompatActivity implements AuthenticationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_MEDIA_LOCATION = 999;
    Dialog customDialog;
    private static final int RC_SIGN_IN = 07;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "public_profile";
    private static final String AUTH_TYPE = "rerequest";
    private CallbackManager mCallbackManager;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    LinearLayout createaccount, porgetpassword;
    private String token = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);
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

        customDialog = new Dialog(this);
        printHashKey();
        checkLocationPermission();
//        checkMediaLocationPermission();
    }

    //


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.d("aaaaaa", "");
            checkLocationPermission();
        }
    }

    public void printHashKey() {

        // Add code to print out the key hash
        try {

            PackageInfo info
                    = getPackageManager().getPackageInfo(
                    this.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md
                        = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:qqq",
                        Base64.encodeToString(
                                md.digest(),
                                Base64.DEFAULT));

                Log.d("KeyHash:", "eeee");
            }
        } catch (PackageManager.NameNotFoundException e) {


            Log.d("KeyHash:", "eeeeee" + e);
        } catch (NoSuchAlgorithmException e) {

            Log.d("KeyHash:", "rrrrr" + e);
        }
    }

    public boolean checkLocationPermission() {
        if (LocationPermissions.isGranted(this)) {
            openMainActivity();
            return true;
        }
        ActivityCompat.requestPermissions(this, LocationPermissions.foregroundPermissions(),
                MY_PERMISSIONS_REQUEST_LOCATION);
        return false;
    }

    private void openMainActivity() {
        if (isFinishing()) return;
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    public boolean checkMediaLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_MEDIA_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_MEDIA_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Media Location")
                        .setMessage("Kapita would like to access your media location for fetching the media information based on your selected media.")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(PreLoginActivity.this,
                                        new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION},
                                        MY_PERMISSIONS_REQUEST_MEDIA_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION},
                        MY_PERMISSIONS_REQUEST_MEDIA_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (LocationPermissions.isGranted(this)) {
                    openMainActivity();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    new AlertDialog.Builder(this)
                            .setTitle("Location Permission")
                            .setMessage("In order to use Kapita user must provide the location permission for this app. Would you like to provide the permission.")
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown

                                    Intent intent = new Intent();
                                    intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, RC_SIGN_IN);
                                }
                            }).setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
//                                    ActivityCompat.requestPermissions(PreLoginActivity.this,
//                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                    finish();
                                }
                            })
                            .create()
                            .show();

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_MEDIA_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }


    protected void loginRequest(String useranme, String password) {
        ProjectUtil.showDialog(
                customDialog,
                "Please wait ..."
        );
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(password);
        loginRequest.setUsername(useranme);

        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<LoginResponse> getAllComplaint = apiService.login("application/json", loginRequest);
        getAllComplaint.enqueue(
                new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<LoginResponse> call,
                            @NotNull Response<LoginResponse> response) {
                        LoginResponse getAllData = response.body();
                        if (getAllData == null) {
                            ProjectUtil.customToast(PreLoginActivity.this, "Internal Server error");
                            return;
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    ProjectUtil.dismissDialog(customDialog);
                                    ProjectUtil.customToast(PreLoginActivity.this, getAllData.getMessage());
                                    ProjectUtil.setSession(PreLoginActivity.this, "1");

                                    ProjectUtil.setUserId(PreLoginActivity.this, getAllData.getData().getUserID());
                                    ProjectUtil.setFirstName(PreLoginActivity.this, getAllData.getData().getFirstName());
                                    ProjectUtil.setLasttName(PreLoginActivity.this, getAllData.getData().getLastName());
                                    ProjectUtil.setSirName(PreLoginActivity.this, getAllData.getData().getTitle());
                                    ProjectUtil.setMobileNumber(PreLoginActivity.this, getAllData.getData().getMobileNumber());
                                    ProjectUtil.setMailId(PreLoginActivity.this, getAllData.getData().getEmail());
                                    ProjectUtil.setZipCode(PreLoginActivity.this, getAllData.getData().getZipCode());
                                    startActivity(new Intent(PreLoginActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                                    finish();
                                } else {
                                    ProjectUtil.customToast(PreLoginActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(PreLoginActivity.this, t.toString());
                        ProjectUtil.dismissDialog(customDialog);

                    }
                });
    }

    protected void registerRequest(RegistratonRequest registratonRequest) {
        ProjectUtil.showDialog(
                customDialog,
                "Please wait ..."
        );


        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<RegistrationResponse> getAllComplaint = apiService.register("application/json", registratonRequest);
        getAllComplaint.enqueue(
                new Callback<RegistrationResponse>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<RegistrationResponse> call,
                            @NotNull Response<RegistrationResponse> response) {
                        RegistrationResponse getAllData = response.body();

                        if (getAllData == null) {
                            ProjectUtil.dismissDialog(customDialog);
                            ProjectUtil.customToast(PreLoginActivity.this, "Internal Server error");
                            return;
                        }
                        int responseCode = response.code();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    ProjectUtil.customToast(PreLoginActivity.this, getAllData.getMessage());
                                    ProjectUtil.dismissDialog(customDialog);
                                    ProjectUtil.setSession(PreLoginActivity.this, "1");
//                                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
//                                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//                                    finish();


                                    ProjectUtil.setUserId(PreLoginActivity.this, getAllData.getData().getUserID());
                                    ProjectUtil.setFirstName(PreLoginActivity.this, getAllData.getData().getFirstName());
                                    ProjectUtil.setLasttName(PreLoginActivity.this, getAllData.getData().getLastName());
                                    ProjectUtil.setSirName(PreLoginActivity.this, getAllData.getData().getTitle());
                                    ProjectUtil.setMobileNumber(PreLoginActivity.this, getAllData.getData().getMobileNumber());
                                    ProjectUtil.setMailId(PreLoginActivity.this, getAllData.getData().getEmail());
                                    ProjectUtil.setZipCode(PreLoginActivity.this, getAllData.getData().getZipCode());
                                    startActivity(new Intent(PreLoginActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                                    finish();

                                } else {
                                    ProjectUtil.dismissDialog(customDialog);
                                    ProjectUtil.customToast(PreLoginActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<RegistrationResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(PreLoginActivity.this, t.toString());
                        ProjectUtil.dismissDialog(customDialog);

                    }
                });
    }


    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null)
            return;
//        appPreferences.putString(AppPreferences.TOKEN, auth_token);
//        token = auth_token;
        getUserInfoByAccessToken(token);
    }

    private void getUserInfoByAccessToken(String token) {
        new RequestInstagramAPI().execute();
    }

    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + token);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if (jsonData.has("id")) {
                        //сохранение данных пользователя
//                        appPreferences.putString(AppPreferences.USER_ID, jsonData.getString("id"));
//                        appPreferences.putString(AppPreferences.USER_NAME, jsonData.getString("username"));
//                        appPreferences.putString(AppPreferences.PROFILE_PIC, jsonData.getString("profile_picture"));
//
////                        //TODO: сохранить еще данные
//                        login();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Ошибка входа!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
