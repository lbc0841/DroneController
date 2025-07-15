package com.ben.dronecontroller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ben.dronecontroller.BluetoothBinder;
import com.ben.dronecontroller.BluetoothService;
import com.ben.dronecontroller.R;
import com.ben.dronecontroller.databinding.ActivityRemoteControlBinding;
import com.ben.dronecontroller.listeners.OnBleConnectionListener;

import java.util.Objects;

public class RemoteControlActivity extends AppCompatActivity implements SensorEventListener, OnBleConnectionListener {

    private  ActivityRemoteControlBinding binding;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    public static double pitch;
    public static double roll;

    private double pitchCalibration = 0.0;
    private double rollCalibration = 0.0;

    private BluetoothBinder bluetoothBinder;

    private String deviceAddress = "";
    private String deviceName = "";

    private boolean gyroLocked = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            bluetoothBinder = (BluetoothBinder) iBinder;
            if (bluetoothBinder != null) {

                bluetoothBinder.connectToDevice(deviceAddress);
                bluetoothBinder.setConnectionListener(RemoteControlActivity.this);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothBinder = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        Intent gattServiceIntent = new Intent(this, BluetoothService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        handler.postDelayed(sendDataRunnable, 500);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                pitchCalibration = pitch;
                rollCalibration = roll;
            }
        });
        binding.lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gyroLocked = !gyroLocked;

                if(gyroLocked){
                    binding.lockButton.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_lock,getTheme()));
                    binding.lockButton.setText(R.string.unlock_gyro);
                }
                else{
                    binding.lockButton.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_lock_open,getTheme()));
                    binding.lockButton.setText(R.string.lock_gyro);
                }
            }
        });
    }

    @Override @SuppressLint("DefaultLocale")
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // 計算傾斜角度
            // θx = [arctan( Ax / squr(Ay*Ay + Az*Az) )] *180/π
            // θy = [arctan( Ay / squr(Ax*Ax + Az*Az) )] *180/π
            pitch = Math.atan2(x, Math.sqrt(y * y + z * z)) * 180 / Math.PI;
            roll = Math.atan2(y, Math.sqrt(x * x + z * z)) * 180 / Math.PI;

            // Reset
            float newPitch = (float) (pitch - pitchCalibration);
            float newRoll = (float) (roll - rollCalibration);

            if(!gyroLocked){
                binding.pitchRollView.updatePoint(newPitch, newRoll);
                int[] v = binding.pitchRollView.getVelocity();
            }

        }

    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (bluetoothBinder != null) {
            bluetoothBinder.connectToDevice(deviceAddress);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void init() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        binding = ActivityRemoteControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deviceName = getIntent().getStringExtra("name");
        deviceAddress = getIntent().getStringExtra("address");

        binding.deviceName.setText(deviceName);
        binding.deviceAddress.setText(deviceAddress);
    }

    private final Handler handler = new Handler();
    private final Runnable sendDataRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] data = new byte[2];
            data[0] = (byte) binding.pitchRollView.getVelocity()[1];
            data[1] = (byte) binding.pitchRollView.getVelocity()[2];

            bluetoothBinder.TransmitData(data);
//            bluetoothBinder.getRemoteRssi();

            handler.postDelayed(this, 500);
        }
    };


    // OnBleConnectionListener
    @Override
    public void onConnectionStateChanged(boolean isConnected) {
        if(!isConnected){
            this.runOnUiThread(new Runnable() {
                public void run() {
                    disconnectDialogBox();
                }
            });

        }
    }

    @Override @SuppressLint("SetTextI18n")
    public void onUpdateRssi(long rssi) {
        // d = 10^((P - RSSI) / (10 * n))
        // P = 1m Signal strength
        binding.rssi.setText("RSSI :  " + rssi + "  dBm");
    }

    @Override
    public void onReceiveData(byte[] value) {

    }

    @Override
    public void onWriteSuccess() {

    }

    @Override
    public void onWriteFailure() {

    }


    // DialogBox
    @SuppressLint("ResourceAsColor")
    private void disconnectDialogBox() {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ble_disconnected);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button okButton = dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}