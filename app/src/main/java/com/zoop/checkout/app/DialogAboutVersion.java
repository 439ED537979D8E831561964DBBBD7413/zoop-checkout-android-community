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
import android.widget.TextView;
import android.widget.Toast;



import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by mainente on 19/01/17.
 */


public class DialogAboutVersion extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_about, container,
                false);
        getDialog().setTitle("Sobre");
        TextView versionSDK = (TextView) rootView
                .findViewById(R.id.lbl_version_sdk);
        versionSDK.setText("Versão SDK: "+com.zoop.zoopandroidsdk.api.Version.getVersion());


        TextView versionCheckout = (TextView) rootView
                .findViewById(R.id.lbl_version);
        versionCheckout.setText("Versão Aplicativo: "+BuildConfig.VERSION_NAME);


        TextView release_date = (TextView) rootView.findViewById(R.id.lbl_release_date);

        try {
            //String daa=getResources().getString(R.string.zoop_release_date);
            release_date.setText("Última atualização em: "+String.valueOf(Extras.getDateFromTimestampStringAtTimezone( getResources().getString(R.string.zoop_release_date), TimeZone.getDefault().getID())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rootView;
    }
}