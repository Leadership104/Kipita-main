package com.mytum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import butterknife.ButterKnife;
//import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.mytum.model.Login.LoginRequest;
import com.mytum.model.Login.LoginResponse;
import com.mytum.model.Registration.CodeRequest;
import com.mytum.model.Registration.OtpCodeResponse;
import com.mytum.model.Registration.OtpRequest;
import com.mytum.model.Registration.RegistrationResponse;
import com.mytum.model.Registration.RegistratonRequest;
import com.mytum.model.Registration.SocialLogin;
import com.mytum.model.Registration.SocialLoginResponse;
import com.mytum.network.APIService;
import com.mytum.network.RetrofitClient;
import com.mytum.proectUtil.GPSTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.mytum.proectUtil.ProjectUtil;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, AuthenticationListener {

    EditText et_mobile;
    EditText et_password;
    Button login;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    LoginButton mLoginButton;
    SignInButton googlebtn;
    Button btn_otp, btn_code;
    private CallbackManager callbackManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_MEDIA_LOCATION = 999;
    Dialog customDialog;
    private static final int RC_SIGN_IN = 07;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "public_profile";
    private static final String AUTH_TYPE = "rerequest";
    private CallbackManager mCallbackManager;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient googleApiClient;
    LinearLayout createaccount, porgetpassword;
    private final String token = null;
    private String deviceId = "";
    String cCode = "";
    int validPhone = 0;
    int validEmail = 0;
    Dialog dialog = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        ButterKnife.bind(this);
        Window window = getWindow();
        View yourView = window.getDecorView();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        if (yourView != null) {
            yourView.setSystemUiVisibility(0);
        }
        dialog = new Dialog(this);
        getToken();
        googleApiClient = getAPIClientInstance();
        googleApiClient.connect();
        GPSTracker mGPS = new GPSTracker(this);
        cCode = getCountryCode(mGPS.getLatitude(), mGPS.getLongitude());
        createaccount = findViewById(R.id.createaccount);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("id", "");
                intent.putExtra("name", "");
                intent.putExtra("email", "");
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });
        porgetpassword = findViewById(R.id.porgetpassword);
        porgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });
        customDialog = new Dialog(this);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requestGPSSettings();
        }

        printHashKey();
        callbackManager = CallbackManager.Factory.create();
        mLoginButton = findViewById(R.id.login_button);
        et_password = findViewById(R.id.et_password);
        et_mobile = findViewById(R.id.et_mobile);
        googlebtn = findViewById(R.id.googlebtn);
        login = findViewById(R.id.login);


        Button facbeloginDummy = findViewById(R.id.facbeloginDummy);
        Button googlebtnDummy = findViewById(R.id.googlebtnDummy);
        Button instagrambtnDummy = findViewById(R.id.instagrambtnDummy);

        instagrambtnDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationDialog authenticationDialog = new AuthenticationDialog(LoginActivity.this, LoginActivity.this);
                authenticationDialog.setCancelable(true);
                authenticationDialog.show();
            }
        });

        googlebtnDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    requestGPSSettings();
                    return;
                }
                signIn();
            }
        });

        facbeloginDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButton.performClick();

            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    requestGPSSettings();
                    return;
                }
                signIn();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    requestGPSSettings();
                    return;
                }

                GPSTracker mGPS = new GPSTracker(LoginActivity.this);
                if (mGPS.canGetLocation()) {
                    mGPS.getLocation();
                    ProjectUtil.setLongitude(LoginActivity.this, "" + mGPS.getLongitude());
                    ProjectUtil.setLotitude(LoginActivity.this, "" + mGPS.getLatitude());
                } else {
                    System.out.println("Unable");
                }

                if (et_mobile.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(LoginActivity.this, "Please Enter Email Id");
                    et_mobile.setError("Please Enter EmailId");
                } else if (et_mobile.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(LoginActivity.this, "Please Enter Password");
                    et_mobile.setError("Please Enter Password");
                } else {
                    loginRequest(et_mobile.getText().toString(), et_password.getText().toString());
                }
            }
        });

        mLoginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("Main", response.toString());
