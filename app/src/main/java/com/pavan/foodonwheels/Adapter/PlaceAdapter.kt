package com.pavan.foodonwheels.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pavan.foodonwheels.Database.OrderEntity
import com.pavan.foodonwheels.R

class PlaceAdapter(val context: Context, val itemList:List<OrderEntity>): RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {
    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item: TextView = view.findViewById(R.id.order_item)
        val price:TextView=view.findViewById(R.id.order_price)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_single_row, parent, false)
        return PlaceViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val orderList=itemList[position]
        holder.item.text=orderList.name
        holder.price.text="Rs. "+orderList.cost_for_one

    }

}
