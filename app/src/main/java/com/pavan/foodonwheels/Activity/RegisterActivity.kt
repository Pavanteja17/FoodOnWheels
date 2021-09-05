package com.pavan.foodonwheels.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.util.ConnectionManager
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    lateinit var name:EditText
    lateinit var email:EditText
    lateinit var mobilenumber:EditText
    lateinit var deliveryaddress:EditText
    lateinit var password:EditText
    lateinit var confirmpassword:EditText
    lateinit var register:Button
    var empty=""
    lateinit var sharedPreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        sharedPreferences=this.getSharedPreferences("meals", Context.MODE_PRIVATE)
        title="Register yourself"
        name=findViewById(R.id.et_name)
        email=findViewById(R.id.et_email)
        mobilenumber=findViewById(R.id.et_mobilenumber)
        deliveryaddress=findViewById(R.id.et_delivery)
        password=findViewById(R.id.et_password)
        confirmpassword=findViewById(R.id.et_confirm)
        register=findViewById(R.id.bt_register)
        register.setOnClickListener {
            val email=email.text.toString()
            val name=name.text.toString()
            val mobilenumber=mobilenumber.text.toString()
            val deliveryaddress=deliveryaddress.text.toString()
            val password=password.text.toString()
            val confirmpassword=confirmpassword.text.toString()
            val queue = Volley.newRequestQueue(this@RegisterActivity)
            val url = "http://13.235.250.119/v2/register/fetch_result/"
            val jsonParams = JSONObject()
            jsonParams.put("name",name)
            jsonParams.put("email",email)
            jsonParams.put("mobile_number",mobilenumber)
            jsonParams.put("address",deliveryaddress)
            jsonParams.put("password",password)
            val success=check(name,email,mobilenumber,deliveryaddress,password,confirmpassword)
            if(success){
                if(ConnectionManager().checkConnectivity(this@RegisterActivity))
                {
                    val jsonRequest=object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {
                        val data=it.getJSONObject("data")
                        val note=data.getBoolean("success")
                        if(!note) {
                            Toast.makeText(this@RegisterActivity, data.getString("errorMessage"), Toast.LENGTH_SHORT)
                                .show()
                        }
                        else{
                            val info=data.getJSONObject("data")
                            sharedPreferences.edit().putString("user_id",info.getString("user_id")).apply()
                            sharedPreferences.edit().putBoolean("loggedIn",true).apply()
                            sharedPreferences.edit().putString("name",name).apply()
                            val name=sharedPreferences.getString("name",name)
                            sharedPreferences.edit().putString("email",email).apply()
                            sharedPreferences.edit().putString("mobile",mobilenumber).apply()
                            sharedPreferences.edit().putString("place",deliveryaddress).apply()
                            val intent= Intent(this@RegisterActivity,
                                HomeActivity::class.java)
                            startActivity(intent)
                        }

                    },Response.ErrorListener {
                        Toast.makeText(this@RegisterActivity,"VolleyError $it",Toast.LENGTH_SHORT).show()
                        val succeed=false
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
                    Toast.makeText(this@RegisterActivity,"No Internet",Toast.LENGTH_SHORT).show()
                }
            }



        }
    }
    fun check(name:String,email:String,mobile:String,delivery:String,password:String,confirm:String):Boolean{
        var success: Boolean = true
        if (name == empty || name.length < 3) {
            Toast.makeText(this@RegisterActivity, "Invalid name", Toast.LENGTH_SHORT).show()
            success = false
            return false
        }
        if (email==empty||!emailValidator(email)) {
            success=false
            Toast.makeText(this@RegisterActivity, "Invalid email Id", Toast.LENGTH_SHORT).show()
            return false
        }
        if(mobile==empty ||mobile.length<10){
            success=false
            Toast.makeText(this@RegisterActivity, "Invalid mobile number", Toast.LENGTH_SHORT).show()
            return false

        }
        if(delivery==empty){
            success=false
            Toast.makeText(this@RegisterActivity, "delivery address cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if(password==empty||password.length<4){
            success=false
            Toast.makeText(this@RegisterActivity, "password must be more than 4 digits", Toast.LENGTH_SHORT).show()
            return false
        }
        if(password!=confirm){
            success=false
            Toast.makeText(this@RegisterActivity, "passwords don't match", Toast.LENGTH_SHORT).show()
        }
        return success

    }
    fun emailValidator(email:String):Boolean{
        val pattern:Pattern
        val matcher:Matcher
        val emailPattern="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern= Pattern.compile(emailPattern)
        matcher=pattern.matcher(email)
        return matcher.matches()
    }

}

