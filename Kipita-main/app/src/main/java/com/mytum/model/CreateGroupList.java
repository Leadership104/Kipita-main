package com.mytum.model;

import com.google.gson.JsonArray;

import org.json.JSONArray;

public class CreateGroupList {
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public JsonArray getInvitedMembers() {
        return invitedMembers;
    }

    public void setInvitedMembers(JsonArray invitedMembers) {
        this.invitedMembers = invitedMembers;
    }

    private String action;
    private String groupName;
    private String groupDescription;
    private String createdBy;
    private JsonArray invitedMembers;


}
