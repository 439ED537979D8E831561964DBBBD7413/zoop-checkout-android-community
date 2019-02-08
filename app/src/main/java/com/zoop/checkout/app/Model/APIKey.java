package com.zoop.checkout.app.Model;

import com.google.gson.annotations.SerializedName;

public class APIKey {
    @SerializedName("publishable_production_key")
    private String publishableKey;

    public APIKey(String publishableKey) {
        this.publishableKey = publishableKey;
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    public void setPublishableKey(String publishableKey) {
        this.publishableKey = publishableKey;
    }
}
