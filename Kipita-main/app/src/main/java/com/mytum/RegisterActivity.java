package com.mytum;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.gson.Gson;
import com.mytum.finestwebview.FinestWebView;
import com.mytum.model.Registration.CodeRequest;
import com.mytum.model.Registration.OtpCodeResponse;
import com.mytum.model.Registration.OtpRequest;
import com.mytum.model.Registration.RegistrationResponse;
import com.mytum.model.Registration.RegistratonRequest;
import com.mytum.network.APIService;
import com.mytum.network.RetrofitClient;
import com.mytum.proectUtil.GPSTracker;
import com.mytum.proectUtil.ProjectUtil;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
//import com.mytum.finestwebview.FinestWebView;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

//import butterknife.BindView;
//import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
//    @BindView(R.id.backbtn)
    ImageView backbtn;
//    @BindView(R.id.privacypolicy)
    TextView privacypolicy;
//    @BindView(R.id.termcondition)
    TextView termcondition;
//    @BindView(R.id.umobile)
    EditText umobile;
//    @BindView(R.id.login)
    TextView login;
//    @BindView(R.id.zipcode)
    EditText zipcode;
//    @BindView(R.id.firstName)
    EditText firstName;
//    @BindView(R.id.lastName)
    EditText lastName;
//    @BindView(R.id.mailId)
    EditText mailId;
//    @BindView(R.id.password)
    EditText password;
//    @BindView(R.id.cpassword)
    EditText cpassword;
//    @BindView(R.id.btn_otp)
    Button btn_otp;
//    @BindView(R.id.btn_code)
    Button btn_code;
    int validPhone = 0;
    int validEmail = 0;
    Dialog dialog = null;
    String cCode = "";
    String vfPhone;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
//        ButterKnife.bind(this);
        Window window = getWindow();
        View yourView = window.getDecorView();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        yourView.setSystemUiVisibility(0);
        GPSTracker mGPS = new GPSTracker(this);
        cCode = getCountryCode(mGPS.getLatitude(), mGPS.getLongitude());
        dialog = new Dialog(this);
        backbtn = findViewById(R.id.backbtn);
        btn_otp = findViewById(R.id.btn_otp);
        btn_code = findViewById(R.id.btn_code);
        umobile = findViewById(R.id.umobile);
        zipcode = findViewById(R.id.zipcode);
