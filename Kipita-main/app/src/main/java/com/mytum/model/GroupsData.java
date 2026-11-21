package com.mytum.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupsData implements Parcelable {

    @SerializedName("groups")
    @Expose
    private List<GroupsList> groups;
    public final static Creator<GroupsData> CREATOR = new Creator<GroupsData>() {


        public GroupsData createFromParcel(android.os.Parcel in) {
            return new GroupsData(in);
        }

        public GroupsData[] newArray(int size) {
            return (new GroupsData[size]);
        }

    };

    @SuppressWarnings({
            "unchecked"
    })
    protected GroupsData(android.os.Parcel in) {
        in.readList(this.groups, (GroupsList.class.getClassLoader()));
    }

    public GroupsData() {
    }

    public List<GroupsList> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupsList> groups) {
        this.groups = groups;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeList(groups);
    }

    public int describeContents() {
        return 0;
    }

}