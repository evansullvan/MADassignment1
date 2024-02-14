package com.wit.assignment1.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.wit.assignment1.R


class RegisterActivity : AppCompatActivity() {

    private lateinit var useremail: EditText
    private lateinit var userpass: EditText
    private lateinit var btnRegister: Button
    private lateinit var btntoLogin: TextView
    private  var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        useremail = findViewById(R.id.userEmail)
        userpass = findViewById(R.id.userPassword)
        btnRegister = findViewById(R.id.RegisterBtn)
        btntoLogin = findViewById(R.id.toLogin)

        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decor = window.decorView
            decor.systemUiVisibility = 0
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        }



        btntoLogin.setOnClickListener {

                startActivity(
                    Intent(this@RegisterActivity, LoginActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                )

        }

        btnRegister.setOnClickListener {
            val txtEmail: String = useremail.getText().toString().trim()
            val txtPassword: String = userpass.getText().toString()
            if (txtEmail.isEmpty()) {
                useremail.error = "Email is required"
                return@setOnClickListener
            }

            if (txtPassword.isEmpty()) {
                userpass.error = "Password is required"
                return@setOnClickListener
            }
            mAuth!!.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnSuccessListener {

                val intent = Intent(
                    this@RegisterActivity,
                    MainActivity::class.java
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }.addOnFailureListener { e ->

                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT).show()
            }


        }


    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }
    }
}