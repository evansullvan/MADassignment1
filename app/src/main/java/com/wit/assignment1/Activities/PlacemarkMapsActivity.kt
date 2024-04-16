package com.wit.assignment1.Activities

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wit.assignment1.Model.House
import com.wit.assignment1.R
import com.wit.assignment1.databinding.ActivityPlacemarkMapsBinding
import com.wit.assignment1.databinding.ContentPlacemarkMapsBinding
import java.io.IOException
import java.util.Locale

class PlacemarkMapsActivity : AppCompatActivity(),GoogleMap.OnMarkerClickListener {

    private lateinit var binding: ActivityPlacemarkMapsBinding
    private lateinit var contentBinding: ContentPlacemarkMapsBinding
    lateinit var map: GoogleMap

    private var postList: ArrayList<House>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlacemarkMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decor = window.decorView
            decor.systemUiVisibility = 0
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        }

        
        postList = ArrayList<House>()
readPosts()
        contentBinding = ContentPlacemarkMapsBinding.bind(binding.root)
        contentBinding.mapView.onCreate(savedInstanceState)

        contentBinding.mapView.getMapAsync {
            map = it
            configureMap()
        }

    }
    private fun configureMap() {
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        postList?.forEach {
            val loc = LatLng(it.lat, it.lng)
            val options = MarkerOptions().title(getAddressFromLocation(it.lat,it.lng)).position(loc)
            map.addMarker(options)?.tag = it.postId
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val postId = marker.tag as? String // Assuming postId is stored as the tag of the marker
        if (postId != null) {
            // Retrieve the post data from Firebase using postId
            FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val postMap = dataSnapshot.value as? Map<String, Any>
                        if (postMap != null) {
                            contentBinding.currentaddress.text = getAddressFromLocation(postMap["lat"] as Double, postMap["lng"] as Double)
                            contentBinding.currentprice.text = "£"+postMap["price"].toString()
                            contentBinding.rooms.text = postMap["roomamount"].toString()+"Rooms"
                            contentBinding.sqft.text = postMap["houseSize"].toString()+"sqft"
                            val imageUrl = postMap["image"] as? String
                            if (!imageUrl.isNullOrEmpty()) {
                                Picasso.get().load(imageUrl).into(contentBinding.currentImage)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error if needed
                    }
                })
        }
        return false
    }



    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }

    private fun readPosts() {
        FirebaseDatabase.getInstance().reference.child("Posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in dataSnapshot.children) {
                        val post: House? = snapshot.getValue(House::class.java)
                        if (post != null) {




                            postList!!.add(post!!)

                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double):String {
        val geocoder = Geocoder(this@PlacemarkMapsActivity, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressText: String = address.getAddressLine(0)
                // Update the TextView with the retrieved address
                return addressText
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "no address";
    }
}