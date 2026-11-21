package com.mytum.model.NewRecomendationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ranga on 2/24/2017.
 */

public class RecommendationSectionResponse implements Serializable {

    @SerializedName("section")
    @Expose
    private ArrayList<RecommendationSubResponse> section;


    public ArrayList<RecommendationSubResponse> getSection() {
        return section;
    }

    public void setSection(ArrayList<RecommendationSubResponse> data) {
        this.section = data;
    }

}
