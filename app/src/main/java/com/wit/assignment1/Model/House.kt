package com.wit.assignment1.Model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

data class House(
    var price: Double = 0.0,
    val roomamount: Int,
    val houseSize: Double = 0.0,
    val publisher: String,
    val houseType: String,
    val postId: String,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f
):Serializable {

    constructor() : this(0.0, 0, 0.0,"0","","0")

    fun getFormattedPrice(): String {
        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY) // Use Locale.GERMANY for Euro symbol
        formatter.currency = Currency.getInstance("EUR")
        formatter.minimumFractionDigits = 2 // Show two decimal places
        return formatter.format(price)
    }
}


data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeFloat(zoom)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }
}