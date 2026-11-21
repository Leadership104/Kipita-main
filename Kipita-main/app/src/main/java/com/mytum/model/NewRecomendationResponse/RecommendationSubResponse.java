package com.mytum.model.NewRecomendationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mytum.model.NewPalnerResponse.NewAdvisaryResponse.PlanerSub1Response;
import com.mytum.model.NewPalnerResponse.NewAdvisaryResponse.PlannerListResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ranga on 2/24/2017.
 */

public class RecommendationSubResponse implements Serializable {

    @SerializedName("order")
    @Expose
    String order;
    @SerializedName("title")
    @Expose
    String title;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("parentType")
    @Expose
    String parentType;

    @SerializedName("queryType")
    @Expose
    String queryType;

    @SerializedName("maxResults")
    @Expose
    int maxResults;

    @SerializedName("rankPreference")
    @Expose
    String rankPreference;

    @SerializedName("radius")
    @Expose
    Double radius;

    @SerializedName("showOpenClosed")
    @Expose
    int showOpenClosed;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
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
}
