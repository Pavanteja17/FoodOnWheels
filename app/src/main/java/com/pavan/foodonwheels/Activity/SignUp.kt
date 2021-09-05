package com.pavan.foodonwheels.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.util.ConnectionManager
import org.json.JSONObject

class SignUp : AppCompatActivity() {
    lateinit var mobile:EditText
    lateinit var password:EditText
    lateinit var btlogin:Button
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        sharedPreferences=getSharedPreferences("meals", Context.MODE_PRIVATE)
        login()
        mobile=findViewById(R.id.et_mobilelogin)
        password=findViewById(R.id.et_passlogin)
        btlogin=findViewById(R.id.bt_login)
        btlogin.setOnClickListener {
            val mobileNum=mobile.text.toString()
            println("mobile number is $mobileNum")
            var passNum=password.text.toString()
            val queue= Volley.newRequestQueue(this@SignUp)
            val url="http://13.235.250.119/v2/login/fetch_result/"
            val jsonParams=JSONObject()
            jsonParams.put("mobile_number",mobile.text.toString())
            jsonParams.put("password",password.text.toString())
            if(ConnectionManager().checkConnectivity(this@SignUp)){
                val jsonRequest=object :JsonObjectRequest(Method.POST,url,jsonParams, Response.Listener {
                    val data=it.getJSONObject("data")
                    val success=data.getBoolean("success")
                    if(success){
                        Toast.makeText(this@SignUp,"success",Toast.LENGTH_SHORT).show()
                        val infojsonObject=data.getJSONObject("data")
                        val userId=infojsonObject.getString("user_id")
                        val name=infojsonObject.getString("name")
                        val email=infojsonObject.getString("email")
                        val mobile=infojsonObject.getString("mobile_number")
                        val address=infojsonObject.getString("address")
                        sharedPreferences.edit().putBoolean("loggedIn",true).apply()
                        sharedPreferences.edit().putString("user_id",userId).apply()
                        sharedPreferences.edit().putString("name",name).apply()
                        sharedPreferences.edit().putString("mobile",mobile).apply()
                        sharedPreferences.edit().putString("email",email).apply()
                        sharedPreferences.edit().putString("place",address).apply()
                        login()


                    }
                    else{
                        Toast.makeText(this@SignUp,data.getString("errorMessage"),Toast.LENGTH_SHORT).show()
                    }

                },Response.ErrorListener {
                    //here we handle error
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers=HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"]="301f1e436c2c8d"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            }
            else{
                Toast.makeText(this@SignUp,"No Internet",Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun register(view: View) {
        val intent= Intent(this@SignUp,
            RegisterActivity::class.java)
        startActivity(intent)
    }
    fun forgotPassword(view: View) {
        val intent= Intent(this@SignUp, ForgotPassword::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
    fun login(){
        val isLogin=sharedPreferences.getBoolean("loggedIn",false)
        if(isLogin){
            val intent=Intent(this@SignUp, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}