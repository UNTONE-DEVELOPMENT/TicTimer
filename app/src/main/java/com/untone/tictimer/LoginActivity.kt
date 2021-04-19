package com.untone.tictimer

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLClassLoader
import java.net.UnknownHostException
import java.util.*
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginBrowser = findViewById<WebView>(R.id.webView2)
        loginBrowser.loadUrl("https://www.untone.uk/id/utauth?client_id=8")
        loginBrowser.settings.javaScriptEnabled = true
        loginBrowser.requestFocus()
        loginBrowser.isVerticalScrollBarEnabled = true
        loginBrowser.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                GlobalScope.launch {
                    if (url.toString().contains("https://tictimer.untone.uk/utauth/auth.php?key")){
                        val urla = URL(url.toString())
                        val reader = urla.readText()
                        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putString("userKey", reader.substring(0, 32))
                        editor.commit()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                super.onPageFinished(view, url)
            }
        }
    }
}