//                        setProfileToView(object);
//                        popupWindowEmailPhone("", object);
                        Log.v("object", object.toString());
                        SocialLogin socialLogin = new SocialLogin();
                        try {
                            socialLogin.setUsername(object.getString("name"));
                            socialLogin.setFirebasetoken(token);
                            socialLogin.setSocialLogin(true);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        socialLogin(socialLogin, object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "cancel_login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "error_login", Toast.LENGTH_SHORT).show();
            }
        });
        checkLocationPermission();
        checkMediaLocationPermission();
    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Fetching FCM registration token failed:-" + task.getException(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Get new FCM registration token
                        deviceId = task.getResult();
                        Log.d("Token", deviceId);
                    }
                });
    }

    //
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    @OnClick({R.id.login, R.id.createaccount, R.id.porgetpassword})
//    public void proceed(View view) {
//        switch (view.getId()) {
//            case R.id.login:
////                mLoginButton.performClick();
//                if (et_mobile.getText().toString().equalsIgnoreCase("")) {
//                    ProjectUtil.customToast(MyApplication.getInstance().getApplicationContext(), "Please Enter Mobile Number");
//                } else if (et_password.getText().toString().equalsIgnoreCase("")) {
//                    ProjectUtil.customToast(MyApplication.getInstance().getApplicationContext(), "Please Enter Password");
//                } else {
//                    loginRequest(et_mobile.getText().toString(), et_password.getText().toString());
//                }
//                break;
//            case R.id.createaccount:
//                startActivity(new Intent(this, RegisterActivity.class));
//                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//                finish();
//                break;
//            case R.id.porgetpassword:
//                startActivity(new Intent(this, ForgetPasswordActivity.class));
//                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//                finish();
//                break;
//
//        }
//    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//=================================FaceBook Code Start=========================================
        callbackManager.onActivityResult(requestCode, resultCode, data);
//=================================FaceBook Code End=========================================
        super.onActivityResult(requestCode, resultCode, data);
        //System.out.println("===============>FaceBook Ankit !");
//=================================Google Code Start=========================================
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
//=================================Google Code Start=========================================
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e);
            ProjectUtil.customToast(this, "" + e);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        RegistratonRequest registratonRequest = new RegistratonRequest();
        registratonRequest.setEmailId(account.getEmail());
        registratonRequest.setMobileNumber("");
        registratonRequest.setFirstName(account.getGivenName());
        registratonRequest.setLastName(account.getFamilyName());
        registratonRequest.setTitle("Mr.");
        registratonRequest.setPassword("");
        registratonRequest.setZipCode("");
        registratonRequest.setSocialLogin(true);
        registerRequest(registratonRequest);
//        ProjectUtil.setSession(LoginActivity.this, "1");
//        Intent i = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(i);
//        finish();
    }


    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:qqq", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", "eeeeee" + e);
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", "rrrrr" + e);
        }
    }

    private void setProfileToView(JSONObject jsonObject) {
        try {
            ProjectUtil.customToast(LoginActivity.this, "" + jsonObject);
//            Log.d("FACEBOOK" , "PROFILE"+jsonObject.toString());
//            Log.d("FACEBOOK" , "PROFILE"+jsonObject.getString("email"));
//            Log.d("FACEBOOK" , "PROFILE"+jsonObject.getString("name"));
//            Log.d("FACEBOOK" , "PROFILE"+jsonObject.getString("id"));
            //imageView.setImageBitmap(getFacebookProfilePicture(jsonObject.getString("id")));
            String firstName = "";
            String lastName = "";
            try {
                String[] name = jsonObject.getString("name").split(" ");
                firstName = name[0];
                lastName = name[1];
            } catch (Exception e) {
            }
            RegistratonRequest registratonRequest = new RegistratonRequest();
            registratonRequest.setEmailId(jsonObject.getString("email"));
            registratonRequest.setMobileNumber("");
            registratonRequest.setFirstName(firstName);
            registratonRequest.setLastName(lastName);
            registratonRequest.setTitle("Mr.");
            registratonRequest.setPassword("");
            registratonRequest.setZipCode("");
            registratonRequest.setSocialLogin(true);
            registerRequest(registratonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this).setTitle("title_location_permission").setMessage("title_location_permission").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }).create().show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean checkMediaLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_MEDIA_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this).setTitle("title_location_permission").setMessage("title_location_permission").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, MY_PERMISSIONS_REQUEST_MEDIA_LOCATION);
                    }
                }).create().show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, MY_PERMISSIONS_REQUEST_MEDIA_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        GPSTracker mGPS = new GPSTracker(this);
                        if (mGPS.canGetLocation()) {
                            mGPS.getLocation();
                            ProjectUtil.setLongitude(LoginActivity.this, "" + mGPS.getLongitude());
                            ProjectUtil.setLotitude(LoginActivity.this, "" + mGPS.getLatitude());
                        } else {
                            System.out.println("Unable");
                        }
                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_MEDIA_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//            locationManager.removeUpdates(this);
        }
    }

    private void requestGPSSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("", "All location settings are satisfied.");
