package com.wit.assignment1.Model

import java.io.Serializable

data class House(
    val price: String,
    val roomamount: String,
    val houseSize: String,
    val publisher: String,
    val houseType: String,
    val postId: String
):Serializable {

    constructor() : this("0", "0", "0","0","","0")
}