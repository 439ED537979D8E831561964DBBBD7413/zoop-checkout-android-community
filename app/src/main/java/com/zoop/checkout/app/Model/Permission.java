package com.zoop.checkout.app.Model;

import com.google.gson.annotations.SerializedName;

public class Permission {
    @SerializedName("marketplace_id")
    private String marketplaceId;

    @SerializedName("customer_id")
    private String sellerId;

    public Permission(String marketplaceId, String sellerId) {
        this.marketplaceId = marketplaceId;
        this.sellerId = sellerId;
    }

    public String getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
