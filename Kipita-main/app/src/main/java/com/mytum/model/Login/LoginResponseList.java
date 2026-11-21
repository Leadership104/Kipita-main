package com.mytum.model.Login;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class LoginResponseList implements Serializable {

    @SerializedName("userID")
    @Expose
    private String userID = "";

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("firstName")
    @Expose
    private String firstName = "";

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber = "";

    @SerializedName("lastName")
    @Expose
    private String lastName = "";

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @SerializedName("zipCode")
    @Expose
    private String zipCode = "";

    @SerializedName("email")
    @Expose
    private String email = "";

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
