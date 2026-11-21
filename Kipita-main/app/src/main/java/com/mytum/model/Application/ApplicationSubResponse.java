package com.mytum.model.Application;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ranga on 2/24/2017.
 */

public class ApplicationSubResponse implements Serializable {

    @SerializedName("advisory_tab")
    @Expose
    ArrayList<ApplicationListResponse> advisory_tab;

    @SerializedName("advisory_main")
    @Expose
    ApplicationSub1Response advisory_main;

    public ArrayList<ApplicationListResponse> getAdvisory_tab() {
        return advisory_tab;
    }

    public void setAdvisory_tab(ArrayList<ApplicationListResponse> advisory_tab) {
        this.advisory_tab = advisory_tab;
    }

    public ApplicationSub1Response getAdvisory_main() {
        return advisory_main;
    }

    public void setAdvisory_main(ApplicationSub1Response advisory_main) {
        this.advisory_main = advisory_main;
    }
}
