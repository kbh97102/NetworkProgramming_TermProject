package com.example.network_termproject

import java.io.Serializable
import java.util.*

class ChatRoomInfo : Serializable {
    var room_id: String? = hashCode().toString()
    var user_ids: ArrayList<String>? = null
}