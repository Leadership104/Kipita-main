package com.mytum.model.Application;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ranga on 2/24/2017.
 */

public class ApplicationSub1Response implements Serializable {

    @SerializedName("image")
    @Expose
    String image;

    @SerializedName("title")
    @Expose
    String title;
    @SerializedName("lastupdated")
    @Expose
    String lastupdated;

    @SerializedName("countryname")
    @Expose
    String countryname;

    @SerializedName("width")
    @Expose
    String width;

    @SerializedName("height")
    @Expose
    String height;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @SerializedName("description")
    @Expose
    String description;



}
