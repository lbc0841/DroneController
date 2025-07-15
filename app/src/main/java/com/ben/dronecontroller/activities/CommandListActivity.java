package com.ben.dronecontroller.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ben.dronecontroller.databinding.ActivityCommandListBinding;

public class CommandListActivity extends AppCompatActivity {

    private ActivityCommandListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);

        init();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void init(){
        binding = ActivityCommandListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}