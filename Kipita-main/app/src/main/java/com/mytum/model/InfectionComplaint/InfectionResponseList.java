package com.mytum.model.InfectionComplaint;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class InfectionResponseList implements Serializable, Parcelable {


    protected InfectionResponseList(Parcel in) {
        dailyNewCases = in.readString();
        pastiveTestRate = in.readString();
        dailyDeath = in.readString();
        country = in.readString();
        rsikLevel = in.readString();
    }

    public InfectionResponseList() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dailyNewCases);
        dest.writeString(pastiveTestRate);
        dest.writeString(dailyDeath);
        dest.writeString(country);
        dest.writeString(rsikLevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InfectionResponseList> CREATOR = new Creator<InfectionResponseList>() {
        @Override
        public InfectionResponseList createFromParcel(Parcel in) {
            return new InfectionResponseList(in);
        }

        @Override
        public InfectionResponseList[] newArray(int size) {
            return new InfectionResponseList[size];
        }
    };

    public String getDailyNewCases() {
        return dailyNewCases;
    }

    public void setDailyNewCases(String dailyNewCases) {
        this.dailyNewCases = dailyNewCases;
    }

    public String getPastiveTestRate() {
        return pastiveTestRate;
    }

    public void setPastiveTestRate(String pastiveTestRate) {
        this.pastiveTestRate = pastiveTestRate;
    }

    public String getDailyDeath() {
        return dailyDeath;
    }

    public void setDailyDeath(String dailyDeath) {
        this.dailyDeath = dailyDeath;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRsikLevel() {
        return rsikLevel;
    }

    public void setRsikLevel(String rsikLevel) {
        this.rsikLevel = rsikLevel;
    }

    private String dailyNewCases = "";
    private String pastiveTestRate = "";
    private String dailyDeath = "";
    private String country = "";
    private String rsikLevel = "";



}
