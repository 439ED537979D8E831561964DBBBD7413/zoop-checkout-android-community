package com.zoop.checkout.app.Model;

import com.google.gson.annotations.SerializedName;

public class Marketplace {
    @SerializedName("id")
    private String id;

    @SerializedName("customer")
    private Seller customer;

    public Marketplace(String id, Seller customer) {
        this.id = id;
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Seller getSeller() {
        return customer;
    }

    public void setCustomer(Seller customer) {
        this.customer = customer;
    }
}
