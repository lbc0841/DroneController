package com.ben.dronecontroller.activities;

import com.ben.dronecontroller.Constants;
import com.ben.dronecontroller.adapters.BluetoothDeviceAdapter;
import com.ben.dronecontroller.R;
import com.ben.dronecontroller.databinding.ActivityMainBinding;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.Manifest;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private final ArrayList<BluetoothDevice> devicesList = new ArrayList<>();
    private final BluetoothDeviceAdapter deviceListAdapter = new BluetoothDeviceAdapter(MainActivity.this);
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter btAdapter;
    private static BluetoothLeScanner btScanner;

    private boolean scanning;

    private final Handler handler = new Handler();

    private int scanPeriod = 8000;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        binding.rescanButton.setOnClickListener(v -> {
            scanDevice();
        });

        binding.navView.setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_gyroTest){
                startActivity(new Intent(MainActivity.this, GyroTestActivity.class));
                return true;
            }
            else if(item.getItemId() == R.id.nav_bleTest){
//                startActivity(new Intent(MainActivity.this, BleConnectionTestActivity.class));
                if(devicesList.isEmpty()){
                    Toast.makeText(MainActivity.this, R.string.no_devices_found, Toast.LENGTH_SHORT).show();
                    scanDevice();
                }

                deviceListAdapter.setAdapterIntent(new Intent(MainActivity.this, BleConnectionTestActivity.class));
                chooseDeviceDialogBox();

                return true;
            }
            else if(item.getItemId() == R.id.nav_commandList){
                startActivity(new Intent(MainActivity.this, CommandListActivity.class));
                return true;
            }
            else if(item.getItemId() == R.id.nav_droneTest){
                if(devicesList.isEmpty()){
                    Toast.makeText(MainActivity.this, R.string.no_devices_found, Toast.LENGTH_SHORT).show();
                    scanDevice();
                }

                deviceListAdapter.setAdapterIntent(new Intent(MainActivity.this, DroneTestActivity.class));
                chooseDeviceDialogBox();

                return true;
            }
            else if(item.getItemId() == R.id.nav_options){
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                return true;
            }
            else if(item.getItemId() == R.id.nav_aboutApp){
                startActivity(new Intent(MainActivity.this, AboutAppActivity.class));
                return true;
            }

            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    private void init() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init permission
        initPermission();

        // init bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if(btAdapter == null){
            Toast.makeText(this, "設備不支援藍芽", Toast.LENGTH_LONG).show();
        }
        else if(btAdapter.isEnabled()){ // bluetooth not enable
            // open bluetooth
            if(!checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) return;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        binding.devicesCountHint.setVisibility(View.INVISIBLE);

        initNavigationView();
        initRecyclerView();

        getAppSettings();
    }

    private void getAppSettings() {
        SharedPreferences pref = getSharedPreferences("AppSettings", MODE_PRIVATE);
        scanPeriod =  pref.getInt(Constants.SCAN_PERIOD, 8000);
    }

    private void initNavigationView() {

        setSupportActionBar(binding.toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );

        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.navView.bringToFront();

    }

    private void initRecyclerView() {

        deviceListAdapter.setAdapterIntent(new Intent(MainActivity.this, RemoteControlActivity.class));
        binding.deviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.deviceRecyclerView.setHasFixedSize(true);
        binding.deviceRecyclerView.setItemViewCacheSize(16);

        binding.deviceRecyclerView.setAdapter(deviceListAdapter);
    }

    // permission
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            };

            ArrayList<String> missingPermissions = new ArrayList<>();

            for(String permission : permissions){
                if (!checkPermission(permission)) {
                    missingPermissions.add(permission);
                }
            }

            if(!missingPermissions.isEmpty()){
                ActivityCompat.requestPermissions(
                        this,
                        missingPermissions.toArray(new String[0]),
                        REQUEST_ENABLE_BT
                );
            }
        }
    }

    private boolean checkPermission(String permission){
        if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "缺少權限", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int results : grantResults){
            if(results != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "權限被拒絕", Toast.LENGTH_LONG).show();
                initPermission();
                return;
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void scanDevice() {
        if (!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) return;

        getAppSettings();

        Animation loadingAnim = AnimationUtils.loadAnimation(this, R.anim.anim_loading);
        binding.loadingBar.startAnimation(loadingAnim);

        devicesList.clear();
        deviceListAdapter.removeDevice();
        deviceListAdapter.notifyDataSetChanged();

        binding.noDevicesFoundHint.setVisibility(View.GONE);
        binding.devicesCountHint.setVisibility(View.VISIBLE);

        if (!scanning) {
            handler.postDelayed(this::stopScan, scanPeriod);

            scanning = true;
            btScanner.startScan(scanCallback);
        }
        else {
            stopScan();
        }
    }

    @SuppressLint("SetTextI18n")
    private void stopScan(){
        if (!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) return;

        scanning = false;
        binding.loadingBar.clearAnimation();

        if(devicesList.isEmpty()) {
            binding.noDevicesFoundHint.setVisibility(View.VISIBLE);
            binding.devicesCountHint.setVisibility(View.INVISIBLE);
        }
        else {
            binding.devicesCountText.setText("Found " + devicesList.size() + " Devices");
        }

        btScanner.stopScan(scanCallback);
    }

    private final ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) return;

            if(!devicesList.contains(result.getDevice()) && result.getDevice().getName() != null){

                devicesList.add(result.getDevice());

                deviceListAdapter.addDevice(result.getDevice());
                deviceListAdapter.notifyItemInserted(devicesList.size() -1);
            }
        }
    };


    // DialogBox
    private void chooseDeviceDialogBox(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_device);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = dialog.findViewById(R.id.deviceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setAdapter(deviceListAdapter);

        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceListAdapter.setAdapterIntent(new Intent(MainActivity.this, RemoteControlActivity.class));
                dialog.cancel();
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dialog.show();
    }
}