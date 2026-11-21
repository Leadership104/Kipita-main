package com.mytum.model.ImageList;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Keep
public class ImageArrayLocalList implements Serializable, Parcelable {

    public ImageArrayLocalList() {
    }

    protected ImageArrayLocalList(Parcel in) {
        imgID = in.readString();
        userID = in.readString();
        location = in.readString();
        path = in.readString();
        name = in.readString();
        dateTime = in.readString();
        in.readList(this.tags, List.class.getClassLoader());
//        tags = in.readString();
    }

    public static final Creator<ImageArrayLocalList> CREATOR = new Creator<ImageArrayLocalList>() {
        @Override
        public ImageArrayLocalList createFromParcel(Parcel in) {
            return new ImageArrayLocalList(in);
        }

        @Override
        public ImageArrayLocalList[] newArray(int size) {
            return new ImageArrayLocalList[size];
        }
    };

    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }

    @SerializedName("imgID")
    @Expose
    private String imgID = "";

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @SerializedName("userID")
    @Expose
    private String userID = "";

    @SerializedName("lat")
    @Expose
    private String location = "";

    @SerializedName("path")
    @Expose
    private String path = "";

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("dateTime")
    @Expose
    private String dateTime = "";

    @SerializedName("tags")
    @Expose
    private ArrayList<String> tags = new ArrayList<>();
//    @SerializedName("tags")
//    @Expose
//    private String tags = "";

    public String getLocation() {
        return location;
    }

    public void setLocation(String lat) {
        this.location = lat;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String name) {
        this.dateTime = name;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

//    public String getTags() {
//        return tags;
//    }
//
//    public void setTags(String tags) {
//        this.tags = tags;
//    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(imgID);
        parcel.writeString(userID);
        parcel.writeString(location);
        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeString(dateTime);
        parcel.writeList(this.tags);
//        parcel.writeString(tags);
    }
}
