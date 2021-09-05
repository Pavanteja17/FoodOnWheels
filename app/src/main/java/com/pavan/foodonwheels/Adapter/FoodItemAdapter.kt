package com.pavan.foodonwheels.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.dataclass.FoodItems

class FoodItemAdapter(val context: Context, val itemList:ArrayList<FoodItems>): RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {
    class FoodItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodName: TextView = view.findViewById(R.id.historyFoodName)
        val foodCost: TextView = view.findViewById(R.id.historyCost)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_items_single_row, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val items=itemList[position]
        holder.foodName.text=items.foodName
        holder.foodCost.text="Rs. "+items.foodCost
    }
}