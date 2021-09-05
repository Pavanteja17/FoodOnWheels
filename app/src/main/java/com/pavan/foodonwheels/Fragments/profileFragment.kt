package com.pavan.foodonwheels.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pavan.foodonwheels.R


class profileFragment : Fragment() {
    lateinit var userName: TextView
    lateinit var email: TextView
    lateinit var mobile: TextView
    lateinit var address: TextView
     lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_profile,container,false)
        userName=view.findViewById(R.id.txt_user)
        email=view.findViewById(R.id.txt_emailId)
        mobile=view.findViewById(R.id.txt_mobile)
        address=view.findViewById(R.id.txt_address)
        sharedPreferences=(activity as  Context).getSharedPreferences("meals",Context.MODE_PRIVATE)
        userName.text=sharedPreferences.getString("name","pavan")
        email.text=sharedPreferences.getString("email","")
        mobile.text=sharedPreferences.getString("mobile","")
        address.text=sharedPreferences.getString("place","")

        return view
    }
}