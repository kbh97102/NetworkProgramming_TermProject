package com.example.network_termproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.network_termproject.databinding.ChatListLayoutBinding;
import com.example.network_termproject.network.AnotherClient;
import com.example.network_termproject.network.Client;
import com.example.network_termproject.network.NetData;
import com.example.network_termproject.recycler.ListAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


public class ChatListActivity extends AppCompatActivity {

    private final int listRequestCode = 10;
    private ChatListLayoutBinding binding;
    private ArrayList<ChatRoom> chatRooms;
    private ArrayList<ChatRoomInfo> chatRoomInfos;
    private ArrayList<AnotherClient> users;
    private ListAdapter listAdapter;
    private NetData.Builder dataBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatListLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }
        else{
            Log.d("Permission", "ok");
        }

        init();
        listAdapter = new ListAdapter(chatRoomInfos, this);
        listAdapter.setIntentChatRoom(this::intentChatRoom);
        binding.chatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatListRecyclerView.setAdapter(listAdapter);


        listAdapter.notifyDataSetChanged();



        binding.chatListAddButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(this, SelectFriend.class);
            startActivityForResult(intent, listRequestCode);
            Log.d("Socket", "is connected? "+Client.getInstance().testMethod());
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == listRequestCode && Objects.nonNull(data)) {
            assert data != null;
            ArrayList<String> userList = data.getStringArrayListExtra("selectedUserList");
            assert userList != null;
            if (Objects.isNull(userList) || userList.size() <= 0){
                return;
            }
            //선택된 친구 아이디 리스트에 자기 자신도 포함
            userList.add(Client.getInstance().getId());
            Log.e("list size", userList.size()+" users");
            //본인 포함 선택된 친구들 리스트와 함께 채팅방 생성 요청
            Client.getInstance().write(dataBuilder.setType("requestAdd")
                    .setUserId(Client.getInstance().getId())
                    .setList(userList)
                    .build());
        }
    }

    private void init() {
        chatRooms = new ArrayList<>();
        users = new ArrayList<>();
        chatRoomInfos = new ArrayList<>();
        dataBuilder = new NetData.Builder();
        Client.getInstance().setAddChatRoom(this::addChatRoom);
    }


    private void addChatRoom(String roomId) {
        runOnUiThread(() -> {
            ChatRoomInfo info = new ChatRoomInfo();
            info.setRoom_id(roomId);
            chatRoomInfos.add(info);
            listAdapter.notifyDataSetChanged();
        });
    }

    private void intentChatRoom(ChatRoomInfo chatRoomInfo){
        Intent intent = new Intent(this, ChatRoom.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("chatRoomInfo", chatRoomInfo);
        intent.putExtra("chatRoomInfo", bundle);
        startActivity(intent);
    }

    private void deleteChatRoom() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Client.getInstance().disconnect();
    }
}
