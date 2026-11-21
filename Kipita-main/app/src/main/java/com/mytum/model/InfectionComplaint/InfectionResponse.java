package com.mytum.model.InfectionComplaint;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Keep
public class InfectionResponse implements Serializable {
    @SerializedName("response")
    @Expose
    private List<InfectionResponseList> response = null;

    public List<InfectionResponseList> getresponse() {
        return this.response;
    }

    public void setresponse(List<InfectionResponseList> response) {
        this.response = response;
    }



    private String message = "";
    private String success = "";

    public String getSuccess() {
        return this.success;
    }

    public void setSuccess(String success2) {
        this.success = success2;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message2) {
        this.message = message2;
    }




}
