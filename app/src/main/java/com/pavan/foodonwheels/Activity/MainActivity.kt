package com.pavan.foodonwheels.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.pavan.foodonwheels.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title=""
        Handler().postDelayed({
            val startAct= Intent(this@MainActivity,
                SignUp::class.java)
            startActivity(startAct)

        },1000)

    }

    override fun onPause() {
        finish()
        super.onPause()
    }
}