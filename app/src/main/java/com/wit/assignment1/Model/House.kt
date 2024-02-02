package com.wit.assignment1.Model

data class House(
    val price: String,
    val roomamount: String,
    val houseSize: String
) {

    constructor() : this("0", "0", "0")
}