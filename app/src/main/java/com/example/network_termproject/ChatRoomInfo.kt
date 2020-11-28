package com.example.network_termproject

import com.example.network_termproject.network.NetData
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class ChatRoomInfo : Serializable {
    var room_id: String? = hashCode().toString()
    var user_ids: ArrayList<String>? = null
}