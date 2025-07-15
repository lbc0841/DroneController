package com.ben.dronecontroller.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import com.ben.dronecontroller.databinding.ActivityGyroTestBinding;

import java.util.Locale;

public class GyroTestActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityGyroTestBinding binding;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    public static double pitch_gyro;
    public static double roll_gyro;

    public static double pitch_acc;
    public static double roll_acc;

    public static double pitch_fusion;
    public static double roll_fusion;

    private double pitchCalibration = 0.0;
    private double rollCalibration = 0.0;

    private long lastUpdateTime = 0;

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

        binding.resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                pitchCalibration = pitch_acc;
                rollCalibration = roll_acc;

                pitch_gyro = 0;
                roll_gyro = 0;
            }
        });
    }

    private void init() {

        // 初始化SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // 初始化感測器
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        binding = ActivityGyroTestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();

        // 加速度計
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // 取得加速度
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            // 計算傾斜角度
            pitch_acc = Math.atan2(ay, Math.sqrt( ax*ax + az * az)) * 180 / Math.PI;
            roll_acc  = Math.atan2(ax, Math.sqrt( ay*ay + az * az)) * 180 / Math.PI;

            // 顯示
            binding.accX.setText( String.format("\uD835\uDC4Ex : %.1f", fixNumber(ax)) );
            binding.accY.setText( String.format("\uD835\uDC4Ey : %.1f", fixNumber(ay)) );
            binding.accZ.setText( String.format("\uD835\uDC4Ez : %.1f", fixNumber(az)) );
        }

        // 陀螺儀
        else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            // 計算 △t(ms)
            double dt = 0;
            if (lastUpdateTime != 0) {
                dt = (double) (currentTime - lastUpdateTime)/1000.0;
            }
            lastUpdateTime = currentTime;

            // 取得角速度
            double gx = event.values[0];
            double gy = event.values[1];
            double gz = event.values[2];

            // 顯示
            binding.gyroX.setText( String.format("ωx : %.1f", fixNumber(gx)) );
            binding.gyroY.setText( String.format("ωy : %.1f", fixNumber(gy)) );
            binding.gyroZ.setText( String.format("ωz : %.1f", fixNumber(gz)) );

            // 計算弧度變化
            gx *= dt;
            gy *= dt;
            gz *= dt;

            // 弧度 -> 度數
            pitch_gyro = pitch_fusion + Math.toDegrees(gx);
            roll_gyro  = roll_fusion  - Math.toDegrees(gy);

        }

        // 角度融合
        degreeFusion();
    }

    // 角度融合
    @SuppressLint("DefaultLocale")
    private void degreeFusion() {

        // θfusion = k*θacc + (1-k)*θgyro
        float k = 0.1f;
        pitch_fusion = k *pitch_acc + (1- k)*pitch_gyro;
        roll_fusion  = k *roll_acc  + (1- k)*roll_gyro;

        binding.pitch.setText( String.format("Pitch : %6.1f", fixNumber(pitch_fusion - pitchCalibration)) );
        binding.roll.setText ( String.format("Roll  : %6.1f", fixNumber(roll_fusion - rollCalibration))   );

        // 圖形
        binding.pitchRollView.updateValue(
                (float) (roll_acc - rollCalibration),
                (float) (pitch_acc - pitchCalibration),
                (float) (roll_fusion - rollCalibration),
                (float) (pitch_fusion - pitchCalibration)
        );
    }

    // 修正數字 (去除負號)
    private float fixNumber(double in) {
        return (float) ((int)(in*10 + 0.5)) /10;
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope,     SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }




}