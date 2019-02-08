package com.zoop.checkout.app.Model;

import com.google.gson.annotations.SerializedName;

public class Seller {
    @SerializedName("id")
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
    }

    public Seller(String id) {

        this.id = id;
    }
}
