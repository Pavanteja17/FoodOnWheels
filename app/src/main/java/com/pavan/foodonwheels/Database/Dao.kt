package com.pavan.foodonwheels.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)
    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)
    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants():List<RestaurantEntity>
    @Query("SELECT * FROM restaurants WHERE res_id=:res_id")
    fun getRestaurantById(res_id:String):RestaurantEntity
}