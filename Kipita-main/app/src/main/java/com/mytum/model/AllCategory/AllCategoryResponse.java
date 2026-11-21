package com.mytum.model.AllCategory;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Keep
public class AllCategoryResponse implements Serializable {
    @SerializedName("response")
    @Expose
    private List<AllCategoryResponseList> response = null;

    public List<AllCategoryResponseList> getresponse() {
        return this.response;
    }

    public void setresponse(List<AllCategoryResponseList> response) {
        this.response = response;
    }



    private String message = "";
    private String success = "";

    public List<AllCategoryResponseList> getResponse() {
        return response;
    }

    public void setResponse(List<AllCategoryResponseList> response) {
        this.response = response;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String salt = "";
    private String key = "";


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
