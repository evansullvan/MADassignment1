package com.wit.assignment1.Fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.wit.assignment1.Activities.MainActivity
import com.wit.assignment1.Activities.MapActivity
import com.wit.assignment1.Helpers.showImagePicker
import com.wit.assignment1.Model.House
import com.wit.assignment1.Model.Location
import com.wit.assignment1.R

class AddHouseDetailsFragment : Fragment() {

    private lateinit var price: EditText
    private lateinit var roomamount: EditText
    private lateinit var sqft: EditText
    private lateinit var address: TextView
    private lateinit var imageview: ImageView
    private lateinit var images: TextView
    private lateinit var post: TextView
    private lateinit var spinnerHouseType: Spinner
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var location = Location(52.245696, -7.139102, 15f)
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    lateinit var selectedImageUri:Uri
    var house = House()




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
        images = view.findViewById(R.id.images)
        post = view.findViewById(R.id.postbtn)
        imageview = view.findViewById(R.id.houseimageview)
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

        address.setOnClickListener{
            val launcherIntent = Intent(requireContext(), MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        images.setOnClickListener{
            showImagePicker(imageIntentLauncher)
        }





        spinnerHouseType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedHouseType: String = parent?.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        post.setOnClickListener { upload() }

        registerMapCallback()
        registerImagePickerCallback()
        return view
    }





    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {

                            location = result.data!!.extras?.getParcelable("location")!!

                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {

                            selectedImageUri = result.data!!.data!!
                            Picasso.get()
                                .load(selectedImageUri)
                                .into(imageview)


                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }


    private fun uploadImageToFirebase(selectedImageUri: Uri, listener: OnImageUploadListener) {
        val uploadedImageFilename = "image_${System.currentTimeMillis()}.jpg"
        val imageRef = FirebaseStorage.getInstance().reference.child("PostImages").child(uploadedImageFilename)

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image upload successful
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Get the download URL
                    val imageUrl = uri.toString()
                    // Remove Toast usage for better practice (consider logging instead)
                    listener.onUploadSuccess(imageUrl)
                }.addOnFailureListener { e ->
                    // Failed to get download URL
                    Log.e("ImageUpload", "Failed to get image URL", e)
                    listener.onUploadFailure()
                }
            }
            .addOnFailureListener { e ->
                // Image upload failed
                Log.e("ImageUpload", "Failed to upload image", e)
                listener.onUploadFailure()
            }
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
        map["lat"] = location.lat
        map["lng"] = location.lng

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

        uploadImageToFirebase(selectedImageUri, object : OnImageUploadListener{


            override fun onUploadSuccess(imageUrl: String?) {

                val postId = ref.push().key
                val map = HashMap<String, Any?>()
                map["postId"] = postId
                map["price"] = houseprice
                map["roomamount"] = houserooms
                map["houseSize"] = housesize
                map["houseType"] = selectedHouseType
                map["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                map["timestamp"] = timeInMillis
                map["lat"] = location.lat
                map["lng"] = location.lng
                map["image"] = imageUrl

                if (postId != null) {
                    ref.child(postId).setValue(map)
                        .addOnSuccessListener {
                            val context = context
                            startActivity(Intent(context, MainActivity::class.java))
                            activity?.finish()
                        }
                        .addOnFailureListener {
                            Log.e("CreateNewPost", "Failed to create new post")
                        }
                } else {
                    Toast.makeText(requireContext(), "Error making post. Try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onUploadFailure() {
                Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()

            }
        })

    }


    internal interface OnImageUploadListener {
        fun onUploadSuccess(imageUrl: String?)
        fun onUploadFailure()
    }
}