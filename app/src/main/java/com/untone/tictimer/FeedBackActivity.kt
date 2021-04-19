package com.untone.tictimer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.Exception
import java.lang.reflect.Type

class FeedBackActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        val sendBtn = findViewById<Button>(R.id.button4)
        val fieldEmail = findViewById<EditText>(R.id.editTextTextMultiLine)
        sendBtn.setOnClickListener()
        {
            val mIntent = Intent(Intent.ACTION_SEND)
            mIntent.data = Uri.parse("mailto:")
            mIntent.type = "text/plain"
            mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("help.tictimer@gmail.com"))
            mIntent.putExtra(Intent.EXTRA_SUBJECT, "TicTimer Feedback")
            mIntent.putExtra(Intent.EXTRA_TEXT, fieldEmail.text)
            try {
                startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
            }

            catch (e: Exception)
            {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Warning")
                builder.setMessage("An error occured while sending feedback. Make sure you have a compatible e-mail client installed on your device.")
                builder.setPositiveButton("OK") {dialogInterface, which ->

                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            }
        }
    }
}