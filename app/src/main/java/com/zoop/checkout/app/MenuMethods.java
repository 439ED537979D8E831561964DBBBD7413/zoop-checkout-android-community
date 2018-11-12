package com.zoop.checkout.app;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.ZLog;

import org.json.JSONException;

/**
 * Created by mainente on 06/07/16.
 */
public class MenuMethods {
    ChargeActivity activity;
    final private static int DIALOG_PRINTER= 1;
    final private static int DIALOG_LOGIN= 2;
    final private static int DIALOG_ABOUT= 3;

    public void showAboutBox(ChargeActivity c){
        activity=c;
        FragmentManager fragmentManager = c.getFragmentManager();
        DialogAboutVersion dialog = new DialogAboutVersion();
        dialog.show(fragmentManager, "dialog");
    }
    public void checkPassword(ChargeActivity c){
        activity=c;
        if (true == APIParameters.getInstance().getBooleanParameter("Enable_password", false)) {
            for(int i=0;i<activity.getjMenu().length();i++) {
                try {
                    if (activity.getjMenu().getJSONObject(i).getString("title").equals("Configurações")) {
                        activity.setPositionValidatePassword(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            FragmentManager fragmentManager = c.getFragmentManager();
            DialogPasswordMenuSelect dialog = new DialogPasswordMenuSelect();
            dialog.setExpandableList(c.getPositionValidatePassword(),c.getExpandableListView());
            dialog.show(fragmentManager, "dialog");
        }
    }
    public void showPrinter(ChargeActivity c){
        activity=c;
        FragmentManager fragmentManager = c.getFragmentManager();
        DialogPrinterSelect dialog = new DialogPrinterSelect();
        dialog.show(fragmentManager, "dialog");
    }
    public void enablePassword(final ChargeActivity c){
        if (true == APIParameters.getInstance().getBooleanParameter("Enable_password", false)) {
            APIParameters.getInstance().putBooleanParameter("Enable_password", false);
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(c);
            alert.setTitle(c.getResources().getString(R.string.dialog_confirm_protect_configuration));
            String sMessage = c.getResources().getString(R.string.dialog_confirm_protect_configuration_message);
            try {
                sMessage = sMessage.replace("[username]", APIParameters.getInstance().getStringParameter("currentLoggedinUsername"));
            }
            catch (Exception e) {
                ZLog.exception(677452, e);
            }
            alert.setMessage(sMessage);
            alert.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Bundle protectionBundle = new Bundle();
                    protectionBundle.putString("status", "activate");
                    FirebaseAnalytics.getInstance(c.getApplicationContext()).logEvent("config_protection", protectionBundle);
                    try {
                        APIParameters.getInstance().putBooleanParameter("Enable_password", true);
                    } catch (Exception e) {
                    }
                }
            });
            alert.setNegativeButton("Desativar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Bundle protectionBundle = new Bundle();
                    protectionBundle.putString("status", "deactivate");
                    FirebaseAnalytics.getInstance(c.getApplicationContext()).logEvent("config_protection", protectionBundle);
                    // Canceled.
                }
            });
            alert.show();
        }
    }
    public void logoutCurrentUser(final ChargeActivity c ){
        final Bundle logoutBundle = new Bundle();

        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setTitle(c.getResources().getString(R.string.dialog_confirm_logout_title));
        String sMessage = c.getResources().getString(R.string.dialog_confirm_logout_message);
        try {
            sMessage = sMessage.replace("[username]", APIParameters.getInstance().getStringParameter("currentLoggedinUsername"));
            sMessage = sMessage.replace("[APP_DESCRIPTOR]", ApplicationConfiguration.APP_DESCRIPTOR);
        }
        catch (Exception e) {
            ZLog.exception(677452, e);
        }

        alert.setMessage(sMessage);
        alert.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                logoutBundle.putString("logout", "success");
                FirebaseAnalytics.getInstance(c.getApplicationContext()).logEvent("logout_confirmation", logoutBundle);
                try {
                    Preferences.getInstance().logout(c);
                }
                catch (Exception e) {
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                logoutBundle.putString("logout", "canceled");
                FirebaseAnalytics.getInstance(c.getApplicationContext()).logEvent("logout_confirmation", logoutBundle);
                // Canceled.
            }
        });

        alert.show();
    }


}
