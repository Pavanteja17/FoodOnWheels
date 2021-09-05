package com.pavan.foodonwheels.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pavan.foodonwheels.*
import com.pavan.foodonwheels.Adapter.RestaurantAdapter
import com.pavan.foodonwheels.Database.OrderDatabase
import com.pavan.foodonwheels.Database.OrderEntity
import com.pavan.foodonwheels.dataclass.RestaurantItems
import com.pavan.foodonwheels.util.ConnectionManager

class RestaurantDetails : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var proceedToCart:Button
    lateinit var toolbar: Toolbar
    lateinit var title:String
    var orderList= arrayListOf<RestaurantItems>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)
        val resId=intent.getStringExtra("resId")
         title=intent.getStringExtra("resName")
        recyclerView=findViewById(R.id.recycler_items)
        progressLayout=findViewById(R.id.progressLayout)
        progressBar=findViewById(R.id.progressBar)
        proceedToCart=findViewById(R.id.bt_proceedCart)
        proceedToCart.visibility=View.GONE
        progressLayout.visibility= View.VISIBLE
        progressBar.visibility=View.VISIBLE
        toolbar=findViewById(R.id.toolbar_resdetails)
        setUpToolbar()
        layoutManager=LinearLayoutManager(this@RestaurantDetails)
        val queue=Volley.newRequestQueue(this@RestaurantDetails)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"+resId
        if(ConnectionManager().checkConnectivity(this@RestaurantDetails)){
            var itemList= arrayListOf<RestaurantItems>(
                RestaurantItems(
                    "1",
                    "chicken",
                    "150"
                )
            )
            val jsonRequest=object :JsonObjectRequest(Method.GET,url,null, Response.Listener {
                val data=it.getJSONObject("data")
                val success=data.getBoolean("success")
                if(success){
                    progressLayout.visibility=View.GONE
                    val itemData=data.getJSONArray("data")
                    for(i in 0 until itemData.length()){
                        val infoJsonObject=itemData.getJSONObject(i)
                        val resItem=
                            RestaurantItems(
                                infoJsonObject.getString("id"),
                                infoJsonObject.getString("name"),
                                infoJsonObject.getString("cost_for_one")
                            )
                        itemList.add(resItem)
                    }
                    itemList.removeAt(0)
                    val restaurantAdapter=
                        RestaurantAdapter(
                            this@RestaurantDetails,
                            itemList,
                            object :
                                RestaurantAdapter.OnItemClickListener {
                                override fun onAddItemClick(restaurantItems: RestaurantItems) {
                                    orderList.add(restaurantItems)
                                    val orderItems = OrderEntity(
                                        restaurantItems.item_id,
                                        restaurantItems.name,
                                        restaurantItems.cost_for_one
                                    )
                                    val async =
                                        ItemsOfCart(applicationContext,
                                            orderItems,
                                            1
                                        ).execute().get()
                                    if (orderList.size > 0) {
                                        proceedToCart.visibility = View.VISIBLE
                                    }
                                }

                                override fun onRemoveItemClick(restaurantItems: RestaurantItems) {
                                    orderList.remove(restaurantItems)
                                    val orderItems = OrderEntity(
                                        restaurantItems.item_id,
                                        restaurantItems.name,
                                        restaurantItems.cost_for_one
                                    )
                                    val async =
                                        ItemsOfCart(
                                            applicationContext,
                                            orderItems,
                                            2
                                        ).execute().get()
                                    if (orderList.isEmpty()) {
                                        proceedToCart.visibility = View.GONE
                                    }
                                }
                            })
                    recyclerView.adapter=restaurantAdapter
                    recyclerView.layoutManager=layoutManager

                }
            },Response.ErrorListener {
                //here we handle errors
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
            Toast.makeText(this@RestaurantDetails,"No internet",Toast.LENGTH_LONG).show()
            val dialog=AlertDialog.Builder(this@RestaurantDetails).setCancelable(true)
            dialog.setTitle("Information")
            dialog.setMessage("No Internet, close App")
            dialog.setPositiveButton("OK"){_,_->
                finishAffinity()
            }
            dialog.create()
            dialog.show()
        }
        proceedToCart.setOnClickListener {
            val intent= Intent(this@RestaurantDetails,
                PlaceOrder::class.java)
            intent.putExtra("res_id",resId)
            intent.putExtra("resName",title)
            startActivity(intent)

        }
    }
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title=title
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onBackPressed() {
        if(orderList.size>0) {
            val dialog = AlertDialog.Builder(this@RestaurantDetails).setCancelable(false)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Items will be reset.Do you want to proceed")
            dialog.setPositiveButton("YES") { text, listener ->
                val async =
                    ItemsOfCart(
                        applicationContext,
                        OrderEntity("", "", ""),
                        3
                    ).execute().get()
                val intent=Intent(this@RestaurantDetails,
                    HomeActivity()::class.java)
                startActivity(intent)

            }
            dialog.setNegativeButton("No") { text, listener ->
                //No action
            }
            dialog.create()
            dialog.show()
        }
        else{
            val intent=Intent(this@RestaurantDetails,
                HomeActivity()::class.java)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
    class ItemsOfCart(context: Context,val orderItems:OrderEntity,val mode:Int):
            AsyncTask<Void,Void,Boolean>(){
        val db= Room.databaseBuilder(context,OrderDatabase::class.java,"order_items-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.OrderDao().insertOrder(orderItems)
                    db.close()
                    return true
                }
                2 -> {
                    db.OrderDao().deleteOrder(orderItems)
                    db.close()
                    return true
                }
                3 -> {
                    db.OrderDao().deleteOrders()
                    db.close()
                    return true
                }
            }
            return false

        }

    }

}