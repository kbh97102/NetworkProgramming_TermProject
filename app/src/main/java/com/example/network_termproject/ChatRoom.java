package com.example.network_termproject;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.network_termproject.databinding.ChatRoomLayoutBinding;
import com.example.network_termproject.network.Client;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ChatRoom extends AppCompatActivity {

    /*
    채팅방이 가져야할 정보들
    1. 사용자 그룹 (1:1) or 그룹 -> arraylist쓰자
    2. 서버소켓?
    3. 카톡 내용들 -> 내부 저장소에 캐시 형태로 저장하면 될 것 같다.
     */
    private ArrayList<Client> clients;
    private String talkDataPath;
    private Executor executor;
    private ChatRoomLayoutBinding binding;
    private HashMap<String, Consumer> displayMap;

    public ChatRoom(ArrayList<Client> clients) {
        this.clients = clients;
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatRoomLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        displayMap = new HashMap<>();


        startChat();
    }

    private void displayText(String text){

    }

    private void startChat(){
        for(Client client : clients) {
            executor.execute(client.read());
        }
    }

    private void display(JSONObject data){

    }

    public void getTalkData(){

    }
}
