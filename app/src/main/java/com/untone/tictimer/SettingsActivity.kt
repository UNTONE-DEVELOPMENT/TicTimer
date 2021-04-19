package com.untone.tictimer

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import java.util.*

class SettingsActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val logiNBtn = findViewById<Button>(R.id.button5)
        val hyperLink = findViewById<TextView>(R.id.textView7)
        val loginTxt = findViewById<TextView>(R.id.textView8)
        val btn6 = findViewById<Button>(R.id.button6)
        val or = findViewById<TextView>(R.id.textView9)
        btn6.setOnClickListener(){
            val gotoTherapistIntent = Intent(this@SettingsActivity, TherapistLoginActivity::class.java)
            startActivity(gotoTherapistIntent)
        }
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        if (sharedPreference.contains("userKey"))
        {
            logiNBtn.visibility = View.GONE
            hyperLink.visibility = View.GONE
            or.visibility = View.GONE
            btn6.visibility = View.GONE
            loginTxt.text = "Logged in as " + sharedPreference.getString("userKey", "")
        }

        if (sharedPreference.contains("theraKey"))
        {
            logiNBtn.visibility = View.GONE
            hyperLink.visibility = View.GONE
            or.visibility = View.GONE
            btn6.visibility = View.GONE
            loginTxt.text = "Logged in as " + sharedPreference.getString("theraKey", "")
        }
        else{
            loginTxt.visibility = View.GONE
        }
        val buildText = findViewById<TextView>(R.id.textView6)
        val hyperLinkText = Html.fromHtml("<a href='https://sites.google.com/view/whatisanuntoneid/home-page/'>What is an UNTONE ID?</a>", 0)
        hyperLink.movementMethod = LinkMovementMethod.getInstance()
        hyperLink.text = hyperLinkText
        val date = Date(BuildConfig.TIMESTAMP)
        buildText.text = "TicTimer Kotlin Rewrite Built on " + date

        logiNBtn.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}