package com.ben.dronecontroller.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ben.dronecontroller.BluetoothBinder;
import com.ben.dronecontroller.BluetoothService;
import com.ben.dronecontroller.Constants;
import com.ben.dronecontroller.DroneCommandHandler;
import com.ben.dronecontroller.databinding.ActivityDroneTestBinding;
import com.ben.dronecontroller.listeners.OnBleConnectionListener;

import java.util.Locale;

public class DroneTestActivity extends AppCompatActivity implements OnBleConnectionListener{

    private ActivityDroneTestBinding binding;

    private DroneCommandHandler commandHandler;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private final Handler transmitHandler = new Handler(Looper.getMainLooper());
    private final int transmitDelay = 200;
    
    private String deviceAddress = "";
    private String deviceName = "";

    private int speedFr, speedFl, speedBr, speedBl;

    private int targetPitch;
    private int targetRoll;

    private float ax, ay, az;
    private float gx, gy, gz;

    private float pitch;
    private float roll;

    private int kp, ki, kd;

    // transmit command runnable
    private final Runnable transmitMotorCommandRunnable = new Runnable(){
        @Override
        public void run() {
            commandHandler.setMotorSpeed(speedFr, speedFl, speedBr, speedBl);
        }
    };

    private final Runnable transmitPidCommandRunnable = new Runnable(){
        @Override
        public void run() {
            commandHandler.setPidGain(kp, ki, kd);
        }
    };

    private final Runnable transmitTargetAngleCommandRunnable = new Runnable(){
        @Override
        public void run() {
            commandHandler.setTargetAngle(targetPitch, targetRoll);
        }
    };

    private BluetoothBinder btBinder;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            btBinder = (BluetoothBinder) iBinder;
            btBinder.setConnectionListener(DroneTestActivity.this);
            btBinder.connectToDevice(deviceAddress);

