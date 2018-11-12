package com.zoop.checkout.app;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zoop.zoopandroidsdk.commons.APIParameters;

import java.lang.reflect.Method;


/**
 * Created by mainente on 22/04/15.
 */
public class PairPinPadActivity extends ZCLMenuWithHomeButtonActivity implements BluetoothListener {

    TextView teste;
    ProgressDialog dialog=null;

    private static final String TAG = "BuscaBluetooth";
    private Bluetooth bluetooth;
    private ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_pax);

        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth

            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 2);
            }/*else{
                mBluetoothAdapter.disable();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 2);
                IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                registerReceiver(mPairReceiver, intent);
            }*/


            try {

                bluetooth = Bluetooth.startFindDevices(this, this);
            } catch (Exception e) {
                Log.e(TAG, "Erro: ", e);
            }

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            ListView listView = (ListView) findViewById(R.id.listview);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                    RadioButton rb = (RadioButton) v.findViewById(R.id.rb_Choice);

                    String teste = arrayAdapter.getItem(arg2).toString();

                    String andress[]=teste.split("\n");


                    BluetoothDevice mDevice;
                    BluetoothAdapter mAdapter = null;

                    if (mAdapter == null)
                        mAdapter = BluetoothAdapter.getDefaultAdapter();

                    mDevice = mAdapter.getRemoteDevice(andress[1]);
                    pairDevice(mDevice);

                    arrayAdapter.notifyDataSetChanged();
                }
            });

            IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mPairReceiver, intent);




            //bluetooth = Bluetooth.startFindDevices(this, this);
        } catch (Exception e) {
            Log.e(TAG, "Erro: ", e);
        }

    /*    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                RadioButton rb = (RadioButton) v.findViewById(R.id.rb_Choice);

                String teste = arrayAdapter.getItem(arg2).toString();
                Toast.makeText(PairPinPadActivity.this, teste, Toast.LENGTH_LONG).show();
                arrayAdapter.notifyDataSetChanged();

            }
        });*/

        MultiplePermissionsListener dialogMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(this)
                        .withTitle("Permissão de localização")
                        .withMessage("Precisamos da permissão para acessar os dados da conexão com a maquininha")
                        .withButtonText(android.R.string.ok)
                        .build();

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(dialogMultiplePermissionsListener)
                .check();
    }

    @Override
    public void action(String action) {




        if (action.compareTo(ACTION_DISCOVERY_STARTED) == 0) {
            dialog= ProgressDialog.show(PairPinPadActivity.this,"Zoop","Procurando dispositivos", false, true);
            dialog.setCancelable(false);




        } else if (action.compareTo(ACTION_DISCOVERY_FINISHED) == 0) {
            preencherLista();
            dialog.dismiss();
        }
    }



    private void preencherLista() {

        for (BluetoothDevice device : bluetooth.getLista()) {
            String regexTerminalsAcceptedt = APIParameters.getInstance().getStringParameter("AS.regexTerminalsAcceptedt");
            if (device.getName().matches(regexTerminalsAcceptedt)) {
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

   /* public  void pairDevice(BluetoothDevice device) {
        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        Intent intent = new Intent(ACTION_PAIRING_REQUEST);
        String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
        intent.putExtra(EXTRA_DEVICE, device);

        String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
        int PAIRING_VARIANT_PIN = 0;
        intent.putExtra(EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivityForResult(intent, 0);


    }*/


   private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
       public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
           if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
           final int state= intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
           final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
               if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                   Toast.makeText(PairPinPadActivity.this,"Terminal pareado com sucesso",Toast.LENGTH_LONG).show();

                   Intent intentstart = new Intent(PairPinPadActivity.this, StartupActivity.class);
                   startActivity(intentstart );
                   } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                   }

               }
           }
       };


    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            try {

                bluetooth = Bluetooth.startFindDevices(this, this);
            } catch (Exception e) {
                Log.e(TAG, "Erro: ", e);
            }

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            ListView listView = (ListView) findViewById(R.id.listview);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                    RadioButton rb = (RadioButton) v.findViewById(R.id.rb_Choice);

                    String teste = arrayAdapter.getItem(arg2).toString();

                    String andress[]=teste.split("\n");


                    BluetoothDevice mDevice;
                    BluetoothAdapter mAdapter = null;

                    if (mAdapter == null)
                        mAdapter = BluetoothAdapter.getDefaultAdapter();

                    mDevice = mAdapter.getRemoteDevice(andress[1]);
                    pairDevice(mDevice);

                    arrayAdapter.notifyDataSetChanged();
                }
            });

        }
    }




}


