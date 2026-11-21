package com.mytum.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupsItemObject implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @SerializedName("isAdmin")
    @Expose
    private Boolean isAdmin;

    public String getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(String totalMessages) {
        this.totalMessages = totalMessages;
    }

    public String getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(String totalMembers) {
        this.totalMembers = totalMembers;
    }

    @SerializedName("totalMessages")
    @Expose
    private String totalMessages;
    @SerializedName("totalMembers")
    @Expose
    private String totalMembers;
    public final static Creator<GroupsItemObject> CREATOR = new Creator<GroupsItemObject>() {


        public GroupsItemObject createFromParcel(android.os.Parcel in) {
            return new GroupsItemObject(in);
        }

        public GroupsItemObject[] newArray(int size) {
            return (new GroupsItemObject[size]);
        }

    };

    @SuppressWarnings({
            "unchecked"
    })
    protected GroupsItemObject(android.os.Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.createdDate = ((String) in.readValue((String.class.getClassLoader())));
        this.totalMessages = ((String) in.readValue((String.class.getClassLoader())));
        this.totalMembers = ((String) in.readValue((String.class.getClassLoader())));
    }

    public GroupsItemObject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(description);
        dest.writeValue(createdDate);
        dest.writeValue(totalMessages);
        dest.writeValue(totalMembers);
    }

    public int describeContents() {
        return 0;
    }

}