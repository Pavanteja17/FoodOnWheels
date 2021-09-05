package com.pavan.foodonwheels.dataclass

import org.json.JSONArray
data class OrderHistory(
    val orderId:String,
    val restaurantName:String,
    val time:String,
    val foodItems:JSONArray
)