            // init CommandHandler
            commandHandler = new DroneCommandHandler(btBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            btBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Motor ===================================================================================
        binding.seekbarMotorFr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedFr = progress;
                binding.speedFr.setText(String.format(Locale.getDefault(), "%3d", progress));
                binding.speedAll.setText(" - ");

                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.postDelayed(transmitMotorCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.post(transmitMotorCommandRunnable);
            }
        });
        binding.seekbarMotorFl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedFl = progress;
                binding.speedFl.setText(String.format(Locale.getDefault(), "%3d", progress));
                binding.speedAll.setText(" - ");
                
                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.postDelayed(transmitMotorCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.post(transmitMotorCommandRunnable);
            }
        });
        binding.seekbarMotorBr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedBr = progress;
                binding.speedBr.setText(String.format(Locale.getDefault(), "%3d", progress));
                binding.speedAll.setText(" - ");

                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.postDelayed(transmitMotorCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.post(transmitMotorCommandRunnable);
            }
        });
        binding.seekbarMotorBl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedBl = progress;
                binding.speedBl.setText(String.format(Locale.getDefault(), "%3d", progress));
                binding.speedAll.setText(" - ");

                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.postDelayed(transmitMotorCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.post(transmitMotorCommandRunnable);
            }
        });

        binding.seekbarMotorAll.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedFr = progress;
                speedFl = progress;
                speedBr = progress;
                speedBl = progress;

                binding.seekbarMotorFr.setProgress(progress);
                binding.seekbarMotorFl.setProgress(progress);
                binding.seekbarMotorBr.setProgress(progress);
                binding.seekbarMotorBl.setProgress(progress);

                binding.speedFr.setText(String.format(Locale.getDefault(), "%d", progress));
                binding.speedFl.setText(String.format(Locale.getDefault(), "%d", progress));
                binding.speedBr.setText(String.format(Locale.getDefault(), "%d", progress));
                binding.speedBl.setText(String.format(Locale.getDefault(), "%d", progress));
                binding.speedAll.setText(String.format(Locale.getDefault(), "%d", progress));

                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.postDelayed(transmitMotorCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitMotorCommandRunnable);
                transmitHandler.post(transmitMotorCommandRunnable);
            }
        });

        // TargetAngle =============================================================================
        binding.seekbarTargetPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetPitch = progress;
                binding.targetPitch.setText (String.format(Locale.getDefault(),"%d", progress));

                transmitHandler.removeCallbacks(transmitTargetAngleCommandRunnable);
                transmitHandler.postDelayed(transmitTargetAngleCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitTargetAngleCommandRunnable);
                transmitHandler.post(transmitTargetAngleCommandRunnable);
            }
        });
        binding.seekbarTargetRoll.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetRoll = progress;
                binding.targetRoll.setText (String.format(Locale.getDefault(),"%d", progress));

                transmitHandler.removeCallbacks(transmitTargetAngleCommandRunnable);
                transmitHandler.postDelayed(transmitTargetAngleCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitTargetAngleCommandRunnable);
                transmitHandler.post(transmitTargetAngleCommandRunnable);
            }
        });

        // PID =====================================================================================
        binding.seekbarKp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                kp = progress;
                binding.kp.setText(String.format(Locale.getDefault(), "%.2f", progress/100.0));

                transmitHandler.removeCallbacks(transmitPidCommandRunnable);
                transmitHandler.postDelayed(transmitPidCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitPidCommandRunnable);
                transmitHandler.post(transmitPidCommandRunnable);
            }
        });
        binding.seekbarKi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ki = progress;
                binding.ki.setText(String.format(Locale.getDefault(), "%.2f", progress/100.0));


                transmitHandler.removeCallbacks(transmitPidCommandRunnable);
                transmitHandler.postDelayed(transmitPidCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitPidCommandRunnable);
                transmitHandler.post(transmitPidCommandRunnable);
            }
        });
        binding.seekbarKd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                kd = progress;
                binding.kd.setText(String.format(Locale.getDefault(), "%.2f", progress/100.0));

                transmitHandler.removeCallbacks(transmitPidCommandRunnable);
                transmitHandler.postDelayed(transmitPidCommandRunnable, transmitDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmitHandler.removeCallbacks(transmitPidCommandRunnable);
                transmitHandler.post(transmitPidCommandRunnable);
            }
        });
    }

    void init() {
        // init binding
        binding = ActivityDroneTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deviceName = getIntent().getStringExtra("name");
        deviceAddress = getIntent().getStringExtra("address");

        // init angleView
        binding.angleView.updateValue(0, 0, 0, 0);
        binding.angleView.setMode(Constants.ChartDisplayMode.TARGET_ANGLE_CHART);

        // init BluetoothService
        Intent gattServiceIntent = new Intent(this, BluetoothService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    // OnBleConnectionListener
    @Override
    public void onConnectionStateChanged(boolean isConnected) {
        runOnUiThread(() -> {
            if(isConnected){
                Toast.makeText(this, "已連接到" + deviceName, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, deviceName + "連接失敗", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onUpdateRssi(long rssi) {}

    @Override
    public void onReceiveData(byte[] data) {
        if(data[0] == (byte) 0xAA) {

            // qmi8658
            if(data[1] == 0x02){
                ax = (float) (data[2] + data[3]/100.0);
                ay = (float) (data[4] + data[5]/100.0);
                az = (float) (data[6] + data[7]/100.0);

                gx = (float) (data[8] + data[9]/100.0);
                gy = (float) (data[10] + data[11]/100.0);
                gz = (float) (data[12] + data[13]/100.0);

                pitch = (float) (data[14] + data[15]/100.0);
                roll = (float) (data[16] + data[17]/100.0);

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.ax.setText(String.format(Locale.getDefault(), "\uD835\uDC4Ex : %.2f", ax));
                        binding.ay.setText(String.format(Locale.getDefault(), "\uD835\uDC4Ey : %.2f", ay));
                        binding.az.setText(String.format(Locale.getDefault(), "\uD835\uDC4Ez : %.2f", az));

                        binding.gx.setText(String.format(Locale.getDefault(), "ωx : %.2f", gx));
                        binding.gy.setText(String.format(Locale.getDefault(), "ωy : %.2f", gy));
                        binding.gz.setText(String.format(Locale.getDefault(), "ωz : %.2f", gz));

                        binding.pitch.setText(String.format(Locale.getDefault(), "pitch : %.2f", pitch));
                        binding.roll.setText(String.format(Locale.getDefault(), "roll : %.2f", roll));

                        binding.angleView.updateValue(targetPitch, targetRoll, pitch, roll);
                    }
                });
            }
        }
    }

    @Override
    public void onWriteSuccess() {

    }

    @Override
    public void onWriteFailure() {

    }
}