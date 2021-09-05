package com.pavan.foodonwheels.Fragments

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.pavan.foodonwheels.Database.RestaurantDatabase
import com.pavan.foodonwheels.Database.RestaurantEntity
import com.pavan.foodonwheels.Adapter.FavouriteAdapter
import com.pavan.foodonwheels.R

class FavouritesFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var favouriteAdapter: FavouriteAdapter
    var restaurantList= arrayListOf<RestaurantEntity>()
    var list= arrayListOf<RestaurantEntity>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view_favourite = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerView = view_favourite.findViewById(R.id.recycler_favourites)
        layoutManager = LinearLayoutManager(activity as Context)

        restaurantList = RetrieveFavourites(
            activity as Context
        ).execute().get() as ArrayList<RestaurantEntity>

        if (activity != null) {
            if(restaurantList.isNullOrEmpty()){
                Toast.makeText(activity as Context,"No records to display",Toast.LENGTH_LONG).show()
                val dialog=AlertDialog.Builder(activity as Context)
                dialog.setTitle("Info")
                dialog.setMessage("No Favorites to display")
                dialog.setPositiveButton("OK"){_,_->
                }
                dialog.create()
                dialog.show()
            }
            favouriteAdapter = FavouriteAdapter(
                activity as Context,
                restaurantList,object :FavouriteAdapter.removeList{
                    override fun removeItem(restaurantEntity: RestaurantEntity) {
                        restaurantList.remove(restaurantEntity)
                        favouriteAdapter.notifyDataSetChanged()
                    }
                }
            )
            recyclerView.adapter = favouriteAdapter
            recyclerView.layoutManager = layoutManager
        }
        return view_favourite
    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>() {

        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

            return db.restaurantDao().getAllRestaurants()
        }

    }


}