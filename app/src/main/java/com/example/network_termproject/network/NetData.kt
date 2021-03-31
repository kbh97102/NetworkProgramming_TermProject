package com.example.network_termproject.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

class NetData {

    lateinit var data: JSONObject

    class Builder {
        private var name = "nope"
        private var userID: String? = null
        private var roomID: String? = null
        private lateinit var type: String
        private var content: String? = null
        private var list: ArrayList<String>? = null

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun setType(type: String): Builder {
            this.type = type
            return this
        }

        fun setUserId(id: String): Builder {
            this.userID = id
            return this
        }

        fun setRoomId(id: String): Builder {
            this.roomID = id
            return this
        }

        fun setContent(content: String): Builder {
            this.content = content
            return this
        }

        fun setList(list: ArrayList<String>): Builder {
            this.list = list
            return this
        }

        fun build(): NetData {
            var clientData = NetData()
            clientData.data = JSONObject()
            val idList = JSONArray()
            if (Objects.nonNull(list)) {
                for (id in list!!) {
                    idList.put(id)
                }
            }
            try {
                if (name == "nope") {
                    name = Client.instance.name
                }
                clientData.data!!.apply {
                    put("name", name)
                    put("type", type)
                    put("content", content)
                    put("list", idList)
                    put("roomId", roomID)
                    put("userId", userID)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            list = null
            return clientData
        }

        fun parseServerData(data: String): NetData {
            val receivedData = NetData()
            try {
                receivedData.data = JSONObject(data)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return receivedData
        }
    }

    fun getType(): String {
        return data["type"] as String
    }

    fun getName(): String {
        return data["name"] as String
    }

    fun getContent(): String {
        return data["content"] as String
    }

    fun getUserId(): String {
        return data["userId"] as String
    }

    fun getRoomId(): String {
        return data["roomId"] as String
    }

    fun getList():ArrayList<String>{
        val array = data.getJSONArray("list")
        val arr = ArrayList<String>()
        if (array.length() <= 0){
            Log.e("Size is zero", "Error")
        }
        for (id in 0 until array.length()){
            arr.add(array.getString(id).replace("[", "").replace("]",""))
        }
        return arr
    }

    fun getImage():Bitmap{
        val imageData = data["content"] as String
        val imageArr = Base64.decode(imageData, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageArr,0, imageArr.size)
    }

    fun parseByteBuffer():ByteBuffer{
        return ByteBuffer.wrap(data.toString().toByteArray())
    }
}