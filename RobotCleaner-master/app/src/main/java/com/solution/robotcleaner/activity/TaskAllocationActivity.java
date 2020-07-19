package com.solution.robotcleaner.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.solution.robotcleaner.adapter.TaskViewAdapter;
import com.solution.robotcleaner.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class TaskAllocationActivity extends AppCompatActivity {
    private com.solution.robotcleaner.databinding.ActivityTaskAllocationBinding binding;
    private List<TaskModel> tasks;
    private TaskViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.solution.robotcleaner.databinding.ActivityTaskAllocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.taskListRecyclerView.setLayoutManager(new LinearLayoutManager(TaskAllocationActivity.this));
        tasks = new ArrayList<>();
        adapter = new TaskViewAdapter(tasks);
        binding.taskListRecyclerView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference("tasks").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tasks.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            tasks.add(snapshot.getValue(TaskModel.class));
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
}
