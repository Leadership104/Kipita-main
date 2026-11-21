package com.mytum.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MamberManageItemObject implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("invitedID")
    @Expose
    private String invitedID;
    @SerializedName("lastLocation")
    @Expose
    private String lastLocation;
    @SerializedName("lastLocationTime")
    @Expose
    private String lastLocationTime;
    @SerializedName("locationSharing")
    @Expose
    private String locationSharing;
    public final static Creator<MamberManageItemObject> CREATOR = new Creator<MamberManageItemObject>() {


        public MamberManageItemObject createFromParcel(android.os.Parcel in) {
            return new MamberManageItemObject(in);
        }

        public MamberManageItemObject[] newArray(int size) {
            return (new MamberManageItemObject[size]);
        }

    };

    @SuppressWarnings({
            "unchecked"
    })
    protected MamberManageItemObject(android.os.Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.role = ((String) in.readValue((String.class.getClassLoader())));
        this.userID = ((String) in.readValue((String.class.getClassLoader())));
        this.invitedID = ((String) in.readValue((String.class.getClassLoader())));
        this.lastLocation = ((String) in.readValue((String.class.getClassLoader())));
        this.lastLocationTime = ((String) in.readValue((String.class.getClassLoader())));
        this.locationSharing = ((String) in.readValue((String.class.getClassLoader())));
    }

    public MamberManageItemObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getInvitedID() {
        return invitedID;
    }

    public void setInvitedID(String invitedID) {
        this.invitedID = invitedID;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getLastLocationTime() {
        return lastLocationTime;
    }

    public void setLastLocationTime(String lastLocationTime) {
        this.lastLocationTime = lastLocationTime;
    }

    public String getLocationSharing() {
        return locationSharing;
    }

    public void setLocationSharing(String locationSharing) {
        this.locationSharing = locationSharing;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(role);
        dest.writeValue(userID);
        dest.writeValue(invitedID);
        dest.writeValue(lastLocation);
        dest.writeValue(lastLocationTime);
        dest.writeValue(locationSharing);
    }

    public int describeContents() {
        return 0;
    }

}