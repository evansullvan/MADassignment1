package com.wit.assignment1.adapters

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wit.assignment1.Fragments.HouseItemClickListener
import com.wit.assignment1.Model.House
import com.wit.assignment1.R
import java.io.IOException
import java.util.Locale

class HouseAdapter(mContext: Context,
                   mPosts: List<House>,
                   private val itemClickListener: HouseItemClickListener? = null) : RecyclerView.Adapter<HouseAdapter.Viewholder>() {

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

        holder.Price.text = "€" + post.getFormattedPrice()
        holder.roomamount.text = post.roomamount.toString()+" rooms"
        holder.houseSize.text = post.houseSize.toString()+"sqft"
        holder.houseType.text =  post.houseType.toString()
        holder.houseaddress.text =  getAddressFromLocation(post.lat, post.lng)
        Picasso.get().load(post.image).placeholder(R.drawable.house_placeholder)
            .into(holder.houseImage)

        holder.itemView.setOnLongClickListener {
            showPopupMenu(holder.itemView, post)
            true
        }



    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double):String {
        val geocoder = Geocoder(mContext, Locale.getDefault())
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

    override fun getItemCount(): Int = mPosts.size
    private fun showPopupMenu(view: View, post: House) {
        val popupMenu = PopupMenu(mContext, view)
        popupMenu.menuInflater.inflate(R.menu.post_options_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_update -> {
                    itemClickListener?.onUpdateClick(post)
                    true
                }
                R.id.menu_delete -> {
                    itemClickListener?.onDeleteClick(post)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var Price: TextView
        var roomamount: TextView
        var houseSize: TextView
        var houseType: TextView
        var houseaddress: TextView
        var houseImage: ImageView

        init {
            //imageProfile = itemView.findViewById<ImageView>(R.id.image_profile)
            Price = itemView.findViewById(R.id.housePrice);
            roomamount = itemView.findViewById(R.id.houseroomamount);
            houseSize = itemView.findViewById(R.id.housesize);
            houseType = itemView.findViewById(R.id.housetype)
            houseaddress = itemView.findViewById(R.id.houseAddress)
            houseImage = itemView.findViewById(R.id.imageView)
        }
    }
}