package com.mytum.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PendingInvitesGroupItem implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("invitedText")
    @Expose
    private String invitedText;
    public final static Creator<PendingInvitesGroupItem> CREATOR = new Creator<PendingInvitesGroupItem>() {


        public PendingInvitesGroupItem createFromParcel(android.os.Parcel in) {
            return new PendingInvitesGroupItem(in);
        }

        public PendingInvitesGroupItem[] newArray(int size) {
            return (new PendingInvitesGroupItem[size]);
        }

    }
            ;

    @SuppressWarnings({
            "unchecked"
    })
    protected PendingInvitesGroupItem(android.os.Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.createdDate = ((String) in.readValue((String.class.getClassLoader())));
        this.invitedText = ((String) in.readValue((String.class.getClassLoader())));
    }

    public PendingInvitesGroupItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getInvitedText() {
        return invitedText;
    }

    public void setInvitedText(String invitedText) {
        this.invitedText = invitedText;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(description);
        dest.writeValue(createdDate);
        dest.writeValue(invitedText);
    }

    public int describeContents() {
        return 0;
    }

}