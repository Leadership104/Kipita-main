package com.mytum.model.NewAdvisaryResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mytum.model.Application.ApplicationListResponse;
import com.mytum.model.Application.ApplicationSub1Response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ranga on 2/24/2017.
 */

public class AdvisarySubResponse implements Serializable {

    @SerializedName("section")
    @Expose
    ArrayList<AdvisaryListResponse> section;
    @SerializedName("general")
    @Expose
    AdvisarySub1Response general;

    public ArrayList<AdvisaryListResponse> getSection() {
        return section;
    }

    public void setSection(ArrayList<AdvisaryListResponse> advisory_tab) {
        this.section = advisory_tab;
    }

    public AdvisarySub1Response getGeneral() {
        return general;
    }

    public void setGeneral(AdvisarySub1Response advisory_main) {
        this.general = advisory_main;
    }
}
