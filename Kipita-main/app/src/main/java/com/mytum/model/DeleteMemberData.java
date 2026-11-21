package com.mytum.model;

public class DeleteMemberData {

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

    private String userID;
    private String invitedID;


}
