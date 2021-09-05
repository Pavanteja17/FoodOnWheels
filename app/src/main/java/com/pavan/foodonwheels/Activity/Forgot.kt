package com.pavan.foodonwheels.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class Forgot : AppCompatActivity() {
    lateinit var otp:EditText
    lateinit var password:EditText
    lateinit var cpassword:EditText
    lateinit var submit: Button
    val empty=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        val mobile=intent.getStringExtra("mobile")
        otp=findViewById(R.id.et_otp)
        password=findViewById(R.id.et_fpassword)
        cpassword=findViewById(R.id.et_cpassword)
        submit=findViewById(R.id.bt_submit)
        submit.setOnClickListener {
            val otp=otp.text.toString()
            val password=password.text.toString()
            val cPassword=cpassword.text.toString()
            val success=check(otp,password,cPassword)
            val queue=Volley.newRequestQueue(this@Forgot)
            val url="http://13.235.250.119/v2/reset_password/fetch_result/"
            val jsonParams=JSONObject()
            jsonParams.put("otp",otp)
            jsonParams.put("mobile_number",mobile)
            jsonParams.put("password",password)
            if(success){
                if(ConnectionManager().checkConnectivity(this@Forgot)){
                    val jsonRequest=object :JsonObjectRequest(Method.POST,url,jsonParams, Response.Listener {
                        try{
                            val data=it.getJSONObject("data")
                            val success=data.getBoolean("success")
                            if(success){
                                Toast.makeText(this@Forgot,data.getString("successMessage"),Toast.LENGTH_SHORT).show()
                                val intent= Intent(this@Forgot,
                                    SignUp::class.java)
                                startActivity(intent)
                            }
                            else{
                                Toast.makeText(this@Forgot,data.getString("errorMessage"),Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch(e:Exception){
                            Toast.makeText(this@Forgot,"some error occured",Toast.LENGTH_SHORT).show()
                        }
                    },Response.ErrorListener {
                        //here errors are handled
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
                else{
                    Toast.makeText(this@Forgot,"No network",Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
    fun check(otp:String,password:String,cpassword:String):Boolean{
        var success=true
        if(otp==empty){
            Toast.makeText(this@Forgot,"Incorrect OTP",Toast.LENGTH_SHORT).show()
            success=false
            return success
        }
        if(password==empty || password.length<4){
            Toast.makeText(this@Forgot,"Invalid password",Toast.LENGTH_SHORT).show()
            success=false
            return success
        }
        if(password!=cpassword){
            Toast.makeText(this@Forgot,"passwords dont match",Toast.LENGTH_SHORT).show()
            success=false
        }
        return success

    }

    override fun onPause() {
        finish()
        super.onPause()
    }
}