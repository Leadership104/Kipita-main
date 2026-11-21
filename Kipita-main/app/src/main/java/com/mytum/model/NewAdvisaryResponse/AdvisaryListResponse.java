package com.mytum.model.NewAdvisaryResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mytum.model.Application.ApplicationLocationResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ranga on 2/24/2017.
 */

public class AdvisaryListResponse implements Serializable, Parcelable {

    @SerializedName("title")
    @Expose
    String title;
    public AdvisaryListResponse() {
    }
    protected AdvisaryListResponse(Parcel in) {
        title = in.readString();
        queryType = in.readString();
        type = in.readString();
        icon = in.readString();
        order = in.readString();
        maxResults = in.readInt();
        radius = in.readDouble();
        rankPreference = in.readString();
        showOpenClosed = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(queryType);
        dest.writeString(type);
        dest.writeString(icon);
        dest.writeString(order);
        dest.writeInt(maxResults);
        dest.writeDouble(radius);
        dest.writeString(rankPreference);
        dest.writeInt(showOpenClosed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AdvisaryListResponse> CREATOR = new Creator<AdvisaryListResponse>() {
        @Override
        public AdvisaryListResponse createFromParcel(Parcel in) {
            return new AdvisaryListResponse(in);
        }

        @Override
        public AdvisaryListResponse[] newArray(int size) {
            return new AdvisaryListResponse[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSearchParam() {
        return searchParam;
    }

    public void setSearchParam(String searchParam) {
        this.searchParam = searchParam;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String getRankPreference() {
        return rankPreference;
    }

    public void setRankPreference(String rankPreference) {
        this.rankPreference = rankPreference;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public int getShowOpenClosed() {
        return showOpenClosed;
    }

    public void setShowOpenClosed(int showOpenClosed) {
        this.showOpenClosed = showOpenClosed;
    }


    @SerializedName("queryType")
    @Expose
    String queryType;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("searchParam")
    @Expose
    String searchParam;
    @SerializedName("icon")
    @Expose
    String icon;
    @SerializedName("order")
    @Expose
    String order;
    @SerializedName("maxResults")
    @Expose
    int maxResults = 20;
    @SerializedName("rankPreference")
    @Expose
    String rankPreference = "";
    @SerializedName("radius")
    @Expose
    Double radius = 2414.00;
    @SerializedName("showOpenClosed")
    @Expose
    int showOpenClosed = 1;
}
