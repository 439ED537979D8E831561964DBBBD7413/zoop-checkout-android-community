package com.zoop.checkout.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.zoop.zoopandroidsdk.TerminalListManager;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.Configuration;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.printer.PrinterListManager;
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by mainente on 17/04/15.
 */
public class ConfigPrinterActivity extends ZCLMenuWithHomeButtonActivity implements DeviceSelectionListener {
    Button btnBuy;

    public static boolean bShowQuickInstructions = false;
    PrinterListManager printerListManager;
    String sTerminalListMoreInfo;
    SimpleAdapter adapter;
    ListView lv;
    ArrayList<HashMap<String, Object>> arrayListZoopTerminalDeviceListForUI;

    int iSelectedDeviceIndex = -1;
    int positionPaper;


    Bundle printerBundle = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_printer);
        APIParameters ap = APIParameters.getInstance();
        lv = (ListView) findViewById(R.id.listViewAvailableTerminals);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter = null;
        arrayListZoopTerminalDeviceListForUI = new ArrayList<HashMap<String, Object>>();
        adapter = new SimpleAdapter(this, arrayListZoopTerminalDeviceListForUI,
                R.layout.listpinpad,
                new String[]{"name", "dateTimeDetected", "selected"},
                //new String[]{"name", "address", "dateTimeDetected", "selected"},
                //new int[]{R.id.tv_MainText, R.id.tv_SubText, R.id.tvBtDeviceLastTimeDetected, R.id.rb_Choice});
                new int[]{R.id.tv_MainText, R.id.tvBtDeviceLastTimeDetected, R.id.rb_Choice});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int piSelectedDeviceIndex, long arg3) {
                try {

                    iSelectedDeviceIndex = piSelectedDeviceIndex;

                    RadioButton rb = (RadioButton) v.findViewById(R.id.rb_Choice);
                    if (!rb.isChecked()) { //OFF->ON - On-OFF
                        HashMap<String, Object> hmSelectedDevice = arrayListZoopTerminalDeviceListForUI.get(iSelectedDeviceIndex);
                        JSONObject joZoopDeviceSelectedByClick = (JSONObject) hmSelectedDevice.get("joZoopDevice");
                        printerBundle.putString("printer_selected", rb.getText().toString());
                        if (Configuration.DEBUG_MODE) {
                            ZLog.t("Selected device by click: " + joZoopDeviceSelectedByClick.toString(3));
                        }
                        printerListManager.requestZoopDeviceSelection(joZoopDeviceSelectedByClick);
                    }

                } catch (Exception e) {
                    ZLog.exception(677601, e);
                }
            }
        });
        positionPaper=APIParameters.getInstance().getIntParameter("positionPaper",1);
        printerListManager = new PrinterListManager(this, getApplicationContext());
        printerListManager.startPrintersDiscovery();
        final Spinner spTypePaper=(Spinner)  findViewById(R.id.typePaper);
        final ArrayList<String> typePaper=new ArrayList<String>();
        typePaper.add("Grande - 60");
        typePaper.add("Médio - 45");
        typePaper.add("Pequeno - 30");
        ArrayAdapter<String> arrayAdapterpaper = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typePaper);
        ArrayAdapter<String> spinnerArrayAdapterpaper = arrayAdapterpaper;
        spinnerArrayAdapterpaper.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spTypePaper.setAdapter(spinnerArrayAdapterpaper);
        spTypePaper.setSelection(positionPaper);
        final EditText breakiline=(EditText)findViewById(R.id.breakline);
        String defeaultBreakLine=APIParameters.getInstance().getStringParameter("defaultbreakline","0");
        breakiline.setText(defeaultBreakLine);


        ((Button) findViewById(R.id.buttonFinishConfiguration)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Column = "";
                if (spTypePaper.getSelectedItem().toString().equals("Grande - 60")){
                    Column="60";

                }else if (spTypePaper.getSelectedItem().toString().equals("Médio - 45")){
                    Column="47";
                }else if (spTypePaper.getSelectedItem().toString().equals("Pequeno - 30")){
                    Column="31";
                }

                printerBundle.putInt("number_of_lines", Integer.parseInt(breakiline.getText().toString()));
                printerBundle.putString("paper", spTypePaper.getSelectedItem().toString());
                FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("printer_config", printerBundle);
                positionPaper=spTypePaper.getSelectedItemPosition();
                APIParameters.getInstance().putStringParameter("printerColumns",Column);
                if(breakiline.getText().toString().equals("")){
                    APIParameters.getInstance().putStringParameter("defaultbreakline", "0");

                }else {
                    APIParameters.getInstance().putStringParameter("defaultbreakline", breakiline.getText().toString());
                }
                APIParameters.getInstance().putLongParameter("positionPaper",positionPaper);

                finish();
            }
        });

        if (bShowQuickInstructions) {
            String sMessage = getResources().getString(R.string.text_terminal_configuration_quick_instructions);
            sMessage = sMessage.replace("[device_type]", Extras.getDeviceTypeString(this));

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.title_configure_zoop_terminal));
            alert.setMessage(sMessage);
            alert.setPositiveButton(getResources().getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Dismiss
                }
            });
            alert.show();

            bShowQuickInstructions = false;
        }

        MultiplePermissionsListener dialogMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(this)
                        .withTitle("Permissão de localização")
                        .withMessage("Precisamos da permissão para acessar os dados da conexão com a impressora")
                        .withButtonText(android.R.string.ok)
                        .build();

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(dialogMultiplePermissionsListener)
                .check();

    }


    @Override
    public void onDestroy() {
        printerListManager.finishTerminalDiscovery();
        super.onDestroy();
    }

    /*
    public void pairNewTerminal(View v) {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
        finish();
    }

    public void Pair(View v){
        Intent TutorialPairIntent = new Intent(ConfigPinPadActivity.this, TutorialPairPAXActivity.class);
        startActivity(TutorialPairIntent);
    }
    */

    public void explainTerminalSelection(View v) {
        //String sMessage = sMessage.replace("[device_type]", Extras.getDeviceTypeString(this));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle(getResources().getString(R.string.title_configure_zoop_terminal));
        alert.setMessage(sTerminalListMoreInfo);
        alert.setPositiveButton(getResources().getString(R.string.label_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Dismiss
            }
        });
        alert.show();
    }


    @Override
    public void showDeviceListForUserSelection(final Vector<JSONObject> pVectorZoopTerminals) {

        try {
            // pVectorZoopTerminals is used for initializing the hashmap
            if (null == pVectorZoopTerminals) {
                findViewById(R.id.listViewAvailableTerminals).setVisibility(View.GONE);
                sTerminalListMoreInfo = getResources().getString(R.string.text_no_printer_available);
                sTerminalListMoreInfo = sTerminalListMoreInfo.replace("[device_type]", Extras.getDeviceTypeString(this));
                ((TextView) findViewById(R.id.textViewLabelTerminalList)).setText(getResources().getString(R.string.label_no_printer_available));
                ((TextView) findViewById(R.id.textViewCurrentlySelectedTerminal)).setText(getResources().getString(R.string.label_no_printer_selected));
            } else {
                String sCurrentlySelectedZoopTerminalManufacturerName = null;
                String sCurrentlySelectedTerminal = null;
                try {

                    //sCurrentlySelectedZoopTerminalManufacturerName = ;
                    sCurrentlySelectedTerminal = getResources().getString(R.string.label_currently_selected_printer_on_configprinter);

//                    sCurrentlySelectedTerminal = sCurrentlySelectedTerminal.replace("[manufacturer]", TerminalListManager.getCurrentSelectedZoopTerminal().getString("manufacturer"));
                    sCurrentlySelectedTerminal = sCurrentlySelectedTerminal.replace("[btName]", PrinterListManager.getCurrentSelectedZoopTerminal().getString("name"));
                    ((TextView) findViewById(R.id.textViewCurrentlySelectedTerminal)).setText(sCurrentlySelectedTerminal);

                } catch (Exception e) {
                    ((TextView) findViewById(R.id.textViewCurrentlySelectedTerminal)).setText(getResources().getString(R.string.label_no_printer_selected));
                }

                sTerminalListMoreInfo = getResources().getString(R.string.text_select_available_terminal);
                ((TextView) findViewById(R.id.textViewLabelTerminalList)).setText(getResources().getString(R.string.label_select_available_printer));



            }

            arrayListZoopTerminalDeviceListForUI = new ArrayList<HashMap<String, Object>>();

            for (JSONObject joZoopTerminal : pVectorZoopTerminals) {
                HashMap<String, Object> hashMapZoopTerminalStringsForUI = new HashMap<String, Object>();

                hashMapZoopTerminalStringsForUI.put("joZoopDevice", joZoopTerminal);
                hashMapZoopTerminalStringsForUI.put("name", joZoopTerminal.getString("name"));
                hashMapZoopTerminalStringsForUI.put("dateTimeDetected", joZoopTerminal.getString("dateTimeDetected"));

                JSONObject joSelectedZoopTerminal = PrinterListManager.getCurrentSelectedZoopTerminal();
                if (null != joSelectedZoopTerminal) {
                    boolean isCurrentlySelected = 0 == joZoopTerminal.getString("uri").compareTo(joSelectedZoopTerminal.getString("uri"));
                    hashMapZoopTerminalStringsForUI.put("selected", isCurrentlySelected);
                } else {
                    hashMapZoopTerminalStringsForUI.put("selected", false);
                }

                if (Configuration.DEBUG_MODE) {
                    ZLog.t("Devices bound to view: " + joZoopTerminal.toString(5));
                }

                arrayListZoopTerminalDeviceListForUI.add(hashMapZoopTerminalStringsForUI);

            }


            adapter = new SimpleAdapter(this, arrayListZoopTerminalDeviceListForUI,
                    R.layout.listpinpad,
                    new String[]{"name", "dateTimeDetected", "selected"},
                    //new String[]{"name", "address", "dateTimeDetected", "selected"},
                    //new int[]{R.id.tv_MainText, R.id.tv_SubText, R.id.tvBtDeviceLastTimeDetected, R.id.rb_Choice});
                    new int[]{R.id.tv_MainText, R.id.tvBtDeviceLastTimeDetected, R.id.rb_Choice});

            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (data == null) {
                        view.setVisibility(View.GONE);
                        return true;
                    }
                    view.setVisibility(View.VISIBLE);
                    return false;
                }
            });

            // Bind to our new adapter.
            lv.setAdapter(adapter);

        } catch (Exception e) {
            ZLog.exception(300064, e);
        }
    }

    @Override
    public void updateDeviceListForUserSelecion(JSONObject joNewlyFoundZoopDevice, Vector<JSONObject> vectorAllAvailableZoopTerminals, int iNewlyFoundDeviceIndex) {
        try {
            HashMap<String, Object> hashMapZoopTerminalStringsForUI = new HashMap<String, Object>();
            hashMapZoopTerminalStringsForUI.put("name", joNewlyFoundZoopDevice.getString("name"));
            hashMapZoopTerminalStringsForUI.put("joZoopDevice", joNewlyFoundZoopDevice);
            //hashMapZoopTerminalStringsForUI.put("address", joZoopTerminal.getString("address"));
            hashMapZoopTerminalStringsForUI.put("dateTimeDetected", joNewlyFoundZoopDevice.getString("dateTimeDetected"));
            JSONObject joSelectedZoopTerminal = TerminalListManager.getCurrentSelectedZoopTerminal();
            if (null != joSelectedZoopTerminal) {
                boolean isCurrentlySelected = 0 == joNewlyFoundZoopDevice.getString("uri").compareTo(joSelectedZoopTerminal.getString("uri"));
                hashMapZoopTerminalStringsForUI.put("selected", isCurrentlySelected);
            } else {
                hashMapZoopTerminalStringsForUI.put("selected", false);
            }

            arrayListZoopTerminalDeviceListForUI.add(hashMapZoopTerminalStringsForUI);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            ZLog.exception(677541, e);
        }
    }

    @Override
    public void bluetoothIsNotEnabledNotification() {
        printerListManager.enableDeviceBluetoothAdapter();
    }

    @Override
    public void deviceSelectedResult(JSONObject joZoopSelectedDevice, Vector<JSONObject> vectorAllAvailableZoopTerminals, int iSelectedDeviceIndex) {
        try {
            if (null != joZoopSelectedDevice) {

                for (HashMap<String, Object> hashMapZoopTerminalStringsForUI : arrayListZoopTerminalDeviceListForUI) {
                    hashMapZoopTerminalStringsForUI.put("selected", false);
                }

                HashMap<String, Object> btSelectedDeviceHashmap = arrayListZoopTerminalDeviceListForUI.get(iSelectedDeviceIndex);
                btSelectedDeviceHashmap.put("selected", true);
                findViewById(R.id.buttonFinishConfiguration).setVisibility(View.VISIBLE);
                APIParameters.getInstance().putBooleanParameter("impressora_config", true);

                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            ZLog.exception(300056, e);
        }
    }
}
