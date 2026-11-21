package com.mytum.model.AllCategory;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class AllCategoryResponseList implements Serializable {
    private String catId="";
    private String descr = "";


    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }







}
