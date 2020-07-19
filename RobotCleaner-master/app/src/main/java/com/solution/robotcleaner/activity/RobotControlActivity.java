package com.solution.robotcleaner.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.solution.robotcleaner.databinding.ActivityRobotControlBinding;

public class RobotControlActivity extends AppCompatActivity {
    private ActivityRobotControlBinding binding;
    private DatabaseReference database;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRobotControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance().getReference("robot");
        binding.arrowDown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                database.child("down").setValue(1);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                database.child("down").setValue(0);
            }
            return true;
        });

        binding.arrowRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                database.child("right").setValue(1);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                database.child("right").setValue(0);
            }
            return true;
        });

        binding.arrowLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                database.child("left").setValue(1);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                database.child("left").setValue(0);
            }
            return true;
        });

        binding.arrowUp.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                database.child("up").setValue(1);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                database.child("up").setValue(0);
            }
            return true;
        });

    }
}
