package com.zoop.checkout.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zoop.zoopandroidsdk.ZoopAPI;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.ZLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by rodrigo on 10/08/16.
 */
public class MigrateVersionActivity extends LoadingActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_documents);

        View formPlan=(View)findViewById(R.id.formPlan);
        showProgress(true, formPlan, "Configurando versão. Aguarde...");
        mRecyclerView = (RecyclerView) findViewById(R.id.cardListDocuments);

        MigrateTask migrateTask = new MigrateTask();
        migrateTask.execute((Void) null);

    }

    public class MigrateTask extends AsyncTask<Void, Void, Boolean> {
        private String ticket;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Extras.getInstance().loginMigrate(MigrateVersionActivity.this);
                APIParameters ap = APIParameters.getInstance();
                String publishableKey = APIParameters.getInstance().getStringParameter("publishableKey");
                String sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
                String seller_id= APIParameters.getInstance().getStringParameter("sellerId");

                String sCheckoutPublicKey = Preferences.getInstance().loadUFUAndGetCheckoutPublicKey( ap.getGlobalStringParameter("currentLoggedinUsername"), null );
                APIParameters.getInstance().putGlobalStringParameter("sCheckoutPublicKey", sCheckoutPublicKey);
                if (null == APIParameters.getInstance().getStringParameter("plan")) {
                    Extras.fetchReceivingPlanFromServer(sMarketplaceId, seller_id, publishableKey, MigrateVersionActivity.this);
                }
             /*   if (null == APIParameters.getInstance().getStringParameter("joDocuments")) {
                    Extras.fetchReceivingDocumentsFromServer(sMarketplaceId, sCheckoutPublicKey,MigrateVersionActivity.this);
                }*/
            }
            catch (Exception e) {
                ZLog.exception(300068, e);
            }
            return false;
        }
        protected void onPostExecute(final Boolean result) {
            boolean bActivateWizardDuringStartup = APIParameters.getInstance().getBooleanParameter(APISettingsConstants.ZoopCheckout_ActivateWizardOnStartup, true);
            if (bActivateWizardDuringStartup) {
                Intent intent = new Intent(MigrateVersionActivity.this, WelcomeCheckoutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MigrateVersionActivity.this, ChargeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(intent, 0);
            }


        }
    }
}

