package com.wit.assignment1.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wit.assignment1.Activities.MainActivity
import android.Manifest

import com.wit.assignment1.Model.House
import com.wit.assignment1.R

class AddHouseDetailsFragment : Fragment() {

    private lateinit var price: EditText
    private lateinit var roomamount: EditText
    private lateinit var sqft: EditText
    private lateinit var address: Button
    private lateinit var post: TextView
    private lateinit var spinnerHouseType: Spinner




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.fragment_add_house_details, container, false)

        price = view.findViewById(R.id.price)
        roomamount = view.findViewById(R.id.roomamount)
        sqft = view.findViewById(R.id.housesize)
        address = view.findViewById(R.id.address)
        post = view.findViewById(R.id.postbtn)
        spinnerHouseType = view.findViewById(R.id.houseTypeSpinner)

        val postf = arguments?.getSerializable("post") as? House
        if (post != null) {
            if (postf != null) {
                price.setText(postf.price.toString())
            }
            if (postf != null) {
                roomamount.setText(postf.roomamount.toString())
            }
            if (postf != null) {
                sqft.setText(postf.houseSize.toString())
            }
        }





        spinnerHouseType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedHouseType: String = parent?.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        post.setOnClickListener { upload() }


        return view
    }







    private fun upload() {
        var houseprice: Double = price.text.toString().toDouble()
        var housesize: Double = sqft.text.toString().toDouble()
        var houserooms: Int = roomamount.text.toString().toInt()

        val timeInMillis = System.currentTimeMillis()

        if (houseprice.isNaN()) {
            price.error = "Price cannot be empty"
            return

        }
        if (housesize.isNaN()) {
            sqft.error = "House size cannot be empty"
            return
        }
        if (houserooms == null) {
            roomamount.error = "Room amount cannot be empty"
            return
        }

        houseprice = houseprice
        houserooms = houserooms
        housesize = housesize

        houseprice = houseprice
        houserooms = houserooms
        housesize = housesize

        if (houseprice == null) {
            price.error = "empty"

        }
        if (houserooms == null) {
            roomamount.error = "empty"

        }
        if (housesize == null) {
            sqft.error = "empty"

        }


        val selectedHouseType: String = spinnerHouseType.selectedItem.toString()

        val ref = FirebaseDatabase.getInstance().getReference("Posts")


        val post: House? = arguments?.getSerializable("post") as? House
        if (post != null) {
            updatePost(ref, post, houseprice, houserooms, housesize, selectedHouseType)
        } else {
            createNewPost(ref, houseprice, houserooms, housesize, timeInMillis, selectedHouseType)
        }
    }

    private fun updatePost(
        ref: DatabaseReference,
        post: House,
        houseprice: Double,
        houserooms: Int,
        housesize: Double,
        selectedHouseType: String
    ) {
        val map = HashMap<String, Any?>()
        map["price"] = houseprice
        map["roomamount"] = houserooms
        map["houseSize"] = housesize
        map["houseType"] = selectedHouseType
        map["timestamp"] = System.currentTimeMillis()

        ref.child(post.postId).updateChildren(map)
            .addOnSuccessListener {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
            .addOnFailureListener {
                Log.e("UpdatePost", "Failed to update post")
            }
    }

    private fun createNewPost(
        ref: DatabaseReference,
        houseprice: Double,
        houserooms: Int,
        housesize: Double,
        timeInMillis: Long,
        selectedHouseType: String,

    ) {
        val postId = ref.push().key
        val map = HashMap<String, Any?>()
        map["postId"] = postId
        map["price"] = houseprice
        map["roomamount"] = houserooms
        map["houseSize"] = housesize
        map["houseType"] = selectedHouseType
        map["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
        map["timestamp"] = timeInMillis


        if (postId != null) {
            ref.child(postId).setValue(map)
                .addOnSuccessListener {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    activity?.finish()
                }
                .addOnFailureListener {
                    Log.e("CreateNewPost", "Failed to create new post")
                }
        } else {
            Toast.makeText(requireContext(), "Error making post. Try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