//        lastName = findViewById(R.id.lastName);
        privacypolicy = findViewById(R.id.privacypolicy);
        termcondition = findViewById(R.id.termcondition);
        login = findViewById(R.id.login);
        firstName = findViewById(R.id.firstName);
        firstName.setText(name);
        lastName = findViewById(R.id.lastName);
        try {
            assert name != null;
            String[] name1 = name.split(" ");
            firstName.setText(name1[0]);
            lastName.setText(name1[1]);
        } catch (Exception e) {
        }
        mailId = findViewById(R.id.mailId);
        if (!Objects.requireNonNull(email).isEmpty()) {
            mailId.setText(email);
            validEmail = 1;
            btn_code.setClickable(false);
            btn_code.setText("Verified");
        }
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });

        CountryCodePicker ccp = findViewById(R.id.ccp);
        ccp.setCountryForNameCode(cCode);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                Toast.makeText(RegisterActivity.this, "Updated " + selectedCountry.getName(), Toast.LENGTH_SHORT).show();
                cCode = "+" + selectedCountry.getPhoneCode();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });

        btn_otp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (umobile.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter Mobile Number");
                    umobile.setError("Please Enter Mobile Number");
                } else {
                    OtpRequest otpRequest = new OtpRequest();
                    otpRequest.setMobileNumber(cCode + umobile.getText().toString());
                    otpRequest.setUserID("0");
                    otpRequest.setAction("validatePhone");
                    CodeRequest codeRequest = new CodeRequest();
                    codeRequest.setEmailId(mailId.getText().toString());
                    codeRequest.setUserID("0");
                    codeRequest.setAction("validateEmail");
                    otpCodeRequest(otpRequest, codeRequest, "0");

                }
            }
        });

        btn_code.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mailId.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter Mail Id");
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
                    otpCodeRequest(otpRequest, otpCodeRequest, "1");
                }
            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (zipcode.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter ZipCode");
                    zipcode.setError("Please Enter ZipCode");
                } else if (password.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter Password");
                    password.setError("Please Enter Password");
                } else if (cpassword.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter confirm Password");
                    cpassword.setError("Please Enter confirm Password");
                } else if (lastName.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter Last Name");
                    lastName.setError("Please Enter Last Name");
                } else if (firstName.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter First Name");
                    firstName.setError("Please Enter First Name");
                } else if (!cpassword.getText().toString().equalsIgnoreCase(password.getText().toString())) {
                    ProjectUtil.customToast(RegisterActivity.this, "Password and Confirm password not match");
                    firstName.setError("Please Enter First Name");
                } else if (mailId.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter Mail Id");
                    mailId.setError("Please Enter Mail Id");
                } else if (umobile.getText().toString().isEmpty()) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Enter Mobile Number");
                    umobile.setError("Please Enter Mobile Number");
                } else if (validEmail == 0) {
                    ProjectUtil.customToast(RegisterActivity.this, "Please Verify Mail Id");
                } else {
                    RegistratonRequest registratonRequest = new RegistratonRequest();
                    registratonRequest.setEmailId(mailId.getText().toString());
                    registratonRequest.setMobileNumber(cCode + umobile.getText().toString());
                    registratonRequest.setFirstName(firstName.getText().toString());
                    registratonRequest.setLastName(lastName.getText().toString());
                    registratonRequest.setTitle("Mr.");
                    registratonRequest.setPassword(cpassword.getText().toString());
                    registratonRequest.setZipCode(zipcode.getText().toString());
                    registratonRequest.setValidEmail(validEmail);
                    registratonRequest.setValidPhone(validPhone);
                    registerRequest(registratonRequest);
                }


            }
        });
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FinestWebView(RegisterActivity.this).theme(R.style.RedTheme)
                        .titleDefault("Privacy Policy")
                        .toolbarScrollFlags(0)
                        .statusBarColorRes(R.color.blue)
                        .toolbarColorRes(R.color.blue)
                        .titleColorRes(R.color.white)
                        .urlColorRes(R.color.white)
                        .iconDefaultColorRes(R.color.white)
                        .progressBarColorRes(R.color.white)
                        .swipeRefreshColorRes(R.color.white)
//                        .menuSelector(R.drawable.selector_light_theme)
                        .menuTextGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT)
//                        .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                        .dividerHeight(0)
                        .gradientDivider(false)
//                        .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold, R.anim.slide_right_out)
                        .disableIconBack(false)
                        .disableIconClose(false)
                        .disableIconForward(false)
                        .disableIconMenu(false)
                        .show("https://www.my2tum.com/privacy");
            }
        });
        termcondition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                new FinestWebView.Builder(RegisterActivity.this).theme(R.style.RedTheme)
