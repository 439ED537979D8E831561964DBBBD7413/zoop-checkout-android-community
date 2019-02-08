package com.zoop.checkout.app.Model;

import java.math.BigDecimal;

/**
 * Created by mainente on 02/01/17.
 */

public class AssociateToken {


    String token;
    String Customer;
    int value;
    int installmentOptions;
    String installmentOptionstext;

    private static AssociateToken instance = null;


    public static AssociateToken getInstance() {
        if (null == instance) {
            instance = new AssociateToken();
        }
        return instance;
    }

    public static void setInstance(AssociateToken instance) {
        AssociateToken.instance = instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getInstallmentOptions() {
        return installmentOptions;
    }

    public void setInstallmentOptions(int installmentOptions) {
        this.installmentOptions = installmentOptions;
    }

    public String getInstallmentOptionstext() {
        return installmentOptionstext;
    }

    public void setInstallmentOptionstext(String installmentOptionstext) {
        this.installmentOptionstext = installmentOptionstext;
    }
}
