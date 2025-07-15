package com.ben.dronecontroller.adapters;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ben.dronecontroller.R;

import java.util.ArrayList;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> deviceArray = new ArrayList<>();
    private final Context adapterContext;
    private Intent adapterIntent;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceName, deviceAddress;
        private final LinearLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            deviceName = itemView.findViewById(R.id.deviceName);
            deviceAddress = itemView.findViewById(R.id.deviceAddress);
            root = itemView.findViewById(R.id.root);
        }

        public TextView getDeviceName(){ return deviceName; }
        public TextView getDeviceAddress(){ return deviceAddress; }
        public LinearLayout getRoot(){ return root; }
    }


    public BluetoothDeviceAdapter(Context context){
        adapterContext = context;
    }

    @NonNull
    @Override
    public BluetoothDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_device_view, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceAdapter.ViewHolder holder, int position) {

        String _deviceName = deviceArray.get(position).getName();
        String _deviceAddress = deviceArray.get(position).getAddress();
        if(_deviceName == null){
            _deviceName = "Unknown Device";
        }

        holder.getDeviceName().setText( _deviceName );
        holder.getDeviceAddress().setText( _deviceAddress );

        String final_deviceName = _deviceName;
        holder.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(adapterIntent != null){
                    adapterIntent.putExtra("name", final_deviceName);
                    adapterIntent.putExtra("address", _deviceAddress);
                    adapterContext.startActivity(adapterIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceArray.size();
    }

    public void addDevice(BluetoothDevice device){
        deviceArray.add(device);
    }

    public void removeDevice(){
        deviceArray.clear();
    }

    public void updateDeviceArray(ArrayList<BluetoothDevice> devices){
        deviceArray = devices;
    }

    public void setAdapterIntent(Intent intent){
        adapterIntent = intent;
    }
}
