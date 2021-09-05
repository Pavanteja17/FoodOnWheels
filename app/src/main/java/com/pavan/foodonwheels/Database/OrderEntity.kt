package com.pavan.foodonwheels.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderEntity(
    @PrimaryKey val item_id:String,
    @ColumnInfo(name="name") val name:String,
    @ColumnInfo(name="cost_for_one") val cost_for_one:String
)