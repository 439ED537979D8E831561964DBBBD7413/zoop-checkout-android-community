package com.zoop.checkout.app;

import java.util.StringTokenizer;

/**
 * Created by mainente on 18/03/15.
 */
public class BluetoothInfo {
    private String name_dispo;

    private String address;
    private Integer state;

    public String getName_dispo() {
        return name_dispo;
    }

    public void setName_dispo(String name_dispo) {
        this.name_dispo = name_dispo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

}
