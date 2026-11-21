package com.mytum.model.Registration;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class SocialLogin implements Serializable {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirebasetoken() {
        return firebasetoken;
    }

    public void setFirebasetoken(String firebasetoken) {
        this.firebasetoken = firebasetoken;
    }

    public Boolean getSocialLogin() {
        return socialLogin;
    }

    public void setSocialLogin(Boolean socialLogin) {
        this.socialLogin = socialLogin;
    }

    @SerializedName("username")
    private String username;

    @SerializedName("firebasetoken")
    private String firebasetoken;

    @SerializedName("socialLogin")
    private Boolean socialLogin;

}
