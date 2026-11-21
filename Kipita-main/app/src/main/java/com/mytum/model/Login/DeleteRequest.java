package com.mytum.model.Login;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class DeleteRequest implements Serializable {

    @SerializedName("userId")
    private String userId;

    @SerializedName("deletePublic")
    private String deletePublic;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeletePublic() {
        return deletePublic;
    }

    public void setDeletePublic(String deletePublic) {
        this.deletePublic = deletePublic;
    }
}
