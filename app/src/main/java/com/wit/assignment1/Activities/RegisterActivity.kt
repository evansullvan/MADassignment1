package com.wit.assignment1.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.wit.assignment1.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var useremail: EditText
    private lateinit var userpass: EditText
    private lateinit var btnRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        useremail = findViewById(R.id.userEmail)
        userpass = findViewById(R.id.userPassword)
        btnRegister = findViewById(R.id.RegisterBtn)


    }
}