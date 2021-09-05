package com.pavan.foodonwheels.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.pavan.foodonwheels.Fragments.*
import com.pavan.foodonwheels.R

class HomeActivity : AppCompatActivity() {
    lateinit var drawerLayout:DrawerLayout
    lateinit var navigationView:NavigationView
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var user:TextView
    lateinit var phoneNumber:TextView
    lateinit var sharedPreferences:SharedPreferences
     var prevMenuItem:MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
       drawerLayout=findViewById(R.id.drawer_layout)
       navigationView=findViewById(R.id.navigation_view)
       toolbar=findViewById(R.id.toolbar)
       coordinatorLayout=findViewById(R.id.coordinator_layout)
       frameLayout=findViewById(R.id.framelayout)
        val view=navigationView.getHeaderView(0)//because header layout is not a part of this activity layout.so we have to extract
        user=view.findViewById(R.id.txt_user)
        phoneNumber=view.findViewById(R.id.txt_phonedrawer)
        sharedPreferences=getSharedPreferences("meals", Context.MODE_PRIVATE)
        val txt=sharedPreferences.getString("name","pavan")
        user.text=txt
        phoneNumber.text=sharedPreferences.getString("mobile","9999999999")
       setUpToolBar()
        openDashBoard()
       val actionBarDrawerToggle=ActionBarDrawerToggle(this@HomeActivity,drawerLayout,
           R.string.open_drawer,
           R.string.close_drawer
       )
       drawerLayout.addDrawerListener(actionBarDrawerToggle)
       actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.dashboard ->{
                    openDashBoard()
                }
                R.id.profile ->{
                    supportActionBar?.title="My Profile"
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        profileFragment()
                    ).commit()
                    drawerLayout.closeDrawers()


                }
                R.id.faq ->{
                        supportActionBar?.title="Frequently Asked Questions"
                        supportFragmentManager.beginTransaction().replace(
                            R.id.framelayout,
                            FaqFragment()
                        ).commit()
                    supportActionBar?.title="Frequently Asked Questions"
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        FaqFragment()
                    ).commit()
                    drawerLayout.closeDrawers()


                }
                R.id.orderHistory ->{
                    supportActionBar?.title="My Orders"
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        HistoryFragment()
                    ).commit()
                    drawerLayout.closeDrawers()

                }
                R.id.favourites ->{
                    supportActionBar?.title="Favourites"
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        FavouritesFragment()
                    ).commit()
                    drawerLayout.closeDrawers()

                }
                R.id.logout ->{
                    val dialog=AlertDialog.Builder(this@HomeActivity).setCancelable(false)
                    drawerLayout.closeDrawers()
                    dialog.setTitle("Logout")
                    dialog.setMessage("Are you sure you want to exit")
                    dialog.setPositiveButton("YES") { text, listener ->
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@HomeActivity,
                            SignUp::class.java)
                        startActivity(intent)
                        finish()
                    }

                    dialog.setNegativeButton("No") { text, listener ->
                        openDashBoard()
                    }
                    dialog.create()
                    dialog.show()

                }
            }

            return@setNavigationItemSelectedListener true
        }

    }
    fun setUpToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="title bar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openDashBoard(){
        supportActionBar?.title="All Restaurants"
        supportFragmentManager.beginTransaction().replace(
            R.id.framelayout,
            HomeFragment()
        ).commit()
        navigationView.setCheckedItem(R.id.dashboard)
        drawerLayout.closeDrawers()


    }

   override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.framelayout)
        when(frag){
            !is HomeFragment ->openDashBoard()
            else->finishAffinity()
        }
   }
}