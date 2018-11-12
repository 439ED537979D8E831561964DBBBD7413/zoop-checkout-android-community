package com.zoop.checkout.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.zoop.zoopandroidsdk.TerminalListManager;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.GenderText;
import com.zoop.zoopandroidsdk.commons.ZLog;

import java.util.Set;

/**
 * Created by mainente on 15/04/15.
 */
public class WelcomeCheckoutActivity extends AppCompatActivity {

    int flipCurrentView = -1;
    FlipperConfigPinPadActivity welcomeFlipperPaneConfigpinpad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_welcome);
        APIParameters ap = APIParameters.getInstance();
        final Button next;
        final Button previous;
        next = (Button) findViewById(R.id.btnNext);
        previous = (Button) findViewById(R.id.btnPrevious);
        FlipperWelcomeCheckout welcomeFlipperPane = new FlipperWelcomeCheckout();
        welcomeFlipperPane.initializeHistory();
        welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
        welcomeFlipperPane.flipToPane(this);
        flipCurrentView = R.layout.welcome_pane;
        previous.setEnabled(false);
        APIParameters.getInstance().putBooleanParameter(APISettingsConstants.ZoopCheckout_ActivateWizardOnStartup, true);
        final Boolean bPlan = APIParameters.getInstance().getBooleanParameter("enablePlan", false);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (flipCurrentView == R.layout.welcome_pane) {
                    if (bPlan) {

                        FlipperWelcomePlan welcomeFlipperPane = new FlipperWelcomePlan();
                        welcomeFlipperPane.initializeHistory();
                        welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                        welcomeFlipperPane.flipToPane(WelcomeCheckoutActivity.this);
                        flipCurrentView = R.layout.welcome_plan_pane;
                        previous.setEnabled(true);
                    } else {
                        welcomeFlipperPaneConfigpinpad = new FlipperConfigPinPadActivity();
                        welcomeFlipperPaneConfigpinpad.initializeHistory();
                        welcomeFlipperPaneConfigpinpad.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                        welcomeFlipperPaneConfigpinpad.flipToPane(WelcomeCheckoutActivity.this);
                        flipCurrentView = R.layout.welcome_search_terminal_pane;
                        previous.setEnabled(true);


                    }


                } else if (flipCurrentView == R.layout.welcome_plan_pane) {
                    welcomeFlipperPaneConfigpinpad = new FlipperConfigPinPadActivity();
                    welcomeFlipperPaneConfigpinpad.initializeHistory();
                    welcomeFlipperPaneConfigpinpad.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                    welcomeFlipperPaneConfigpinpad.flipToPane(WelcomeCheckoutActivity.this);
                    flipCurrentView = R.layout.welcome_search_terminal_pane;


                } else if (flipCurrentView == R.layout.welcome_search_terminal_pane) {

                    welcomeFlipperPaneConfigpinpad.stopDiscovery();

                    try {

                        if (null == TerminalListManager.getCurrentSelectedZoopTerminal()) {

                            FlipperWelcomeBuyTerminal welcomeFlipperPane = new FlipperWelcomeBuyTerminal();

                            welcomeFlipperPane.initializeHistory();
                            welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                            welcomeFlipperPane.flipToPane(WelcomeCheckoutActivity.this);
                            flipCurrentView = R.layout.welcome_buy_terminal_pane;

                        } else {


                            FlipperWelcomeEnableTransaction welcomeFlipperPane = new FlipperWelcomeEnableTransaction();
                            welcomeFlipperPane.initializeHistory();
                            welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                            welcomeFlipperPane.flipToPane(WelcomeCheckoutActivity.this);
                            flipCurrentView = R.layout.welcome_transaction_ok_pane;
                            next.setText("Finalizar");


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (flipCurrentView == R.layout.welcome_buy_terminal_pane) {


                    FlipperWelcomeEnableTransaction welcomeFlipperPane = new FlipperWelcomeEnableTransaction();
                    welcomeFlipperPane.initializeHistory();
                    welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                    welcomeFlipperPane.flipToPane(WelcomeCheckoutActivity.this);
                    flipCurrentView = R.layout.welcome_transaction_ok_pane;
                    next.setText("Finalizar");


                } else if (flipCurrentView == R.layout.welcome_transaction_ok_pane) {

                    APIParameters.getInstance().putBooleanParameter(APISettingsConstants.ZoopCheckout_ActivateWizardOnStartup, false);

                    //xx_16 WTF? Pra que era isso abaixo?
                    //Extras.checkZoopTerminalsAndGoToNextStep(WelcomeCheckoutActivity.this);
                    Intent feedbackIntent = new Intent(WelcomeCheckoutActivity.this, ChargeActivity.class);
                    feedbackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(feedbackIntent);


                }


            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flipCurrentView == R.layout.welcome_plan_pane) {
                    FlipperWelcomeCheckout welcomeFlipperPane = new FlipperWelcomeCheckout();
                    welcomeFlipperPane.initializeHistory();
                    welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                    welcomeFlipperPane.flipToPane(WelcomeCheckoutActivity.this);
                    flipCurrentView = R.layout.welcome_pane;
                    previous.setEnabled(false);


                } else if (flipCurrentView == R.layout.welcome_search_terminal_pane) {
                    if (bPlan) {
                        welcomeFlipperPaneConfigpinpad.stopDiscovery();

                        FlipperWelcomePlan welcomeFlipperPane = new FlipperWelcomePlan();
                        welcomeFlipperPane.initializeHistory();
                        welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                        welcomeFlipperPane.flipToPane(WelcomeCheckoutActivity.this);
                        flipCurrentView = R.layout.welcome_plan_pane;
                    } else {

                        FlipperWelcomeCheckout welcomeFlipperPane = new FlipperWelcomeCheckout();
                        welcomeFlipperPane.initializeHistory();
                        welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                        welcomeFlipperPane.flipToPane(WelcomeCheckoutActivity.this);
                        flipCurrentView = R.layout.welcome_pane;
                        previous.setEnabled(false);

                    }


                } else if (flipCurrentView == R.layout.welcome_buy_terminal_pane) {
                    welcomeFlipperPaneConfigpinpad = new FlipperConfigPinPadActivity();
                    welcomeFlipperPaneConfigpinpad.initializeHistory();
                    welcomeFlipperPaneConfigpinpad.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                    welcomeFlipperPaneConfigpinpad.flipToPane(WelcomeCheckoutActivity.this);
                    flipCurrentView = R.layout.welcome_search_terminal_pane;


                } else if (flipCurrentView == R.layout.welcome_transaction_ok_pane) {
                    try {
                        next.setText("Próximo");

                        if (null == TerminalListManager.getCurrentSelectedZoopTerminal()) {


                            FlipperWelcomeBuyTerminal welcomeFlipperPane = new FlipperWelcomeBuyTerminal();
                            welcomeFlipperPane.initializeHistory();
                            welcomeFlipperPane.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                            welcomeFlipperPane.flipToPane(WelcomeCheckoutActivity.this);
                            flipCurrentView = R.layout.welcome_buy_terminal_pane;
                        } else {

                            welcomeFlipperPaneConfigpinpad = new FlipperConfigPinPadActivity();
                            welcomeFlipperPaneConfigpinpad.initializeHistory();
                            welcomeFlipperPaneConfigpinpad.setBaseView((ViewFlipper) findViewById(R.id.LinearLayoutPaymentRightPaneFlipper));
                            welcomeFlipperPaneConfigpinpad.flipToPane(WelcomeCheckoutActivity.this);
                            flipCurrentView = R.layout.welcome_search_terminal_pane;

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }

    public void okGotItOnclick(View v) {
        APIParameters.getInstance().putBooleanParameter(APISettingsConstants.ZoopCheckout_ActivateWizardOnStartup, false);
        // xx_16 Por que isso estava aqui...
        //Extras.checkZoopTerminalsAndGoToNextStep(WelcomeCheckoutActivity.this);
        Intent feedbackIntent = new Intent(WelcomeCheckoutActivity.this, ChargeActivity.class);
        feedbackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
/*
        APIParameters.getInstance().getBooleanParameter(APISettingsConstants.ZoopCheckout_ActivateWizardOnStartup, false);
*/

        startActivity(feedbackIntent);

        ZLog.t(300302);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }
}