package com.solution.robotcleaner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.preference.PowerPreference;
import com.solution.robotcleaner.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private String[] userType = new String[]{"USER", "ADMIN"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, userType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.userSpinner.setAdapter(adapter);
        binding.userSpinner.setPrompt("Select User");
        binding.userSpinner.setSelection(0);

        binding.registerbtn.setOnClickListener(v -> {
            String user = binding.registerUsername.getText().toString();
            String userPassword = binding.userPassword.getText().toString();
            if (userPassword.isEmpty() || userPassword.length() < 6) {
                binding.userPassword.setError("Enter valid userPassword");
                return;
            }
            if (user.isEmpty() || !user.contains("@")) {
                binding.registerUsername.setError("Enter valid Email address");
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(user.split("@")[0]).child("type").setValue(binding.userSpinner.getSelectedItemPosition() == 1)
                                    .addOnCompleteListener(t -> {
                                        if (t.isSuccessful()) {
                                            PowerPreference.getDefaultFile().setBoolean("admin", binding.userSpinner.getSelectedItemPosition() == 1);
                                            PowerPreference.getDefaultFile().setBoolean("subscribed", false);
                                            startActivity(new Intent(RegisterActivity.this, MapsActivity.class));
                                            finish();
                                        }
                                    });
                        }
                    });
        });
        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

}
