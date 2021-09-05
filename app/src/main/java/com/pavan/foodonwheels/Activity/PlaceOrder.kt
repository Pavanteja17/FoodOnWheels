package com.pavan.foodonwheels.Activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.pavan.foodonwheels.Database.OrderDatabase
import com.pavan.foodonwheels.Database.OrderEntity
import com.pavan.foodonwheels.dataclass.FoodItem
import com.pavan.foodonwheels.Adapter.PlaceAdapter
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class PlaceOrder : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    val food_item_list= arrayListOf<FoodItem>()
    lateinit var subTotal:TextView
    lateinit var grandTotal:TextView
    lateinit var resName:TextView
    lateinit var placeOrder:Button
    lateinit var relativeLayout: RelativeLayout
    lateinit var okButton:Button
    lateinit var orderPreview:RelativeLayout
    var sum=0
    var count=1
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)
        relativeLayout=findViewById(R.id.success_layout)
        relativeLayout.visibility= View.GONE
        orderPreview=findViewById(R.id.order_preview)
        okButton=findViewById(R.id.bt_ok)
        subTotal = findViewById(R.id.sub_total)
        grandTotal = findViewById(R.id.grand_total)
        toolbar = findViewById(R.id.toolbar)
        placeOrder = findViewById(R.id.bt_placeorder)
        resName = findViewById(R.id.txt_placeorder)
        recyclerView = findViewById(R.id.recycler_order)
        layoutManager = LinearLayoutManager(this@PlaceOrder)
        resName.text = "Ordering from :" + intent.getStringExtra("resName")
        setUpToolBar()
        val sharedPreferences = getSharedPreferences("meals", Context.MODE_PRIVATE)
        val list = RetrieveOrders(
            applicationContext
        ).execute().get()
        val id=list[0]
        val recyclerAdapter =
            PlaceAdapter(this@PlaceOrder, list)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = layoutManager
        val gson = Gson()
        val orderlist = gson.toJson(list)
        val jsonArray = JSONArray(orderlist)
        for (i in 0 until jsonArray.length()) {
            val data = jsonArray.getJSONObject(i)
            val cost = data.getString("cost_for_one")
            sum = sum + cost.toInt()
            val item_list = FoodItem(
                data.getString("item_id")
            )
            food_item_list.add(item_list)
        }

        subTotal.text = "Rs. "+sum.toString()
        grandTotal.text = "Rs. "+sum.toString()
        val itemList = gson.toJson(food_item_list)
        val itemArray = JSONArray(itemList)
        val queue = Volley.newRequestQueue(this@PlaceOrder)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        val jsonParams = JSONObject()
        jsonParams.put("user_id", sharedPreferences.getString("user_id", ""))
        jsonParams.put("restaurant_id", intent.getStringExtra("res_id"))
        jsonParams.put("total_cost",sum.toString())
        jsonParams.put("food", itemArray)
        placeOrder.setOnClickListener {
            if(ConnectionManager().checkConnectivity(this@PlaceOrder)) {
                val jsonRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            orderPreview.visibility=View.GONE
                            relativeLayout.visibility=View.VISIBLE
                            count=0
                            okButton.setOnClickListener {
                                val result=
                                    DeleteOrders(
                                        applicationContext
                                    ).execute().get()
                                val intent= Intent(this@PlaceOrder,
                                    HomeActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }, Response.ErrorListener { }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "301f1e436c2c8d"
                            return headers
                        }
                    }
                queue.add(jsonRequest)
            }
            else
            {
                Toast.makeText(this@PlaceOrder,"No network",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        if(count==0){
            //no action
        }
        else{
            super.onBackPressed()
        }
    }
    fun setUpToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="My cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
    class RetrieveOrders(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {

        override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order_items-db").build()
            val pass=db.OrderDao().getAllOrders()
            db.close()
            return pass
        }

    }
    class DeleteOrders(val context: Context):AsyncTask<Void,Void,Boolean>(){
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order_items-db").build()
            db.OrderDao().deleteOrders()
            db.close()
            return true
        }

    }

}