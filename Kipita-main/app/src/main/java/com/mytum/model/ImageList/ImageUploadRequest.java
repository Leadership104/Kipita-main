package com.mytum.model.ImageList;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Keep
public class ImageUploadRequest implements Serializable, Parcelable {
    public ImageUploadRequest() {
    }

    @SerializedName("imageName")
    private String imageName;

    @SerializedName("imageTag")
    private String imageTag;

    @SerializedName("userID")
    private String userID;

    protected ImageUploadRequest(Parcel in) {
        imageName = in.readString();
        imageTag = in.readString();
        userID = in.readString();
//        imageData = in.readString();
        in.readList(this.imageData, List.class.getClassLoader());
        in.readList(this.location, List.class.getClassLoader());
        in.readList(this.createdDateTime, List.class.getClassLoader());
        imageLat = in.readString();
        imageLong = in.readString();
        mode = in.readString();
        action = in.readString();
        imageId = in.readString();
        currentLat = in.readString();
        currentLng = in.readString();
        timeZone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageName);
        dest.writeString(imageTag);
        dest.writeString(userID);
//        dest.writeString(imageData);
        dest.writeList(this.imageData);
        dest.writeList(this.location);
        dest.writeList(this.createdDateTime);
        dest.writeString(imageLat);
        dest.writeString(imageLong);
        dest.writeString(mode);
        dest.writeString(action);
        dest.writeString(imageId);
        dest.writeString(currentLat);
        dest.writeString(currentLng);
        dest.writeString(timeZone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageUploadRequest> CREATOR = new Creator<ImageUploadRequest>() {
        @Override
        public ImageUploadRequest createFromParcel(Parcel in) {
            return new ImageUploadRequest(in);
        }

        @Override
        public ImageUploadRequest[] newArray(int size) {
            return new ImageUploadRequest[size];
        }
    };

    public ArrayList<String> getImageData() {
        return imageData;
    }

    public void setImageData(ArrayList<String> imageData) {
        this.imageData = imageData;
    }

    @SerializedName("imageData")
    private ArrayList<String> imageData = new ArrayList<>();


    public ArrayList<String> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<String> location) {
        this.location = location;
    }

    public ArrayList<String> getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(ArrayList<String> createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @SerializedName("location")
    private ArrayList<String> location = new ArrayList<>();

    @SerializedName("createdDate")
    private ArrayList<String> createdDateTime = new ArrayList<>();

    @SerializedName("imageLat")
    private String imageLat;

    public String getImageLat() {
        return imageLat;
    }

    public void setImageLat(String imageLat) {
        this.imageLat = imageLat;
    }

    public String getImageLong() {
        return imageLong;
    }

    public void setImageLong(String imageLong) {
        this.imageLong = imageLong;
    }

    @SerializedName("imageLong")
    private String imageLong;


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @SerializedName("mode")
    private String mode;

    @SerializedName("action")
    private String action;

    @SerializedName("imageId")
    private String imageId;

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    @SerializedName("dateAdded")
    private String dateAdded;

    public String getCurrentLng() {
        return currentLng;
    }
    public void setCurrentLng(String lng) {
        this.currentLng = lng;
    }

    public String getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(String lat) {
        this.currentLat = lat;
    }

    @SerializedName("timeZone")
    private String timeZone;
    @SerializedName("currentLong")
    private String currentLng;
    @SerializedName("currentLat")
    private String currentLat;

}
