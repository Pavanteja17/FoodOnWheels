package com.pavan.foodonwheels.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pavan.foodonwheels.Adapter.DashBoardAdapter
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.dataclass.Restaurants
import com.pavan.foodonwheels.util.ConnectionManager
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {
    lateinit var recyclerDashboard:RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashBoardAdapter
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    var resList= arrayListOf<Restaurants>()
    var checkedItem=-1
    var ratingComparator= Comparator<Restaurants>{ res1, res2->
        res1.rating.compareTo(res2.rating,true)
    }
    var costComparator= Comparator<Restaurants>{ res1, res2->
        res1.cost_for_one.compareTo(res2.cost_for_one,true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val view=inflater.inflate(R.layout.fragment_home, container, false)
        recyclerDashboard=view.findViewById(R.id.recycler_dashboard)
        layoutManager=LinearLayoutManager(activity)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout.visibility=View.VISIBLE
        progressBar.visibility=View.VISIBLE
        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            resList= arrayListOf<Restaurants>(
                Restaurants(
                    "102",
                    "pavan",
                    "5",
                    "5",
                    "300"
                )
            )
            val jsonRequest=object :JsonObjectRequest(Method.GET,url,null, Response.Listener {
                val data=it.getJSONObject("data")
                val success=data.getBoolean("success")
                println("success is $success")
                progressLayout.visibility=View.GONE
                if(success){
                    val data=data.getJSONArray("data")
                    for (i in 0 until data.length())
                    {
                        val resJsonObject=data.getJSONObject(i)
                        val restaurantObject=
                            Restaurants(
                                resJsonObject.getString("id"),
                                resJsonObject.getString("name"),
                                resJsonObject.getString("rating"),
                                resJsonObject.getString("cost_for_one"),
                                resJsonObject.getString("image_url")
                            )
                        resList.add(restaurantObject)
                    }
                    resList.removeAt(0)
                    recyclerAdapter=
                        DashBoardAdapter(
                            activity as Context,
                            resList
                        )
                    recyclerDashboard.adapter=recyclerAdapter
                    recyclerDashboard.layoutManager=layoutManager
                }
            },Response.ErrorListener {
                Toast.makeText(activity as Context,"volley error",Toast.LENGTH_SHORT).show()
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="301f1e436c2c8d"
                    return headers
                }
            }
            queue.add(jsonRequest)
            }
        else {
            Toast.makeText(activity as Context, "No network", Toast.LENGTH_SHORT).show()
            val dialog=AlertDialog.Builder(activity as Context).setCancelable(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id== R.id.action_sort)
        {
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By?")
            val sortMethods=arrayOf("cost(low to high)","cost(high to low)","Rating")
            dialog.setSingleChoiceItems(sortMethods,checkedItem){ _,which->
                checkedItem=which
            }
            dialog.setPositiveButton("OK"){_,_->
                when(checkedItem){
                    0->{
                        Collections.sort(resList,costComparator)
                    }
                    1->{
                        Collections.sort(resList,costComparator)
                        resList.reverse()
                    }
                    2->{
                        Collections.sort(resList,ratingComparator)
                        resList.reverse()
                    }
                }
                recyclerAdapter.notifyDataSetChanged()

            }
            dialog.setNegativeButton("Cancel"){_,_->}
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

}