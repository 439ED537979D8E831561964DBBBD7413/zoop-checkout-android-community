package com.zoop.checkout.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.zoop.zoopandroidsdk.commons.*;

import org.json.JSONObject;

/**
 * Created by mainente on 19/01/17.
 */

public class ChargeIntentIntegration {
    ChargeActivity activity;
    SendUrlResponse sendUrlResponse ;


    public ChargeIntentIntegration(ChargeActivity activity){
        this.activity=activity;
    }
    public void intentIntegrationFinishSuccessfully(JSONObject pjoTransactionResponse) {
        ZLog.t(300050);
        try {
            activity.setInfoSellerOriginalIntent();

            Intent intent = new Intent();
            intent.putExtra("user_message", "Transação realizada com sucesso");
            intent.putExtra("json_response", pjoTransactionResponse.toString(5));
            intent.putExtra("success", true);
            activity.setResult(0, intent);
            activity.finish();
        }
        catch (Exception e) {
            ZLog.exception(300051, e);
        }
    }
    public void intentIntegrationUrlFinishSuccessfully(JSONObject pjoTransactionResponse) {
        ZLog.t(300050);
        try {
            activity.setInfoSellerOriginalIntent();

            sendUrlResponse = new SendUrlResponse();
            sendUrlResponse.execute((pjoTransactionResponse.toString()));
        }
        catch (Exception e) {
            ZLog.exception(300051, e);
        }
    }
    public void intentIntegrationFinishUnsuccessful(String sUserMessage) {
        activity.setInfoSellerOriginalIntent();
        ZLog.t(300049, sUserMessage);
        Intent intent=new Intent();
        intent.putExtra("user_message", sUserMessage);
        intent.putExtra("success", false);
        activity.setResult(0, intent);
        activity.finish();
    }
    public void intentIntegrationUrlFinishUnsuccessful(String sUserMessage,String resultUrl) {
        activity.setInfoSellerOriginalIntent();

        sendUrlResponse = new SendUrlResponse();
        sendUrlResponse.execute(sUserMessage,resultUrl);
    }
    public void intentIntegrationUrlFinishUnsuccessful(JSONObject joResponse,String resultUrl) {
        activity.setInfoSellerOriginalIntent();

        sendUrlResponse = new SendUrlResponse();
        sendUrlResponse.execute(joResponse.toString(),resultUrl);
    }
    public void intentIntegrationFinishUnsuccessful(JSONObject joResponse) {
        ZLog.t(300048);
        Intent intent = new Intent();
        try {
            activity.setInfoSellerOriginalIntent();

            intent.putExtra("json_response", joResponse.toString(5));
            JSONObject joErrorDetails = joResponse.getJSONObject("error");
            intent.putExtra("user_message", joErrorDetails.getString("i18n_checkout_message"));
            intent.putExtra("response_code", joErrorDetails.getString("response_code"));
            intent.putExtra("i18n_response_message", joErrorDetails.getString("i18n_checkout_message"));
            intent.putExtra("i18n_response_message_explanation", joErrorDetails.getString("i18n_checkout_message_explanation"));
        }
        catch (Exception e) {
            ZLog.exception(300045, e);
        }
        intent.putExtra("success", false);
        activity.setResult(0
                , intent);
        activity.finish();
    }
    public class SendUrlResponse extends AsyncTask<String, Void, Boolean> {
        private String ticket;
        @Override
        protected Boolean doInBackground(String... params) {
           /* try {
                //xx
                String response=params[0];
                String sURL = params[1];
               // com.zoop.zoopandroidsdk.commons.ZoopSession.getInstance().getSynchronousRequest(sURL, null);
                return true;
            }catch (ZoopSessionHTTPJSONResponseException zhe) {
                ZLog.exception(300009, zhe);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
*/
            return false;
        }
        protected void onPostExecute(Boolean result) {
            activity.finish();
        }
    }
}
