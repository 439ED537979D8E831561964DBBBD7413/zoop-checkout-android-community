package com.zoop.checkout.app;

import com.zoop.checkout.app.Model.SellerSelected;
import com.zoop.zoopandroidsdk.commons.APIParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mainente on 22/05/15.
 */
public class APIParametersCheckout   {

    private JSONObject Seller;
    private JSONObject plan;
    private JSONObject planSubscription;





    private static APIParametersCheckout instance = null;

    public JSONObject getPlan() {
        try {
            plan = new JSONObject( APIParameters.getInstance().getStringParameter("plan"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return plan;
    }

    public JSONObject getPlanSubscription()  {

        try {
            JSONObject joPlan=getPlan();
            int length=joPlan.getJSONArray("items").length();

            String idPlan= APIParameters.getInstance().getStringParameter("planSubscriptionId");
            for(int i=0;i<length;i++){
                if(joPlan.getJSONArray("items").getJSONObject(i).getString("id").equals(idPlan)){
                    planSubscription=joPlan.getJSONArray("items").getJSONObject(i);
                }
            }
            if (planSubscription == null) {
                JSONObject joPlanSubscriptions = new JSONObject(APIParameters.getInstance().getStringParameter("planSubscriptions"));
                length = joPlanSubscriptions.getJSONArray("items").length();
                for(int i=0;i<length;i++){
                    if(joPlanSubscriptions.getJSONArray("items").getJSONObject(i).getString("id").equals(idPlan)){
                        planSubscription=joPlanSubscriptions.getJSONArray("items").getJSONObject(i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return planSubscription;
    }


    private APIParametersCheckout() {


        try {
            Seller = new JSONObject( APIParameters.getInstance().getStringParameter("Seller"));
            plan = new JSONObject( APIParameters.getInstance().getStringParameter("plan"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public static APIParametersCheckout getInstance() {
        if (null == instance) {

            try {
                instance = new APIParametersCheckout();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        

        return instance;
    }

    public JSONObject getSeller()  {
        try {
            Seller = new JSONObject( APIParameters.getInstance().getStringParameter("Seller"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Seller;
    }



}
