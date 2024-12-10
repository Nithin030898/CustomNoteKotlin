package com.example.myapplication

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.stream.Collectors

class StoreRetrieveData(var context: Context, var fileName: String) {

    private var mContext : Context = context
    private var mFileName : String = fileName

    fun toJSONArray(list: ArrayList<Items>): JSONArray {
        val JSONArray = JSONArray()

        for (listItem:Items in list){
            val jsonObject : JSONObject = listItem.toJSON()
            JSONArray.put(jsonObject)
        }

        return JSONArray
    }

    fun saveToFile(list: ArrayList<Items>){
        val fileOutStream : FileOutputStream = mContext.openFileOutput(mFileName,Context.MODE_PRIVATE)
        val outputStreamWriter : OutputStreamWriter = OutputStreamWriter(fileOutStream)
        outputStreamWriter.write(toJSONArray(list).toString())
        outputStreamWriter.close()
        fileOutStream.close()
    }

    fun loadFromFile():ArrayList<Items>{
        val list = ArrayList<Items>()
        var bufferedReader : BufferedReader? = null
        var fileInputStream : FileInputStream? = null
        try {
            fileInputStream = mContext.openFileInput(mFileName)
            val builder = StringBuilder()
            var line : String
            bufferedReader = BufferedReader(InputStreamReader(fileInputStream))
            line =  bufferedReader.lines().collect(Collectors.joining());
            Log.v("my message", "buffer" +" " +bufferedReader.readLine().toString())
        //    val iterator = bufferedReader.lineSequence().iterator()
        //    while ((bufferedReader.readLine().also { line = it }) != null) {
        //        Toast.makeText(mContext,bufferedReader.readLine(),Toast.LENGTH_LONG).show()
        //        builder.append(line)
        //    }
        //    while (bufferedReader.readLine() != null){
         //       line = bufferedReader.readLine()
        //        builder.append(line)
        //        Toast.makeText(mContext,builder.toString(),Toast.LENGTH_LONG).show()
         //   }
         //   Toast.makeText(mContext,builder.toString(),Toast.LENGTH_LONG).show()
          //  val iterator = bufferedReader.lineSequence().iterator()
          //  Log.v("my message", "builder"+iterator.toString())
            builder.append(line)
            val jsonArray : JSONArray = JSONTokener (builder.toString()).nextValue() as JSONArray
            Log.v("my message", "jarray"+builder)
            for (i in 0 until jsonArray.length()){
                val Items = Items(jsonArray.getJSONObject(i))
                list.add(Items)
                //Toast.makeText(mContext,Items.getTitle(),Toast.LENGTH_LONG).show()
            }

        }catch (exep : Exception){
            Log.v("catch exception",exep.toString())

        }finally {
            if (bufferedReader!=null){
                bufferedReader.close()
            }
            if (fileInputStream!=null){
                fileInputStream.close()
            }

        }
        return list
    }

}