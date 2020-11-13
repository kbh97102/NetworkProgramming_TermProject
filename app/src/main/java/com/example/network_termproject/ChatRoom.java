package com.example.network_termproject;

import java.util.ArrayList;

public class ChatRoom {

    /*
    채팅방이 가져야할 정보들
    1. 사용자 그룹 (1:1) or 그룹 -> arraylist쓰자
    2. 서버소켓?
    3. 카톡 내용들 -> 내부 저장소에 캐시 형태로 저장하면 될 것 같다.
     */
    private ArrayList<Client> clients;
    private String talkDataPath;

    public ChatRoom(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public void getTalkData(){

    }
}
