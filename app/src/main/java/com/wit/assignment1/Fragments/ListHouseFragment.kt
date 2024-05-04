package com.wit.assignment1.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log.i
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wit.assignment1.Model.House
import com.wit.assignment1.R
import com.wit.assignment1.adapters.HouseAdapter
import java.text.DecimalFormat


class ListHouseFragment : Fragment()  {

    private lateinit var recyclerViewPosts: RecyclerView

    private var postAdapter: HouseAdapter? = null
    private lateinit var minPriceEditText:EditText
    private lateinit var maxPriceEditText:EditText
    private lateinit var minSizeEditText:EditText
    private lateinit var maxSizeEditText:EditText
    private lateinit var houseTypeSpinner:Spinner
    private lateinit var houseTypesArray: Array<String>
    private var postList: ArrayList<House>? = null
    private lateinit var filterbtn:FloatingActionButton
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_list_house, container, false)

        recyclerViewPosts = view.findViewById<RecyclerView>(R.id.recycler_view_posts)
        filterbtn = view.findViewById(R.id.filterbtn)
        recyclerViewPosts.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerViewPosts.layoutManager = linearLayoutManager

        postList = ArrayList<House>()
        postAdapter = context?.let { HouseAdapter(it, postList as ArrayList<House>) }
        recyclerViewPosts.adapter = postAdapter

        filterbtn.setOnClickListener{
            showPostfilterDialog()
        }

        readPosts()

        return view
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            }
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
                    postAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

    }


    private fun showPostfilterDialog() {
        val alertFilterDialog: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.fitlerpopup, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(alertFilterDialog)

      minPriceEditText = alertFilterDialog.findViewById<EditText>(R.id.minpriceedittext)
        maxPriceEditText = alertFilterDialog.findViewById<EditText>(R.id.maxpriceedittext)

        houseTypeSpinner = alertFilterDialog.findViewById<Spinner>(R.id.housetypespinner)
       houseTypesArray = resources.getStringArray(R.array.house_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, houseTypesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        houseTypeSpinner.adapter = adapter

      minSizeEditText = alertFilterDialog.findViewById<EditText>(R.id.minsizeedittext)
       maxSizeEditText = alertFilterDialog.findViewById<EditText>(R.id.maxsizeedittext)
        val applyButton = alertFilterDialog.findViewById<Button>(R.id.applyButton)

        val dialog = builder.create()

        // Make window behind popup transparent
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.gravity = Gravity.BOTTOM
        dialog.window?.attributes = layoutParams
        dialog.show()

        applyButton.setOnClickListener {
            val minPrice = minPriceEditText.text.toString().toDoubleOrNull() ?: Double.MIN_VALUE
            val maxPrice = maxPriceEditText.text.toString().toDoubleOrNull() ?: Double.MAX_VALUE
            val houseType = houseTypeSpinner.selectedItem.toString()
            val minSize = minSizeEditText.text.toString().toIntOrNull() ?: Int.MIN_VALUE
            val maxSize = maxSizeEditText.text.toString().toIntOrNull() ?: Int.MAX_VALUE
            saveFilterSelection(minPrice, maxPrice, houseType, minSize, maxSize)
            // Apply filters and dismiss the dialog
            applyFilters(minPrice, maxPrice, houseType, minSize, maxSize)
            restoreFilterSelection()
            dialog.dismiss()


        }
    }

    private fun applyFilters(
        minPrice: Double,
        maxPrice: Double,
        houseType: String,
        minSize: Int,
        maxSize: Int
    ) {
        val filteredList = postList?.filter { house ->
            val price = house.price.toString().toDoubleOrNull() ?: 0.0
            val size = house.houseSize.toString().toIntOrNull() ?: 0
            val type = house.houseType ?: ""

            price in minPrice..maxPrice && type == houseType && size in minSize..maxSize
        }
        if (filteredList != null) {
            postAdapter?.updateList(filteredList)
        }
    }


    private fun restoreFilterSelection() {
        val preferences = requireActivity().getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)
        val minPrice = preferences.getFloat("minPrice", Float.MIN_VALUE).toDouble()
        val maxPrice = preferences.getFloat("maxPrice", Float.MAX_VALUE).toDouble()
        val houseType = preferences.getString("houseType", "") ?: ""
        val minSize = preferences.getInt("minSize", Int.MIN_VALUE)
        val maxSize = preferences.getInt("maxSize", Int.MAX_VALUE)


        minPriceEditText.setText(minPrice.toString())
        maxPriceEditText.setText(maxPrice.toString())
        // Set selected item in spinner
        val houseTypePosition = houseTypesArray.indexOf(houseType)
        houseTypeSpinner.setSelection(houseTypePosition)
        minSizeEditText.setText(minSize.toString())
        maxSizeEditText.setText(maxSize.toString())
    }

    private fun saveFilterSelection(
        minPrice: Double,
        maxPrice: Double,
        houseType: String,
        minSize: Int,
        maxSize: Int
    ) {
        val preferences = requireActivity().getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putFloat("minPrice", minPrice.toFloat())
        editor.putFloat("maxPrice", maxPrice.toFloat())
        editor.putString("houseType", houseType)
        editor.putInt("minSize", minSize)
        editor.putInt("maxSize", maxSize)
        editor.apply()
    }




}


