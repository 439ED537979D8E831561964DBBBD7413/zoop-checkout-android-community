package com.zoop.checkout.app;

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.api.ZoopAPIErrors;
import com.zoop.zoopandroidsdk.terminal.ZoopTerminalException;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.ZLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mainente on 19/01/17.
 */


public class DialogPrinterSelect extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_printer_config, container,
                false);
        getDialog().setTitle("Configurações de impressão");


        Button configbutton = (Button) rootView
                .findViewById(R.id.BtnPrinterConfig);
        final ArrayList<BluetoothInfo> itens = new ArrayList<BluetoothInfo> ();
        final ArrayList<String> lista=new ArrayList<String>();
        final ArrayList<String> TypePaper=new ArrayList<String>();
        TypePaper.add("Grande - 60");
        TypePaper.add("Médio - 45");
        TypePaper.add("Pequeno - 30");

        Map<String, Object> l = new HashMap<String, Object>();
        BluetoothInfo B=new BluetoothInfo();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (!mBluetoothAdapter.isEnabled()) {
            try {
                throw new ZoopTerminalException(677001, ZoopAPIErrors.BLUETOOTH_NOT_AVAILABLE, 0, APIParameters.getInstance().getStringParameter("ZoopAPIError/8781022"));
            } catch (ZoopTerminalException e) {
                e.printStackTrace();
            }
        }
        ZLog.t(677169);
        final List<String> namesdevice = new ArrayList<String>();
        namesdevice.add("Não selecionar impressora");
        lista.add("Não selecionar impressora");
        for (BluetoothDevice bt : pairedDevices) {
            String regexTerminalsAcceptedt = APIParameters.getInstance().getStringParameter("AS.regexTerminalsAcceptedt");




/*
            if (!(bt.getName().matches(regexTerminalsAcceptedt))) {
*/

                B = new BluetoothInfo();
                ZLog.t(677170, bt.getName() + "/ " + bt.getAddress(), bt.getBondState());
                B.setName_dispo(bt.getName());
                B.setAddress(bt.getAddress());
                B.setState(bt.getBondState());
                lista.add(bt.getName());
                itens.add(B);
                namesdevice.add(bt.getName());
          //  }

        }
        final Spinner SpNameDevice=(Spinner)  rootView.findViewById(R.id.nameprinter);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, namesdevice);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        SpNameDevice.setAdapter(spinnerArrayAdapter);
        final Spinner SpTypePaper=(Spinner)  rootView.findViewById(R.id.typePaper);
        ArrayAdapter<String> arrayAdapterpaper = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, TypePaper);
        ArrayAdapter<String> spinnerArrayAdapterpaper = arrayAdapterpaper;
        spinnerArrayAdapterpaper.setDropDownViewResource(android.R.layout.simple_spinner_item);
        SpTypePaper.setAdapter(spinnerArrayAdapterpaper);



        SpNameDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        configbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Preferences demoPreferences = Preferences.getInstance();
                Integer position=SpNameDevice.getSelectedItemPosition();
                Toast.makeText(getActivity(),"Configurações salvas.",Toast.LENGTH_LONG).show();
                if (!(namesdevice.get(position).equals("Não selecionar impressora"))) {
                    APIParameters.getInstance().putParameter("name_printer", itens.get(position-1).getName_dispo());
                    APIParameters.getInstance().putParameter("BTPrinterMACAddress", itens.get(position-1).getAddress());
                    APIParameters.getInstance().putParameter("status", (itens.get(position-1).getState()).toString());
                    APIParameters.getInstance().putBooleanParameter("impressora_config", true);
                    String Column = "";
                    if (SpTypePaper.getSelectedItem().toString().equals("Grande - 60")){
                        Column="60";
                    }else if (SpTypePaper.getSelectedItem().toString().equals("Médio - 45")){
                        Column="47";
                    }else if (SpTypePaper.getSelectedItem().toString().equals("Pequeno - 30")){
                        Column="31";
                    }
                    APIParameters.getInstance().putStringParameter("printerColumns",Column);
                }else{
                    APIParameters.getInstance().putBooleanParameter("impressora_config", false);
                }
                getDialog().dismiss();
           }
        });
        return rootView;
    }
}