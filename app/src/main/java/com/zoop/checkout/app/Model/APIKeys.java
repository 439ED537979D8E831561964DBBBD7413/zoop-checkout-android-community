package com.zoop.checkout.app.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class APIKeys {
    @SerializedName("items")
    private List<APIKey> apiKeys;

    public APIKeys(List<APIKey> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public List<APIKey> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(List<APIKey> apiKeys) {
        this.apiKeys = apiKeys;
    }
}
