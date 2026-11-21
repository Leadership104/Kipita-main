package com.mytum.model.NewPalnerResponse.NewAdvisaryResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ranga on 2/24/2017.
 */

public class PlanerAdvisaryResponse implements Serializable {


    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private PlannerSubResponse data;

    public PlannerSubResponse getData() {
        return data;
    }

    public void setData(PlannerSubResponse data) {
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
