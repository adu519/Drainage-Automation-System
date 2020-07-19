package com.solution.robotcleaner.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.solution.robotcleaner.R;
import com.solution.robotcleaner.databinding.ActivityBatteryReportBinding;
import com.solution.robotcleaner.model.Sensor;

public class BatteryReportActivity extends AppCompatActivity {
    private ActivityBatteryReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBatteryReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Drawable sensorBack = getDrawable(R.drawable.battery);
        sensorBack.setAlpha(50);
        binding.getRoot().setBackground(sensorBack);
        FirebaseDatabase.getInstance().getReference("battery").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Sensor sensor = dataSnapshot.getValue(Sensor.class);
                if (sensor != null) {
                    binding.batteryGas.setText(sensor.getGas());
                    binding.batteryCarbon.setText(sensor.getCarbonMonoxide());
                    binding.batteryMethane.setText(sensor.getMethane());
                    binding.batterySulphur.setText(sensor.getSulphur());
                    binding.batteryTemp.setText(sensor.getTemperature());
                    binding.batteryWater.setText(sensor.getWater());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
