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
public class ImageArrayListList implements Serializable, Parcelable {

    public ImageArrayListList() {
    }

    protected ImageArrayListList(Parcel in) {
        imgID = in.readString();
        userID = in.readString();
        lng = in.readString();
        lat = in.readString();
        path = in.readString();
        name = in.readString();
        uploadedBy = in.readString();
        mode = in.readString();
        dateAdded = in.readString();
        thumb = in.readString();
        thumbHeight = in.readInt();
        thumbWidth = in.readInt();
        in.readList(this.tags, List.class.getClassLoader());
//        tags = in.readString();
    }

    public static final Creator<ImageArrayListList> CREATOR = new Creator<ImageArrayListList>() {
        @Override
        public ImageArrayListList createFromParcel(Parcel in) {
            return new ImageArrayListList(in);
        }

        @Override
        public ImageArrayListList[] newArray(int size) {
            return new ImageArrayListList[size];
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

    @SerializedName("lng")
    @Expose
    private String lng = "";

    @SerializedName("lat")
    @Expose
    private String lat = "";

    @SerializedName("path")
    @Expose
    private String path = "";

    @SerializedName("name")
    @Expose
    private String name = "";

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    @SerializedName("uploadedBy")
    @Expose
    private String uploadedBy = "";

    @SerializedName("mode")
    @Expose
    private String mode = "";

    @SerializedName("dateAdded")
    @Expose
    private String dateAdded = "";

    @SerializedName("thumb")
    @Expose
    private String thumb = "";

    @SerializedName("thumbHeight")
    @Expose
    private int thumbHeight = 0;

    @SerializedName("thumbWidth")
    @Expose
    private int thumbWidth = 0;

    @SerializedName("tags")
    @Expose
    private ArrayList<String> tags = new ArrayList<>();
//    @SerializedName("tags")
//    @Expose
//    private String tags = "";

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
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

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(imgID);
        parcel.writeString(userID);
        parcel.writeString(lng);
        parcel.writeString(lat);
        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeString(uploadedBy);
        parcel.writeString(mode);
        parcel.writeString(dateAdded);
        parcel.writeString(thumb);
        parcel.writeInt(thumbHeight);
        parcel.writeInt(thumbWidth);
        parcel.writeList(this.tags);
//        parcel.writeString(tags);
    }
}
