package com.mytum.model.Registration;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class RegistratonRequest implements Serializable {

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    public int getValidEmail() {
        return validEmail;
    }

    public void setValidEmail(int validEmail) {
        this.validEmail = validEmail;
    }

    public int getValidPhone() {
        return validPhone;
    }

    public void setValidPhone(int validPhone) {
        this.validPhone = validPhone;
    }

    @SerializedName("validEmail")
    private int validEmail;

    @SerializedName("validPhone")
    private int validPhone;

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

    public boolean isSocialLogin() {
        return socialLogin;
    }

    public void setSocialLogin(boolean socialLogin) {
        this.socialLogin = socialLogin;
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

    @SerializedName("socialLogin")
    private boolean socialLogin;

}
