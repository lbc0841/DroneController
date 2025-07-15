package com.ben.dronecontroller.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;

import com.ben.dronecontroller.Constants;
import com.ben.dronecontroller.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;

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

        binding.hideUnknownDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    saveChanges(Constants.HIDE_UNKNOWN_DEVICES, 1);
                }
                else{
                    saveChanges(Constants.HIDE_UNKNOWN_DEVICES, 0);
                }

            }
        });

        binding.scanPeriodEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length() != 0)
                {
                    saveChanges(Constants.SCAN_PERIOD, Integer.parseInt(s.toString()));
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.optionsScrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.scanPeriodEditText.clearFocus();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void init(){
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 讀取資料
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int scanPeriod = prefs.getInt(Constants.SCAN_PERIOD, 8000);
        int hideUnknownDevice = prefs.getInt(Constants.HIDE_UNKNOWN_DEVICES, 1);

        binding.scanPeriodEditText.setText( ""+scanPeriod );
        // 0 -> false | 1 -> true
        binding.hideUnknownDeviceSwitch.setChecked( hideUnknownDevice == 1 );

    }

    void saveChanges(String index, int value){
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(index, value);
        editor.apply();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();

            if(view != null){
                view.clearFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(SettingActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}