package com.example.network_termproject;

import com.example.network_termproject.network.NetData;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoomInfo implements Serializable {

    private String room_id;
    private ArrayList<String> user_ids;
    private ArrayList<NetData> talks;

    public ChatRoomInfo() {
        room_id = String.valueOf(hashCode());
        talks = new ArrayList<>();
    }

    public void setUser_ids(ArrayList<String> user_ids) {
        this.user_ids = user_ids;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public ArrayList<String> getUser_ids() {
        return user_ids;
    }

    public void setTalks(ArrayList<NetData> talks){
        this.talks = talks;
    }
}
