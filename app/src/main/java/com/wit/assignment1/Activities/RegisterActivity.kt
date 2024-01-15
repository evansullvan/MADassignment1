package com.wit.assignment1.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wit.assignment1.R


class RegisterActivity : AppCompatActivity() {

    private lateinit var useremail: EditText
    private lateinit var userpass: EditText
    private lateinit var btnRegister: Button
    private  var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        useremail = findViewById(R.id.userEmail)
        userpass = findViewById(R.id.userPassword)
        btnRegister = findViewById(R.id.RegisterBtn)

        mAuth = FirebaseAuth.getInstance();


        btnRegister.setOnClickListener {
            val txtEmail: String = useremail.getText().toString().trim()
            val txtPassword: String = userpass.getText().toString()

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
}