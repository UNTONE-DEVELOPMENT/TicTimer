package com.untone.tictimer

import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type


class ViewPrev: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewprev)
        val gson = Gson()
        val txt = findViewById<TextView>(R.id.textView4)
        val prg = findViewById<ProgressBar>(R.id.progressBar2)
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val getSelected = sharedPreference.getString("selected","")
        val jSN: List<JsonData> = gson.fromJson(File(getSelected).readLines().toString(), Array<JsonData>::class.java).toList()
        val ticsget = jSN.get(0).tics
        val minsget = jSN.get(0).mins
        val secsget = jSN.get(0).secs
        prg.progress = ticsget
        txt.text = "Tics: " + ticsget.toString() + ", Length: " + minsget.toString().padStart(2, '0') + ":" + secsget.toString().padStart(2, '0')

    }
}