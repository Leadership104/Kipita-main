package com.mytum.model.Registration;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mytum.model.Login.LoginResponseList;

import java.io.Serializable;

@Keep
public class OtpCodeResponse implements Serializable {
    @SerializedName("message")
    @Expose
    private String message = "";

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private String data = "";


}
