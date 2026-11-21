package com.mytum.model.ImageList;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class ImageListRequest implements Serializable {

    @SerializedName("userID")
    private String userID;

    @SerializedName("screenWidth")
    private int screenWidth;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

}
