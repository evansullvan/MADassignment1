package com.wit.assignment1.Activities





import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.wit.assignment1.Fragments.AddHouseDetailsFragment
import com.wit.assignment1.Fragments.ListHouseFragment
import com.wit.assignment1.Fragments.ProfileFragment
import com.wit.assignment1.R

import java.util.Stack


class MainActivity : AppCompatActivity() {

    private val fragmentStack: Stack<Fragment> = Stack<Fragment>()

    private lateinit var mBottomNavigationView: BottomNavigationView
    private lateinit var selectedFragment: Fragment
    private val NAV_HOME: Int = com.wit.assignment1.R.id.nav_home
    private val NAV_ADD: Int = com.wit.assignment1.R.id.nav_add
    private val NAV_PROFILE: Int = R.id.nav_profile

    private lateinit var logout: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.wit.assignment1.R.layout.activity_main)

        FirebaseDatabase.getInstance("https://daftproject-77b1f-default-rtdb.europe-west1.firebasedatabase.app")
            .setPersistenceEnabled(true)



        logout = findViewById(R.id.logout)
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val fragment: Fragment = AddHouseDetailsFragment() // Replace with your actual fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(com.wit.assignment1.R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()




// Add the fragment to the stack
        fragmentStack.push(fragment)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decor: View = window.decorView
            decor.setSystemUiVisibility(0)
            window.statusBarColor = ContextCompat.getColor(this, com.wit.assignment1.R.color.blue)
        }
        if (currentUser == null) {
            // User is not signed in
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish() // This prevents the user from navigating back to the MainActivity
        } else {
            mBottomNavigationView = findViewById(com.wit.assignment1.R.id.bottom_nav)
            mBottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                val currentFragment: Fragment? =
                    supportFragmentManager.findFragmentById(com.wit.assignment1.R.id.fragment_container)
                val itemId = menuItem.itemId

                val selectedFragment: Fragment? = when (itemId) {
                    NAV_HOME -> ListHouseFragment()
                    NAV_ADD -> AddHouseDetailsFragment()
                    NAV_PROFILE -> ProfileFragment()
                    else -> null
                }

                if (selectedFragment != null) {
                    // Push the current fragment to the stack before replacing it with the selected fragment
                    fragmentStack.push(currentFragment)
                    supportFragmentManager.beginTransaction()
                        .replace(com.wit.assignment1.R.id.fragment_container, selectedFragment)
                        .commit()
                }
                true
            }

            supportFragmentManager.beginTransaction()
                .replace(com.wit.assignment1.R.id.fragment_container, ListHouseFragment()).commit()


            logout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}