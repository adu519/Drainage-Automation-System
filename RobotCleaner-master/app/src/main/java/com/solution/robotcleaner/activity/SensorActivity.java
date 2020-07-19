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
import com.solution.robotcleaner.databinding.ActivitySensorBinding;
import com.solution.robotcleaner.model.Sensor;

public class SensorActivity extends AppCompatActivity {
    private ActivitySensorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySensorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Drawable sensorBack = getDrawable(R.drawable.sensor_bg);
        sensorBack.setAlpha(50);
        binding.getRoot().setBackground(sensorBack);

        FirebaseDatabase.getInstance().getReference("sensor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Sensor sensor = dataSnapshot.getValue(Sensor.class);
                if (sensor != null) {
                    binding.sensorGas.setText(sensor.getGas());
                    binding.sensorCarbon.setText(sensor.getCarbonMonoxide());
                    binding.sensorMethane.setText(sensor.getMethane());
                    binding.sensorSulphur.setText(sensor.getSulphur());
                    binding.sensorTemp.setText(sensor.getTemperature());
                    binding.sensorWater.setText(sensor.getWater());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
