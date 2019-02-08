package com.zoop.checkout.app.Model;

/**
 * Created by mainente on 06/02/17.
 */

public class JsonTransactionNotPresent {

    String joTransaction;
    private static JsonTransactionNotPresent instance = null;


    public static JsonTransactionNotPresent getInstance() {
        if (null == instance) {
            instance = new JsonTransactionNotPresent();
        }
        return instance;
    }

    public String getJoTransaction() {
        return joTransaction;
    }

    public void setJoTransaction(String joTransaction) {
        this.joTransaction = joTransaction;
    }
}
