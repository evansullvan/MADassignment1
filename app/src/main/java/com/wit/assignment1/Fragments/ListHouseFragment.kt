package com.wit.assignment1.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wit.assignment1.Model.House
import com.wit.assignment1.R
import java.text.DecimalFormat


class ListHouseFragment : Fragment()  {

    private lateinit var recyclerViewPosts: RecyclerView

    private var postAdapter: HouseAdapter? = null
    private var postList: ArrayList<House>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_list_house, container, false)

        recyclerViewPosts = view.findViewById<RecyclerView>(R.id.recycler_view_posts)
        recyclerViewPosts.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerViewPosts.layoutManager = linearLayoutManager

        postList = ArrayList<House>()
        postAdapter = context?.let { HouseAdapter(it, postList as ArrayList<House>) }
        recyclerViewPosts.adapter = postAdapter

        readPosts()

        return view
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



}


