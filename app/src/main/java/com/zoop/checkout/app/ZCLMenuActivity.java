
package com.zoop.checkout.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.ZLog;

public class ZCLMenuActivity extends ZCLActivity {
    final private static int DIALOG_LOGIN = 1;


    @Override
    protected void onStart() {
        super.onStart();
        //Preferences.initialize(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//		getSupportActionBar().setTitle("ADS KDSAS");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.zcheckout_easy_action_bar_with_menu);
        getSupportActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));



        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.imageViewZoopLogo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZLog.t(300035);
            }
        });
        //getSupportActionBar().setHomeButtonEnabled(true);

        ImageView ivTransactionsHistory = (ImageView) findViewById(R.id.imageViewTransactionsHistory);
        ivTransactionsHistory.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("history_topbar", null);
                Intent paymentsListIntent = new Intent(ZCLMenuActivity.this, PaymentsListActivity.class);
                startActivity(paymentsListIntent );
            }
        });

        ImageView ivConfigurations = (ImageView) findViewById(R.id.imageViewConfigurations);
        ivConfigurations.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (true == APIParameters.getInstance().getBooleanParameter("Enable_password",false)) {
                 //   Intent ConfigPasswordIntent = new Intent(ZCLMenuActivity.this, ConfigSenhaActivity.class);
                   // startActivity(ConfigPasswordIntent);
                    showDialog(DIALOG_LOGIN);


                }else {
                  //  Intent configIntent = new Intent(ZCLMenuActivity.this, ConfigActivity.class);
                    //startActivity(configIntent);
                }
            }
        });

//        ImageView imageViewLockIcon = (ImageView) findViewById(R.id.imageViewLockIcon);
//        imageViewLockIcon.setVisibility(View.GONE);
        /*imageViewLockIcon.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ZCLMenuActivity.this);
                alert.setTitle(getResources().getString(R.string.dialog_confirm_logout_title));
                String sMessage = getResources().getString(R.string.dialog_confirm_logout_message);
                sMessage = sMessage.replace("[username]", APIParameters.getInstance().getStringParameter("username"));
                alert.setMessage(sMessage);
                alert.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            Preferences.getInstance().logout(ZCLMenuActivity.this);
                        }
                        catch (Exception e) {

                        }

                    }
                });

                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });*/





    }

    @Override
    protected Dialog onCreateDialog(int id) {

        AlertDialog dialogDetails = null;

        switch (id) {
            case DIALOG_LOGIN:
                LayoutInflater inflater = LayoutInflater.from(this);
                View dialogview = inflater.inflate(R.layout.activity_login_config, null);
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
                dialogbuilder.setTitle("Validar por senha");

                dialogbuilder.setView(dialogview);
                dialogDetails = dialogbuilder.create();

                break;
        }

        return dialogDetails;
    }
    private AlertDialog alertDialog;
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        switch (id) {
            case DIALOG_LOGIN:
                  alertDialog = (AlertDialog) dialog;
                Button loginbutton = (Button) alertDialog
                        .findViewById(R.id.btn_login);

                Button cancelbutton = (Button) alertDialog
                        .findViewById(R.id.btn_cancel);

                final EditText password = (EditText) alertDialog.findViewById(R.id.password);

                loginbutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Preferences demoPreferences = Preferences.getInstance();

                        if (!(password.getText().toString().equals(""))){

                            if (password.getText().toString().equals(APIParameters.getInstance().getStringParameter("currentLoggedinSecurityToken")
                            )){
                                alertDialog.dismiss();
                                password.setText("");


                                Intent ConfigIntent = new Intent(ZCLMenuActivity.this, PaymentMethodsActivity.class);
                                startActivity(ConfigIntent);
                            }else{
                                Toast.makeText(ZCLMenuActivity.this, "Senha inválida", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(ZCLMenuActivity.this, "Senha não informada" , Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                cancelbutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }

                });
                break;

        }

    }

  /*  @Override
    protected void onRestart() {
        super.onRestart();
        alertDialog.dismiss();

    }*/
}


