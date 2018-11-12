package com.zoop.checkout.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.zoop.zoopandroidsdk.api.ZoopAPIErrors;
import com.zoop.zoopandroidsdk.terminal.ZoopTerminalException;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.ZLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mainente on 06/05/15.
 */
public class PrinterConfigActivity extends Activity {

    final private static int DIALOG_LOGIN = 1;


    public void SelectPrinterOnclick(Context Context) throws ZoopTerminalException {
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(Context);
        builder.setTitle(getResources().getString(R.string.dialog_title_select_zoop_bluetooth_device));
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!(lista.get(which).equals("Não selecionar impressora"))) {
                    APIParameters.getInstance().putParameter("name_printer", itens.get(which).getName_dispo());
                    APIParameters.getInstance().putParameter("BTPrinterMACAddress", itens.get(which).getAddress());
                    APIParameters.getInstance().putParameter("status", (itens.get(which).getState()).toString());
                    APIParameters.getInstance().putBooleanParameter("impressora_config", true);
              //      config_printer.setEnabled(true);


                }else{
                    APIParameters.getInstance().putBooleanParameter("impressora_config", false);
                //    config_printer.setEnabled(false);


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
                               // Toast.makeText(MethodsPaymentActivity.this, "Informe um valor inteiro para o tamanho do papel ",
                                 //       Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }




                        }else{
                           // Toast.makeText(MethodsPaymentActivity.this, "Tamanho da coluna do papel não informada " ,
                                  //  Toast.LENGTH_SHORT).show();
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
