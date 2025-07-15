package com.ben.dronecontroller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothService extends Service {

    private BluetoothBinder bluetoothBinder;

    @Override
    public IBinder onBind(Intent intent) {

        bluetoothBinder = new BluetoothBinder(this);
        return bluetoothBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bluetoothBinder.close();
        return super.onUnbind(intent);
    }
}
