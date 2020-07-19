package com.solution.robotcleaner.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.preference.PowerPreference
import com.solution.robotcleaner.R
import kotlinx.android.synthetic.main.activity_login_auth.*

class LoginAuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_auth)

        loginBtn.setOnClickListener {
            val emailVal = email.text.toString()
            val passVal = password.text.toString()

            if (TextUtils.isEmpty(emailVal) and !Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
                email.error = "Enter valid email"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(passVal) and (passVal.length < 6)) {
                password.error = "Min 6 letters needed"
                return@setOnClickListener
            }
            FirebaseDatabase.getInstance().getReference("users").child(emailVal.split("@")[0])
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChild("type")) {
                                val admin: Boolean? = p0.child("type").getValue(Boolean::class.java)

                                if (!admin!!) {
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailVal, passVal)
                                            .addOnCompleteListener { task: Task<AuthResult?> ->
                                                if (task.isSuccessful) {
                                                    PowerPreference.getDefaultFile().setBoolean("admin", false)
                                                    PowerPreference.getDefaultFile().setBoolean("subscribed", false)
                                                    val intent = Intent(this@LoginAuthActivity, MapsActivity::class.java)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    Snackbar.make(window.decorView.rootView, "Authentication Failure", Snackbar.LENGTH_LONG).show()
                                                }
                                            }

                                } else Snackbar.make(window.decorView.rootView, "You are not an admin", Snackbar.LENGTH_LONG).show()

                            }
                        }


                    })
        }

        adminLogin.setOnClickListener {
            val emailVal = email.text.toString()
            val passVal = password.text.toString()

            if (TextUtils.isEmpty(emailVal) and !Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
                email.error = "Enter valid email"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(passVal) and (passVal.length < 6)) {
                password.error = "Min 6 letters needed"
                return@setOnClickListener
            }
            FirebaseDatabase.getInstance().getReference("users").child(emailVal.split("@")[0])
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChild("type")) {
                                val admin: Boolean? = p0.child("type").getValue(Boolean::class.java)

                                if (admin!!) {
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailVal, passVal)
                                            .addOnCompleteListener { task: Task<AuthResult?> ->
                                                if (task.isSuccessful) {
                                                    PowerPreference.getDefaultFile().setBoolean("admin", true)
                                                    PowerPreference.getDefaultFile().setBoolean("subscribed", false)
                                                    val intent = Intent(this@LoginAuthActivity, MapsActivity::class.java)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    Snackbar.make(window.decorView.rootView, "Authentication failed", Snackbar.LENGTH_LONG).show()
                                                }
                                            }
                                } else {
                                    Snackbar.make(window.decorView.rootView, "You are not an admin", Snackbar.LENGTH_LONG).show()
                                }

                            }
                        }


                    })
        }

        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterAuthActivity::class.java))
        }
        forgotPassword.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val input = EditText(this)
            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            lp.setMargins(20, 10, 20, 10)
            input.layoutParams = lp
            dialog.setView(input)
            dialog.setTitle("Enter email")
            dialog.setCancelable(false)
            dialog.setNegativeButton("Cancel") { d: DialogInterface, i: Int -> d.cancel() }
            dialog.setPositiveButton("Confirm", null)
            val alertDialog = dialog.create()
            alertDialog.setOnShowListener { dialog1: DialogInterface ->
                val button = (dialog1 as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                button.setOnClickListener {
                    val email = input.text.toString()
                    if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task: Task<Void?> ->

                            val result = if (task.isSuccessful) "Please check your email for reset link" else "Email address not found. Register first!!!"
                            dialog1.cancel()
                            Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        input.error = "Invalid email"
                    }
                }
            }
            alertDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                val intent = Intent(this, MapsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
    }
}
