package com.untone.tictimer

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.nfc.cardemulation.CardEmulation
import android.os.Bundle
import android.os.Handler
import android.service.autofill.Validators.not
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList

class TherapistLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_therapist)
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val key = findViewById<TextInputEditText>(R.id.name_edit_text)
        val login = findViewById<Button>(R.id.button7)
        login.setOnClickListener(){
            GlobalScope.launch {
                val readKey = URL("https://tictimer.untone.uk/utauth/therapist_api_userinfo.php?key=" + key.text).readText()
                if (readKey == "Key does not exist.")
                {
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG)
                }
                else{
                    val editor = sharedPreference.edit()
                    editor.putString("theraKey", key.text.toString())
                    val gson = Gson()
                    val decoded = gson.fromJson(readKey, JsonTheraData::class.java)
                    editor.putString("theraName", decoded.name)
                    editor.commit()
                    val intent = Intent(this@TherapistLoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}

data class JsonTheraData(
    val name: String,
    val id: Int)

