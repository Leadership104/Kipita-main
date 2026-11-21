package com.mytum.model.Application;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ranga on 2/24/2017.
 */

public class ApplicationListResponse implements Serializable {

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("content")
    @Expose
    String content;

    @SerializedName("location")
    @Expose
    ArrayList<ApplicationLocationResponse> location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<ApplicationLocationResponse> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<ApplicationLocationResponse> location) {
        this.location = location;
    }
}
