package com.example.network_termproject;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.network_termproject.databinding.ActivityMainBinding;
import com.example.network_termproject.databinding.ChatListLayoutBinding;
import com.example.network_termproject.recycler.ListAdapter;

import java.util.ArrayList;


public class ChatListActivity extends AppCompatActivity {

    private ChatListLayoutBinding binding;
    private ArrayList<ChatRoom> chatRooms;
    private ArrayList<Client> clients;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatListLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listAdapter = new ListAdapter(chatRooms);
        binding.chatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatListRecyclerView.setAdapter(listAdapter);
    }

    private void init(){
        chatRooms = new ArrayList<>();
        clients = new ArrayList<>();
    }
}
