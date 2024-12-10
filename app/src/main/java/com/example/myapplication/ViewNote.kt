package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

var position : Int = 0
val FILENAME = "Notes.json"
var list = ArrayList<Items>()
var isEdited = false
var isDeleted = false

class ViewNote : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val extras: Bundle? = intent.extras;
        val dataStore = StoreRetrieveData(applicationContext,FILENAME)
        list = loadDatafromFile(dataStore,applicationContext)
        if (extras!=null){
            position = extras.getInt("position")
            //Toast.makeText(applicationContext, position.toString(),Toast.LENGTH_LONG).show()
        }
        val titleView : TextView = findViewById(R.id.view_note_title)
        val dateView : TextView = findViewById(R.id.view_note_date)
        val descView : TextView = findViewById(R.id.view_note_description);
        val button : ImageButton = findViewById(R.id.edit_item_button);
        val button_back : ImageButton = findViewById(R.id.edit_item_back);
        val buttonDeleteItem : ImageButton = findViewById(R.id.edit_item_delete_button);
        if (isDarkModeOn()){
            button.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.baseline_edit_24))
            button_back.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.baseline_arrow_back_24))
            buttonDeleteItem.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.baseline_delete_24_light))
        }
        button_back.setOnClickListener(View.OnClickListener {
            finish()
        })
        titleView.text = list.get(position).getTitle()
        descView.text = list.get(position).getDesc()
        dateView.text = list.get(position).getDate()

        button.setOnClickListener(View.OnClickListener {
            var intent = Intent(applicationContext,AddContent::class.java);
            intent.putExtra("position", position)
            startActivity(intent)
            isEdited = true
        })

        buttonDeleteItem.setOnClickListener(View.OnClickListener {
            isDeleted = true
            list.removeAt(position)
            dataStore.saveToFile(list)
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

    override fun onResume() {
        super.onResume()
        if (isEdited){
            val dataStore = StoreRetrieveData(applicationContext,FILENAME)
            list = loadDatafromFile(dataStore,applicationContext)
            val titleView : TextView = findViewById(R.id.view_note_title)
            val dateView : TextView = findViewById(R.id.view_note_date)
            val descView : TextView = findViewById(R.id.view_note_description);
            titleView.text = list.get(position).getTitle()
            descView.text = list.get(position).getDesc()
            dateView.text = list.get(position).getDate()
            isEdited = false
        }

    }

    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }
}