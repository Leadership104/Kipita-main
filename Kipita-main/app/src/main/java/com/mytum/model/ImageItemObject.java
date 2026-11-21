package com.mytum.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageItemObject implements Parcelable {

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("thumb")
    @Expose
    private String thumb;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("imgID")
    @Expose
    private String imgID;
    public final static Creator<ImageItemObject> CREATOR = new Creator<ImageItemObject>() {


        public ImageItemObject createFromParcel(android.os.Parcel in) {
            return new ImageItemObject(in);
        }

        public ImageItemObject[] newArray(int size) {
            return (new ImageItemObject[size]);
        }

    };

    @SuppressWarnings({
            "unchecked"
    })
    protected ImageItemObject(android.os.Parcel in) {
        this.path = ((String) in.readValue((String.class.getClassLoader())));
        this.thumb = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.imgID = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ImageItemObject() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(path);
        dest.writeValue(thumb);
        dest.writeValue(name);
        dest.writeValue(imgID);
    }

    public int describeContents() {
        return 0;
    }

}