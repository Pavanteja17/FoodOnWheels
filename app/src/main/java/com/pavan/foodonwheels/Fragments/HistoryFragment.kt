package com.pavan.foodonwheels.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pavan.foodonwheels.Adapter.HistoryAdapter
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.dataclass.OrderHistory
import com.pavan.foodonwheels.util.ConnectionManager
import java.lang.Exception


class HistoryFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager:RecyclerView.LayoutManager
    var orderList= arrayListOf<OrderHistory>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView=view.findViewById(R.id.recycler_history)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout.visibility=View.VISIBLE
        layoutManager=LinearLayoutManager(activity as Context)
        val sharedPreferences=(activity as Context).getSharedPreferences("meals",Context.MODE_PRIVATE)
        val userId=sharedPreferences.getString("user_id","")
        println("user id is $userId")
        val queue= Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/orders/fetch_result/"+userId
        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonRequest=object: JsonObjectRequest(Method.GET,url,null, Response.Listener {
                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")
                progressLayout.visibility = View.GONE
                try {
                    if (success) {
                        val dataItem = data.getJSONArray("data")
                        println("data is $dataItem")
                        for (i in 0 until dataItem.length()) {
                            val orderObject = dataItem.getJSONObject(i)
                            val foodItems = orderObject.getJSONArray("food_items")
                            val orderHistory = OrderHistory(
                                orderObject.getString("order_id"),
                                orderObject.getString("restaurant_name"),
                                orderObject.getString("order_placed_at"),
                                foodItems
                            )
                            orderList.add(orderHistory)
                        }
                        println("order list is $orderList")
                        val historyAdapter=HistoryAdapter(activity as Context,orderList)
                        recyclerView.adapter=historyAdapter
                        recyclerView.layoutManager=layoutManager
                    }
                }
                catch (e:Exception){
                    Toast.makeText(activity as Context,"some error occured",Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
                //we handle errors
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "301f1e436c2c8d"
                    return headers
                }

            }
            queue.add(jsonRequest)
        }
        else{
            val dialog= AlertDialog.Builder(activity as Context).setCancelable(true)
            dialog.setTitle("Information")
            dialog.setMessage("No Internet, close App")
            dialog.setPositiveButton("OK"){_,_->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }


}