package com.mytum.model.NewPalnerResponse.NewAdvisaryResponse;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PlannerListResponse implements Serializable, Parcelable {

    @SerializedName("title")
    @Expose
    String title;
    @SerializedName("showOpenClosed")
    @Expose
    int showOpenClosed;
    @SerializedName("queryType")
    @Expose
    String queryType;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("url")
    @Expose
    String url;

    @SerializedName("order")
    @Expose
    String order;

    public PlannerListResponse() {
    }
    protected PlannerListResponse(Parcel in) {
        title = in.readString();
        queryType = in.readString();
        type = in.readString();
        url = in.readString();
        order = in.readString();
        showOpenClosed = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(queryType);
        dest.writeString(type);
        dest.writeString(url);
        dest.writeString(order);
        dest.writeInt(showOpenClosed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlannerListResponse> CREATOR = new Creator<PlannerListResponse>() {
        @Override
        public PlannerListResponse createFromParcel(Parcel in) {
            return new PlannerListResponse(in);
        }

        @Override
        public PlannerListResponse[] newArray(int size) {
            return new PlannerListResponse[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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


    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getShowOpenClosed() {
        return showOpenClosed;
    }

    public void setShowOpenClosed(int showOpenClosed) {
        this.showOpenClosed = showOpenClosed;
    }

}
