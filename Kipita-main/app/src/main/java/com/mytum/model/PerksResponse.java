package com.mytum.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mytum.model.NewAdvisaryResponse.AdvisarySubResponse;

import java.io.Serializable;

public class PerksResponse implements Serializable {
    @SerializedName("name")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private PerksListResponse data;

    public PerksListResponse getData() {
        return data;
    }

    public void setData(PerksListResponse data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
