package com.mytum.model.NewPalnerResponse.NewAdvisaryResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ranga on 2/24/2017.
 */

public class PlannerSubResponse implements Serializable {

    @SerializedName("section")
    @Expose
    ArrayList<PlannerListResponse> planner_tab;

    public ArrayList<PlannerListResponse> getPlanner_tab() {
        return planner_tab;
    }

    public void setPlanner_tab(ArrayList<PlannerListResponse> planner_tab) {
        this.planner_tab = planner_tab;
    }

    public PlanerSub1Response getPlanner_main() {
        return planner_main;
    }

    public void setPlanner_main(PlanerSub1Response planner_main) {
        this.planner_main = planner_main;
    }

    @SerializedName("general")
    @Expose
    PlanerSub1Response planner_main;


}
