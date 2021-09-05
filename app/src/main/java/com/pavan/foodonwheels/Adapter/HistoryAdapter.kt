package com.pavan.foodonwheels.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.dataclass.FoodItems
import com.pavan.foodonwheels.dataclass.OrderHistory

class HistoryAdapter(val context: Context, val itemList:ArrayList<OrderHistory>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ResName: TextView = view.findViewById(R.id.historyResName)
        val date: TextView = view.findViewById(R.id.historyDate)
        val recyclerView:RecyclerView=view.findViewById(R.id.recycler_food_items)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_history_single_row, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        var orderHistory = itemList[position]
        holder.ResName.text = orderHistory.restaurantName
        holder.date.text = orderHistory.time
        setRecyclerView(holder.recyclerView,orderHistory)
    }
    fun setRecyclerView(recyclerView: RecyclerView,orderHistory: OrderHistory){
        val foodItemList= arrayListOf<FoodItems>()
        for(i in 0 until orderHistory.foodItems.length()){
            val infoJson=orderHistory.foodItems.getJSONObject(i)
            val foodItems=FoodItems(
                infoJson.getString("food_item_id"),
                infoJson.getString("name"),
                infoJson.getString("cost")
            )
            foodItemList.add(foodItems)
        }
        val layoutManager=LinearLayoutManager(context)
        val foodItemAdapter=FoodItemAdapter(context,foodItemList)
        recyclerView.adapter=foodItemAdapter
        recyclerView.layoutManager=layoutManager

    }
}