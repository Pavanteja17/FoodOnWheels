package com.pavan.foodonwheels.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val res_id:String,
    @ColumnInfo(name = "restaurant_name") val  name:String,
    @ColumnInfo(name="rating") val rating:String,
    @ColumnInfo(name="cost_for_one") val cost_for_one:String,
    @ColumnInfo(name="image") val image:String

)