//                        Toast.makeText(getApplication(), "GPS is already enable", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("", "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ");
                        try {
                            status.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("Applicationsett", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.");
                        Toast.makeText(getApplication(), "Location settings are inadequate, and cannot be fixed here", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private GoogleApiClient getAPIClientInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    protected void loginRequest(String useranme, String password) {
        ProjectUtil.showDialog(customDialog, "Please wait ...");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(password);
        loginRequest.setUsername(useranme);
        loginRequest.setFirebasetoken(deviceId);

        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<LoginResponse> getAllComplaint = apiService.login("application/json", loginRequest);
        getAllComplaint.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                LoginResponse getAllData = response.body();
                if (getAllData == null) {
                    ProjectUtil.customToast(LoginActivity.this, "Internal Server error");
                    return;
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        if (getAllData.getStatus().equalsIgnoreCase("success")) {
                            ProjectUtil.dismissDialog(customDialog);
                            ProjectUtil.customToast(LoginActivity.this, getAllData.getMessage());
                            ProjectUtil.setSession(LoginActivity.this, "1");

                            ProjectUtil.setUserId(LoginActivity.this, getAllData.getData().getUserID());
                            ProjectUtil.setFirstName(LoginActivity.this, getAllData.getData().getFirstName());
                            ProjectUtil.setLasttName(LoginActivity.this, getAllData.getData().getLastName());
                            ProjectUtil.setSirName(LoginActivity.this, getAllData.getData().getTitle());
                            ProjectUtil.setMobileNumber(LoginActivity.this, getAllData.getData().getMobileNumber());
                            ProjectUtil.setMailId(LoginActivity.this, getAllData.getData().getEmail());
                            ProjectUtil.setZipCode(LoginActivity.this, getAllData.getData().getZipCode());
                            Intent in = new Intent(LoginActivity.this, MainActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(in);
                            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                            finish();
                        } else {
                            ProjectUtil.dismissDialog(customDialog);
                            ProjectUtil.customToast(LoginActivity.this, getAllData.getMessage());
                        }
                    }
                }, 2000);
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                ProjectUtil.customToast(LoginActivity.this, t.toString());
                ProjectUtil.dismissDialog(customDialog);

            }
        });
    }

    protected void registerRequest(RegistratonRequest registratonRequest) {
        ProjectUtil.showDialog(customDialog, "Please wait ...");
        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<RegistrationResponse> getAllComplaint = apiService.register("application/json", registratonRequest);
        getAllComplaint.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(@NotNull Call<RegistrationResponse> call, @NotNull Response<RegistrationResponse> response) {
                RegistrationResponse getAllData = response.body();
                if (getAllData == null) {
                    ProjectUtil.dismissDialog(customDialog);
                    ProjectUtil.customToast(LoginActivity.this, "Internal Server error");
                    return;
                }
                int responseCode = response.code();
                Log.v("Main", String.valueOf(response.body()));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        if (getAllData.getStatus().equalsIgnoreCase("success")) {
                            ProjectUtil.customToast(LoginActivity.this, getAllData.getMessage());
                            ProjectUtil.dismissDialog(customDialog);
                            ProjectUtil.setSession(LoginActivity.this, "1");
//                                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
//                                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//                                    finish();


                            ProjectUtil.setUserId(LoginActivity.this, getAllData.getData().getUserID());
                            ProjectUtil.setFirstName(LoginActivity.this, getAllData.getData().getFirstName());
                            ProjectUtil.setLasttName(LoginActivity.this, getAllData.getData().getLastName());
                            ProjectUtil.setSirName(LoginActivity.this, getAllData.getData().getTitle());
                            ProjectUtil.setMobileNumber(LoginActivity.this, getAllData.getData().getMobileNumber());
                            ProjectUtil.setMailId(LoginActivity.this, getAllData.getData().getEmail());
                            ProjectUtil.setZipCode(LoginActivity.this, getAllData.getData().getZipCode());
                            Intent in = new Intent(LoginActivity.this, MainActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(in);
                            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                            finish();

                        } else {
                            ProjectUtil.dismissDialog(customDialog);
                            ProjectUtil.customToast(LoginActivity.this, getAllData.getMessage());
                        }
                    }
                }, 2000);
            }

            @Override
            public void onFailure(@NonNull Call<RegistrationResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                ProjectUtil.customToast(LoginActivity.this, t.toString());
                ProjectUtil.dismissDialog(customDialog);

            }
        });
    }

    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null) return;
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

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void popupWindowEmailPhone(String type, JSONObject jsonObject) {
        try {
            LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_pohne_email, findViewById(R.id.sign_otp));
            PopupWindow pw = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            pw.setAnimationStyle(R.style.popup_window_animation);
            pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_round_top));
            pw.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            EditText umobile = layout.findViewById(R.id.umobile);
            EditText mailId = layout.findViewById(R.id.mailId);
            LinearLayout ll_mail = layout.findViewById(R.id.ll_mail);
            LinearLayout ll_phone = layout.findViewById(R.id.ll_phone);
            if (jsonObject.getString("email").isEmpty()) {
                ll_mail.setVisibility(View.VISIBLE);
            } else {
                ll_mail.setVisibility(View.GONE);
            }
            btn_otp = findViewById(R.id.btn_otp);
            btn_code = findViewById(R.id.btn_code);
            Button btn_cancel = layout.findViewById(R.id.btn_cancel);
            ImageView image_top = layout.findViewById(R.id.image_top);
            image_top.setOnClickListener(v -> pw.dismiss());
            btn_cancel.setOnClickListener(v -> pw.dismiss());
            CountryCodePicker ccp = findViewById(R.id.ccp);
            ccp.setCountryForNameCode(cCode);
            ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                @Override
                public void onCountrySelected(Country selectedCountry) {
                    Toast.makeText(LoginActivity.this, "Updated " + selectedCountry.getName(), Toast.LENGTH_SHORT).show();
                    cCode = "+" + selectedCountry.getPhoneCode();
                }
            });
            btn_otp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!umobile.getText().toString().isEmpty()) {
                        if (umobile.getText().toString().isEmpty()) {
                            ProjectUtil.customToast(LoginActivity.this, "Please Enter Mobile Number");
                            umobile.setError("Please Enter Mobile Number");
                        } else {
                            OtpRequest otpRequest = new OtpRequest();
                            otpRequest.setMobileNumber(cCode + umobile.getText().toString());
                            otpRequest.setUserID("0");
                            otpRequest.setAction("validatePhone");
                            CodeRequest codeRequest = new CodeRequest();
                            try {
                                codeRequest.setEmailId(jsonObject.getString("email"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            codeRequest.setUserID("0");
                            codeRequest.setAction("validateEmail");
                            otpCodeRequest(otpRequest, codeRequest, "0", cCode + umobile.getText().toString());
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter Data", Toast.LENGTH_LONG).show();
                    }
                }
            });
            btn_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mailId.getText().toString().isEmpty()) {
                        if (mailId.getText().toString().isEmpty()) {
                            ProjectUtil.customToast(LoginActivity.this, "Please Enter Mail Id");
                            mailId.setError("Please Enter Mail Id");
                        } else {
                            OtpRequest otpRequest = new OtpRequest();
                            otpRequest.setMobileNumber(cCode + umobile.getText().toString());
                            otpRequest.setUserID("0");
                            otpRequest.setAction("validatePhone");
                            CodeRequest otpCodeRequest = new CodeRequest();
                            otpCodeRequest.setEmailId(mailId.getText().toString());
                            otpCodeRequest.setUserID("0");
                            otpCodeRequest.setAction("validateEmail");
                            otpCodeRequest(otpRequest, otpCodeRequest, "1", mailId.getText().toString());
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter Data", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void socialLogin(SocialLogin otpRequest, JSONObject object) {
        ProjectUtil.showDialog(
                dialog,
                "Please wait ..."
        );
        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<SocialLoginResponse> getAllComplaint = apiService.socialLogin("application/json", otpRequest);
        Gson gson = new Gson();
        System.out.println(gson.toJson(otpRequest));
        getAllComplaint.enqueue(
                new Callback<SocialLoginResponse>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<SocialLoginResponse> call,
                            @NotNull Response<SocialLoginResponse> response) {
                        SocialLoginResponse getAllData = response.body();
                        if (getAllData == null) {
                            ProjectUtil.dismissDialog(dialog);
                            ProjectUtil.customToast(LoginActivity.this, "Internal Server error");
                            return;
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                ProjectUtil.dismissDialog(dialog);
                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    setProfileToView(object);
                                } else {
                                    ProjectUtil.customToast(LoginActivity.this, getAllData.getMessage());
                                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    try {
                                        intent.putExtra("id", object.getString("id"));
                                        intent.putExtra("name", object.getString("name"));
                                        intent.putExtra("email", object.getString("email"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    startActivity(intent);
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<SocialLoginResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(LoginActivity.this, t.toString());
                        ProjectUtil.dismissDialog(dialog);

                    }
                });
    }

    protected void otpCodeRequest(OtpRequest otpRequest, CodeRequest codeRequest, String type, String no) {
        ProjectUtil.showDialog(
                dialog,
                "Please wait ..."
        );
        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<OtpCodeResponse> getAllComplaint = null;
        if (type.equals("0")) {
            getAllComplaint = apiService.otp("application/json", otpRequest);
        } else {
            getAllComplaint = apiService.code("application/json", codeRequest);
        }
        Gson gson = new Gson();
        System.out.println(gson.toJson(otpRequest));
        getAllComplaint.enqueue(
                new Callback<OtpCodeResponse>() {
                    @Override
                    public void onResponse(
                            @NotNull Call<OtpCodeResponse> call,
                            @NotNull Response<OtpCodeResponse> response) {
                        OtpCodeResponse getAllData = response.body();
                        if (getAllData == null) {
                            ProjectUtil.dismissDialog(dialog);
                            ProjectUtil.customToast(LoginActivity.this, "Internal Server error");
                            return;
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                ProjectUtil.dismissDialog(dialog);
                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    popupWindowSentOtp(type, getAllData.getData(), no);
                                } else {
                                    ProjectUtil.customToast(LoginActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<OtpCodeResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(LoginActivity.this, t.toString());
                        ProjectUtil.dismissDialog(dialog);

                    }
                });
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void popupWindowSentOtp(String type, String data, String no) {
        try {
            LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_sent_otp, findViewById(R.id.sign_otp));
            PopupWindow pw = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            pw.setAnimationStyle(R.style.popup_window_animation);
            pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_round_top));
            pw.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            EditText inputPassword = layout.findViewById(R.id.et_data);
            Button btn_update = layout.findViewById(R.id.btn_update);
            Button btn_cancel = layout.findViewById(R.id.btn_cancel);
            ImageView image_top = layout.findViewById(R.id.image_top);
            TextView text_otp_no = layout.findViewById(R.id.text_otp_no);
            TextView text_timer = layout.findViewById(R.id.text_timer);
            if (type.equals("0")) {
                inputPassword.setHint("OTP");
                text_otp_no.setText("We have sent a OTP to " + no);
            } else {
                inputPassword.setHint("CODE");
                text_otp_no.setText("We have sent a Code to " + no);
            }
            new CountDownTimer(60000, 1000) {
                public void onTick(long duration) {
                    //tTimer.setText("seconds remaining: " + millisUntilFinished / 1000);
                    //here you can have your logic to set text to edittext resource id
                    long Mmin = (duration / 1000) / 60;
                    long Ssec = (duration / 1000) % 60;
                    if (Ssec < 10) {
                        text_timer.setText("Sec." + Mmin + ":0" + Ssec);
                    } else {
                        text_timer.setText("Sec." + Mmin + ":" + Ssec);
                    }
                    btn_cancel.setVisibility(View.GONE);
                }

                public void onFinish() {
                    text_timer.setText("0:00");
                    btn_cancel.setVisibility(View.VISIBLE);
                }
            }.start();
            image_top.setOnClickListener(v -> pw.dismiss());
            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!inputPassword.getText().toString().isEmpty()) {
                        if (inputPassword.getText().toString().equalsIgnoreCase(data)) {
                            if (type.equals("0")) {
                                validPhone = 1;
                                Toast.makeText(LoginActivity.this, "Phone Number Successfully Verify", Toast.LENGTH_SHORT).show();
                            } else {
                                validEmail = 1;
                                Toast.makeText(LoginActivity.this, "Mail Id Successfully Verify", Toast.LENGTH_SHORT).show();
                            }
                            pw.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "Data Not Verify", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter Data", Toast.LENGTH_LONG).show();
                    }
                }
            });
            btn_cancel.setOnClickListener(v -> pw.dismiss());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCountryCode(Double latitude, Double longitude) {
        String countryCode = "";
        try {
            Geocoder geocoder = new Geocoder(MyApplication.getInstance().getApplicationContext(), Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = null;
            if (addresses != null && addresses.size() > 0) {
                for (int i = 0; i < addresses.size(); i++) {
                    address = addresses.get(i);
                    if (address.getPostalCode() != null) {
                        countryCode = address.getCountryCode();
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return countryCode;
    }

}
