package com.zoop.checkout.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zoop.zoopandroidsdk.api.ZoopAPIErrors;
import com.zoop.zoopandroidsdk.terminal.ZoopTerminalException;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.ZLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class PaymentMethodsActivity extends ZCLMenuWithHomeButtonActivity {
    private CheckBox debito;
    private CheckBox credito;
    private CheckBox credito_parcel;
    private CheckBox enable_password;
    private CheckBox enable_printer;
    private Button select_printer;
    private Button config_printer;
    final private static int DIALOG_LOGIN = 1;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentmethods);
        debito = (CheckBox) findViewById(R.id.debito);
        credito = (CheckBox) findViewById(R.id.credito);
        credito_parcel = (CheckBox) findViewById(R.id.credito_parcelado);
      /*  enable_password = (CheckBox) findViewById(R.id.enable_password);
        enable_printer = (CheckBox) findViewById(R.id.enable_printer);
        select_printer=(Button) findViewById(R.id.select_printer);
        config_printer=(Button) findViewById(R.id.config_printer);
     */   if (true == APIParameters.getInstance().getBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, true)) {
            credito_parcel.setChecked(true);
        }
        if (true == APIParameters.getInstance().getBooleanParameter(APISettingsConstants.PaymentType_showCredit, true)) {
            credito.setChecked(true);


        }
        if (true == APIParameters.getInstance().getBooleanParameter(APISettingsConstants.PaymentType_showDebit, true)) {
            debito.setChecked(true);


        }
      /*  if (true == APIParameters.getInstance().getBooleanParameter("Enable_password", false)) {
            enable_password.setChecked(true);


        }
        if (true == APIParameters.getInstance().getBooleanParameter("Enable_printer", false)) {
            enable_printer.setChecked(true);
            select_printer.setVisibility(View.VISIBLE);
            config_printer.setVisibility(View.VISIBLE);


        }
        if (false == APIParameters.getInstance().getBooleanParameter("impressora_config", false)) {
            config_printer.setEnabled(false);


        }*/


    }

    @Override
    protected void onDestroy() {
        Bundle methodsBundle = new Bundle();
        methodsBundle.putBoolean("debit", debito.isChecked());
        methodsBundle.putBoolean("credit", credito.isChecked());
        methodsBundle.putBoolean("credit_installment", credito_parcel.isChecked());

        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("settings_payment_methods", methodsBundle);

        super.onDestroy();
    }

    public void onPaymentsOptions(View v) {
        boolean checked = ((CheckBox) v).isChecked();

        switch (v.getId()) {
            case R.id.debito:
                if (checked) {
                    APIParameters.getInstance().putBooleanParameter(APISettingsConstants.PaymentType_showDebit, true);
                } else {
                    APIParameters.getInstance().putBooleanParameter(APISettingsConstants.PaymentType_showDebit, false);
                }
                break;
            case R.id.credito:
                if (checked) {
                    APIParameters.getInstance().putBooleanParameter(APISettingsConstants.PaymentType_showCredit, true);

                } else {
                    APIParameters.getInstance().putBooleanParameter(APISettingsConstants.PaymentType_showCredit, false);

                }
                break;
            case R.id.credito_parcelado:
                if (checked) {
                    APIParameters.getInstance().putBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, true);

                } else {
                    APIParameters.getInstance().putBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, false);

                }
                break;
        /*  //  case R.id.enable_password:
                if (checked) {
                    APIParameters.getInstance().putBooleanParameter("Enable_password", true);
                    //  Intent ConfigPasswordGenaratorIntent = new Intent(ConfigActivity.this, ConfigPasswordGenaratorActivity.class);
                    // startActivity(ConfigPasswordGenaratorIntent);

                } else {
                    APIParameters.getInstance().putBooleanParameter("Enable_password", false);

                }
                break;
         //   case R.id.enable_printer:
                if (checked) {
                    APIParameters.getInstance().putBooleanParameter("Enable_printer", true);
                    select_printer.setVisibility(View.VISIBLE);
                    config_printer.setVisibility(View.VISIBLE);
                    //  Intent ConfigPasswordGenaratorIntent = new Intent(ConfigActivity.this, ConfigPasswordGenaratorActivity.class);
                    // startActivity(ConfigPasswordGenaratorIntent);

                } else {
                    APIParameters.getInstance().putBooleanParameter("Enable_printer", false);
                    select_printer.setVisibility(View.GONE);
                    config_printer.setVisibility(View.GONE);

                }
                break;*/
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void ConfigPrinterOnclick(View v) {
        showDialog(DIALOG_LOGIN);


    }

    public void feedbackOnclick(View v) {
        Intent feedbackIntent = new Intent(PaymentMethodsActivity.this, testeActivity.class);
        startActivity(feedbackIntent);

    }

    public void SelectPrinterOnclick() throws ZoopTerminalException {
        final ArrayList<BluetoothInfo> itens = new ArrayList<BluetoothInfo> ();
        final ArrayList<String> lista=new ArrayList<String>();
        Map<String, Object> l = new HashMap<String, Object>();
        BluetoothInfo B=new BluetoothInfo();
        final Context c=this;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (!mBluetoothAdapter.isEnabled()) {
            throw new ZoopTerminalException(677001, ZoopAPIErrors.BLUETOOTH_NOT_AVAILABLE, 0, APIParameters.getInstance().getStringParameter(APISettingsConstants.ZoopAPI_ErrMsg + ZoopAPIErrors.BLUETOOTH_NOT_AVAILABLE));
        }
        ZLog.t(677169);
        for (BluetoothDevice bt : pairedDevices) {
            B=new BluetoothInfo();
            ZLog.t(677170, bt.getName() + "/ " + bt.getAddress(), bt.getBondState());
            B.setName_dispo(bt.getName());
            B.setAddress(bt.getAddress());
            B.setState(bt.getBondState());
            lista.add(bt.getName());
            itens.add(B);


        }
        lista.add("Não selecionar impressora");


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lista);

        final AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethodsActivity.this);
        builder.setTitle(getResources().getString(R.string.dialog_title_select_zoop_bluetooth_device));
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!(lista.get(which).equals("Não selecionar impressora"))) {
                    APIParameters.getInstance().putParameter("name_printer", itens.get(which).getName_dispo());
                    APIParameters.getInstance().putParameter("BTPrinterMACAddress", itens.get(which).getAddress());
                    APIParameters.getInstance().putParameter("status", (itens.get(which).getState()).toString());
                    APIParameters.getInstance().putBooleanParameter("impressora_config", true);
                    config_printer.setEnabled(true);


                }else{
                    APIParameters.getInstance().putBooleanParameter("impressora_config", false);
                    config_printer.setEnabled(false);


                }
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();



        // A Linha abaixo recupera o filtro de nomes de Pinpads (PP_* ou PAX* ou Gertecs (não lembro da string)
        String regexTerminalsAcceptedt = APIParameters.getInstance().getStringParameter("AS.regexTerminalsAcceptedt");
        //  if (bt.getName().matches(regexTerminalsAcceptedt)) {
        //    ZLog.t(677276, bt.getName());

    }
