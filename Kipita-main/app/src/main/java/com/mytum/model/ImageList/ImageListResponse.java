package com.mytum.model.ImageList;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@Keep
public class ImageListResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<ImageArrayListList> data = null;

    public ArrayList<ImageArrayListList> getData() {
        return data;
    }

    public void setData(ArrayList<ImageArrayListList> data) {
        this.data = data;
    }

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

    @SerializedName("allTags")
    @Expose
    private String allTags = "";

    public String getAllTags() {
        return allTags;
    }

    public void setAllTags(String allTags) {
        this.allTags = allTags;
    }

    @SerializedName("timeTags")
    @Expose
    private String timeTags = "";

    public String getTimeTags() {
        return timeTags;
    }

    public void setTimeTags(String timeTags) {
        this.timeTags = timeTags;
    }

    @SerializedName("locationTags")
    @Expose
    private String locationTags = "";

    public String getLocationTags() {
        return locationTags;
    }

    public void setLocationTags(String locationTags) {
        this.locationTags = locationTags;
    }


}
