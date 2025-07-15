package com.ben.dronecontroller.listeners;

public interface OnBleConnectionListener {
    void onConnectionStateChanged(boolean isConnected);
    void onUpdateRssi(long rssi);
    void onReceiveData(byte[] value);
    void onWriteSuccess();
    void onWriteFailure();

}
