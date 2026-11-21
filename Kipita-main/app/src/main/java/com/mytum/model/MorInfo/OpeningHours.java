
package com.mytum.model.MorInfo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;
    @SerializedName("periods")
    @Expose
    private List<Period> periods;
    @SerializedName("weekday_text")
    @Expose
    private ArrayList<String> weekdayText;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public ArrayList<String> getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(ArrayList<String> weekdayText) {
        this.weekdayText = weekdayText;
    }

}
