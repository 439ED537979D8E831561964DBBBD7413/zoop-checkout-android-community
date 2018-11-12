package com.zoop.checkout.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.UFUC;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopSessionHTTPJSONResponseException;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mainente on 19/01/17.
 */

public class ChangePlanTransaction {
    private String old_plan=null;
    private Activity a;

    public ChangePlanTransaction(String old_plan, Activity a){
        this.old_plan=old_plan;
        this.a=a;
    }
    public void changePlan(){
        ChangePlan aChangePlan = new ChangePlan();
        aChangePlan.execute((Void) null);
    }
    public class ChangePlan extends AsyncTask<Void, Void, Boolean> {
        private String ticket;
        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject joUser = null;
            JSONObject joResponse = null;
            try {
                String publishableKey =  APIParameters.getInstance().getStringParameter("publishableKey");
                String sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
                String seller_id = APIParameters.getInstance().getStringParameter("sellerId");
                String sCheckoutPublicKey= APIParameters.getInstance().getStringParameter("sCheckoutPublicKey");
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("wished_plan",old_plan);
                requestParams.put("zoop_publishable_key",publishableKey);
                //   requestParams.put("laravel_session",cookie);
                String planSubscriptionId=  APIParameters.getInstance().getStringParameter("planSubscriptionId");
                if(planSubscriptionId!=null) {
                    String sPlanSubscriptionChangesUrlDelete = UFUC.getUFU("https://portal.pagzoop.com/api/v1/payments_api/marketplaces/" + sMarketplaceId + "/seller/" + seller_id + "/subscriptions/" + planSubscriptionId);
                    JSONObject JoDelete;

                    //  ZoopSession.getInstance().deleteSynchronousRESTRequest(sPlanSubscriptionChangesUrlDelete, sCheckoutPublicKey, null, "laravel_session=" + cookie);
                    Map<String, String>  requestParamsDelete = new HashMap<>();
                    requestParamsDelete.put("zoop_publishable_key", publishableKey);
                    JoDelete = ZoopSessionsPayments.getInstance().syncDeleteWithCookie( sPlanSubscriptionChangesUrlDelete, sCheckoutPublicKey, requestParamsDelete, a);
                }
                String sPlanSubscriptionChangesUrl = UFUC.getUFU("https://portal.pagzoop.com/api/v1/payments_api/marketplaces/" + sMarketplaceId + "/seller/" + seller_id + "/subscriptions");
                joResponse=ZoopSessionsPayments.getInstance().syncPostWithCookie(sPlanSubscriptionChangesUrl, sCheckoutPublicKey, requestParams, a);
                JSONObject joPlan=APIParametersCheckout.getInstance().getPlan();
                int length=joPlan.getJSONArray("items").length();
                for(int i=0;i<length;i++){
                    if(joPlan.getJSONArray("items").getJSONObject(i).getString("id").equals(old_plan)){
                        APIParameters.getInstance().putStringParameter("planSubscription", joPlan.getJSONArray("items").getJSONObject(i).toString());
                        //  APIParameters.getInstance().putStringParameter("planSubscriptionId", joPlan.getJSONArray("items").getJSONObject(i).getString("id"));
                    }
                }
                APIParameters.getInstance().putStringParameter("planSubscriptionId", joResponse.getJSONObject("message").getString("resource"));
                return true;
            }catch (ZoopSessionHTTPJSONResponseException zhe) {
                ZLog.exception(300009, zhe);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return false;
        }
        protected void  onPostExecute(final Boolean result) {
            if (result) {
                Toast.makeText(a, "Sua conta retornou ao seu plano original",
                        Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(a, "Erro ao retornar ao seu plano original",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
