package com.example.network_termproject

import com.example.network_termproject.network.NetData
import java.io.Serializable
import java.util.*

class ChatRoomInfo : Serializable {
    var room_id: String? = hashCode().toString()
    var user_ids: ArrayList<String>? = null
    private var talks: ArrayList<NetData>
    fun setTalks(talks: ArrayList<NetData>) {
        this.talks = talks
    }

    init {
        talks = ArrayList()
    }
}