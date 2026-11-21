package com.mytum.model.NewPalnerResponse.NewAdvisaryResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ranga on 2/24/2017.
 */

public class PlanerSub1Response implements Serializable {

    @SerializedName("image")
    @Expose
    String image;

    @SerializedName("last_updated")
    @Expose
    String lastupdated;

    @SerializedName("location")
    @Expose
    String location;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
