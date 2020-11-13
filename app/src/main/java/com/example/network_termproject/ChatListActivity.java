package com.example.network_termproject;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.network_termproject.databinding.ActivityMainBinding;

import java.util.ArrayList;


public class ChatListActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<ChatRoom> chatRooms;
    private ArrayList<Client> clients;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    private void init(){
        chatRooms = new ArrayList<>();
        clients = new ArrayList<>();
    }
}
