package com.mytum.network;

import androidx.annotation.Keep;


import com.mytum.model.ForgetPassword.ForgetPasswordRequest;
import com.mytum.model.ForgetPassword.ForgetResponse;
import com.mytum.model.Login.DeleteRequest;
import com.mytum.model.Login.LoginRequest;
import com.mytum.model.Login.LoginResponse;
import com.mytum.model.Registration.CodeRequest;
import com.mytum.model.Registration.OtpCodeResponse;
import com.mytum.model.Registration.OtpRequest;
import com.mytum.model.Registration.RegistrationResponse;
import com.mytum.model.Registration.RegistratonRequest;
import com.mytum.model.Registration.RegistratonUpdateRequest;
import com.mytum.model.Registration.SocialLogin;
import com.mytum.model.Registration.SocialLoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


@Keep
public interface APIService {
    @Headers({"Accept: application/json"})
    @POST("login")
    Call<LoginResponse> login(@Header("Content-Type") String content_type, @Body LoginRequest loginRequest);

    @Headers({"Accept: application/json"})
    @POST("register")
    Call<RegistrationResponse> register(@Header("Content-Type") String content_type, @Body RegistratonRequest registratonRequest);

    @Headers({"Accept: application/json"})
    @POST("register")
    Call<RegistrationResponse> update(@Header("Content-Type") String content_type, @Body RegistratonUpdateRequest registratonRequest);

    @Headers({"Accept: application/json"})
    @POST("forgotPassword")
    Call<ForgetResponse> forgotPassword(@Header("Content-Type") String content_type, @Body ForgetPasswordRequest forgetPasswordRequest);

    @Headers({"Accept: application/json", "Authorization: Bearer uWwWVaVVFSyTvOv0SMn9L03TfDNo_Zad1UKQXlFyx3ThsQYhc6C97VOIBKjks0q3UCpNGao5rdTg988pZZdvRxReZczmiiLLzIsFqBVZFt9PzyRryIkEZ3lwo1ozY3Yx"})
    @POST("deleteAccount")
    Call<RegistrationResponse> deleteAccount(@Header("Content-Type") String content_type, @Body DeleteRequest deleteRequest);

    @Headers({"Accept: application/json"})
    @POST("validateProfile")
    Call<OtpCodeResponse> otp(@Header("Content-Type") String content_type, @Body OtpRequest otpRequest);

    @Headers({"Accept: application/json"})
    @POST("validateProfile")
    Call<OtpCodeResponse> code(@Header("Content-Type") String content_type, @Body CodeRequest codeRequest);

    @Headers({"Accept: application/json"})
    @POST("socialLogin")
    Call<SocialLoginResponse> socialLogin(@Header("Content-Type") String content_type, @Body SocialLogin codeRequest);
}
