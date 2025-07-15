package com.ben.dronecontroller.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ben.dronecontroller.BluetoothBinder;
import com.ben.dronecontroller.BluetoothService;
import com.ben.dronecontroller.data_classes.MessageModel;
import com.ben.dronecontroller.R;
import com.ben.dronecontroller.adapters.DropdownAdapter;
import com.ben.dronecontroller.adapters.MessageAdapter;
import com.ben.dronecontroller.databinding.ActivityBleConnectionTestBinding;
import com.ben.dronecontroller.listeners.OnBleConnectionListener;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class BleConnectionTestActivity extends AppCompatActivity implements OnBleConnectionListener{
    private ActivityBleConnectionTestBinding binding;

    private final MessageAdapter messageAdapter = new MessageAdapter(BleConnectionTestActivity.this);

    private String deviceAddress = "";
    private String deviceName = "";

    private BluetoothBinder bluetoothBinder;

    private int messageCount = 0;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            bluetoothBinder = (BluetoothBinder) iBinder;
            if (bluetoothBinder != null) {

                bluetoothBinder.connectToDevice(deviceAddress);
                bluetoothBinder.setConnectionListener(BleConnectionTestActivity.this);

                binding.loadingProgressBar.setVisibility(View.GONE);
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
        initSpinner();
        initRecyclerView();

        Intent gattServiceIntent = new Intent(this, BluetoothService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NotifyDataSetChanged", "SimpleDateFormat"})
            @Override
            public void onClick(View v) {

                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                if(!binding.dataInputBox.getText().toString().isEmpty()){

                    messageCount += 1;
                    bluetoothBinder.TransmitData(binding.dataInputBox.getText().toString().getBytes());

                    messageAdapter.addMessage(new MessageModel(
                            binding.dataInputBox.getText().toString(),
                            df.format(Calendar.getInstance().getTime()),
                            0
                    ));
                    messageAdapter.notifyItemInserted(messageCount - 1);

                    binding.dataInputBox.setText(null);
                    binding.messageRecyclerView.scrollToPosition(messageCount - 1);
                }
            }
        });
    }

    private void init(){
        binding = ActivityBleConnectionTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deviceName = getIntent().getStringExtra("name");
        deviceAddress = getIntent().getStringExtra("address");
    }

    private void initSpinner(){

        ArrayList<String> textList = new ArrayList<>();
        textList.add(getResources().getString(R.string.text));
        textList.add(getResources().getString(R.string.byte_array_hex));

        ArrayList<Integer> iconList = new ArrayList<>();
        iconList.add(R.drawable.ic_text);
        iconList.add(R.drawable.ic_data_array);

        DropdownAdapter adapter = new DropdownAdapter(this, textList, iconList);

        adapter.setDropDownViewResource(R.layout.mode_dropdown);
        binding.modeSpinner.setAdapter(adapter);
    }

    private void initRecyclerView(){
        binding.messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.messageRecyclerView.setHasFixedSize(true);
        binding.messageRecyclerView.setItemViewCacheSize(16);

        binding.messageRecyclerView.setAdapter(messageAdapter);
    }

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

    @Override
    public void onUpdateRssi(long rssi) {}

    @Override @SuppressLint("SimpleDateFormat")
    public void onReceiveData(byte[] value) {
        messageCount += 1;

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        messageAdapter.addMessage(new MessageModel(
                byteArray_to_hexString(value),
                df.format(Calendar.getInstance().getTime()),
                1
        ));

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                messageAdapter.notifyItemInserted(messageCount - 1);
                binding.messageRecyclerView.scrollToPosition(messageCount - 1);
            }
        });
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

    //
    private String byteArray_to_hexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {

            int v = b & 0xFF;
            String hexString = Integer.toHexString(v);

            if (hexString.length() == 1) {
                sb.append('0');
            }
            sb.append(hexString).append(" ");
        }
        return sb.toString();
    }

    private String byteArray_to_utf8(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }

}