/*
*/

    @Override
    protected Dialog onCreateDialog(int id) {

        AlertDialog dialogDetails = null;

        switch (id) {
            case DIALOG_LOGIN:
                LayoutInflater inflater = LayoutInflater.from(this);
                View dialogview = inflater.inflate(R.layout.config_printer_dialog, null);
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
                dialogbuilder.setTitle("Informe o tamanho do papel para impressão");

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
                Button configbutton = (Button) alertDialog
                        .findViewById(R.id.btn_login);

                Button cancelbutton = (Button) alertDialog
                        .findViewById(R.id.btn_cancel);

                final EditText printerColumns = (EditText) alertDialog.findViewById(R.id.printerColumns);

                configbutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Preferences demoPreferences = Preferences.getInstance();

                        if (!(printerColumns.getText().toString().equals(""))){
                           try{
                              Integer Valuecolumn= Integer.valueOf(printerColumns.getText().toString());
                               APIParameters.getInstance().putStringParameter("printerColumns", printerColumns.getText().toString());

                               alertDialog.dismiss();
                           } catch (Exception e) {
                               Toast.makeText(PaymentMethodsActivity.this, "Informe um valor inteiro para o tamanho do papel " ,
                                       Toast.LENGTH_SHORT).show();
                               e.printStackTrace();
                           }




                        }else{
                            Toast.makeText(PaymentMethodsActivity.this, "Tamanho da coluna do papel não informada " ,
                                    Toast.LENGTH_SHORT).show();
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
}
