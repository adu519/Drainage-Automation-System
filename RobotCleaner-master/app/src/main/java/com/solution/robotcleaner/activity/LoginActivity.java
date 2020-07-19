package com.solution.robotcleaner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.preference.PowerPreference;
import com.solution.robotcleaner.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private String[] userType = new String[]{"USER", "ADMIN"};
    private boolean admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.login.setOnClickListener(v -> {
            String user = binding.username.getText().toString();
            String password = binding.password.getText().toString();
            if (password.isEmpty() || password.length() < 6) {
                binding.password.setError("Enter valid password");
                return;
            }
            if (user.isEmpty() || !user.contains("@")) {
                binding.username.setError("Enter valid Email address");
            }
            FirebaseDatabase.getInstance().getReference("users").child(user.split("@")[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("type")) {
                        boolean admin = dataSnapshot.child("type").getValue(Boolean.class);
                        if (admin) {

                            FirebaseAuth.getInstance().signInWithEmailAndPassword(user, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            PowerPreference.getDefaultFile().setBoolean("admin", admin);
                                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });


                        } else if (binding.spinner.getSelectedItemPosition() == 0) {
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(user, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            PowerPreference.getDefaultFile().setBoolean("admin", admin);
                                            PowerPreference.getDefaultFile().setBoolean("subscribed", false);
                                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });


                        } else if (binding.spinner.getSelectedItemPosition() == 1) {
                            Toast.makeText(LoginActivity.this, "You are not an admin. Login as USER", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "You are not a normal user. Please Login as ADMIN", Toast.LENGTH_LONG).show();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, userType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setPrompt("Select User");
        binding.spinner.setSelection(0);


        binding.forgotPassword.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            final EditText input = new EditText(LoginActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(20, 10, 20, 10);
            input.setLayoutParams(lp);
            dialog.setView(input);
            dialog.setTitle("Enter email");
            dialog.setCancelable(false);
            dialog.setNegativeButton("Cancel", (d, i) -> {
                d.cancel();
            });
            dialog.setPositiveButton("Confirm", null);
            AlertDialog alertDialog = dialog.create();
            alertDialog.setOnShowListener(dialog1 -> {
                Button button = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(v1 -> {
                    String email = input.getText().toString();
                    if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                            String result;
                            if (task.isSuccessful())
                                result = "Please check your email for reset link";
                            else result = "Email address not found. Register first!!!";
                            dialog1.cancel();
                            Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();

                        });
                    } else {
                        input.setError("Invalid email");
                    }
                });
            });
            alertDialog.show();
        });
        binding.signUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
