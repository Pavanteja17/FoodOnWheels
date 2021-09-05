package com.pavan.foodonwheels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class LogActivity : AppCompatActivity() {
    lateinit var mobile: EditText
    lateinit var passWord:EditText
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mobile=findViewById(R.id.et_mobilelogin)
        passWord=findViewById(R.id.et_passlogin)
        button=findViewById(R.id.bt_login)
        val mobile=mobile.text.toString()
        val password=passWord.text.toString()
        button.setOnClickListener {
            println(" password and mobile number is $mobile")
        }
    }

   /* fun forgotPassword(view: View) {
        val intent= Intent(this@LogActivity,ForgotPassword::class.java)
        startActivity(intent)
    }
    fun register(view: View) {
        val intent= Intent(this@LogActivity,RegisterActivity::class.java)
        startActivity(intent)
    }*/
}