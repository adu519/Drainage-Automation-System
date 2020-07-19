package com.solution.robotcleaner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.preference.PowerPreference;
import com.solution.robotcleaner.databinding.ActivityAdminBinding;


public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean admin = PowerPreference.getDefaultFile().getBoolean("admin");
        if (!admin) {
            binding.batteryReport.setVisibility(View.GONE);
            binding.taskAllocation.setVisibility(View.GONE);
            binding.textView.setText("USER");
        } else {
            binding.taskAllocation.setOnClickListener(v -> {
                startActivity(new Intent(AdminActivity.this, TaskAllocationActivity.class));
            });
            binding.batteryReport.setOnClickListener(
                    v -> startActivity(new Intent(AdminActivity.this, BatteryReportActivity.class)));

        }


        binding.robotControl.setOnClickListener(
                v -> startActivity(new Intent(AdminActivity.this, RobotControlActivity.class)));

    }
}
