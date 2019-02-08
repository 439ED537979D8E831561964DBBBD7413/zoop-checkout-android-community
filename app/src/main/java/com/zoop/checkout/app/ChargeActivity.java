package com.zoop.checkout.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.zoop.zoopandroidsdk.ZoopAPI;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopAPIDBOpenHelper;
import com.zoop.zoopandroidsdk.terminal.ApplicationDisplayListener;
import com.zoop.zoopandroidsdk.terminal.ExtraCardInformationListener;
import com.zoop.zoopandroidsdk.TerminalListManager;
import com.zoop.zoopandroidsdk.terminal.TerminalMessageType;
import com.zoop.zoopandroidsdk.ZoopTerminalPayment;
import com.zoop.zoopandroidsdk.terminal.TerminalPaymentListener;
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
public class ChargeActivity extends ZCLMenuActivity implements TerminalPaymentListener, DeviceSelectionListener, ExtraCardInformationListener, ApplicationDisplayListener, DialogInterface.OnCancelListener,
        DialogInterface.OnClickListener {
    public static ZoopTerminalPayment terminalPayment = null;
    public static int selectedTerminal = 0;
    public static final int CAPTURE_CARDHOLDER_SIGNATURE = 1;
    public static final int CHANGE_PLAN = 4;
    int selectedPaymentOption = -1;
    int flipCurrentView = -1;
    public static final int CHANGE_PLAN_TRANSACTION = 3;
    public static final int INTERFACE_CAPTURE_PAYMENT_TOTAL = 0;
    JSONArray jSubMenu;
    int selectedChargeTypeIndex = -1;
    BigDecimal valueToCharge = null;
    int numberOfInstallments = 2;
    Integer idPaymentOption;
    public ArrayList<Integer> icon;
    JSONObject joTransactionResponse = null;
    boolean bCaptureSignatureRequested = false;
    String resultUrl=null;
    JSONArray jMenu;
    FlipperInstallmentOptions flipperInstallmentOptions;
    private boolean bActivityCalledForIntentIntegration = false;
    private boolean bActivityCalledForIntentIntegrationUri = false;
    String loginErrorMessage = "";
    Integer number_installments=0;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int iDialogsUponLoginCounter = 0;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private LinearLayout menuL;
    private String[] mZoopOptions;
    private ExpandableListView expandableListView;
    private List<String> listGroup;
    private HashMap<String, List<String>> listData;
    int back=1;
    private Integer PositionValidatePassword;
    private JSONObject intentIntegrationSuccessfulTransactionResponse = null;
    private JSONObject intentIntegrationUrlSuccessfulTransactionResponse = null;
    String sellerIdOriginalIntent;
    String markeplaceIdOriginalIntent;
    String publishablekeyOriginalIntent;
    Uri uri ;
    String old_plan=null;
    ChargeIntentIntegration chargeIntentIntegration;
    @Override
    protected void onStop() {
        super.onStop();
        back = 1;
    }

    @Override
    public void onBackPressed() {
        if(back<=1){
            back++;
            Toast.makeText(this,"Clique novamente em voltar para sair da aplicação",Toast.LENGTH_LONG).show();
        }else {
            if (bActivityCalledForIntentIntegration) {
                chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Operação cancelada pelo usuário");
            }
            try {
                if (R.layout.flipper_charge_status == flipCurrentView) {
                    //  terminalPayment.requestAbortCharge();
                }
                changePlan();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onBackPressed();
        }
    }
    public void onDestroy(){
        if (bActivityCalledForIntentIntegration){
            chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Operação cancelada pelo usuário");
        }
        try {
            changePlan();
        } catch (Exception e) {
            e.printStackTrace();
        }
        back=1;
        super.onDestroy();
    }

    public void setPositionValidatePassword(Integer positionValidatePassword) {
        PositionValidatePassword = positionValidatePassword;
    }

    public Integer getPositionValidatePassword() {
        return PositionValidatePassword;
    }

    public ExpandableListView getExpandableListView() {
        return expandableListView;
    }

    public JSONArray getjMenu() {
        return jMenu;
    }

    LinearLayout lnBrand;
    Bundle params;
    String marketplaceId;
    String sellerId;
    String publishableKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String resultUrl=null;
        Intent intent = getIntent();
        String action = getIntent().getAction();

        String androidId = Settings.Secure.getString(ZoopAPI.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        params = intent.getExtras();
        if (params != null&& !Intent.ACTION_VIEW.equals(action)) {
            bActivityCalledForIntentIntegration = true;
            try {
                try {
                    Thread.setDefaultUncaughtExceptionHandler(
                            new Thread.UncaughtExceptionHandler() {

                                @Override
                                public void uncaughtException(Thread thread, Throwable ex) {
                                    try {
                                        ZLog.initialize();
                                        ZLog.setLoggingDB(ZoopAPIDBOpenHelper.getInstance());
                                        ZLog.exception(677489, ex);
                                        System.exit(1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
                catch (Exception e) {

                }
                ZoopAPI.basicInitialize(getApplication(), ApplicationConfiguration.APPLICATION_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

            HintedImageButton Config=(HintedImageButton)findViewById(R.id.imageViewConfigurations);
            Config.setVisibility(View.GONE);
            HintedImageButton Marketplace=(HintedImageButton)findViewById(R.id.imageViewTransactionsHistory);
            Marketplace.setVisibility(View.GONE);
            HintedImageButton Logout=(HintedImageButton)findViewById(R.id.imageViewLockIcon);
            Logout.setVisibility(View.GONE);
            TerminalListManager terminalListManager=new TerminalListManager(this,this);
            marketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
            sellerId = APIParameters.getInstance().getStringParameter("sellerId");
            publishableKey = APIParameters.getInstance().getStringParameter("publishableKey");
            if(!params.getString("seller_id").equals("")) {
                marketplaceId = params.getString("marketplace_id");
                sellerId = params.getString("seller_id");
                publishableKey = params.getString("publishable_key");
                try {
                    JSONObject joSelectedTerminal = TerminalListManager.getCurrentSelectedZoopTerminal();
                    ZoopAPI.initialize(getApplication());
                    terminalListManager.requestZoopDeviceSelection(joSelectedTerminal);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                APIParameters.getInstance().putStringParameter("publishableKey", publishableKey);
                APIParameters.getInstance().putStringParameter("marketplaceId", marketplaceId);
                APIParameters.getInstance().putStringParameter("sellerId", sellerId);
            }


        }else if(Intent.ACTION_VIEW.equals(action)){
            try {
                Thread.setDefaultUncaughtExceptionHandler(
                        new Thread.UncaughtExceptionHandler() {

                            @Override
                            public void uncaughtException(Thread thread, Throwable ex) {
                                try {
                                    ZLog.initialize();
                                    ZLog.setLoggingDB(ZoopAPIDBOpenHelper.getInstance());
                                    ZLog.exception(677489, ex);
                                    System.exit(1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                ZoopAPI.basicInitialize(getApplication(), ApplicationConfiguration.APPLICATION_ID);
            }
            catch (Exception e) {

            }
            uri = getIntent().getData();
            bActivityCalledForIntentIntegrationUri = true;
            HintedImageButton Config=(HintedImageButton)findViewById(R.id.imageViewConfigurations);
            Config.setVisibility(View.GONE);
            HintedImageButton Marketplace=(HintedImageButton)findViewById(R.id.imageViewTransactionsHistory);
            Marketplace.setVisibility(View.GONE);
            HintedImageButton Logout=(HintedImageButton)findViewById(R.id.imageViewLockIcon);
            Logout.setVisibility(View.GONE);
            marketplaceId= uri.getQueryParameter("marketplace_id");
            sellerId= uri.getQueryParameter("seller_id");
            publishableKey=uri.getQueryParameter("publishable_key");
            resultUrl=uri.getQueryParameter("resultURL");
            APIParameters.getInstance().putStringParameter("publishableKey", publishableKey);
            APIParameters.getInstance().putStringParameter("marketplaceId", marketplaceId);
            APIParameters.getInstance().putStringParameter("sellerId",sellerId);
        }
        else {
            marketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
            sellerId = APIParameters.getInstance().getStringParameter("sellerId");
            publishableKey = APIParameters.getInstance().getStringParameter("publishableKey");
        }

        /**
         * Developer options in the Zoop Android SDK API Demo is used to show, for instance, the option to allow user to select terminal
         * to be used: Zoop Virtual Terminal - for developers without the terminal - or Zoop Terminal, among other options
         */
        setContentView(R.layout.activity_charge);
        chargeIntentIntegration=new ChargeIntentIntegration(this);
        mTitle = mDrawerTitle = "Zoop";
        mZoopOptions = getResources().getStringArray(R.array.ZoopOptions_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        buildList();

        menuL=(LinearLayout) findViewById(R.id.LinearMenu);
        TextView menuUsername=(TextView)findViewById(R.id.nameusermenu);
        menuUsername.setText( APIParameters.getInstance().getStringParameter("firstname")+ " "+ APIParameters.getInstance().getStringParameter("lastname"));
        TextView menuUseremail=(TextView)findViewById(R.id.emailusermenu);
        menuUseremail.setText(APIParameters.getInstance().getStringParameter("currentLoggedinUsername"));
        expandableListView = (ExpandableListView) findViewById(R.id.left_drawer);
        mZoopOptions = getResources().getStringArray(R.array.ZoopOptions_array);
        OptionsMenu Menu = new OptionsMenu();
        expandableListView.setAdapter(new ExpandableAdapter(ChargeActivity.this, listGroup, listData, icon));
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                try {
                    JSONArray jSubMenu=jMenu.getJSONObject(groupPosition).getJSONArray("submenu");
                    actionMenu(jSubMenu.getJSONObject(childPosition).getString("action"),jSubMenu.getJSONObject(childPosition).getString("parameter"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                try {
                    if (jMenu.getJSONObject(groupPosition).getString("title").equals("Configurações")) {
                        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("settings_navbar_open", null);
                    }
                    if (jMenu.getJSONObject(groupPosition).getString("title").equals("Suporte")) {
                        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("support_navbar_open", null);
                    }
                    if(jMenu.getJSONObject(groupPosition).has("action")) {
                        actionMenu(jMenu.getJSONObject(groupPosition).getString("action"), jMenu.getJSONObject(groupPosition).getString("parameter"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener(){
            @Override
            public void onGroupCollapse(int groupPosition) {
                try {
                    if (jMenu.getJSONObject(groupPosition).getString("title").equals("Configurações")) {
                        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("settings_navbar_close", null);
                    }
                    if (jMenu.getJSONObject(groupPosition).getString("title").equals("Suporte")) {
                        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("support_navbar_close", null);
                    }
                    if(jMenu.getJSONObject(groupPosition).has("action")) {
                        if(!jMenu.getJSONObject(groupPosition).getString("parameter").equals("checkPassword")) {
                            actionMenu(jMenu.getJSONObject(groupPosition).getString("action"), jMenu.getJSONObject(groupPosition).getString("parameter"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */

        )
        {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        ImageView ivConfigurations = (ImageView) findViewById(R.id.imageViewConfigurations);
        ivConfigurations.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("gear_topbar_close", null);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("gear_topbar_open", null);
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });
        if (savedInstanceState == null) {
            // selectItem(0);
        }
        lnBrand=(LinearLayout)findViewById(R.id.lnbrand);
        lnBrand.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckoutApplication.getFirebaseAnalytics().logEvent("brands_show", null);
                FragmentManager fragmentManager = getFragmentManager();

                DialogCardBrandDetail dialog = new DialogCardBrandDetail();
                dialog.setPositionBasedOnView(v);


                dialog.show(fragmentManager, "dialog");
            }
        });
        Button buttonCaptureSignature = (Button) findViewById(R.id.buttonCaptureSignature);
        ((View) findViewById(R.id.layoutButtonsIfTransactionApproved)).setVisibility(View.GONE);
        ((View) findViewById(R.id.layoutButtonsNewTransaction2)).setVisibility(View.GONE);
        buttonCaptureSignature = (Button) findViewById(R.id.buttonCaptureSignature);
        buttonCaptureSignature.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    Intent signatureIntent = new Intent(ChargeActivity.this, CaptureSignatureActivity.class);
                    Bundle b = new Bundle();
                    b.putString("transactionJSON", joTransactionResponse.toString());
                    signatureIntent.putExtras(b);
                    startActivityForResult(signatureIntent, CAPTURE_CARDHOLDER_SIGNATURE);
                }catch (Exception e){

                }
            }
        });
        try {
            terminalPayment = new ZoopTerminalPayment();
            terminalPayment.setTerminalPaymentListener(ChargeActivity.this);
            terminalPayment.setDeviceSelectionListener(ChargeActivity.this);
            terminalPayment.setExtraCardInformationListener(ChargeActivity.this);
            terminalPayment.setApplicationDisplayListener(ChargeActivity.this);
        }
        catch (Exception e) {
            L.e("Error instantiating TerminalPayment", e);
        }
        ((View) findViewById(R.id.bannerZoop)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ZLog.t(677275);
            }
        });
        FlipperKeypad flipperKeypad = new FlipperKeypad();
        flipperKeypad.initializeHistory();
        flipperKeypad.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
        flipperKeypad.flipToPane(this);
        flipCurrentView = R.layout.flipper_keypad;
        String password = APIParameters.getInstance().getStringParameter("currentLoggedinSecurityToken");
        if (bActivityCalledForIntentIntegration ||bActivityCalledForIntentIntegrationUri) {
            if((params.getString("seller_id").equals(""))&&(password==null)){
                chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Nenhum usuário logado");
            }else {
                chargeIntent();
            }

        }
        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (R.layout.flipper_keypad == flipCurrentView) {
                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("new_sale_entered_value", null);
                    FlipperPaymentOptions flipperPaymentOptions = new FlipperPaymentOptions(getValueForCharge());
                    flipperPaymentOptions.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
                    flipCurrentView = R.layout.flipper_payment_options;
                    flipperPaymentOptions.flipToPane(ChargeActivity.this);
                }
                else if (R.layout.flipper_payment_options == flipCurrentView) {
                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("new_sale_selected_payment_type", null);
                    if (ZoopTerminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS ==  selectedPaymentOption) {
                        ((Button) findViewById(R.id.buttonNext)).setText( getResources().getString(R.string.label_charge));
                        flipperInstallmentOptions = new FlipperInstallmentOptions();
                        TextView tvValue = (TextView) findViewById(R.id.editTextValueToCharge);
                        flipperInstallmentOptions.setTotalValue(getValueForCharge());
                        flipperInstallmentOptions.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
                        flipperInstallmentOptions.flipToPane(ChargeActivity.this);
                        flipCurrentView = R.layout.flipper_installment_options;
                    }
                    else {
                        FlipperChargeStatus flipperChargeStatus = new FlipperChargeStatus();
                        flipperChargeStatus.setSelectedPaymentOption(selectedPaymentOption);
                        flipperChargeStatus.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
                        flipperChargeStatus.flipToPane(ChargeActivity.this);
                        flipCurrentView = R.layout.flipper_charge_status;
                        processCharge();
                    }
                }
                else if (R.layout.flipper_installment_options == flipCurrentView) {
                    FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("new_sale_selected_installment", null);
                    FlipperChargeStatus flipperChargeStatus = new FlipperChargeStatus();
                    flipperChargeStatus.setSelectedPaymentOption(selectedPaymentOption);
                    flipperChargeStatus.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
                    flipperChargeStatus.flipToPane(ChargeActivity.this);
                    flipCurrentView = R.layout.flipper_charge_status;
                    processCharge();
                }
                else if (R.layout.flipper_charge_status == flipCurrentView) {
                    try {
                        terminalPayment.requestAbortCharge();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }
            }
        });
        TextView textViewValueToCharge = (TextView) findViewById(R.id.editTextValueToCharge);
        textViewValueToCharge.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (R.layout.flipper_charge_status != flipCurrentView) {
                    resetPaymentOptions();
                    ZoopFlipperPane.initializeHistory();
                    ZoopFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
                    flipCurrentView = R.layout.flipper_keypad;
                    FlipperKeypad flipperKeypad = new FlipperKeypad();
                    //flipperKeypad.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
                    flipperKeypad.flipToPane(ChargeActivity.this);
                    ((Button) findViewById(R.id.buttonNext)).setText( getResources().getString(R.string.label_next));
                }
            }
        });

        textViewValueToCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                BigDecimal valueToCharge = Extras.getInstance().getBigDecimalFromDecimalString(s.toString());
                if ((valueToCharge.compareTo(terminalPayment.getMinimumChargeValue())>=0) && (valueToCharge.compareTo(terminalPayment.getMaximumChargeValue())<=0)) {
                    ((Button) findViewById(R.id.buttonNext)).setEnabled(true);
                }
                else {
                    ((Button) findViewById(R.id.buttonNext)).setEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
        Button buttonNewTransaction2 = (Button) findViewById(R.id.buttonNewTransaction2);
        buttonNewTransaction2.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extras.startNewChargeActivity(ChargeActivity.this);
                Intent intent  = new Intent(ChargeActivity.this, ChargeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        showPertinentDialogsUponCreation();
        try {
            JSONObject joPlanSubscription=APIParametersCheckout.getInstance().getPlanSubscription();

            old_plan=joPlanSubscription.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("topbar_hamburguer_close", null);
            } else {
                FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("topbar_hamburguer_open", null);
            }
        }
        if (102 == id) {
            Preferences.getInstance().logout(this);
        }
        else if (103 == id) {
            Intent paymentsListIntent = new Intent(ChargeActivity.this, PaymentsListActivity.class);
            startActivity(paymentsListIntent );
        }
        else if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
    public void buildList(){
        listGroup = new ArrayList<String>();
        icon=new ArrayList<Integer>();
        listData = new HashMap<String, List<String>>();
        List<String> auxList = new ArrayList<String>();
        List<Integer> auxListchild = new ArrayList<Integer>();
        Boolean virtual=APIParameters.getInstance().getBooleanParameter("virtual_enable",false);
        String jsonMenu;
        if(virtual) {
            jsonMenu = getResources().getString(R.string.json_menu);
        }else {
            jsonMenu = getResources().getString(R.string.json_menu_without_virtual);
        }        try {
            jMenu=new JSONArray(jsonMenu);
            for(int i=0;i<jMenu.length();i++){
                String resource = "drawable";
                int id = getResources().getIdentifier(jMenu.getJSONObject(i).getString("icon"), resource, this.getPackageName());
                icon.add(id);
                listGroup.add(jMenu.getJSONObject(i).getString("title"));
                if(jMenu.getJSONObject(i).has("submenu")){
                    jSubMenu=jMenu.getJSONObject(i).getJSONArray("submenu");
                    auxListchild = new ArrayList<Integer>();
                    auxList = new ArrayList<String>();
                    for(int j=0;j<jSubMenu.length();j++){
                        int idchild = getResources().getIdentifier(jSubMenu.getJSONObject(j).getString("icon"), resource, "com.zoop.checkout.app");
                        auxList.add(jSubMenu.getJSONObject(j).getString("title"));
                        auxListchild.add(idchild);
                        listData.put(listGroup.get(i), auxList);
                    }
                }else {
                    auxList = new ArrayList<String>();
                    listData.put(listGroup.get(i), auxList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void chargeIntent(){

        if (bActivityCalledForIntentIntegration ||bActivityCalledForIntentIntegrationUri) {
            try{
                String paymentOption = null;
                String metadata=null;
                if(params.containsKey("metadata")){
                    try {


                        metadata=params.getString("metadata");
                        JSONObject joMetadata=new JSONObject(metadata);
                        terminalPayment = new ZoopTerminalPayment();
                        terminalPayment.setTerminalPaymentListener(ChargeActivity.this);
                        terminalPayment.setDeviceSelectionListener(ChargeActivity.this);
                        terminalPayment.setExtraCardInformationListener(ChargeActivity.this);
                        terminalPayment.setApplicationDisplayListener(ChargeActivity.this);
                        //terminalPayment.setTransactionMetadata(joMetadata);
                    }catch (Exception e){

                    }


                }

                if(bActivityCalledForIntentIntegration) {
                    valueToCharge = Extras.getInstance().getBigDecimalFromDecimalString(params.getString("value"));
                    paymentOption = params.getString("payment_type");
                }else if(bActivityCalledForIntentIntegrationUri) {

                    valueToCharge = Extras.getInstance().getBigDecimalFromDecimalString(uri.getQueryParameter("value"));
                    paymentOption = uri.getQueryParameter("payment_type");

                }
                if (valueToCharge.compareTo(new BigDecimal(1)) < 0) {
                    // ToDo: Add string with value substitution to String resource. Code properly @mainente
                    chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Valor da compra é inferior a R$1,00");
                }
                if ( ((paymentOption.equalsIgnoreCase("debit")) || (paymentOption.equalsIgnoreCase("credit")))) {

                    FlipperPaymentOptions flipperPaymentOptions = new FlipperPaymentOptions(valueToCharge);
                    flipperPaymentOptions.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
                    FlipperInstallmentOptions flipperInstallmentOptions = new FlipperInstallmentOptions();
                    idPaymentOption = null;
                    if (paymentOption.equalsIgnoreCase("debit")) {
                        idPaymentOption = 1;
                    } else if (paymentOption.equalsIgnoreCase("credit")) {
                        if (params.getInt("number_of_installments") > 0){
                            idPaymentOption = 2;
                            if(bActivityCalledForIntentIntegration) {

                                number_installments = params.getInt("number_of_installments");
                            }else if(bActivityCalledForIntentIntegrationUri){

                                number_installments = Integer.valueOf(uri.getQueryParameter("number_of_installments"));


                            }
                        }
                        else{
                            idPaymentOption = 0;
                        }
                    }

                    // ToDo: @mainente: Sempre usar as funções de formatação que estão no Extras. Por que? Porque em teoria estamos nos preocupando com o símbolo (R$, $, etc) e
                    // o separador de decimais.
                    // ToDo: Revisar nossa política de detecção de moéda e separador de decimais
                    String valueFormat="";
                    try{

                        valueFormat=Extras.getInstance().formatBigDecimalAsLocalMoneyString(valueToCharge);

                    }catch (Exception e){
                        ZLog.exception(1,e);

                    }
                    TextView tvValue = (TextView) findViewById(R.id.editTextValueToCharge);

                    tvValue.setText(valueFormat.replace("R$",""));
                    flipperInstallmentOptions.setTotalValue(valueToCharge);
                    FlipperChargeStatus flipperChargeStatus = new FlipperChargeStatus();
                    flipperChargeStatus.setSelectedPaymentOption(idPaymentOption);
                    flipperChargeStatus.setBaseView((ViewFlipper) findViewById(R.id.layoutFlipperPane));
                    flipperChargeStatus.flipToPane(ChargeActivity.this);
                    flipCurrentView = R.layout.flipper_charge_status;



                    if (number_installments>0) {
                        if ( (valueToCharge.divide(new BigDecimal(number_installments))).compareTo(new BigDecimal(5)) < 0) {
                            chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Valor da parcela é inferior a R$ 5,00");
                        }
                    }
                    processCharge();
                }else{
                    chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Forma de pagamento inválida");
                }
            }
            catch (Exception e) {
                chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Valor inválido");
            }
        }


    }


    public void actionMenu(String nameAction, String nameParameter) {

        if (nameParameter.equals("WalletActivity"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("CNP_navbar", null);

        if (nameParameter.equals("PaymentsListActivity"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("history_navbar", null);

        if (nameParameter.equals("ChargeActivity"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("new_sale_navbar", null);

        if (nameParameter.equals("ConfigPinPadActivity"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("mpos_navbar", null);

        if (nameParameter.equals("PaymentMethodsActivity"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("settings_payment_methods_navbar", null);

        if (nameParameter.equals("ConfigPrinterActivity"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("settings_printer_navbar", null);

        if (nameParameter.equals("enablePassword"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("settings_protect_navbar", null);

        if (nameParameter.equals("WebViewActivity"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("dashboard_navbar", null);

        if (nameParameter.equals("FAQActivity"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("support_faq_navbar", null);

        if (nameParameter.equals("showAboutBox"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("support_about_navbar", null);

        if (nameParameter.equals("logoutCurrentUser"))
            FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("logout_navbar", null);

        ZLog.warning(300077, nameAction, 0, nameParameter);
        if(nameAction.equals("intent")){
            try {
                Class<?> classname = Class.forName("com.zoop.checkout.app."+nameParameter);
                Intent intent = new Intent(ChargeActivity.this,classname );
                if(nameParameter.equals("ChargeActivity")) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                if(nameParameter.equals("PlansActivity")) {
                    startActivityForResult(intent, CHANGE_PLAN);
                }else {
                    startActivity(intent);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }else if(nameAction.equals("method")){
            try {
                MenuMethods instance = new MenuMethods();
                Class<?> clazz = instance.getClass();
                Method method = clazz.getMethod(nameParameter, ChargeActivity.class);
                method.invoke(instance,this);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /* The click listner for ListView in the navigation drawer */
    @Override
    public void paymentAborted() {
        Bundle transactionBundle = new Bundle();
        transactionBundle.putString("new_sale_status", "aborted");
        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("new_sale_transaction", transactionBundle);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    changePlan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bActivityCalledForIntentIntegration) {
                    chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Operação cancelada");
                }
                if (bActivityCalledForIntentIntegrationUri) {
                    chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Operação cancelada");
                }
                ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_denied));
                ((View) findViewById(R.id.layoutButtonsNewTransaction2)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.buttonNext)).setVisibility(View.GONE);
                TextView textViewMessageExplanationText = (TextView) findViewById(R.id.textViewMessageExplanationText);
                textViewMessageExplanationText.setVisibility(View.GONE);
                String applicationMessage = "Operação cancelada pelo usuário";
                ZLog.t(300018, applicationMessage);
                displayApplicationStatus(applicationMessage);


            }
        });
    }

    /**
     * #ZoopAPICall: Implements the application response to a notification of a failed payment. This method returns a "user friendly message" (sUserMessage) and the JSONObject as documented
     * in the Zoop developer documentation CP Transactions (Card Present transactions)
     * Link to reference (2014-04-17): https://pagzoop.com/api/docs/#create-cp-transactions
     */
    @Override
    public void paymentFailed(final JSONObject joResponse) {
        Bundle transactionBundle = new Bundle();
        transactionBundle.putString("new_sale_status", "failed");
        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("new_sale_transaction", transactionBundle);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ZLog.error(300021);
                    ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.icon_denied));

                    ((LinearLayout) findViewById(R.id.layoutButtonsNewTransaction2)).setVisibility(View.VISIBLE);
                    ((View) findViewById(R.id.linearLayoutChargeButtons)).setVisibility(View.GONE);

                    // OK, there is an error. What kind of error?
                    if(joResponse.has("response_code")){
                        if(joResponse.getString("response_code").equals("8781013")) {
                            FragmentManager fragmentManager = getFragmentManager();

                            DialogCardBrandError dialog = new DialogCardBrandError();
                            dialog.setActivity(ChargeActivity.this);
                            dialog.setPositionBasedOnView(lnBrand);
                            dialog.setCancelable(false);
                            dialog.show(fragmentManager, "dialog");
                        }
                    }
                    if (joResponse.has("error")) {
                        JSONObject  joErrorDetails = joResponse.getJSONObject("error");
                        TextView textViewMessageExplanationText = (TextView) findViewById(R.id.textViewMessageExplanationText);
                        if (joErrorDetails.has("i18n_checkout_message_explanation")) {
                            textViewMessageExplanationText.setVisibility(View.VISIBLE);
                            textViewMessageExplanationText.setText(joErrorDetails.getString("i18n_checkout_message_explanation"));
                        }
                        else {
                            textViewMessageExplanationText.setVisibility(View.GONE);
                        }

                        if (joErrorDetails.has("i18n_checkout_message")) {
                            String applicationMessage = joErrorDetails.getString("i18n_checkout_message");
                            ZLog.t(300018, applicationMessage);
                            displayApplicationStatus(applicationMessage);
                        }
                        else {
                            String applicationMessage = joErrorDetails.getString("message");
                            ZLog.t(300018, applicationMessage);
                            displayApplicationStatus(joErrorDetails.getString("message"));
                        }

                        if (bActivityCalledForIntentIntegration) {
                            chargeIntentIntegration.intentIntegrationFinishUnsuccessful(joResponse);
                        }

                        if(bActivityCalledForIntentIntegrationUri){
                            chargeIntentIntegration.intentIntegrationUrlFinishUnsuccessful(joResponse, resultUrl);
                        }
                    }
                    else {
                        ZLog.error(300020);
                        if (bActivityCalledForIntentIntegration) {
                            if(joResponse.has("i18n_checkout_message")) {
                                chargeIntentIntegration.intentIntegrationFinishUnsuccessful(joResponse.getString("i18n_checkout_message"));
                            }else {

                                chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Erro");

                            }
                        }

                        if(bActivityCalledForIntentIntegrationUri){
                            if(joResponse.has("i18n_checkout_message")) {
                                chargeIntentIntegration.intentIntegrationFinishUnsuccessful(joResponse.getString("i18n_checkout_message"));
                            }else {

                                chargeIntentIntegration.intentIntegrationFinishUnsuccessful("Erro");

                            }
                        }
                    }
                }
                catch (Exception e) {
                    ZLog.exception(300022, e);
                }
            }
        });
    }
    public void changePlan() throws Exception {
      /*  if(old_plan!=null){
            JSONObject joPlanSubscription=APIParametersCheckout.getInstance().getPlanSubscription();
            String new_plan=joPlanSubscription.getString("id");
            if(!old_plan.equals(new_plan)){
                ChangePlanTransaction aChangePlan = new ChangePlanTransaction(old_plan,this);
                aChangePlan.changePlan();
            }
        }*/
    }
    @Override
    public void paymentSuccessful(final JSONObject pjoTransactionResponse) {
        Bundle transactionBundle = new Bundle();
        transactionBundle.putString("new_sale_status", "success");
        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("new_sale_transaction", transactionBundle);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTerminalNotificationDisplay(getString(R.string.text_transaction_step4_approved));
                ZLog.t(300023);
                joTransactionResponse = pjoTransactionResponse;
                try {
                    changePlan();
                    ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_approved));
                    Button buttonSendReceipt = (Button) findViewById(R.id.buttonSendReceipt);
                    buttonSendReceipt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent receiptIntent = new Intent(ChargeActivity.this, ReceiptActivity.class);
                            Bundle b = new Bundle();
                            b.putString("transactionJSON", pjoTransactionResponse.toString());
                            receiptIntent.putExtras(b); //Put your id to your next Intent
                            ChargeActivity.this.startActivity(receiptIntent);
                        }
                    });
                    if (bActivityCalledForIntentIntegration) {
                        if (!bCaptureSignatureRequested) {
                            ZLog.t(300047);
                            chargeIntentIntegration.intentIntegrationFinishSuccessfully(pjoTransactionResponse);
                        } else {
                            intentIntegrationSuccessfulTransactionResponse = pjoTransactionResponse;
                        }
                    }

                    if (bActivityCalledForIntentIntegrationUri) {
                        if (!bCaptureSignatureRequested) {
                            ZLog.t(300047);
                            chargeIntentIntegration.intentIntegrationUrlFinishSuccessfully(pjoTransactionResponse);
                        } else {
                            intentIntegrationUrlSuccessfulTransactionResponse = pjoTransactionResponse;
                        }
                    }
                    Button buttonNewTransaction = (Button) findViewById(R.id.buttonNewTransaction);
                    buttonNewTransaction.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Extras.startNewChargeActivity(ChargeActivity.this);
                        }
                    });

                    if (!bCaptureSignatureRequested) {
                        ((View) findViewById(R.id.layoutButtonsIfTransactionApproved)).setVisibility(View.VISIBLE);
                    }
                    ((View) findViewById(R.id.linearLayoutChargeButtons)).setVisibility(View.GONE);

                } catch (Exception e) {
                    System.out.println("Exception e=" + e.getMessage());
                }
            }
        });
    }

    public void showMessage(final String stringMessage, final TerminalMessageType messageType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMessage(stringMessage, messageType, null);
            }
        });
    }
    public void showMessage(final String stringMessage, final TerminalMessageType messageType, final String sExplanation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    TextView textViewMessageExplanationText = (TextView) findViewById(R.id.textViewMessageExplanationText);
                    if (null != sExplanation) {
                        textViewMessageExplanationText.setVisibility(View.VISIBLE);
                        textViewMessageExplanationText.setText(sExplanation);
                    } else {
                        textViewMessageExplanationText.setVisibility(View.GONE);
                    }
                    ZLog.t(677303, stringMessage);
                    // To change images accordingly
                    // Not the best way
                    if ((messageType == TerminalMessageType.WAIT) || (messageType == TerminalMessageType.WAIT_PROCESSING) || (messageType == TerminalMessageType.WAIT_BLUETOOTH_CONNECTED) || (messageType == TerminalMessageType.WAIT_BLUETOOTH_CONNECTING) || (messageType == TerminalMessageType.WAIT_COMMUNICATION) || (messageType == TerminalMessageType.WAIT_INITIALIZING) || (messageType == TerminalMessageType.WAIT_TIMEOUT_OCURRED)) {
                        ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clock_icon));
                    } else if (messageType == TerminalMessageType.ACTION_INSERT_CARD) {
                        ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_card));
                    } else if (messageType == TerminalMessageType.TRANSACTION_APPROVED) {
                        ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_approved));
                    } else if (messageType == TerminalMessageType.TRANSACTION_APPROVED_REMOVE_CARD) {
                        ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_approved));
                    } else if (messageType == TerminalMessageType.TRANSACTION_DENIED) {
                        ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_denied));
                    } else if (messageType == TerminalMessageType.TRANSACTION_DENIED_REMOVE_CARD) {
                        ((ImageView) findViewById(R.id.imageViewChargeStatus)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_denied));
                    } else if (TerminalMessageType.WAIT_TIMEOUT_PRETIMEOUT_WARNING == messageType) {
                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.jobro_5_beep_b);
                    }
                    setTerminalNotificationDisplay(stringMessage);
                }
                catch (Exception e) {
                    ZLog.exception(677520, e);
                }
            }
        });
    }

    /**
     * Simple logging in the simple (not developer) user interface, using the same panel as the panel for terminal messages
     */
    public void displayApplicationStatus(final String stringMessage) {
        setTerminalNotificationDisplay(stringMessage);
    }
    public void hideApplicationStatusIcon() {
//		((ImageView) findViewById(R.id.imageViewTransactionStatus)).setVisibility(View.GONE);
    }

    /**
     * #ZoopAPICall: This method is called IF the current transaction has been approved and requires the cardholder signature as a proof of transaction/ confirmation (which is NOT the most common
     * use case for some markets like Brazil, where Chip&PIN is widely used).
     * The signature should be obtained and passed to the Zoop API using the facilitating methods.
     * ATTENTION: This call will not wait for a signature. The application should - not mandatory - capture the user signature and add it to the transaction.
     * In this implementation, the application receives the request to get cardholder signature and enables the button that activates the Android activity responsible
     * for capturing the user signature and passing it back to the API.
     */
    @Override
    public void cardholderSignatureRequested() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * Show the button to get signature
                 */
                Button buttonCaptureSignature = (Button) findViewById(R.id.buttonCaptureSignature);
                buttonCaptureSignature.setVisibility(View.VISIBLE);
                bCaptureSignatureRequested = true;

            }
        });
    }

    /**
     * #ZoopAPICall: Check the Interface documentation. It tells the application if it can abort a in progress charge or not.
     * In the ZoopAPIDemo application, this method will "enable or disable" the "Abort a charge" in progress button.
     * The application should implement this method to avoid letting the user try to cancel a transaction when it's impossible. It would result in an error for the
     * application trying to cancel a transaction in an incorrect moment.
     *
     * @param canAbortCurrentCharge Notified the application if it can abort a current charge in progress or not.
     */
    @Override
    public void currentChargeCanBeAbortedByUser(final boolean canAbortCurrentCharge) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Button) findViewById(R.id.buttonNext)).setEnabled(canAbortCurrentCharge);
            }
        });
    }


    /**
     * #ZoopAPICall: Callback to inform that the signature has been successfully added to the receipt.
     * The API will queue the signature for uploading whenever possible, without blocking or consuming resources from the application.
     * @param result If result has the value SIGNATURE_APPROVED_WITHOUT_CHECKING (0)
     */
    @Override
    public void signatureResult(final int result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Button) findViewById(R.id.buttonCaptureSignature)).setVisibility(View.GONE);
                if (terminalPayment.RESULT_OK_SIGNATURE_ADDED_OK == result) {
                    ZLog.t(300028);
                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.signature_added_to_the_receipt), Toast.LENGTH_SHORT).show();
                }
                else {
                    ZLog.warning(300032);
                }
                if ((bActivityCalledForIntentIntegration) && (null != intentIntegrationSuccessfulTransactionResponse)) {
                    ZLog.t(300046);
                    chargeIntentIntegration.intentIntegrationFinishSuccessfully(intentIntegrationSuccessfulTransactionResponse);
                }
            }
        });
    }
    /**
     * This method receives the signature data and adds it to the transaction. With the call to addCardholderSignature, the Zoop Android API appends the cardholder signature
     * data to the transaction.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CAPTURE_CARDHOLDER_SIGNATURE == requestCode) {
            if (resultCode == RESULT_OK) {
                try {
                    terminalPayment.addCardholderSignature(data.getStringExtra("signatureData"),joTransactionResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((Button) findViewById(R.id.buttonCaptureSignature)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.layoutButtonsIfTransactionApproved)).setVisibility(View.VISIBLE);


            }
        }
        else if (requestCode == ExtraCardInfoActivity.EXTRA_INFO_CARD_CVC) {
            if(resultCode == Activity.RESULT_OK) {

                try {
                    terminalPayment.addCardCVC(data.getStringExtra("extraInfo"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {

            }
        }
        else if (requestCode == ExtraCardInfoActivity.EXTRA_INFO_CARD_EXPIRATION_DATE) {
            try {
                terminalPayment.addCardExpirationDate(data.getStringExtra("extraInfo"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == ExtraCardInfoActivity.EXTRA_INFO_CARD_LAST_4_DIGITS) {
            try {
                terminalPayment.addCardLast4Digits(data.getStringExtra("extraInfo"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(requestCode==CHANGE_PLAN){
            if(resultCode == Activity.RESULT_OK) {
                try {
                    JSONObject joPlanSubscription=APIParametersCheckout.getInstance().getPlanSubscription();

                    old_plan=joPlanSubscription.getString("id");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode==CHANGE_PLAN_TRANSACTION){

            if(resultCode == Activity.RESULT_OK) {

                ViewPaymentOption viewPaymentOptionDebit					= (ViewPaymentOption) findViewById(R.id.layoutPaymentDebit);
                final ViewPaymentOption viewPaymentOptionCredit					= (ViewPaymentOption) findViewById(R.id.layoutPaymentCredit);
                ViewPaymentOption viewPaymentOptionCreditWithInstallments	= (ViewPaymentOption) findViewById(R.id.layoutPaymentCreditWithInstallments);
                try {
                    JSONObject joPlanSubscription=new JSONObject(data.getExtras().getString("old_plan"));
                    //old_plan=joPlanSubscription.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String sValueToCharge = ((TextView) findViewById(R.id.editTextValueToCharge)).getText().toString();
                final BigDecimal valueToCharge = Extras.getInstance().getBigDecimalFromDecimalString(sValueToCharge);
                if(R.layout.flipper_installment_options == flipCurrentView) {
                    try {

                        flipperInstallmentOptions.setInfoPlan();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    final Boolean bPlan=APIParameters.getInstance().getBooleanParameter("enablePlan",true);
                    if(bPlan) {
                        viewPaymentOptionDebit.setInfoPlan(Extras.getInstance().getPlanInfo("debit", valueToCharge, 1));
                        viewPaymentOptionCredit.setInfoPlan(Extras.getInstance().getPlanInfo("credit", valueToCharge, 1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void setTerminalNotificationDisplay(String stringMessage) {
        if (null != stringMessage) {
            TextView textViewStatusText = (TextView) findViewById(R.id.textViewStatusText);
            textViewStatusText.setText(stringMessage);
        }
    }
    @Override
    public void cardLast4DigitsRequested() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent dialogExtraInfo = new Intent(ChargeActivity.this, ExtraCardInfoActivity.class);
                dialogExtraInfo.putExtra("extraInfoToRequest", ExtraCardInfoActivity.EXTRA_INFO_CARD_LAST_4_DIGITS);
                startActivityForResult(dialogExtraInfo, ExtraCardInfoActivity.EXTRA_INFO_CARD_LAST_4_DIGITS);

            }
        });
    }
    @Override
    public void cardExpirationDateRequested() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent dialogExtraInfo = new Intent(ChargeActivity.this, ExtraCardInfoActivity.class);
                dialogExtraInfo.putExtra("extraInfoToRequest", ExtraCardInfoActivity.EXTRA_INFO_CARD_EXPIRATION_DATE);
                startActivityForResult(dialogExtraInfo, ExtraCardInfoActivity.EXTRA_INFO_CARD_EXPIRATION_DATE);

            }
        });
    }
    @Override
    public void cardCVCRequested() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // ToDo: Set CVC not available and CVC not readable buttons
                Intent dialogExtraInfo = new Intent(ChargeActivity.this, ExtraCardInfoActivity.class);
                dialogExtraInfo.putExtra("extraInfoToRequest", ExtraCardInfoActivity.EXTRA_INFO_CARD_CVC);
                startActivityForResult(dialogExtraInfo, ExtraCardInfoActivity.EXTRA_INFO_CARD_CVC);

            }
        });
    }

    //ToDo: Fazer isso funcionar corretamente
    @Override
    public void showDeviceListForUserSelection(final Vector<JSONObject> vectorZoopTerminals) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChargeActivity.this);
                    builder.setTitle(getResources().getString(R.string.dialog_title_select_zoop_bluetooth_device));

                    String[] terminalNames = new String[vectorZoopTerminals.size()];
                    for (int i = 0; i < vectorZoopTerminals.size(); i++) {
                        terminalNames[i] = vectorZoopTerminals.get(i).getString("name");
                    }

                    builder.setItems(terminalNames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                //TerminalListManager.setSelectedTerminal(vectorZoopTerminals.get(which));
                            }
                            catch (Exception e) {
                                ZLog.exception(677547, e);
                            }
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
                catch (Exception e) {
                    ZLog.exception(300065, e);
                }
            }
        });
    }

    public void setInfoSellerOriginalIntent(){
        try {
            if((!params.getString("seller_id").equals(""))) {
                JSONObject joSelectedTerminal = TerminalListManager.getCurrentSelectedZoopTerminal();
                //  ZoopAPI.initialize(getApplication(), markeplaceIdOriginalIntent, sellerIdOriginalIntent, publishablekeyOriginalIntent);
                TerminalListManager terminalListManager=new TerminalListManager(this,this);
                terminalListManager.requestZoopDeviceSelection(joSelectedTerminal);
                APIParameters.getInstance().putStringParameter("publishableKey", publishablekeyOriginalIntent);
                APIParameters.getInstance().putStringParameter("marketplaceId", markeplaceIdOriginalIntent);
                APIParameters.getInstance().putStringParameter("sellerId", sellerIdOriginalIntent);
                terminalPayment = new ZoopTerminalPayment();
                terminalPayment.setTerminalPaymentListener(ChargeActivity.this);
                terminalPayment.setDeviceSelectionListener(ChargeActivity.this);
                terminalPayment.setExtraCardInformationListener(ChargeActivity.this);
                terminalPayment.setApplicationDisplayListener(ChargeActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDeviceListForUserSelecion(JSONObject joNewlyFoundZoopDevice, Vector<JSONObject> vectorAllAvailableZoopTerminals, int iNewlyFoundDeviceIndex) {

    }

    @Override
    public void bluetoothIsNotEnabledNotification() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void deviceSelectedResult(JSONObject joZoopSelectedDevice, Vector<JSONObject> vectorAllAvailableZoopTerminals, int iSelectedDeviceIndex) {

    }


    public void resetPaymentOptions() {
        selectedChargeTypeIndex = -1;
        numberOfInstallments = 2;
        selectedPaymentOption = -1;
        setValueToCharge(new BigDecimal(0));
        flipCurrentView = R.layout.flipper_keypad;
    }
    public void setValueToCharge(BigDecimal pValueToCharge) {
        valueToCharge = pValueToCharge;
        ((TextView) findViewById(R.id.editTextValueToCharge)).setText(Extras.getInstance().formatBigDecimalAsLocalString(valueToCharge));
    }
    public BigDecimal getValueForCharge() {
        String sValueToCharge = ((TextView) findViewById(R.id.editTextValueToCharge)).getText().toString();
        BigDecimal valueToCharge = Extras.getInstance().getBigDecimalFromDecimalString(sValueToCharge);
        return valueToCharge;
    }
    public void setSelectedPaymentOption(int pSelectedPaymentOption) {
        selectedPaymentOption = pSelectedPaymentOption;
    }
    public void setNumberOfInstallments(int piNumberOfInstallments) {
        numberOfInstallments = piNumberOfInstallments;
    }
    public void processCharge() {
        try {
            if (bActivityCalledForIntentIntegration||bActivityCalledForIntentIntegrationUri) {
                selectedPaymentOption=idPaymentOption;
                numberOfInstallments=number_installments;
            }
            else {
                String sValueToCharge = ((TextView) findViewById(R.id.editTextValueToCharge)).getText().toString();
                valueToCharge = Extras.getInstance().getBigDecimalFromDecimalString(sValueToCharge);
            }
            ((Button) findViewById(R.id.buttonNext)).setText(getResources().getString(R.string.label_cancel));

            if (terminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS ==  selectedPaymentOption) {
                terminalPayment.charge(valueToCharge, selectedPaymentOption,numberOfInstallments,marketplaceId,sellerId,publishableKey);
            }
            else {
                terminalPayment.charge(valueToCharge, selectedPaymentOption,1,marketplaceId,sellerId,publishableKey);
            }
        }
        catch (Exception e) {
            ZLog.exception(300037, e);
        }
    }
    public void showMessageZoopTerminalAutomaticallySelectedForUseByCheckoutApp() {
        try {
            String sMessage = getResources().getString(R.string.text_one_terminal_automatically_preconfigured);
            sMessage = sMessage.replace("[manufacturer]", TerminalListManager.getCurrentSelectedZoopTerminal().getString("manufacturer"));
            sMessage = sMessage.replace("[btName]", TerminalListManager.getCurrentSelectedZoopTerminal().getString("name"));
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.text_one_terminal_automatically_preconfigured_dialog_title));
            alert.setMessage(sMessage);
            alert.setPositiveButton(getResources().getString(R.string.label_ok), this);
            alert.setOnCancelListener(this);
            alert.show();
        }
        catch (Exception e) {
            ZLog.exception(677527, e);
        }
    }
    public void showMessageSellerIsNotReadyForChargingCustomers() {
        try {
            String sMessage = getResources().getString(R.string.text_dialog_customer_not_ready_for_charging_customers);
            //[manufacturer]/ [btName]
            sMessage = sMessage.replace("[manufacturer]", TerminalListManager.getCurrentSelectedZoopTerminal().getString("manufacturer"));
            sMessage = sMessage.replace("[btName]",  TerminalListManager.getCurrentSelectedZoopTerminal().getString("name"));
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.title_dialog_customer_not_ready_for_charging_customers));
            alert.setMessage(sMessage);
            alert.setPositiveButton(getResources().getString(R.string.label_ok), (DialogInterface.OnClickListener) this);
            alert.setOnCancelListener(this);
            alert.show();
        }
        catch (Exception e) {
            ZLog.exception(677528, e);
        }
    }
    public void showPertinentDialogsUponCreation() {
        if (null != baDialogsToShow) {
            for (; iDialogsUponLoginCounter < baDialogsToShow.length; iDialogsUponLoginCounter++) {
                if (true == baDialogsToShow[iDialogsUponLoginCounter]) {
                    if (SHOW_MESSAGE_SELLER_IS_NOT_READY_FOR_CHARGING_CUSTOMERS == iDialogsUponLoginCounter ) {
                        showMessageSellerIsNotReadyForChargingCustomers();
                    }
                    else if (SHOW_MESSAGE_ZOOP_TERMINAL_AUTOMATICALLY_SELECTED_BY_USE_BY_CHECKOUT_APP == iDialogsUponLoginCounter) {
                        showMessageZoopTerminalAutomaticallySelectedForUseByCheckoutApp();
                    }
                    baDialogsToShow[iDialogsUponLoginCounter] = false;
                }
            }
        }
    }
    static boolean[] baDialogsToShow = null;
    static int SHOW_MESSAGE_SELLER_IS_NOT_READY_FOR_CHARGING_CUSTOMERS = 0;
    ;    static int SHOW_MESSAGE_ZOOP_TERMINAL_AUTOMATICALLY_SELECTED_BY_USE_BY_CHECKOUT_APP = 1;
    public static void addMessageUponLogin(int dialogToShow) {
        if (null == baDialogsToShow) {
            baDialogsToShow = new boolean[2];
            for (int i=0; i<baDialogsToShow.length; i++) {
                baDialogsToShow[i] = false;
            }
        }
        baDialogsToShow[dialogToShow] = true;
    }
    @Override
    public void onCancel(DialogInterface dialogInterface) {
        showPertinentDialogsUponCreation();
    }
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        showPertinentDialogsUponCreation();
    }
}