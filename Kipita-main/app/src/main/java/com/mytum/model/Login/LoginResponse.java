package com.mytum.model.Login;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class LoginResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private LoginResponseList data = null;


    @SerializedName("message")
    @Expose
    private String message = "";

    public LoginResponseList getData() {
        return data;
    }

    public void setData(LoginResponseList data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @SerializedName("status")
    @Expose
    private String status = "";



}
