package com.mytum.client.models;

import java.io.Serializable;

/**
 * Created by Ranga on 2/24/2017.
 */

public class AdvisoryCatResponse implements Serializable {


    String Level;
    String BT;

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getBT() {
        return BT;
    }

    public void setBT(String BT) {
        this.BT = BT;
    }
}
