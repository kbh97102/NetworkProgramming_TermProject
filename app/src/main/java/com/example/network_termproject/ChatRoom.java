package com.example.network_termproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.network_termproject.databinding.ChatRoomLayoutBinding;
import com.example.network_termproject.network.AnotherClient;
import com.example.network_termproject.network.Client;
import com.example.network_termproject.recycler.ChatAdapter;

import org.json.JSONException;
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
    private ArrayList<AnotherClient> users;
    private String talkDataPath;
    private Executor executor;
    private ChatRoomLayoutBinding binding;
    private ChatAdapter chatAdapter;
    private ArrayList<JSONObject> datas;
    private Client client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatRoomLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        chatAdapter = new ChatAdapter(datas, client.getName());
        binding.chatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRoomRecyclerView.setAdapter(chatAdapter);
        binding.chatRoomSendButton.setOnClickListener((v)->{
            JSONObject test = new JSONObject();
            try {
                test.put("name", "testName");
                test.put("type", "text");
                test.put("content", "Tltvkf RHfqkerp gksp");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            datas.add(test);
            chatAdapter.notifyDataSetChanged();
        });
    }

    private void init(){
        executor = Executors.newFixedThreadPool(2);
//
//        Intent intent = getIntent();
//        users = (ArrayList<AnotherClient>) intent.getExtras().get("users");
//        client = (Client)intent.getExtras().get("client");

        datas = new ArrayList<>();
        client = new Client();
        users = new ArrayList<>();
        client.setDisplay(this::display);
        client.setName("testName");

    }

    private void display(JSONObject data){
        runOnUiThread(()->{
            datas.add(data);
            chatAdapter.notifyDataSetChanged();
        });
    }

    public void getTalkData(){

    }
}
