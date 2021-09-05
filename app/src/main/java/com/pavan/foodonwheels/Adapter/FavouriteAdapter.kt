package com.pavan.foodonwheels.Adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.pavan.foodonwheels.Activity.RestaurantDetails
import com.pavan.foodonwheels.Database.RestaurantDatabase
import com.pavan.foodonwheels.Database.RestaurantEntity
import com.pavan.foodonwheels.R
import com.squareup.picasso.Picasso

class FavouriteAdapter(val context: Context, val itemList:ArrayList<RestaurantEntity>,val listener:removeList): RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {
    interface removeList{
        fun removeItem(restaurantEntity: RestaurantEntity)
    }
    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resname: TextView = view.findViewById(R.id.txt_restaurant)
        val llcontent: LinearLayout = view.findViewById(R.id.llcontent)
        val imageButton: ImageButton = view.findViewById(R.id.img_button)
        val imageView: ImageView = view.findViewById(R.id.imgRestaurant)
        val price: TextView = view.findViewById(R.id.txtprice)
        val rating: TextView = view.findViewById(R.id.txt_rating)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favourite_fragment_single_row, parent, false)
        return FavouriteViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.resname.text = restaurant.name
        Picasso.get().load(restaurant.image).into(holder.imageView);
        holder.rating.text = restaurant.rating
        holder.price.text = "Rs. " + restaurant.cost_for_one + "/person"
        holder.llcontent.setOnClickListener {
            val intent= Intent(context,
                RestaurantDetails::class.java)
            intent.putExtra("resId",restaurant.res_id)
            intent.putExtra("resName",restaurant.name)
            context.startActivity(intent)
        }
        val restaurantEntity = RestaurantEntity(
            restaurant.res_id,
            restaurant.name,
            restaurant.rating,
            restaurant.cost_for_one,
            restaurant.image
        )
        val checkFav = DBAsyncTask(
            context,
            restaurantEntity,
            1
        ).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imageButton.setImageResource(R.drawable.ic_clickfav)
        } else {
            holder.imageButton.setImageResource(R.drawable.ic_clickfavborder)
        }

        holder.imageButton.setOnClickListener {
            if (!DBAsyncTask(
                    context,
                    restaurantEntity,
                    1
                ).execute().get()
            ) {

                val async =
                    DBAsyncTask(
                        context,
                        restaurantEntity,
                        2
                    ).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.imageButton.setImageResource(R.drawable.ic_clickfav)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val async = DBAsyncTask(
                    context,
                    restaurantEntity,
                    3
                ).execute()
                val result = async.get()

                if (result) {
                    listener.removeItem(restaurantEntity)
                    Toast.makeText(
                        context,
                        "removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.imageButton.setImageResource(R.drawable.ic_clickfavborder)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }


    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {


        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {

                    val restaurant: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.res_id)
                    db.close()
                    return restaurant != null

                }

                2 -> {

                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true

                }

                3 -> {

                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true

                }
            }
            return false
        }

    }
}


