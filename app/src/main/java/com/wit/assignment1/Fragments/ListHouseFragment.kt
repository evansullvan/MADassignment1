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


class ListHouseFragment : Fragment(), HouseItemClickListener {

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
        postAdapter = context?.let { HouseAdapter(it, postList as ArrayList<House>, this) }
        recyclerViewPosts.adapter = postAdapter

        readPosts()

        return view
    }
    override fun onDeleteClick(post: House) {
        // Handle delete action
        val postReference = FirebaseDatabase.getInstance().reference.child("Posts").child(post.postId)
        postReference.removeValue()
    }

    override fun onUpdateClick(post: House) {
        // Handle update action
        val addPostFragment = AddHouseDetailsFragment()

        // Pass the post details to the fragment
        val bundle = Bundle()
        bundle.putSerializable("post", post)
        addPostFragment.arguments = bundle

        // Navigate to the AddPostFragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, addPostFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun readPosts() {
        FirebaseDatabase.getInstance().reference.child("Posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in dataSnapshot.children) {
                        val post: House? = snapshot.getValue(House::class.java)
                        if (post != null) {
                            if (post.publisher.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
                                postList!!.add(post!!)
                            }
                        }
                    }
                    postAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error if needed
                }
            })

    }

}

interface HouseItemClickListener {
    fun onDeleteClick(post: House)
    fun onUpdateClick(post: House)
}
class HouseAdapter(mContext: Context, mPosts: List<House>,  private val itemClickListener: HouseItemClickListener) : RecyclerView.Adapter<HouseAdapter.Viewholder>() {

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

        holder.Price.text = "Price: "+post.price.toString()
        holder.roomamount.text = "Room amount: "+post.roomamount.toString()
        holder.houseSize.text ="House size: "+ post.houseSize

        holder.itemView.setOnLongClickListener {
            showPopupMenu(holder.itemView, post)
            true
        }



    }

    override fun getItemCount(): Int = mPosts.size
    private fun showPopupMenu(view: View, post: House) {
        val popupMenu = PopupMenu(mContext, view)
        popupMenu.menuInflater.inflate(R.menu.post_options_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_update -> {
                    itemClickListener.onUpdateClick(post)
                    true
                }
                R.id.menu_delete -> {
                    itemClickListener.onDeleteClick(post)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var imageProfile: ImageView
        var Price: TextView
        var roomamount: TextView
        var houseSize: TextView

        init {
            //imageProfile = itemView.findViewById<ImageView>(R.id.image_profile)
         Price = itemView.findViewById(R.id.housePrice);
            roomamount = itemView.findViewById(R.id.houseroomamount);
            houseSize = itemView.findViewById(R.id.housesize);
        }
    }
}