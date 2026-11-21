package com.mytum.model.ForgetPassword;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class ForgetPasswordRequest implements Serializable {


    @SerializedName("emailId")
    private String emailId;


    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
