package com.wit.assignment1.Fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wit.assignment1.Activities.MainActivity
import com.wit.assignment1.Model.House
import com.wit.assignment1.R


class AddHouseDetailsFragment : Fragment() {

    private lateinit var price: EditText
    private lateinit var roomamount: EditText
    private lateinit var sqft: EditText
    private lateinit var address: Button
    private lateinit var post: Button
    private val mAuth: FirebaseAuth? = null
    private val mDatabase: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(com.wit.assignment1.R.layout.fragment_add_house_details, container, false)

        price = view.findViewById(R.id.price)
        roomamount = view.findViewById(R.id.roomamount)
        sqft = view.findViewById(R.id.housesize)
        address = view.findViewById(R.id.address)
        post = view.findViewById(R.id.postbtn)

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

        post.setOnClickListener { upload() }




        return view
    }

    private fun upload() {
        var houseprice: String = price.text.toString().trim()
        var housesize: String = sqft.text.toString().trim()
        var houserooms: String = roomamount.text.toString().trim()

        val timeInMillis = System.currentTimeMillis()

        houseprice = houseprice.trim()
        houserooms = houserooms.trim()
        housesize = housesize.trim()

        houseprice = houseprice.replace("\\s+".toRegex(), " ")
        houserooms = houserooms.replace("\\s+".toRegex(), " ")
        housesize = housesize.replace("\\s+".toRegex(), " ")

        if (houseprice.isEmpty()) {
            price.error = "empty"
            return
        }
        if (houserooms.isEmpty()) {
            roomamount.error = "empty"
            return
        }
        if (housesize.isEmpty()) {
            sqft.error = "empty"
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Posts")

        // Check if updating an existing post
        val post: House? = arguments?.getSerializable("post") as? House
        if (post != null) {
            updatePost(ref, post, houseprice, houserooms, housesize)
        } else {
            createNewPost(ref, houseprice, houserooms, housesize, timeInMillis)
        }
    }

    private fun updatePost(
        ref: DatabaseReference,
        post: House,
        houseprice: String,
        houserooms: String,
        housesize: String
    ) {
        val map = HashMap<String, Any?>()
        map["price"] = houseprice
        map["roomamount"] = houserooms
        map["houseSize"] = housesize
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
        houseprice: String,
        houserooms: String,
        housesize: String,
        timeInMillis: Long
    ) {
        val postId = ref.push().key
        val map = HashMap<String, Any?>()
        map["postId"] = postId
        map["price"] = houseprice
        map["roomamount"] = houserooms
        map["houseSize"] = housesize
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