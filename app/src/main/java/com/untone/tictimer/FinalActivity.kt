package com.untone.tictimer

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.net.URL
import java.security.Provider
import java.sql.Time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FinalActivity : AppCompatActivity(){
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finalview_activity)
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val ticsget = sharedPreference.getInt("Tics", 0)
        val minsget = sharedPreference.getInt("Mins", 0)
        val secsget = sharedPreference.getInt("Secs", 0)
        val txt = findViewById<TextView>(R.id.textView3)
        txt.text = "Tics: " + ticsget + ", Length: " + minsget.toString().padStart(2, '0') + ":" + secsget.toString().padStart(2, '0')
        val prg = findViewById<ProgressBar>(R.id.progressBar)
        prg.progress = ticsget
        prg.max = ticsget + 50
        val main = MainActivity()
        val gson = Gson()
        val currentDateTime = LocalDateTime.now()
        var writeDataToJson = gson.toJson(JsonData(ticsget, minsget, secsget))
        File(filesDir.toString() + "//" + currentDateTime).writeText(writeDataToJson)
        println(writeDataToJson)
        if (sharedPreference.contains("userKey"))
        {
                GlobalScope.launch {
                    try{
                        val userKey = sharedPreference.getString("userKey", "")
                        URL("https://tictimer.untone.uk/api/" + Config().type + "/upload_session.php?key=" + userKey + "&session=" + writeDataToJson + "private_key=" + Config().key).readText()
                    }
                    catch (e: Exception) {
                        runOnUiThread({
                            Toast.makeText(applicationContext, "Unable to send session. You are offline. Will retry sending session again in a minute.", Toast.LENGTH_LONG).show()
                        })
                        val editor = sharedPreference.edit()
                        editor.putString("jsonToBeSent", writeDataToJson)
                        editor.commit()
                        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                        val jobInfo = JobInfo.Builder(11, ComponentName(baseContext, TrySendSessionData::class.java)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build()
                        jobScheduler.schedule(jobInfo)
                    }
                }
        }

        if (sharedPreference.contains("theraKey"))
        {
            GlobalScope.launch {
                try{
                    val userKey = sharedPreference.getString("theraKey", "")
                    URL("https://tictimer.untone.uk/utauth/therapist/upload_session.php?key=" + userKey + "&session=" + writeDataToJson).readText()
                }
                catch (e: Exception) {
                    runOnUiThread({
                        Toast.makeText(applicationContext, "Unable to send session. You are offline. Will retry sending session again in a minute.", Toast.LENGTH_LONG).show()
                    })
                    val editor = sharedPreference.edit()
                    editor.putString("jsonToBeSent", writeDataToJson)
                    editor.commit()
                    val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                    val jobInfo = JobInfo.Builder(11, ComponentName(baseContext, TrySendSessionData::class.java)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build()
                    jobScheduler.schedule(jobInfo)
                }
            }
        }
    }

    override fun onBackPressed() {
        val switchIntent = Intent(this, MainActivity::class.java)
        startActivity(switchIntent)
    }
}

data class JsonData(
    val tics: Int,
    val mins: Int,
    val secs: Int
)

class TrySendSessionData : JobService() {

    override fun onStartJob(parameters: JobParameters?): Boolean {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val jsonToBeSent = sharedPreference.getString("jsonToBeSent", "")
        val userKey = sharedPreference.getString("userKey", "")
            try{
                GlobalScope.launch {
                    URL("https://tictimer.untone.uk/api/" + Config().type + "/upload_session.php?key=" + userKey + "&session=" + jsonToBeSent + "private_key=" + Config().key).readText()
                }
                Toast.makeText(applicationContext, "Sent session later", Toast.LENGTH_LONG).show()
                return false
            }
            catch(e: Exception){
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
            }
        return true
    }

    override fun onStopJob(parameters: JobParameters?): Boolean {
        return false
    }
}