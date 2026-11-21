package com.mytum.model.Registration;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class RegistratonUpdateRequest implements Serializable {


    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

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

    public String getTitle() {
        return title;
    }

    public Boolean getUpdate_profile() {
        return update_profile;
    }

    public void setUpdate_profile(Boolean update_profile) {
        this.update_profile = update_profile;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @SerializedName("title")
    private String title;

    @SerializedName("mobileNumber")
    private String mobileNumber;

    @SerializedName("emailId")
    private String emailId;

    @SerializedName("password")
    private String password;

    @SerializedName("zipCode")
    private String zipCode;

    @SerializedName("update_profile")
    private Boolean update_profile;





}
