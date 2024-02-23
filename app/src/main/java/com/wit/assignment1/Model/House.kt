package com.wit.assignment1.Model

import java.io.Serializable
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

data class House(
    var price: Double = 0.0,
    val roomamount: String,
    val houseSize: String,
    val publisher: String,
    val houseType: String,
    val postId: String
):Serializable {

    constructor() : this(0.0, "0", "0","0","","0")

    fun getFormattedPrice(): String {
        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY) // Use Locale.GERMANY for Euro symbol
        formatter.currency = Currency.getInstance("EUR")
        formatter.minimumFractionDigits = 2 // Show two decimal places
        return formatter.format(price)
    }
}