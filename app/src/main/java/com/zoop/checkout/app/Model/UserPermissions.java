package com.zoop.checkout.app.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserPermissions {
    @SerializedName("items")
    private List<Permission> permissions;

    public UserPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
