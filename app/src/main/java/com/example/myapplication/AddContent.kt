package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar

class AddContent : AppCompatActivity() {
    final val FILENAME = "Notes.json"
    @SuppressLint("SimpleDateFormat", "MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dataStore = StoreRetrieveData(applicationContext,FILENAME)
        val entireList : ArrayList<Items> = loadDatafromFile(dataStore,applicationContext)
        val add_button : ImageButton = findViewById(R.id.add_button)
        val close_button : ImageButton = findViewById(R.id.close_button)
        val titleText : EditText = findViewById(R.id.input_title)
        val textTitle : TextView = findViewById(R.id.add_content_title)
        val descriptionText : EditText = findViewById(R.id.input_description)
        if (isDarkModeOn()){
            add_button.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.baseline_check_24_light))
            close_button.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.baseline_clear_24_whi))
        }
        val extras: Bundle? = intent.extras;
        if (extras!=null){
            position = extras.getInt("position")
            titleText.setText(entireList.get(position).getTitle())
            descriptionText.setText(entireList.get(position).getDesc())
            textTitle.text = "Edit Note"
        }

        add_button.setOnClickListener(View.OnClickListener {
            val title : String = titleText.text.toString()
            val description : String = descriptionText.text.toString()
            val timeStamp: String =
                SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime())

            if (title.isEmpty()){
                Toast.makeText(applicationContext,"Title is require",Toast.LENGTH_LONG).show()
            }else{
                val item = Items(title,description,timeStamp)
                if (extras!=null){
                    entireList.set(position,item)
                }else{
                    entireList.add(0,item)
                }
                try {
                    dataStore.saveToFile(entireList)
                }catch (e:Exception){
                    e.stackTrace
                }
                finish()
            }

        })

        close_button.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
    fun loadDatafromFile(dataStore: StoreRetrieveData, context: Context) : ArrayList<Items> {
        var list = ArrayList<Items>()
        try {
            list = dataStore.loadFromFile()
        }catch (e:Exception){
            e.stackTrace
        }
        return list
    }

    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

}