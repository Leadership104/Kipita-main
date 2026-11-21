package com.mytum.model;

public class AddDeleteMember {

    private String createdBy;
    private String groupID;
    private String action;
    private String groupDescription;
    private String DeletedMembers;
    private String invitedMembers;
    private String groupName;


    public String getDeletedMembers() {
        return DeletedMembers;
    }

    public void setDeletedMembers(String deletedMembers) {
        DeletedMembers = deletedMembers;
    }

    public String getInvitedMembers() {
        return invitedMembers;
    }

    public void setInvitedMembers(String invitedMembers) {
        this.invitedMembers = invitedMembers;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

}
