package com.wit.assignment1.Activities

import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.wit.assignment1.R


class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decor = window.decorView
            decor.systemUiVisibility = 0
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        }


        // how to use gif in slasph screen tutorial by https://www.youtube.com/watch?v=IpNLx75b0hE
       val source: ImageDecoder.Source = ImageDecoder.createSource(
           resources, R.drawable.onlygafs
       )
        val drawable:Drawable = ImageDecoder.decodeDrawable(source)

        val gifView:ImageView = findViewById(R.id.splashscreengif)
        gifView.setImageDrawable(drawable)
        (drawable as? AnimatedImageDrawable)?.start()
//-----------------------------------------------------------------------------------


        Handler().postDelayed({
            if (FirebaseAuth.getInstance().currentUser != null) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@SplashActivity, RegisterActivity::class.java))
                finish()
            }
        }, SPLASH_DELAY)


    }


}