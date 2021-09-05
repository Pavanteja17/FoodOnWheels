package com.pavan.foodonwheels.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pavan.foodonwheels.R
import com.pavan.foodonwheels.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ForgotPassword : AppCompatActivity() {
    lateinit var mobile:EditText
    lateinit var email:EditText
    lateinit var next: Button
    lateinit var progressBar:ProgressBar
    lateinit var progressLayout:RelativeLayout
    lateinit var rlayout:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        mobile=findViewById(R.id.et_mobile)
        email=findViewById(R.id.et_email)
        next=findViewById(R.id.bt_forgot)
        progressBar=findViewById(R.id.progressBar)
        progressLayout=findViewById(R.id.progressLayout)
        rlayout=findViewById(R.id.rlayout)
        val queue=Volley.newRequestQueue(this@ForgotPassword)
        val url="http://13.235.250.119/v2/forgot_password/fetch_result/"
        next.setOnClickListener {
            progressLayout.visibility= View.VISIBLE
            progressBar.visibility=View.VISIBLE
            rlayout.visibility=View.GONE
            val mobile=mobile.text.toString()
            val email=email.text.toString()
            val jsonParams=JSONObject()
            jsonParams.put("mobile_number",mobile)
            jsonParams.put("email",email)
            if(ConnectionManager().checkConnectivity(this@ForgotPassword)){
                val jsonRequest=object: JsonObjectRequest(Method.POST,url,jsonParams, Response.Listener {
                    try{
                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")
                        progressLayout.visibility=View.GONE
                        if(success){
                            val dialog = AlertDialog.Builder(this@ForgotPassword).setCancelable(false)
                            dialog.setTitle("Information")
                            dialog.setMessage("Refer the email for OTP")
                            dialog.setPositiveButton("OK") { text, listener ->
                                val intent= Intent(this@ForgotPassword,
                                    Forgot::class.java)
                                intent.putExtra("mobile",mobile)
                                startActivity(intent)

                            }
                            dialog.create()
                            dialog.show()



                        }
                        else{
                            rlayout.visibility=View.VISIBLE
                            Toast.makeText(this@ForgotPassword,data.getString("errorMessage"),Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch(e:Exception)
                    {
                        Toast.makeText(this@ForgotPassword,"some error occurred",Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    //here we handle with exceptions
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers=HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "301f1e436c2c8d"
                        return headers
                    }
                }
                queue.add(jsonRequest)

            }
            else {
                Toast.makeText(this@ForgotPassword, "No Internet", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onResume() {
        rlayout.visibility=View.VISIBLE
        super.onResume()
    }
}