package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

var incomingList : ArrayList<Items>? = null

var adapter : Adapter? = null

var itemsDeleted = false

var addContentClicked = false

var onClink = false


class ContentActivity : AppCompatActivity() {
    final val FILENAME = "Notes.json"
    var mContext: Context? = null
    private var mClearPaint: Paint? = null
    private var mBackground: ColorDrawable? = null
    private var backgroundColor = 0
    private var deleteDrawable: Drawable? = null
    private var intrinsicWidth = 0
    private var intrinsicHeight = 0


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var addContent : ImageButton = findViewById(R.id.addItem);
        if (isDarkModeOn()){
            addContent.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.baseline_add_24_light))
        }
        val dataStore = StoreRetrieveData(applicationContext,FILENAME)
        incomingList = loadDatafromFile(dataStore,applicationContext)

        addContent.setOnClickListener {
            addContentClicked = true
            startActivity(Intent(this, AddContent::class.java))
        }

        val recyclerView : RecyclerView = findViewById(R.id.CardContainerLayout)
        val manager = LinearLayoutManager(applicationContext,RecyclerView.VERTICAL,false)
        adapter = Adapter(incomingList!!,applicationContext)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        mBackground = ColorDrawable()
        backgroundColor = Color.parseColor("#E53935")
        mClearPaint = Paint()
        mClearPaint!!.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
        deleteDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.baseline_delete_outline_24)
        intrinsicWidth = deleteDrawable!!.intrinsicWidth
        intrinsicHeight = deleteDrawable!!.intrinsicHeight
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean,
            ) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.height

                val isCancelled = dX.toInt() === 0 && !isCurrentlyActive

                if (isCancelled) {
                    clearCanvas(
                        c,
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return
                }

                mBackground!!.setColor(backgroundColor)
                mBackground!!.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                mBackground!!.draw(c)

                val deleteIconTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin: Int = 20
                val deleteIconLeft: Int = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom: Int = deleteIconTop + intrinsicHeight

                deleteDrawable!!.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteDrawable!!.draw(c)

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            private fun clearCanvas(
                c: Canvas,
                left: Float,
                top: Float,
                right: Float,
                bottom: Float,
            ) {
                c.drawRect(left, top, right, bottom, mClearPaint!!)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val deletedCourse: Items =
                    incomingList!!.get(viewHolder.adapterPosition)
                itemsDeleted = true

                val position = viewHolder.adapterPosition

                incomingList!!.removeAt(viewHolder.adapterPosition)

                adapter!!.notifyItemRemoved(viewHolder.adapterPosition)

                Snackbar.make(recyclerView, deletedCourse.getTitle(), Snackbar.LENGTH_LONG)
                    .setAction("Undo"
                    ) {
                        incomingList!!.add(position, deletedCourse)
                        adapter!!.notifyItemInserted(position)
                    }.show()
            }
        }).attachToRecyclerView(recyclerView)

        onClink = adapter!!.getClick()

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun loadDatafromFile(dataStore: StoreRetrieveData, context: Context) : ArrayList<Items> {
        var list = ArrayList<Items>()
        try {
            list = dataStore.loadFromFile()
        }catch (e:Exception){
            e.stackTrace
        }
        return list
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onResume() {
        super.onResume()

        if (addContentClicked || onClick) {
            val recyclerView: RecyclerView = findViewById(R.id.CardContainerLayout)
            val manager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            recyclerView.layoutManager = manager
            val dataStore = StoreRetrieveData(applicationContext, FILENAME)
            incomingList = loadDatafromFile(dataStore, this)
            adapter = Adapter(incomingList!!, applicationContext)
            recyclerView.adapter = adapter
            //Toast.makeText(applicationContext,"res",Toast.LENGTH_LONG).show()
            onClink = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (itemsDeleted){
            val dataStore = StoreRetrieveData(applicationContext,FILENAME)
            dataStore.saveToFile(incomingList!!)
        }
        //Toast.makeText(applicationContext,"pause",Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (itemsDeleted){
            val dataStore = StoreRetrieveData(applicationContext,FILENAME)
            dataStore.saveToFile(incomingList!!)

        }
        //Toast.makeText(applicationContext,"des",Toast.LENGTH_LONG).show()

    }

    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

}