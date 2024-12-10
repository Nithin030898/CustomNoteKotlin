package com.example.myapplication

import org.json.JSONException
import org.json.JSONObject

class Items {

    private final var TITLESTRING = "NOTETITLE"
    private final var DESCRIPTIONSTRING = "NOTEDESCRIPTION"
    private final var DATESTRING = "DATESTRING"
    private final var mTitle:String = ""
        get() = field
        set(value) {
            field = value
        }
    private final var mdescription:String = ""
        get() = field
        set(value) {
            field = value
        }
    private final var mDate : String = ""

    constructor(title: String, description: String,date: String){
        mTitle = title
        mdescription = description
        mDate = date
    }

    constructor(jsonObject: JSONObject) {
        mTitle = jsonObject.getString(TITLESTRING)
        mdescription = jsonObject.getString(DESCRIPTIONSTRING)
        mDate = jsonObject.getString(DATESTRING)
    }

    @Throws(JSONException::class)
    fun toJSON():JSONObject{
        val jsonObject = JSONObject()
        jsonObject.put(TITLESTRING,mTitle)
        jsonObject.put(DESCRIPTIONSTRING,mdescription)
        jsonObject.put(DATESTRING,mDate)
        return jsonObject
    }

    fun getTitle() : String{
        return mTitle
    }

    fun setTitle(value: String){
        this.mTitle = value
    }

    fun getDesc():String{
        return mdescription
    }

    fun setDesc(value: String){
        this.mdescription = value
    }

    fun getDate():String{
        return mDate
    }

    fun setDate(value: String){
        this.mDate = value
    }


}