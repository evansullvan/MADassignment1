package com.wit.assignment1.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wit.assignment1.Model.House
import com.wit.assignment1.R


class ListHouseFragment : Fragment() {

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
        recyclerViewPosts.setAdapter(postAdapter)
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
                        post?.let { postList?.add(it) }
                    }
                    postAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error if needed
                }
            })

    }

}


class HouseAdapter(mContext: Context, mPosts: List<House>) : RecyclerView.Adapter<HouseAdapter.Viewholder>() {

    private val mContext: Context = mContext
    private val mPosts: List<House> = mPosts





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val activity = mContext as FragmentActivity
        val fragment = activity.supportFragmentManager.findFragmentById(R.id.fragment_container)
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.new_post_item, parent, false)
        return HouseAdapter.Viewholder(view)
    }



    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val post: House = mPosts!![position]
        val activity = mContext as FragmentActivity
        val fragment = activity.supportFragmentManager.findFragmentById(R.id.fragment_container)
    }

    override fun getItemCount(): Int = mPosts.size




    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var imageProfile: ImageView
        var Price: TextView
        var roomamount: TextView
        var address: TextView

        init {
            //imageProfile = itemView.findViewById<ImageView>(R.id.image_profile)
         Price = itemView.findViewById(R.id.housePrice);
            roomamount = itemView.findViewById(R.id.houseroomamount);
            address = itemView.findViewById(R.id.houseaddress);
        }
    }
}