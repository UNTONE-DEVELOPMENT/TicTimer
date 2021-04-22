package com.untone.tictimer

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.CookieManager
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import java.util.*
import java.util.Calendar.getInstance
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var handler: Handler = Handler()
    var hasOpenedPopUp = false
    var runnable: Runnable? = null
    var delay = 1000
    var hasStarted = false
    var mins = 0
    var secs = 0
    var tics = 0
    var mins2 = 0
    var secs2 = 0
    override fun onBackPressed() {
        mins = 0
        tics = 0
        secs = 0
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println(Config().type)
        val constrl = findViewById<ConstraintLayout>(R.id.cl1)
        constrl.visibility = View.INVISIBLE
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        if(sharedPreference.contains("theraKey"))
        {
            GlobalScope.launch {
            val cv = findViewById<CardView>(R.id.cv1)
            cv.visibility = View.VISIBLE
            cv.setOnClickListener(){
                    try{
                        hasOpenedPopUp = !hasOpenedPopUp
                        val theraKey = sharedPreference.getString("theraKey", "")
                        val theraName = sharedPreference.getString("theraName", "")
                        if (hasOpenedPopUp == true) {
                            val cardPopUp = findViewById<CardView>(R.id.cv2)
                            val listOptions = findViewById<ListView>(R.id.ls2)
                            cardPopUp.visibility = View.VISIBLE
                            var listOfOpts: MutableList<String> = ArrayList()
                            listOfOpts.add(theraName.toString())
                            listOfOpts.add("Log Out")
                            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, listOfOpts)
                            listOptions.adapter = adapter
                            listOptions.onItemClickListener =
                                    AdapterView.OnItemClickListener { parent, view, position, id ->
                                        if (position == 0) {

                                        }

                                        else if (position == 1){
                                            val editor = sharedPreference.edit()
                                            editor.clear()
                                            editor.commit()
                                            val intent = Intent(this@MainActivity, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                        }

                        else if (hasOpenedPopUp == false)
                        {
                            val cardPopUp = findViewById<CardView>(R.id.cv2)
                            cardPopUp.visibility = View.GONE
                        }
                    }
                    catch (e: java.lang.Exception){

                    }
                }
            }
        }
        if (sharedPreference.contains("userKey"))
        {
            GlobalScope.launch {
                try {
                    val gson = Gson()
                    val userKey = sharedPreference.getString("userKey", "")
                    val urlUT = URL("https://tictimer.untone.uk/api/" + Config().type + "/get_user_info.php?key=" + userKey + "&private_key=" + Config().key)
                    val utJson = urlUT.readText()
                    val utJsonDecode = gson.fromJson(utJson, AuthJsonData::class.java)
                    val url = URL(utJsonDecode.pfp)
                    val usernameUT = utJsonDecode.name
                    val userID = utJsonDecode.id
                    val editor = sharedPreference.edit()
                    editor.putString("currentUserName", usernameUT)
                    editor.putString("currentUserID", userID)
                    editor.commit()
                    val getBmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    runOnUiThread({
                        try {
                            val imageView1 = findViewById<ImageView>(R.id.imageView4)
                            imageView1.setImageBitmap(getBmp)
                        } catch (e: java.lang.Exception) {
                        }
                    })
                } catch (e: java.lang.Exception){

                }
            }
            val crd = findViewById<CardView>(R.id.cv1)
            crd.visibility = View.VISIBLE
            crd.setOnClickListener() {
                hasOpenedPopUp = !hasOpenedPopUp
                if (hasOpenedPopUp == true) {
                    val cardPopUp = findViewById<CardView>(R.id.cv2)
                    val listOptions = findViewById<ListView>(R.id.ls2)
                    cardPopUp.visibility = View.VISIBLE
                    var listOfOpts: MutableList<String> = ArrayList()
                    val getUserName = sharedPreference.getString("currentUserName", "")
                    listOfOpts.add(getUserName.toString())
                    listOfOpts.add("Edit profile")
                    listOfOpts.add("Log out")
                    val adapter =
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfOpts)
                    listOptions.adapter = adapter
                    listOptions.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, view, position, id ->
                            val userid = sharedPreference.getString("currentUserID", "")
                            if (position == 0) {
                                val url = "https://www.untone.uk/user/" + userid
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setData(Uri.parse(url))
                                startActivity(intent)

                            } else if (position == 1) {
                                val url = "https://www.untone.uk/id/edit_profile"
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setData(Uri.parse(url))
                                startActivity(intent)

                            } else if (position == 2) {

                                val alert = AlertDialog.Builder(this)
                                alert.setTitle("Warning")
                                alert.setMessage("Logging out will delete all locally stored sessions. Are you sure you want to log out? (The application will close.)")
                                alert.setIcon(android.R.drawable.stat_sys_warning)
                                alert.setPositiveButton("Yes, do it!") {dialog, id ->
                                    val editor = sharedPreference.edit()
                                    editor.clear()
                                    editor.commit()
                                    listOptions.visibility = View.GONE
                                    deleteAppData()
                                    val swie = Intent(this@MainActivity, MainActivity::class.java)
                                    startActivity(swie)
                                }
                                alert.setNegativeButton("Nope!") {dialog, id ->
                                    val swie = Intent(this@MainActivity, MainActivity::class.java)
                                    startActivity(swie)
                                }
                                val createAl = alert.create()
                                val showAl = createAl.show()
                                val doItBtn = createAl.getButton(DialogInterface.BUTTON_POSITIVE)
                                doItBtn.setTextColor(Color.RED)
                                val nopeBtn = createAl.getButton(DialogInterface.BUTTON_NEGATIVE)
                                nopeBtn.setTextColor(Color.GREEN)

                            }
                        }
                }

                else if (hasOpenedPopUp == false)
                {
                    val cardPopUp = findViewById<CardView>(R.id.cv2)
                    cardPopUp.visibility = View.GONE
                }
            }
        }
        val setBtn = findViewById<ImageButton>(R.id.imageButton5)
        setBtn.setOnClickListener(){
            val switchtoSetIntent = Intent(this, SettingsActivity::class.java)
            startActivity(switchtoSetIntent)
        }
        FetchBG()
        val btnFeedBack = findViewById<ImageButton>(R.id.imageButton)
        val btn1 = findViewById<Button>(R.id.button)
        val btnTic = findViewById<Button>(R.id.button3)
        val ticString = findViewById<TextView>(R.id.textView2)
        val refresher = findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        refresher.setOnRefreshListener(){
            FetchBG()
        }
        btnTic.setOnClickListener()
        {
            if (hasStarted == true) {
                tics = tics + 1
                ticString.text = "You have ticced " + tics.toString() + " times"
                mins = 0
                secs = 0
            } else {

            }
        }
        val btnAll = findViewById<Button>(R.id.button2)
        val txt = findViewById<TextView>(R.id.textView)
        btnAll.setOnClickListener()
        {
            val switchIntent = Intent(this, AllActivity::class.java)
            startActivity(switchIntent)
        }

        btnFeedBack.setOnClickListener()
        {
            val switchIntent = Intent(this, FeedBackActivity::class.java)
            startActivity(switchIntent)
        }

        btn1.setOnClickListener()
        {

            runOnUiThread({
                if (btn1.text == "Start") {
                    hasStarted = true
                    btn1.text = "Stop"
                    handler.postDelayed(Runnable {
                        secs2 = secs2 + 1
                        if (secs2 >= 60) {
                            secs2 = 0
                            mins2 = mins2 + 1
                        }
                        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("Tics", tics)
                        editor.putInt("Mins", mins2)
                        editor.putInt("Secs", secs2)
                        editor.commit()
                        handler.postDelayed(runnable!!, delay.toLong())
                        secs = secs + 1
                        if (secs >= 60) {
                            secs = 0
                            mins = mins + 1
                        }
                        txt.text = mins.toString().padStart(2, '0') + ":" + secs.toString().padStart(2, '0')
                    }.also { runnable = it }, delay.toLong())
                } else if (btn1.text == "Stop") {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Warning")
                    builder.setMessage("Would you like to end the current session?")
                    builder.setPositiveButton("Yes") { dialogInterface, which ->
                        handler.removeCallbacks(runnable!!)
                        btn1.text = "Start"
                        hasStarted = false
                        val switchToEndIntent = Intent(this, FinalActivity::class.java)
                        startActivity(switchToEndIntent)
                    }
                    builder.setNegativeButton("No") { dialogInterface, which ->

                    }

                    val alertDialog: AlertDialog = builder.create()

                    alertDialog.show()

                }
            })
        }
    }


    fun FetchBG() {
        val constrl = findViewById<ConstraintLayout>(R.id.cl1)
        val refresher = findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        GlobalScope.launch {
            refresher.isRefreshing = true
            try {
                val minto = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
                val secto = Calendar.getInstance()[Calendar.MINUTE]
                if (minto == 4 && secto == 20)
                {
                    val imageView1 = findViewById<ImageView>(R.id.im1)
                    val drw = resources.getDrawable(R.drawable.a420, theme)
                    imageView1.setImageDrawable(drw)
                }

                else if (minto == 6 && secto == 9)
                {
                    val imageView1 = findViewById<ImageView>(R.id.im1)
                    val drw = resources.getDrawable(R.drawable.a609, theme)
                    imageView1.setImageDrawable(drw)
                }

                else {
                    val url = URL("https://picsum.photos/1440/2650")
                    val getBmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    runOnUiThread({
                        val imageView1 = findViewById<ImageView>(R.id.im1)
                        imageView1.setImageBitmap(getBmp)
                    })
                }
            } catch (e: Exception) {
                runOnUiThread({
                    val imageView1 = findViewById<ImageView>(R.id.im1)
                    val getErrorDrawable = resources.getDrawable(R.drawable.error, theme)
                    imageView1.setImageDrawable(getErrorDrawable)
                    e.printStackTrace()
                    var msg = ""
                    msg = "${e.message}"
                    when (e) {
                        is SocketTimeoutException -> {
                            msg = "Timeout - Please check your internet connection"
                        }
                        is UnknownHostException -> {
                            msg = "Unable to make a connection. Please check your internet"
                        }
                        is ConnectionShutdownException -> {
                            msg = "Connection shutdown. Please check your internet"
                        }
                        is IOException -> {
                            msg = "Server is unreachable, please try again later."
                        }
                        is IllegalStateException -> {
                            msg = "${e.message}"
                        }
                        else -> {
                            msg = "${e.message}"
                        }
                    }
                    val tst = Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG)
                    tst.show()
                })
            }
            runOnUiThread({
                refresher.isRefreshing = false
                constrl.visibility = View.VISIBLE
            })
        }
    }

    fun deleteAppData(){
        try {
            val packageName = applicationContext.packageName
            val runtime = Runtime.getRuntime()
            runtime.exec("pm clear " + packageName)
        }
        catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }
}

data class AuthJsonData(
    val name: String,
    val pfp: String,
    val id: String
)