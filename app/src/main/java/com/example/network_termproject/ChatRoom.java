package com.example.network_termproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.network_termproject.databinding.ChatRoomLayoutBinding;
import com.example.network_termproject.network.AnotherClient;
import com.example.network_termproject.network.Client;
import com.example.network_termproject.network.NetData;
import com.example.network_termproject.recycler.ChatAdapter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatRoom extends AppCompatActivity {

    /*
    채팅방이 가져야할 정보들
    1. 사용자 그룹 (1:1) or 그룹 -> arraylist쓰자
    3. 카톡 내용들 -> 내부 저장소에 캐시 형태로 저장하면 될 것 같다.
     */
    private String talkDataPath;
    private String roomName;
    private ChatRoomLayoutBinding binding;
    private ChatAdapter chatAdapter;
    private ArrayList<NetData> datas;
    private NetData.Builder dataBuilder;
    private ChatRoomInfo chatRoomInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatRoomLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        chatRoomInfo = (ChatRoomInfo) getIntent().getSerializableExtra("chatRoomInfo");
        Bundle bundle = getIntent().getBundleExtra("chatRoomInfo");
        assert bundle != null;
        chatRoomInfo = (ChatRoomInfo)bundle.getSerializable("chatRoomInfo");

        init();

        binding.chatRoomToolbar.setTitle(chatRoomInfo.getRoom_id());

        chatAdapter = new ChatAdapter(datas, Client.getInstance().getName());
        binding.chatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRoomRecyclerView.setAdapter(chatAdapter);
        binding.chatRoomSendButton.setOnClickListener((v)->{
            NetData clientData = dataBuilder.setType("text")
                    .setName(Client.getInstance().getName())
                    .setUserId(Client.getInstance().getId())
                    .setRoomId(chatRoomInfo.getRoom_id())
                    .setContent(binding.chatRoomEditText.getText().toString())
                    .build();
            datas.add(clientData);
            Client.getInstance().write(clientData);
            chatAdapter.notifyDataSetChanged();
            binding.chatRoomEditText.setText("");
        });

        datas.add(dataBuilder.setName("hello").setContent("message").build());
        datas.add(dataBuilder.setName("hello").setContent("message").build());
        datas.add(dataBuilder.setName("hello").setContent("message").build());
        datas.add(dataBuilder.setName("hello").setContent("message").build());
        datas.add(dataBuilder.setName("hello").setContent("message").build());

        chatAdapter.notifyDataSetChanged();

    }

    private void init(){
        datas = new ArrayList<>();
        Client.getInstance().setDisplay(this::display);
        Client.getInstance().setName("testName");
        dataBuilder = new NetData.Builder();
    }


    private void display(NetData data){
        runOnUiThread(()->{
            datas.add(data);
            chatAdapter.notifyDataSetChanged();
        });
    }

    public void getTalkData(){

    }

    public String getRoomName(){
        return roomName;
    }
}
