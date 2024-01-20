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



        post.setOnClickListener { upload() }




        return view
    }

    private fun upload() {
        var houseprice: String = price.getText().toString().trim()
        var housesize: String = sqft.getText().toString().trim()
        var houseromms: String = roomamount.getText().toString().trim()

        val timeInMillis = System.currentTimeMillis()
        // Remove leading and trailing spaces
        houseprice = houseprice.trim { it <= ' ' }
        houseromms = houseromms.trim { it <= ' ' }
        housesize = housesize.trim { it <= ' ' }

        // Remove multiple consecutive spaces
        houseprice = houseprice.replace("\\s+".toRegex(), " ")
        houseromms = houseromms.replace("\\s+".toRegex(), " ")
        housesize = housesize.replace("\\s+".toRegex(), " ")
        if (houseprice.isEmpty()) {
           price.setError("empty")
            return
        }
        if (houseromms.isEmpty()) {
            roomamount.setError("empty")
            return
        }
        if (housesize.isEmpty()) {
            sqft.setError("empty")
            return
        }

Log.e("upload function", "finished all validation")


        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        Log.e("upload function", "got reference and put in val ref")
        val postId = ref.push().key
        Log.e("upload function", "got post id")
        val map = HashMap<String, Any?>()
        Log.e("upload function", "start of creating map")
        map["postId"] = postId
        map["price"] = houseprice
        map["roomamount"] = houseromms
        map["houseSize"] = housesize
        map["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
        map["timestamp"] = timeInMillis
        Log.e("upload function", "end of creating map")
        if (postId != null) {
            ref.child(postId).setValue(map).addOnSuccessListener {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                Log.e("upload function", "successfully added post")
                activity?.finish()
            }.addOnFailureListener {
                // Toast.makeText(requireContext(), "Failed to ask the question", Toast.LENGTH_SHORT).show()
                Log.e("upload function", "post failed")
            }
        }else{
            Toast.makeText(requireContext(), "error making post try again", Toast.LENGTH_SHORT).show()
            Log.e("upload function", "error making post try again")
        }

    }


}