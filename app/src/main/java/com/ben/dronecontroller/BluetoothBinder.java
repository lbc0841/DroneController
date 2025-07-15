package com.ben.dronecontroller;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.net.wifi.aware.Characteristics;
import android.os.Binder;
import android.util.Log;

import androidx.annotation.NonNull;
import com.ben.dronecontroller.listeners.OnBleConnectionListener;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BluetoothBinder extends Binder {

    private final BluetoothAdapter btAdapter;
    private BluetoothGatt btGatt;
    private OnBleConnectionListener onBleConnectionListener;
    private final Context context;

    boolean alreadyEnableNotifications = false;

    public BluetoothBinder(Context _context){
        this.context = _context;
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // bluetooth gatt call back
    private final BluetoothGattCallback connectGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                    Log.d("BLE", "STATE_CONNECTED");
                    onBleConnectionListener.onConnectionStateChanged(true);
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BLE", "STATE_DISCONNECTED");
                    onBleConnectionListener.onConnectionStateChanged(false);
                }
            }
            else{
                onBleConnectionListener.onConnectionStateChanged(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                getCharacteristic(gatt);
                setCharacteristicNotification();
            }
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
            Log.d("BLE", "onCharacteristicRead");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            onBleConnectionListener.onReceiveData(characteristic.getValue());
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            onBleConnectionListener.onUpdateRssi(rssi);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Descriptor written successfully");
            } else {
                Log.d("BLE", "Failed to write descriptor, status: " + status);
            }
        }
    };

    public void connectToDevice(String address) {
        if(btGatt != null){
            btGatt.close();
            btGatt = null;
        }

        if (btAdapter == null || !btAdapter.isEnabled() || address == null ) {
            return;
        }

        try {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            btGatt = device.connectGatt(context, false, connectGattCallback);
        }
        catch (IllegalArgumentException ignored) {}
    }

    // get Service & Characteristic UUID
    private void getCharacteristic(BluetoothGatt gatt){
        List<BluetoothGattService> services = gatt.getServices();
        for (BluetoothGattService service : services) {
            Log.e("Service", String.valueOf(service.getUuid()));

            for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                Log.e("Characteristic", String.valueOf(characteristic.getUuid()));

                if((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0){
                    Log.e("Write Characteristic", String.valueOf(characteristic.getUuid()));
                }
            }
        }
    }


    public void TransmitData(byte[] _data){

        if(btGatt != null) {
            try {
                BluetoothGattService service = btGatt.getService(Constants.serviceUuid[3]);
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(Constants.service3CharacteristicUuid[1]);

                characteristic.setValue(_data);
                btGatt.writeCharacteristic(characteristic);
            }
            catch (Exception e) {
                Log.e("BLE Transmit", String.valueOf(e));
            }
        }
    }



    // enable Characteristic Notifications
    // make onCharacteristicChanged work
    public void setCharacteristicNotification() {
        if(!alreadyEnableNotifications){
            if (btGatt == null) return;

            BluetoothGattService service = btGatt.getService(Constants.serviceUuid[3]);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(Constants.service3CharacteristicUuid[0]);

            boolean notificationSet = btGatt.setCharacteristicNotification(characteristic, true);
            if (!notificationSet) return;

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            );

            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                boolean descriptorWrite = btGatt.writeDescriptor(descriptor);

                if (descriptorWrite) {
                    alreadyEnableNotifications = true;
                }
                else {
                    Log.e("BLE", "Failed to write descriptor for notifications");
                }
            }
            else {
                Log.e("BLE", "Descriptor not found for characteristic: " + characteristic.getUuid());
            }
        }

    }

    public void getRemoteRssi() {
        if(btGatt != null) {
            btGatt.readRemoteRssi();
        }
    }

    public void close() {
        if (btGatt != null) {
            btGatt.close();
            btGatt = null;
            onBleConnectionListener = null;
        }
    }

    public void setConnectionListener(OnBleConnectionListener listener) {
        onBleConnectionListener = listener;
    }
}