//                        .titleDefault("Term &amp; condition ")
//                        .toolbarScrollFlags(0)
//                        .statusBarColorRes(R.color.blue)
//                        .toolbarColorRes(R.color.blue)
//                        .titleColorRes(R.color.white)
//                        .urlColorRes(R.color.white)
//                        .iconDefaultColorRes(R.color.white)
//                        .progressBarColorRes(R.color.white)
//                        .swipeRefreshColorRes(R.color.white)
////                        .menuSelector(R.drawable.selector_light_theme)
//                        .menuTextGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT)
////                        .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
//                        .dividerHeight(0)
//                        .gradientDivider(false)
////                        .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold, R.anim.slide_right_out)
//                        .disableIconBack(false)
//                        .disableIconClose(false)
//                        .disableIconForward(false)
//                        .disableIconMenu(false)
//                        .show("https://www.my2tum.com/terms");
            }
        });
    }

    protected void otpCodeRequest(OtpRequest otpRequest, CodeRequest codeRequest, String type) {
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
                            ProjectUtil.customToast(RegisterActivity.this, "Internal Server error");
                            return;
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                ProjectUtil.dismissDialog(dialog);
                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    popupWindowSentOtp(type, getAllData.getData());
                                } else {
                                    ProjectUtil.customToast(RegisterActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<OtpCodeResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(RegisterActivity.this, t.toString());
                        ProjectUtil.dismissDialog(dialog);

                    }
                });
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void popupWindowSentOtp(String type, String data) {
        try {
            LayoutInflater inflater = (LayoutInflater) RegisterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                text_otp_no.setText("We have sent a OTP to " + cCode + umobile.getText().toString());
            } else {
                inputPassword.setHint("CODE");
                text_otp_no.setText("We have sent a Code to " + mailId.getText().toString());
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
                                vfPhone = umobile.getText().toString();
                                btn_otp.setClickable(false);
                                btn_otp.setText("Verified");
                                Toast.makeText(RegisterActivity.this, "Phone Number Successfully Verify", Toast.LENGTH_SHORT).show();
                                umobile.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        if (vfPhone.contentEquals(s)) {
                                            btn_otp.setClickable(false);
                                            btn_otp.setText("Verified");
                                        } else {
                                            btn_otp.setText("OTP");
                                            btn_otp.setClickable(true);
                                        }
                                    }
                                });
                            } else {
                                validEmail = 1;
                                btn_code.setClickable(false);
                                btn_code.setText("Verified");
                                Toast.makeText(RegisterActivity.this, "Mail Id Successfully Verify", Toast.LENGTH_SHORT).show();
                            }
                            pw.dismiss();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Data Not Verify", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please enter Data", Toast.LENGTH_LONG).show();
                    }
                }
            });
            btn_cancel.setOnClickListener(v -> pw.dismiss());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    protected void registerRequest(RegistratonRequest registratonRequest) {
        ProjectUtil.showDialog(
                dialog,
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
                            ProjectUtil.dismissDialog(dialog);
                            ProjectUtil.customToast(RegisterActivity.this, "Internal Server error");
                            return;
                        }
                        int responseCode = response.code();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
                                    ProjectUtil.customToast(RegisterActivity.this, getAllData.getMessage());
                                    ProjectUtil.dismissDialog(dialog);
                                    ProjectUtil.setSession(RegisterActivity.this, "1");
                                    ProjectUtil.setUserId(RegisterActivity.this, getAllData.getData().getUserID());
                                    ProjectUtil.setFirstName(RegisterActivity.this, getAllData.getData().getFirstName());
                                    ProjectUtil.setLasttName(RegisterActivity.this, getAllData.getData().getLastName());
                                    ProjectUtil.setSirName(RegisterActivity.this, getAllData.getData().getTitle());
                                    ProjectUtil.setMobileNumber(RegisterActivity.this, getAllData.getData().getMobileNumber());
                                    ProjectUtil.setMailId(RegisterActivity.this, getAllData.getData().getEmail());
                                    ProjectUtil.setZipCode(RegisterActivity.this, getAllData.getData().getZipCode());
                                    Intent in = new Intent(RegisterActivity.this, MainActivity.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(in);
                                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                                    finish();
//                                if (getAllData.getStatus().equalsIgnoreCase("success")) {
//                                    ProjectUtil.customToast(RegisterActivity.this, getAllData.getMessage());
//                                    ProjectUtil.dismissDialog(dialog);
//                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//                                    finish();
                                } else {
                                    ProjectUtil.dismissDialog(dialog);
                                    ProjectUtil.customToast(RegisterActivity.this, getAllData.getMessage());
                                }
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(@NonNull Call<RegistrationResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        ProjectUtil.customToast(RegisterActivity.this, t.toString());
                        ProjectUtil.dismissDialog(dialog);
                    }
                });
    }

    public static String getCountryCode(Double latitude, Double longitude) {
        String countryCode = "";
        try {
            Geocoder geocoder = new Geocoder(MyApplication.getInstance().getApplicationContext(), Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = null;
            if (addresses != null && !addresses.isEmpty()) {
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
