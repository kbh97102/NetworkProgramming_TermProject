package com.example.network_termproject.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.network_termproject.ChatRoom;
import com.example.network_termproject.databinding.ChatRoomLayoutBinding;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {

    private ArrayList<ChatRoom> chatRooms;

    public ListAdapter(ArrayList<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    class ListHolder extends RecyclerView.ViewHolder {

        private ChatRoomLayoutBinding binding;

        public ListHolder(ChatRoomLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(ChatRoom chatRoom){

        }
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatRoomLayoutBinding binding = ChatRoomLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        ChatRoom selectedChatRoom = chatRooms.get(position);
        holder.bind(selectedChatRoom);
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }
}
