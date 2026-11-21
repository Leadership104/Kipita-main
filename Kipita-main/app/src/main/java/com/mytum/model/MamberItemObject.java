package com.mytum.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MamberItemObject implements Parcelable {

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
    public final static Creator<MamberItemObject> CREATOR = new Creator<MamberItemObject>() {


        public MamberItemObject createFromParcel(android.os.Parcel in) {
            return new MamberItemObject(in);
        }

        public MamberItemObject[] newArray(int size) {
            return (new MamberItemObject[size]);
        }

    }
            ;

    @SuppressWarnings({
            "unchecked"
    })
    protected MamberItemObject(android.os.Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.role = ((String) in.readValue((String.class.getClassLoader())));
        this.userID = ((String) in.readValue((String.class.getClassLoader())));
        this.invitedID = ((String) in.readValue((String.class.getClassLoader())));
    }

    public MamberItemObject() {
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

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(role);
        dest.writeValue(userID);
        dest.writeValue(invitedID);
    }

    public int describeContents() {
        return 0;
    }

}