package com.mytum.model.NewAdvisaryResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mytum.model.Application.ApplicationSubResponse;

import java.io.Serializable;

/**
 * Created by Ranga on 2/24/2017.
 */

public class NewAdvisaryResponse implements Serializable {


    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private AdvisarySubResponse data;


    public AdvisarySubResponse getData() {
        return data;
    }

    public void setData(AdvisarySubResponse data) {
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
