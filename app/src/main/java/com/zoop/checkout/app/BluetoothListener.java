package com.zoop.checkout.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by mainente on 22/04/15.
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public interface BluetoothListener {

    public static final String ACTION_DISCOVERY_STARTED = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
    public static final String ACTION_FOUND = BluetoothDevice.ACTION_FOUND;
    public static final String ACTION_DISCOVERY_FINISHED = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;

    public void action(String action);
}