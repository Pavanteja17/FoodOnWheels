package com.pavan.foodonwheels.Database

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities=[OrderEntity::class] ,version = 1)
abstract class OrderDatabase:RoomDatabase() {
    abstract fun OrderDao():OrderDao
}