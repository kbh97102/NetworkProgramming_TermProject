package com.example.network_termproject.recycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.network_termproject.ChatRoomInfo;
import com.example.network_termproject.databinding.ChatListItemLayoutBinding;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {

    private ArrayList<ChatRoomInfo> chatRoomInfos;
    private Context context;
    private Consumer<ChatRoomInfo> intentChatRoom;

    public ListAdapter(ArrayList<ChatRoomInfo> chatRooms, Context context) {
        this.chatRoomInfos = chatRooms;
        this.context = context;
    }

    public void setIntentChatRoom(Consumer<ChatRoomInfo> intentChatRoom) {
        this.intentChatRoom = intentChatRoom;
    }

    class ListHolder extends RecyclerView.ViewHolder {

        private ChatListItemLayoutBinding binding;

        public ListHolder(ChatListItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatRoomInfo chatRoom) {
            binding.chatListNameTextView.setText(chatRoom.getRoom_id());
        }
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatListItemLayoutBinding binding = ChatListItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
//        holder.binding.getRoot().setOnClickListener((v) -> context.startActivity(new Intent(context, chatRoomInfos.get(position).getClass())));
        holder.binding.getRoot().setOnClickListener(view -> intentChatRoom.accept(chatRoomInfos.get(position)));
        ChatRoomInfo selectedChatRoom = chatRoomInfos.get(position);
        holder.bind(selectedChatRoom);
    }

    @Override
    public int getItemCount() {
        return chatRoomInfos.size();
    }
}
