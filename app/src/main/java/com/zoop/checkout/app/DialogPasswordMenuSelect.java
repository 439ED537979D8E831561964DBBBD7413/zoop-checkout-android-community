package com.zoop.checkout.app;

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.commons.APIParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mainente on 19/01/17.
 */


public class DialogPasswordMenuSelect extends DialogFragment {
    Integer PositionValidatePassword;
    ExpandableListView expandableListView;
    public void setExpandableList(Integer PositionValidatePassword, ExpandableListView expandableListView){
        this.PositionValidatePassword = PositionValidatePassword;
        this.expandableListView = expandableListView;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_login_config, container,
                false);
        getDialog().setTitle("Validar por senha");
        Button loginbutton = (Button) rootView
                .findViewById(R.id.btn_login);

        Button cancelbutton = (Button) rootView
                .findViewById(R.id.btn_cancel);

        final EditText password = (EditText) rootView.findViewById(R.id.password);
        getDialog().setCancelable(false);
        loginbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Preferences demoPreferences = Preferences.getInstance();

                if (!(password.getText().toString().equals(""))) {

                    if (password.getText().toString().equals(APIParameters.getInstance().getStringParameter("currentLoggedinSecurityToken") )) {
                        if (PositionValidatePassword == 2) {
                            Intent intent = new Intent(getActivity(), ConfigPinPadActivity.class);
                            startActivity(intent);
                            expandableListView.collapseGroup(PositionValidatePassword);
                        }
                        getDialog().dismiss();
                        password.setText("");


                    } else {
                        Toast.makeText(getActivity(), "Senha inválida ",
                                Toast.LENGTH_SHORT).show();
                        expandableListView.collapseGroup(PositionValidatePassword);
                        getDialog().dismiss();
                        password.setText("");
                        expandableListView.expandGroup(PositionValidatePassword);

                    }
                } else {
                    Toast.makeText(getActivity(), "Senha não informada ",
                            Toast.LENGTH_SHORT).show();
                    expandableListView.collapseGroup(PositionValidatePassword);
                    getDialog().dismiss();
                    password.setText("");
                    expandableListView.expandGroup(PositionValidatePassword);

                }

            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getDialog().dismiss();
                password.setText("");
                expandableListView.collapseGroup(PositionValidatePassword);

            }

        });
        return rootView;
    }
}