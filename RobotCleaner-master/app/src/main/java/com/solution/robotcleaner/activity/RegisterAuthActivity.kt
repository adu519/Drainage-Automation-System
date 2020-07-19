package com.solution.robotcleaner.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.preference.PowerPreference
import com.solution.robotcleaner.R
import kotlinx.android.synthetic.main.activity_register_auth.*

class RegisterAuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_auth)

        goToSignIn.setOnClickListener {
            startActivity(Intent(this, LoginAuthActivity::class.java))
        }

        register.setOnClickListener {
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
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailVal, passVal)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(emailVal.split("@").toTypedArray().get(0)).child("type").setValue(false)
                                    .addOnCompleteListener { t: Task<Void?> ->
                                        if (t.isSuccessful) {
                                            PowerPreference.getDefaultFile().setBoolean("admin", false)
                                            PowerPreference.getDefaultFile().setBoolean("subscribed", false)
                                            startActivity(Intent(this@RegisterAuthActivity, MapsActivity::class.java))
                                            finish()
                                        }
                                    }
                        }
                    }
        }
    }
}
