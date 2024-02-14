package com.wit.assignment1.Activities

import android.R.attr.password
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.wit.assignment1.R


class LoginActivity : AppCompatActivity() {


    private var mAuth: FirebaseAuth? = null
    private lateinit var useremail: EditText
    private lateinit var userpass: EditText
    private lateinit var btnRegister: Button
    private lateinit var btntoRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance();
        useremail = findViewById(R.id.userEmail)
        userpass = findViewById(R.id.userPassword)
        btnRegister = findViewById(R.id.LoginBtn)
        btntoRegister = findViewById(R.id.toRegister)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decor = window.decorView
            decor.systemUiVisibility = 0
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        }



        btntoRegister.setOnClickListener {

                startActivity(
                    Intent(this@LoginActivity, RegisterActivity::class.java).addFlags(
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
                mAuth!!.signInWithEmailAndPassword(txtEmail, txtPassword).addOnSuccessListener {

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { e ->

                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                }

        }
    }
}