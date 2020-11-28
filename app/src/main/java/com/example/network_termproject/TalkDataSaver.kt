package com.example.network_termproject

import android.util.Log
import com.example.network_termproject.network.NetData
import java.lang.StringBuilder

class TalkDataSaver {

    fun generate(name: String, user_id: String, type: String, content: String): String {
        val builder = StringBuilder()
        builder.apply {
            append(name)
            append(" ")
            append(user_id)
            append(" ")
            append(type)
            append(" ")
            append(content)
        }
        return builder.toString()
    }

    fun parseToNetData(data : String):NetData{
        val dataArray = data.split(" ")
        val builder = NetData.Builder()
        builder.apply {
            setName(dataArray[0])
            setUserId(dataArray[1])
            setType(dataArray[2])
            setContent(dataArray[3])
        }
        return builder.build()
    }
}