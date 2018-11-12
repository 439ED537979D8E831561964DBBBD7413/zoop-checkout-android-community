package com.zoop.checkout.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.zoop.zoopandroidsdk.ZoopAPI;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.Configuration;
import com.zoop.zoopandroidsdk.commons.UFUC;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopAPIDBOpenHelper;
import com.zoop.zoopandroidsdk.commons.ZoopSQLiteDatabase;
import com.zoop.zoopandroidsdk.commons.ZoopSessionHTTPJSONResponseException;
import com.zoop.zoopandroidsdk.sessions.Retrofit;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsCheckout;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class StartupActivity extends Activity {
    Preferences demoPreferences;

    private View mLoginStatusView;
    TextView mLoginStatusMessageView;
    String currentLoggedinSecurityToken = null;
    boolean bRunMigrationFromVersion1_7_b30106 = false;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.setDefaultUncaughtExceptionHandler(
                    new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread thread, Throwable ex) {
                            try {
                                Log.e("Zoop Checkout Lite", "677569", ex);
                                ZLog.initialize();
                                //ZoopAPIDBOpenHelper.setApplicationContext(getApplication());
                                ZLog.setLoggingDB(ZoopAPIDBOpenHelper.getInstance());
                                ZLog.exception(677489, ex);
                                System.exit(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("Zoop Checkout", "54674 - 677570", e);
        }

        try {
           // AWSMobileClient.getInstance().initialize(this).execute();

         //   java.util.logging.Logger.getLogger("com.amazonaws").setLevel(java.util.logging.Level.FINEST);

           /* PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    getApplicationContext(),
                    AWSMobileClient.getInstance().getCredentialsProvider(),
                    AWSMobileClient.getInstance().getConfiguration());

            pinpointManager = new PinpointManager(pinpointConfig);

            // Start a session with Pinpoint
            pinpointManager.getSessionClient().startSession();

            // Stop the session and submit the default app started event
            pinpointManager.getSessionClient().stopSession();
            pinpointManager.getAnalyticsClient().submitEvents();*/

            APIParameters ap = APIParameters.getInstance();
            ZoopAPI.resetApplicationContext(getApplicationContext());
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),    /* get the context for the application */
                    "us-east-1:a8709bfd-2788-4dba-8e65-ade2c7c14536",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            java.util.logging.Logger.getLogger("com.amazonaws").setLevel(Level.ALL);


            currentLoggedinSecurityToken = null;


            if ((ap.checkIfAPIParametersFullInitializationIsNeeded()) || (ap.getAndUpdateParameterBooleanTrueIfNewVersion("newVersion1.8_b30106"))) {
                bRunMigrationFromVersion1_7_b30106 = true;
                super.onCreate(savedInstanceState);


                setContentView(R.layout.activity_documents);
                View formPlan = (View) findViewById(R.id.formPlan);
                showProgress(true, formPlan, getResources().getString(R.string.initializing_checkout));

                StartupTask startupTask = new StartupTask();
                startupTask.execute((Void) null);
            } else {
                processMainStartupActivity();
                finishProcessingStartupActivity();
            }
        } catch (Exception e) {
            L.e("54673 Error during login onStart", e);
        }
    }

    public class StartupTask extends AsyncTask<Void, Void, Boolean> {
        private String ticket;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                processMainStartupActivity();
            } catch (Exception e) {
                ZLog.exception(300068, e);
                return false;
            }

            return true;
        }

        protected void onPostExecute(final Boolean result) {
            finishProcessingStartupActivity();
        }


    }

    public void processMainStartupActivity() {
        APIParameters ap = APIParameters.getInstance();
        try {
            ZoopAPI.basicInitialize(getApplication(), ApplicationConfiguration.APPLICATION_ID);
            // Check if a internal re-login flow is needed for users updating from 1.7_b30017 to new version 1.8_b30105 (or something)
            try {
                if (bRunMigrationFromVersion1_7_b30106) {
                    migrateLoginFromVersion1_7_b30017();
                }
            } catch (Exception e) {
                ZLog.exception("Exception at migrateLoginFromVersion1_7_b30017?", e);
            }


            try {
                String sPublishableKey = ap.getStringParameter("publishableKey");
                String sMarketplaceId = ap.getStringParameter("marketplaceId");
                String sSellerId = ap.getStringParameter("sellerId");
                try {
              /*      sPublishableKey = ZoopAPI.getInstance().getPublishableKey();
                    sMarketplaceId = ZoopAPI.getInstance().getMarketplaceId();
                    sSellerId = ZoopAPI.getInstance().getSellerId();
           */     } catch (Exception e) {
                    ZLog.t(e.toString());
                }

                if ((null != sMarketplaceId) && (null != sSellerId) && (null != sPublishableKey)) {
                    APIParameters.getInstance().processAPIParametersInitialization(sSellerId);

                    CallUpdateApiParameters.getInstance().initializeApiParameters(StartupActivity.this);

                    currentLoggedinSecurityToken = ap.getGlobalStringParameter("currentLoggedinSecurityToken");
                } else {
                    // ToDo: Analizar esse trecho: Já fizemos isso acima, incondicionalmente. Algo errado?
                    APIParameters.getInstance().processAPIParametersInitialization(null);
                }


            } catch (Exception e) {
                ZLog.exception(300058, e);
            }

//            TerminalListManager printerListManager = new TerminalListManager(null, ZoopAPI.getApplicationContext());
//            printerListManager.migratePreviousDeviceListAndSelectionStorageToNewZPTerm2(ap);

            Context context = getApplicationContext();
            //Preferences.initialize(context);
            demoPreferences = Preferences.getInstance();
        } catch (Exception e) {
            L.e("54672 Error during login onStart", e);
        }
    }


    public void finishProcessingStartupActivity() {
        APIParameters ap = APIParameters.getInstance();
        try {
            //xx12 Check if we will need time to process creation of APIParameters database
            if (null != currentLoggedinSecurityToken) {
                String sLoggedInUsername = ap.getGlobalStringParameter("currentLoggedinUsername");
                String sAdditionalLoginString = ap.getGlobalStringParameter(APISettingsConstants.ZoopCheckout_AdditionalLoggedInString);
                demoPreferences.loadUFUAndGetCheckoutPublicKey(sLoggedInUsername, sAdditionalLoginString);

                //				mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
                boolean bActivateWizardDuringStartup = ap.getBooleanParameter(APISettingsConstants.ZoopCheckout_ActivateWizardOnStartup, false);
                /*if (bActivateWizardDuringStartup) {
                    Intent intent = new Intent(StartupActivity.this, WelcomeCheckoutActivity.class);
                    startActivity(intent);
                } else {
                    // Migrate the plan configuration - 2 parameter variables are needed: sCheckoutPublicKey and plan. If they are not set, that's probably because the "new version" login was not made.
                    // So we need to force the activity to do the tasks needed to set  sCheckoutPublicKey and plan

                }*/

                if ((null == APIParameters.getInstance().getStringParameter("sCheckoutPublicKey")) || (null == APIParameters.getInstance().getStringParameter("plan"))) {
                    Intent migrateIntent = new Intent(StartupActivity.this, MigrateVersionActivity.class);
                    migrateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(migrateIntent);
                }
                // All set, just run normally.
                else {
                    if (bActivateWizardDuringStartup && !Extras.checkZoopTerminalsAndGoToNextStepStartupActivity(StartupActivity.this)) {
                        Intent intent = new Intent(StartupActivity.this, WelcomeCheckoutActivity.class);
                        startActivity(intent);
                    } else {

                        Intent intent = new Intent(StartupActivity.this, ChargeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    }
                }


            } else {
                Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //startActivityForResult(intent , 0);
            }


        } catch (Exception e) {
            L.e("54675 Error during login onStart", e);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View mLoginFormView, String msg) {


        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) mLoginStatusView.findViewById(R.id.login_status_message);

        mLoginStatusMessageView.setText(msg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginStatusView.setVisibility(show ? View.VISIBLE
                            : View.GONE);
                }
            });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE
                            : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public boolean migrateLoginFromVersion1_7_b30017() {

        try {
            APIParameters ap = APIParameters.getInstance();
            JSONObject joResponse = null;
            JSONObject joUser = null;

            String sUsername = "";
            String sPassword = "";
            SQLiteDatabase db = ZoopAPIDBOpenHelper.getInstance().getDB();
            ZoopSQLiteDatabase zdb = new ZoopSQLiteDatabase(db);

            // Update nas configurações de maquininhas para ser global
            db.execSQL("update ZoopParameters set setId = null where name like 'ZTL#%'");
            db.execSQL("update ZoopParameters set setId = null where name ='szt'");

            // Recupera dados do usuário se for um usuário único logado.
            // Se 2 usuários tiverem logano na app, terão que fazer login novamente.
            String[] columns = {"name, value, setId"};

            Cursor cParametersMatched = zdb.getDB().query("ZoopParameters", columns, "name in ('currentLoggedinUsername', 'currentLoggedinSecurityToken')", null, null, null, "setId desc, name");
            cParametersMatched.moveToFirst();
            if (2 == cParametersMatched.getCount()) {
                for (int i = 0; i < cParametersMatched.getCount(); i++) {
                    ZLog.t("Param name  = " + cParametersMatched.getString(0));
                    ZLog.t("Param value = " + cParametersMatched.getString(1));
                    if (0 == cParametersMatched.getString(0).compareTo("currentLoggedinUsername")) {
                        sUsername = cParametersMatched.getString(1);
                    }
                    if (0 == cParametersMatched.getString(0).compareTo("currentLoggedinSecurityToken")) {
                        sPassword = cParametersMatched.getString(1);
                    }
                    cParametersMatched.moveToNext();
                }
                cParametersMatched.close();
            }


            Map<String, String> requestParams = new HashMap<>();
            String sCheckoutPublicKey = ApplicationConfiguration.CHECKOUT_API_PRODUCTION_PUBLIC_KEY;
            String sMarketplaceId = null;
            JSONObject joContent = null;
            JSONObject joMerchant = null;
            String publishableKey = null;
            String sFirstName = null;
            String slastName = null;
            String merchant = null;
            String sSellerId = null;
            String phoneNumber = null;
            try {
                String sURL;
                if (ApplicationConfiguration.LOGIN_API.equals("Checkout")) {
                    requestParams.put("email", sUsername);
                    requestParams.put("password", sPassword);
                    sURL ="https://api.zoopcheckout.com/v1/sessions";
                    joResponse = ZoopSessionsCheckout.getInstance().syncPost(sURL, sCheckoutPublicKey, requestParams,StartupActivity.this);
                } else if (ApplicationConfiguration.LOGIN_API.equals("Compufour")) {
                    sURL = "https://app.compufacil.com.br/rpc/v1/application.authenticate.json";
                    String baseUrl="https://app.compufacil.com.br";

                    requestParams.put("login", sUsername);
                    requestParams.put("password", sPassword);
                    joResponse = Retrofit.getInstance().syncPost(sURL, baseUrl, sCheckoutPublicKey, requestParams,StartupActivity.this,null);

                }
            } catch (ZoopSessionHTTPJSONResponseException zhe) {
                ZLog.exception(300009, zhe);
                //{"status":false,"message":"O usuário não foi encontrado.","code":401}
                if (zhe.getJSONOutputObject().has("message")) {
                } else {
                }
                return false;
            } catch (Exception e) {
                ZLog.exception(300010, e);
                return false;
            }
            if (joResponse.getString("status").equals("false") || joResponse.getString("status").equals("0")) {
                return false;
            } else {
                try {
                    if (ApplicationConfiguration.LOGIN_API.equals("Checkout")) {

                        sMarketplaceId = joResponse.getJSONObject("content").getJSONArray("marketplace").getString(0);
                        joContent = joResponse.getJSONObject("content");
                        joUser = joContent.getJSONObject("user");
                        joMerchant = joUser.getJSONObject("merchant");
                        publishableKey = joContent.getJSONArray("publishable_key").getString(0);
                        sFirstName = joUser.getString("firstName");
                        slastName = joUser.getString("lastName");
                        merchant = joResponse.getJSONObject("content").getString("merchant");
                        sSellerId = joMerchant.getString("sellerId");
                        phoneNumber = joUser.getString("phone").substring(1, 3);
                    } else if (ApplicationConfiguration.LOGIN_API.equals("Compufour")) {
                        sMarketplaceId = "0569cf0b4bc84479bb1835d1c4a62858";
                        publishableKey = joResponse.getJSONObject("remote_payment_source").getString("publishable_key");
                        sSellerId = joResponse.getJSONObject("remote_payment_source").getString("seller_id");

                    }


                    ZoopAPI.initialize(getApplication(), sMarketplaceId, sSellerId, publishableKey);

                    String regexUsersToRemoveInstallments = ap.getStringParameter(APISettingsConstants.PaymentType_UsernamesToRemoveRegex);
                    if (true == APIParameters.getInstance().getBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, true)) {
                        if (sUsername.matches(regexUsersToRemoveInstallments)) {
                            ap.putBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, false);
                        } else {
                            ap.putBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, true);
                        }
                    }

                    // Get the first seller in the list.
                    // For simple testers, there will always be a seller selected.
                    //sellerId = "50b44c1890f44de58a76c722b37e4362"; // U Street Café
                    //Zoop Seller ID produção - sellerId = "6fbf7ec8fa7c4429a3b03eb85dd43364";
                    String sSellersURL = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/sellers/" + sSellerId);
                    JSONObject joSeller = ZoopSessionsPayments.getInstance().syncGet(sSellersURL, publishableKey,StartupActivity.this);
                    if (Configuration.DEBUG_MODE) {
                        ap.dumpAPISettings();
                    }
                    try {
                        if (ApplicationConfiguration.LOGIN_API.equals("Checkout")) {
                            ap.putStringParameter("merchant", merchant);
                            ap.putGlobalStringParameter("merchant", merchant);


                        } else if (ApplicationConfiguration.LOGIN_API.equals("Compufour")) {
                            if (joSeller.getString("type").equals("business")) {
                                sFirstName = joSeller.getJSONObject("owner").getString("first_name");
                                slastName = joSeller.getJSONObject("owner").getString("last_name");
                                phoneNumber = joSeller.getJSONObject("owner").getString("phone_number").substring(1, 3);
                            } else if (joSeller.getString("type").equals("individual")) {
                                sFirstName = joSeller.getString("first_name");
                                slastName = joSeller.getString("last_name");
                                phoneNumber = joSeller.getString("phone_number").substring(1, 3);
                            }

                        }

                        ap.putStringParameter("publishableKey", publishableKey);
                        ap.putStringParameter("sCheckoutPublicKey", sCheckoutPublicKey);
                        ap.putStringParameter("marketplaceId", sMarketplaceId);
                        ap.putStringParameter("phoneddd", phoneNumber);
                        ap.putStringParameter("currentLoggedinUsername", sUsername);
                        ap.putStringParameter("currentLoggedinSecurityToken", sPassword);
                        ap.putStringParameter("firstname", sFirstName);
                        ap.putStringParameter("lastname", slastName);
                        ap.putStringParameter("Seller", joSeller.toString());
                        ap.putStringParameter("sellerId", sSellerId);

                        ap.putStringParameter("publishableKey", publishableKey);
                        ap.putGlobalStringParameter("publishableKey", publishableKey);
                        ap.putGlobalStringParameter("sCheckoutPublicKey", sCheckoutPublicKey);
                        ap.putGlobalStringParameter("marketplaceId", sMarketplaceId);
                        ap.putGlobalStringParameter("phoneddd", phoneNumber);
                        ap.putGlobalStringParameter("firstname", sFirstName);
                        ap.putGlobalStringParameter("lastname", slastName);
                        ap.putGlobalStringParameter("Seller", joSeller.toString());
                        ap.putGlobalStringParameter("sellerId", sSellerId);

                        ap.putGlobalStringParameter("currentLoggedinUsername", sUsername);
                        ap.putGlobalStringParameter("currentLoggedinSecurityToken", sPassword);
                        try {
                            CallUpdateApiParameters.getInstance().initializeApiParameters(StartupActivity.this);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String statusJoSeller = joSeller.getString("status");
                        if ((statusJoSeller.equals("active")) || (statusJoSeller.equals("enabled"))) {
                            ap.putBooleanParameter("seller_activate", true);
                        } else {
                            ap.putBooleanParameter("seller_activate", false);

                        }
                        String sSellerStatus = joSeller.getString("status");
                        String sSellerStatusRegex = ap.getStringParameter(APISettingsConstants.ZoopCheckout_RegexAcceptedSellerStatuses);
                        Preferences.getInstance().setApplicationSellerAttributes(joSeller);
                        if (sSellerStatus.matches(sSellerStatusRegex)) {
                        } else {
                            // ToDo: Add message retrieved from server
                            ChargeActivity.addMessageUponLogin(ChargeActivity.SHOW_MESSAGE_SELLER_IS_NOT_READY_FOR_CHARGING_CUSTOMERS);
                        }
                        String sPlanURL = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/plans");
                        JSONObject joPlan= ZoopSessionsPayments.getInstance().syncGet(sPlanURL, publishableKey,StartupActivity.this);
                        String sPlanSubscriptionsUrl = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/sellers/" + sSellerId + "/subscriptions");
                        JSONObject joPlanSubscriptions= ZoopSessionsPayments.getInstance().syncGet(sPlanSubscriptionsUrl, publishableKey,StartupActivity.this);
                        ap.putStringParameter("plan", joPlan.toString());
                        if (joPlanSubscriptions.getJSONArray("items").length() < 1) {
                            int length = joPlan.getJSONArray("items").length();
                            for (int i = 0; i < length; i++) {
                                if (joPlan.getJSONArray("items").getJSONObject(i).getString("name").equals("Plano Standard")) {
                                    ap.putStringParameter("planSubscription", joPlan.getJSONArray("items").getJSONObject(i).toString());
                                    ap.putStringParameter("planSubscriptionId", null);
                                }
                            }
                        } else {
                            ap.putStringParameter("planSubscription", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getJSONObject("plan").toString());
                            ap.putStringParameter("planSubscriptionId", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getString("id"));
                        }
                    } catch (Exception e) {
                        ZLog.exception(677292, e);
                    }
                } catch (Exception e) {
                    ZLog.exception(300033, e);
                    return false;
                }
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }
}
