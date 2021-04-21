package com.untone.tictimer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.nio.file.Files

class AllActivity: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
        val lsv = findViewById<ListView>(R.id.ls1)
        var listItems: MutableList<String> = ArrayList()
        var listOfJsons: MutableList<String> = ArrayList()
        val gson = Gson() //this comment exists just to test CI
        val lister = Files.list(filesDir.toPath()).forEach{
            listItems.add(it.fileName.toString())
        }
        listItems.add("View online sessions")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems.reversed())
        lsv.adapter = adapter
        lsv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (position == 0)
            {
                val url = "https://tictimer.untone.uk/utauth/viewsessions.php"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
            else
            {
                val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("selected", filesDir.toString() + "//" + lsv.getItemAtPosition(position).toString())
                editor.commit()
                val switchIntent = Intent(this, ViewPrev::class.java)
                startActivity(switchIntent)
            }
        }
    }
}
