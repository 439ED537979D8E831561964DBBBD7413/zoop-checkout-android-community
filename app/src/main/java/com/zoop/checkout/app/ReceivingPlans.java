package com.zoop.checkout.app;

import com.zoop.zoopandroidsdk.commons.APIParameters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mainente on 22/05/15.
 */
public class ReceivingPlans {

    private JSONObject Seller;
    private JSONObject plan;
    private JSONObject planSubscription;

    private static ReceivingPlans instance = null;

    public JSONObject getPlan() {
        try {
            plan = new JSONObject( APIParameters.getInstance().getStringParameter("plan"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return plan;
    }

    public JSONObject getPlanSubscription() {

        try {
            planSubscription = new JSONObject( APIParameters.getInstance().getStringParameter("planSubscription"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return planSubscription;
    }

    private ReceivingPlans() {
        try {
            Seller=new JSONObject( APIParameters.getInstance().getStringParameter("Seller"));
            plan=new JSONObject( APIParameters.getInstance().getStringParameter("plan"));
            planSubscription=new JSONObject( APIParameters.getInstance().getStringParameter("planSubscription"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ReceivingPlans getInstance() {
        if (null == instance) {
            instance = new ReceivingPlans();
        }
        return instance;
    }

    public JSONObject getSeller() {
        try {
            Seller=new JSONObject( APIParameters.getInstance().getStringParameter("Seller"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Seller;
    }



}
