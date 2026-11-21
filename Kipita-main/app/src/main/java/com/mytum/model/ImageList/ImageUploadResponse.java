package com.mytum.model.ImageList;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@Keep
public class ImageUploadResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<ImageUploadData> data = null;

    public ArrayList<ImageUploadData> getData() {
        return data;
    }

    public void setData(ArrayList<ImageUploadData> data) {
        this.data = data;
    }
}
