package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

var onClick = false

class Adapter(item: ArrayList<Items>, context: Context) : RecyclerView.Adapter<Adapter.viewHolder>() {

    var mitems : ArrayList<Items> = item;
    var mContext : Context = context

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card_title : TextView = itemView.findViewById(R.id.card_title)
        var card_description : TextView = itemView.findViewById(R.id.card_description)
        var card_date: TextView = itemView.findViewById(R.id.card_Date);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView : View = LayoutInflater.from(parent.context).inflate(R.layout.card,parent,false)
        return viewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mitems.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val nItems: Items = mitems.get(position)
        holder.card_title.text = nItems.getTitle()
        var description: String = nItems.getDesc()
        if (description.length > 100){
            val rnds : Int = (100..150).random()
            description = description.substring(0,rnds) + "..."
        }
        if (nItems.getDesc().isEmpty()){
            holder.card_description.visibility = View.GONE
        }else{
            holder.card_description.text = description
        }
        holder.card_date.text = nItems.getDate()

        holder.itemView.setOnClickListener(View.OnClickListener {
            var intent = Intent(mContext,ViewNote::class.java)
            intent.putExtra("position",position)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(intent)
            onClick = true
        })
    }
    public fun getClick(): Boolean{
        return onClick
    }
}